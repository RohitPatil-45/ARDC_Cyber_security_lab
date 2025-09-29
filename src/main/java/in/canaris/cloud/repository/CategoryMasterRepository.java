package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.CategoryMaster;

public interface CategoryMasterRepository extends JpaRepository<CategoryMaster, Integer> {

}
