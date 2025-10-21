package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.ElectiveSubject;
import in.canaris.cloud.openstack.entity.ElectvieSubjectUserMapping;
import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface ElectvieSubjectUserMappingRepository extends JpaRepository<ElectvieSubjectUserMapping, Integer> {

	List<ElectvieSubjectUserMapping> findByUserName(String userName);

	boolean existsByUserNameAndElectiveAndSemester(String studentUsername, SubjectMaster subject,
			SemesterMaster semester);

	List<ElectvieSubjectUserMapping> findByUserNameAndElective(String userName, SubjectMaster subject);

//	Optional<ElectvieSubjectUserMapping> findById(Long mappingId);
//
//	void deleteById(Long mappingId);



	
	
	

}
