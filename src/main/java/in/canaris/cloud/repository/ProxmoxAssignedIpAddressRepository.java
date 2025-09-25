package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.canaris.cloud.entity.ProxmoxAssignedIpAddress;

public interface ProxmoxAssignedIpAddressRepository extends JpaRepository<ProxmoxAssignedIpAddress, Integer> {

	@Query(value = "SELECT ipAddress FROM proxmox_assigned_ip_address "
			+ "ORDER BY INET_ATON(ipAddress) DESC LIMIT 1", nativeQuery = true)
	String findMaxIp();

}
