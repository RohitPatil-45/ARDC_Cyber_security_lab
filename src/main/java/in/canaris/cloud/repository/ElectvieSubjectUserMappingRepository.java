package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.ElectiveSubject;
import in.canaris.cloud.openstack.entity.ElectvieSubjectUserMapping;

public interface ElectvieSubjectUserMappingRepository extends JpaRepository<ElectvieSubjectUserMapping, Integer> {

	List<ElectvieSubjectUserMapping> findByUserName(String userName);

	
	
	

}
