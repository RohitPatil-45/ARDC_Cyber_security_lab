package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.ElectiveSubject;

public interface ElectiveSubjectRepository extends JpaRepository<ElectiveSubject, Integer> {
	
	

}
