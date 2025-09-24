package in.canaris.cloud.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import in.canaris.cloud.openstack.entity.Discover_Docker_Network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import in.canaris.cloud.repository.DiscoverDockerNetworkRepository;

@Service
public class DockerService {

	private final DockerClient dockerClient;

	@Autowired
	DiscoverDockerNetworkRepository DiscoverDockerNetworkRepository;

	public DockerService() {
		// Configure docker client (uses /var/run/docker.sock by default)
		DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

		DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost())
				.sslConfig(config.getSSLConfig()).maxConnections(100).connectionTimeout(Duration.ofSeconds(30))
				.responseTimeout(Duration.ofSeconds(45)).build();

		this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
	}

	public String runContainer(String imageName, String containerName, Map<Integer, Integer> portMappings,
			String networkName) {

		Ports ports = new Ports();

		// Map hostPort -> containerPort
		portMappings.forEach((hostPort, containerPort) -> {
			ExposedPort exposedPort = ExposedPort.tcp(containerPort);
			ports.bind(exposedPort, Ports.Binding.bindPort(hostPort));
		});

		HostConfig hostConfig = HostConfig.newHostConfig().withPortBindings(ports).withNetworkMode(networkName); // network

		// Set environment variables
		List<String> envVars = new ArrayList<>();
		envVars.add("CONTAINER_NAME=" + containerName); // Add more as needed

		CreateContainerResponse container = dockerClient.createContainerCmd(imageName).withName(containerName)
				.withTty(true) // same as -it
				.withHostConfig(hostConfig).withEnv(envVars) // <--- set environment variables here
				.exec();

		dockerClient.startContainerCmd(container.getId()).exec();

		InspectContainerResponse inspect = dockerClient.inspectContainerCmd(container.getId()).exec();

		if (inspect.getState().getRunning()) {
			return "success";
		} else {
			return "fail";
		}

	}

	public void stopContainer(String containerId) {
		dockerClient.stopContainerCmd(containerId).exec();
	}

	/**
	 * Remove a container
	 */
	public void removeContainer(String containerId) {
		dockerClient.removeContainerCmd(containerId).withForce(true).exec();
	}

	public void startContainerByName(String containerName) {
		try {
			String containerId = dockerClient.inspectContainerCmd(containerName).exec().getId();
			dockerClient.startContainerCmd(containerId).exec();
			System.out.println("Container '" + containerName + "' started successfully.");
		} catch (Exception e) {
			System.err.println("Failed to start container '" + containerName + "': " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void stopContainerByName(String containerName) {
		try {
			String containerId = dockerClient.inspectContainerCmd(containerName).exec().getId();
			dockerClient.stopContainerCmd(containerId).exec();
		} catch (Exception e) {
			System.err.println("Failed to stop container '" + containerName + "': " + e.getMessage());
		}
	}

	public void removeContainerByName(String containerName) {
		try {
			String containerId = dockerClient.inspectContainerCmd(containerName).exec().getId();
			dockerClient.removeContainerCmd(containerId).withForce(true).exec();
		} catch (Exception e) {
			System.err.println("Failed to remove container '" + containerName + "': " + e.getMessage());
		}
	}

	public List<String> listDockerNetworks() {
		System.out.println("inside _listDockerNetworks");
		try {
			List<Network> networks = dockerClient.listNetworksCmd().exec();

			// Header line (optional)
			List<String> output = new ArrayList<>();
			output.add(String.format("%-15s %-25s %-10s %-10s", "NETWORK ID", "NAME", "DRIVER", "SCOPE"));

			for (Network network : networks) {
				String id = network.getId().substring(0, 12); // Trim to 12 characters
				String name = network.getName();
				String driver = network.getDriver() != null ? network.getDriver() : "null";
				String scope = network.getScope();

				String formattedLine = String.format("%-15s %-25s %-10s %-10s", id, name, driver, scope);
				output.add(formattedLine);
			}

			return output;

		} catch (Exception e) {
			System.err.println("Error listing Docker networks: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public void discoverAndSaveDockerNetworks(String physicalServerName) {
		List<Network> networks = dockerClient.listNetworksCmd().exec();

		for (Network network : networks) {
			Discover_Docker_Network entity = new Discover_Docker_Network();

			entity.setName(network.getName());
			entity.setNetworkId(network.getId().substring(0, 12)); // like docker CLI
			entity.setDriver(network.getDriver() != null ? network.getDriver() : "null");
			entity.setScope(network.getScope());

			// Fetch IPAM/Gateway/Subnet Info
			if (network.getIpam() != null && network.getIpam().getConfig() != null
					&& !network.getIpam().getConfig().isEmpty()) {
				Network.Ipam.Config config = network.getIpam().getConfig().get(0);
				entity.setGateway(config.getGateway() != null ? config.getGateway() : "N/A");

				// Here you can parse subnet or IP range
				String subnet = config.getSubnet();
				if (subnet != null && subnet.contains("/")) {
					String[] parts = subnet.split("/");
					entity.setStartIp(parts[0]); // crude way; refine if needed
					entity.setEndIp("N/A"); // Docker Java API doesn’t always give IP range, so set accordingly
				} else {
					entity.setStartIp("N/A");
					entity.setEndIp("N/A");
				}
			} else {
				entity.setGateway("N/A");
				entity.setStartIp("N/A");
				entity.setEndIp("N/A");
			}

			entity.setPhysicalServer(physicalServerName); // From your input (e.g. IP or name)

			// Save the entity
			DiscoverDockerNetworkRepository.save(entity);
		}
	}

	public String loadImageFromTar(String filePath, String expectedImageName) {
		try {
			System.out.println("inside load image ");
			File imageFile = new File(filePath);
			if (!imageFile.exists()) {
				return "fail: Image file not found at " + filePath;
			}

			// Detect OS
			String os = System.getProperty("os.name").toLowerCase();
			boolean isWindows = os.contains("win");

			// Build docker load command
			String dockerLoadCmd = String.format("docker load -i \"%s\"", filePath);

			ProcessBuilder processBuilder;
			if (isWindows) {
				processBuilder = new ProcessBuilder("cmd.exe", "/c", dockerLoadCmd);
			} else {
				processBuilder = new ProcessBuilder("/bin/sh", "-c", dockerLoadCmd);
			}

			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// Stream output live
			Thread outputThread = new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("[docker load] " + line); // live log
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			outputThread.start();

			int exitCode = process.waitFor();
			outputThread.join(); // wait until all logs are printed

			if (exitCode != 0) {
				return "fail: Docker load command failed (exit " + exitCode + ")";
			}

			// Verify if image exists
			String checkCmd = String.format("docker images -q %s", expectedImageName);
			ProcessBuilder checkBuilder;
			if (isWindows) {
				checkBuilder = new ProcessBuilder("cmd.exe", "/c", checkCmd);
			} else {
				checkBuilder = new ProcessBuilder("/bin/sh", "-c", checkCmd);
			}

			checkBuilder.redirectErrorStream(true);
			Process checkProcess = checkBuilder.start();

			String imageId = null;
			try (BufferedReader checkReader = new BufferedReader(
					new InputStreamReader(checkProcess.getInputStream()))) {
				imageId = checkReader.readLine();
			}

			int checkExit = checkProcess.waitFor();
			if (checkExit == 0 && imageId != null && !imageId.isEmpty()) {
				return "success";
			} else {
				return "fail";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	public String runWindowsContainer(String imageName, String newInstanceName, Map<Integer, Integer> portMappings,
			String network) {
		try {
			Integer rdpPort = null;
			Integer novncPort = null;

			for (Map.Entry<Integer, Integer> entry : portMappings.entrySet()) {
				if (entry.getValue() == 3389) {
					rdpPort = entry.getKey();
				} else if (entry.getValue() == 8080) {
					novncPort = entry.getKey();
				}
			}

			if (rdpPort == null || novncPort == null) {
				throw new IllegalArgumentException("RDP (3389) and noVNC (8006) ports must be mapped in portMappings");
			}

			String scriptPath = "/home/ubuntu/Desktop/ARDC_Lab/windows-docker/win.sh";

			ProcessBuilder pb = new ProcessBuilder("bash", scriptPath, "--lab-name", newInstanceName, "--rdp-port",
					String.valueOf(rdpPort), "--novnc-port", String.valueOf(novncPort), "--net-name", network);

			pb.redirectErrorStream(true);
			Process process = pb.start();

			// Read container ID from script output
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String containerId = null;
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("output :: " + line.trim());
				containerId = line.trim(); // last line should be container ID
			}

			int exitCode = process.waitFor();
			if (exitCode != 0 || containerId == null || containerId.isEmpty()) {
				return "fail";
			}

			// Verify container is running using Docker API
			InspectContainerResponse inspect = dockerClient.inspectContainerCmd(containerId).exec();
			if (inspect.getState().getRunning()) {
				return "success";
			} else {
				return "fail";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	public String getContainerIpViaCli(String containerName) {
		try {
			ProcessBuilder pb = new ProcessBuilder("docker", "inspect", "-f",
					"{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}", containerName);
			pb.redirectErrorStream(true);
			Process process = pb.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String ip = reader.readLine();

			process.waitFor();
			return ip != null ? ip.trim() : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
