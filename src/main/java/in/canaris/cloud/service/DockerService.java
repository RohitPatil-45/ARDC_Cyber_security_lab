package in.canaris.cloud.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

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

}
