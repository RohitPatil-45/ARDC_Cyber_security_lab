package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.projects;

@Repository
@Transactional
public interface projectRepository extends JpaRepository<projects, Integer> {



	@Query("SELECT i.projectId, i.projectName FROM projects i")
	List<Object[]> findByprojectName();

}
