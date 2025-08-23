package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import groovy.cli.Option;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.RequestApproval;

@Repository
@Transactional
public interface RequestApprovalRepository extends JpaRepository<RequestApproval, Long> {

	RequestApproval findByRequestId(long request_id);

	List<RequestApproval> findByRequesterName(String requester_name);
	
	//List<RequestApproval> findByadminApprovalOrderByRequestIdDesc(String approval);

	//List<RequestApproval> findByRequesterNameOrderByIdDesc(String requester_name);

	List<RequestApproval> findAllByOrderByIdDesc();

	@Query("SELECT ra FROM RequestApproval ra WHERE ra.requesterName IN :users ORDER BY ra.id DESC")
	List<RequestApproval> findByRequesters(List<String> users);
	
	@Query("SELECT r FROM RequestApproval r WHERE r.requesterName = :requesterName ORDER BY r.id DESC")
	List<RequestApproval> findByRequesterTest(String requesterName);

	
	@Query("SELECT ra FROM RequestApproval ra WHERE ra.requesterName IN :users order by ra.id desc")
	List<RequestApproval> findByRequestersorderbyid(List<String> users);
	
	
	@Query("SELECT ra.requestId FROM RequestApproval ra WHERE ra.requesterName=:users order by ra.id desc")
	List<Integer> findByRequesterNameCustom(String users);
	
	

}
