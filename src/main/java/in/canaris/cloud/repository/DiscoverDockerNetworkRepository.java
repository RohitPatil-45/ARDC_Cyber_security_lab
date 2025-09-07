package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.entity.ADConfig;
import in.canaris.cloud.openstack.entity.Discover_Docker_Network;

public interface DiscoverDockerNetworkRepository extends JpaRepository<Discover_Docker_Network, Integer> {

	@Query(value = "SELECT network_id, name FROM discover_docker_network", nativeQuery = true)
	List<Object[]> getDockerNetworks();



	@Query(value = "SELECT d FROM Discover_Docker_Network d")
	List<Discover_Docker_Network> findByDriver();


	

	


}
