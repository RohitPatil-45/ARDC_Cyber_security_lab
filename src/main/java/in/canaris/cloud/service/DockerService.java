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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DockerService {

	private final DockerClient dockerClient;

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

		CreateContainerResponse container = dockerClient.createContainerCmd(imageName).withName(containerName)
				.withTty(true) // same as -it
				.withHostConfig(hostConfig).exec();

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
	    try {
	        List<Network> networks = dockerClient.listNetworksCmd().exec();
	        return networks.stream()
	                .map(network -> String.format("%s (%s, %s)", network.getName(), network.getDriver(), network.getScope()))
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        System.err.println("Error listing Docker networks: " + e.getMessage());
	        e.printStackTrace();
	        return List.of(); // Return empty list on error
	    }
	}
	
	public String loadImageFromTar(String filePath, String expectedImageName) {
	    try {
	        File imageFile = new File(filePath);
	        if (!imageFile.exists()) {
	            return "fail: Image file not found at " + filePath;
	        }

	        // Load the image into Docker
	        try (InputStream inputStream = new FileInputStream(imageFile)) {
	            dockerClient.loadImageCmd(inputStream).exec();
	        }

	        
	        boolean imageExists = dockerClient.listImagesCmd()
	                .withImageNameFilter(expectedImageName) 
	                .exec()
	                .size() > 0;

	        return imageExists
	                ? "success"
	                : "fail";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "fail";
	    }
	}

}
