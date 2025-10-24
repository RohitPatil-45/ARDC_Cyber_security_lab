package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.entity.DiscoverDockerContainers;

public interface DiscoverDockerContainersRepository extends JpaRepository<DiscoverDockerContainers, Integer>{
	
	DiscoverDockerContainers findByContainerId(String containerId);

}
