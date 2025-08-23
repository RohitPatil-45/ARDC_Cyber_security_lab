package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.canaris.cloud.entity.CloudInstanceNodeHealthMonitoring;
import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.server.entity.HardwareInventory;
import in.canaris.cloud.server.entity.NodeHealthMonitoring;

public interface CloudInstanceNodeHealthMonitoringRepository
		extends JpaRepository<CloudInstanceNodeHealthMonitoring, Integer> {

	@Query("SELECT e FROM CloudInstanceNodeHealthMonitoring e WHERE e.nodeIp=:server_ip AND e.vmName=:vm_name")
	CloudInstanceNodeHealthMonitoring findBynodeIp(String server_ip,String vm_name);
	
	
	@Query("SELECT e FROM CloudInstanceNodeHealthMonitoring e ")
	List<CloudInstanceNodeHealthMonitoring> findByHealthMonitoring();

	
	@Query("SELECT e FROM CloudInstanceNodeHealthMonitoring e WHERE e.vmName in(:vm_name)")
	List<CloudInstanceNodeHealthMonitoring> findByHealthMonitoring(List<String> vm_name);

}
