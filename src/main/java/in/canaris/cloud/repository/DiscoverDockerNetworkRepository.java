package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.entity.ADConfig;
import in.canaris.cloud.openstack.entity.Discover_Docker_Network;
import in.canaris.cloud.openstack.entity.DockerNetworkUserDTO;

public interface DiscoverDockerNetworkRepository extends JpaRepository<Discover_Docker_Network, Integer> {

	@Query(value = "SELECT network_id, name FROM discover_docker_network", nativeQuery = true)
	List<Object[]> getDockerNetworks();

	@Query(value = "SELECT d FROM Discover_Docker_Network d")
	List<Discover_Docker_Network> findByDriver();

	@Query(value = "SELECT d.name AS networkName, d.network_id AS networkId, d.driver, d.scope, "
			+ "d.gateway, d.start_ip AS startIp, d.end_ip AS endIp, d.physicalserver AS physicalServer, "
			+ "u.username, u.scenario_id AS scenarioId, u.ip_address AS lastActiveConnection "
			+ "FROM discover_docker_network d "
			+ "LEFT JOIN user_lab u ON d.network_id = u.ip_address", nativeQuery = true)
	List<Object[]> fetchDockerNetworksWithUsersNative();

}
