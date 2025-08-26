package in.canaris.cloud.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface CloudInstanceRepository extends JpaRepository<CloudInstance, Integer> {

	@Query(value = "SELECT * FROM cloud_instance c WHERE c.id = :id AND DATE(c.vm_start_stop_time) = CURDATE() - INTERVAL 1 DAY", nativeQuery = true)
	Optional<CloudInstance> findByIdAndVmStartStopTimeYesterday(int id);

	@Query("SELECT  c.id,c.instance_ip FROM CloudInstance c WHERE c.instance_ip is not null AND c.instance_ip != '-' AND c.instance_ip != '' GROUP BY c.instance_ip")
	List<Object[]> getAllInstanceIP();

	@Query("SELECT  c.id,c.instance_name FROM CloudInstance c Where c.isMonitoring=:monitoring")
	List<Object[]> getInstanceName(boolean monitoring);

	@Query("SELECT  c.id,c.instance_name FROM CloudInstance c where c.groupName IN :groupName AND c.isMonitoring=:monitoring")
	List<Object[]> getInstanceNameByGroup(List<String> groupName, boolean monitoring);

	@Query("SELECT   c.instance_ip,c.instance_name FROM CloudInstance c where c.groupName IN :groupName")
	List<Object[]> getInstanceNameByGroupvalue(List<String> groupName);

//	@Query("SELECT  c.id,c.instance_name FROM CloudInstance c")
//	List<Object[]> getInstanceName2();

	@Query("SELECT  c.instance_ip,c.instance_name FROM CloudInstance c")
	List<Object[]> getInstanceNameAndInstanceIP();

	List<CloudInstance> findByIsMonitoring(boolean monitoring);

	List<CloudInstance> findByIsMonitoringOrderByIdDesc(boolean monitoring);
	List<CloudInstance> findAllByOrderByIdDesc();

	@Query("SELECT  c.instance_name FROM CloudInstance c WHERE c.isMonitoring=:monitoring")
	List<String> findByIsMonitoringOrderByIdDescOnlyVM(boolean monitoring);

	@Query("SELECT  c FROM CloudInstance c WHERE groupName=:groupName")
	List<CloudInstance> findByGroup(String groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE groupName IN :groupName")
	List<CloudInstance> findByGroupIN(List<String> groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE customerName=:customerName")
	List<CloudInstance> findByCustomer(String customerName);

	@Query("SELECT  c FROM CloudInstance c WHERE customerName=:customerName AND  c.groupName=:groupName")
	List<CloudInstance> findByCustomerandgroupbysort(String customerName, String groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE vm_state=:vm_status")
	List<CloudInstance> findByVMStatus(String vm_status);

	@Query("SELECT  c FROM CloudInstance c WHERE vm_state IN :vm_status AND c.isMonitoring=:monitoring")
	List<CloudInstance> findByVMStatusIN(List<String> vm_status, boolean monitoring);

	@Query("SELECT  c FROM CloudInstance c WHERE c.vm_state=:vm_status AND c.groupName=:groupName")
	List<CloudInstance> findByVMStatusbygroupsort(String vm_status, String groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE c.vm_state IN :vm_status AND c.groupName=:groupName AND c.isMonitoring=:monitoring")
	List<CloudInstance> findByVMStatusbygroupsortIn(List<String> vm_status, String groupName, boolean monitoring);

	@Query("SELECT  c FROM CloudInstance c WHERE instance_name=:instance_name")
	List<CloudInstance> findByInstanceName(String instance_name);

	@Query("SELECT  c FROM CloudInstance c WHERE instance_name=:instance_name")
	CloudInstance findByInstanceNamee(String instance_name);

	@Query("SELECT  c FROM CloudInstance c WHERE c.groupName IN :groupName AND c.isMonitoring=:monitoring")
	List<CloudInstance> findByIsMonitoringAndGroupName(boolean monitoring, List<String> groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE c.groupName IN :groupName AND c.isMonitoring=:monitoring order by c.id desc")
	List<CloudInstance> findByIsMonitoringAndGroupNameOrderByIdDesc(boolean monitoring, List<String> groupName);

	@Query("SELECT  c FROM CloudInstance c WHERE c.groupName IN :groupName order by c.id desc")
	List<CloudInstance> findByGroupNameOrderByIdDesc(List<String> groupName);

	@Query("SELECT  c.instance_name FROM CloudInstance c WHERE c.groupName IN :groupName AND c.isMonitoring=:monitoring order by c.id desc")
	List<String> findByIsMonitoringAndGroupNameOrderByIdDescOnlyVM(boolean monitoring, List<String> groupName);

	@Modifying
	@Query("UPDATE CloudInstance d SET d.groupName=:groupName WHERE d.id=:instanceID")
	void addVmToGroup(int instanceID, String groupName);

	@Modifying
	@Query("UPDATE CloudInstance d SET d.customerName=:customerName WHERE d.id=:instanceID")
	void addCustomerToVM(int instanceID, String customerName);

	@Query("SELECT  c.id,c.instance_name FROM CloudInstance c where c.id IN :ids and isMonitoring=:isMonitoring")
	List<Object[]> getInstanceNameByRequester(List<Integer> ids, boolean isMonitoring);

	@Query("SELECT   c.instance_ip,c.instance_name FROM CloudInstance c where c.id IN :ids and isMonitoring=:isMonitoring")
	List<Object[]> getInstanceNameByRequestervalue(List<Integer> ids, boolean isMonitoring);

	List<CloudInstance> findByidInAndIsMonitoring(List<Integer> ids, boolean isMonitoring);

	@Query("SELECT c.instance_name FROM CloudInstance c where c.id IN :ids and isMonitoring=:isMonitoring")
	List<String> findByidInAndIsMonitoringOnlyVm(List<Integer> ids, boolean isMonitoring);

	@Query("SELECT c FROM CloudInstance c WHERE c.instance_name = :instanceName AND c.virtualization_type = :virtualizationType AND c.physicalServerIP = :serverIP")
	CloudInstance findByInstanceNameAndVirtualizationType(String instanceName, String virtualizationType,
			String serverIP);

	@Query("SELECT c.instance_name FROM CloudInstance c where  c.physicalServerIP = :serverIP AND isMonitoring=:isMonitoring")
	List<String> getInstanceNameByServerIP(String serverIP, boolean isMonitoring);

//	@Query("UPDATE CloudInstance c SET c.isMonitoring = false WHERE c.physicalServerIP = :serverIP AND c.virtualization_type = 'KVM'")
//	int setMonitoringFalseForKVM(String serverIP);

	// List<CloudInstance> findAllById(List<Long> instanceName);

//	@Query("SELECT  c FROM CloudInstance c WHERE instance_name=:instance_name")
//	CloudInstance findByInstanceNameAndVirtualizationType(String instanceName, String virtualizationType);
	@Modifying
	@Transactional
	@Query("UPDATE CloudInstance c SET c.instance_name = :updateVMName,isMonitoring=:isMonitoring WHERE c.physicalServerIP = :serverIP AND c.instance_name = :vmname")
	int updateInstanceFalse(String serverIP, String vmname, String updateVMName, boolean isMonitoring);

	@Modifying
	@Transactional
	@Query("UPDATE CloudInstance c SET c.instance_ip = :instanceIP,c.mac_address= :vm_mac WHERE c.physicalServerIP = :serverIP AND c.instance_name = :kVMvmName")
	int updateInstanceIpIfExists(String serverIP, String kVMvmName, String instanceIP, String vm_mac);

	@Modifying
	@Transactional
	@Query("UPDATE CloudInstance c SET c.memoryAssigned = :memoryAssigned WHERE c.id = :id")
	int updateVMRam(int id, String memoryAssigned);

	@Modifying
	@Transactional
	@Query("UPDATE CloudInstance c SET c.cpuAssigned = :cpuAssigned WHERE c.id = :id")
	int updateVMCPU(int id, String cpuAssigned);

	@Query("SELECT c FROM CloudInstance c where c.AssignedLab = 'No'")
	List<CloudInstance> getInstanceNameNotAssigned();

	@Modifying
	@Transactional
	@Query("UPDATE CloudInstance c SET c.AssignedLab = 'Yes' WHERE c.id = :labId")
	int updateInstanceNameAssigned(int labId);
}
