package in.canaris.cloud.openstack.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.OpenstackVMRequest;

@Repository
@Transactional
public interface OpenstackVMRequestRepository extends JpaRepository<OpenstackVMRequest, Integer> {
	
	List<OpenstackVMRequest> findByrequestBy(String username);
	
	List<OpenstackVMRequest> findByadminApprovalStatus(String approval);
	
	@Query("SELECT ra FROM OpenstackVMRequest ra WHERE ra.requestBy IN :users")
	List<OpenstackVMRequest> findByRequesters(List<String> users);
}
