package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.ElectiveSubject;
import in.canaris.cloud.openstack.entity.ElectvieSubjectUserMapping;
import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface ElectvieSubjectUserMappingRepository extends JpaRepository<ElectvieSubjectUserMapping, Integer> {

	List<ElectvieSubjectUserMapping> findByUserName(String userName);

	boolean existsByUserNameAndElectiveAndSemester(String studentUsername, SubjectMaster subject,
			SemesterMaster semester);

	
	
	

}
