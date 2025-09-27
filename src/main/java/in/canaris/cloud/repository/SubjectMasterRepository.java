package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, Integer> {

}
