package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.SemesterMaster;

public interface SemesterMasterRepository extends JpaRepository<SemesterMaster, Integer> {

}
