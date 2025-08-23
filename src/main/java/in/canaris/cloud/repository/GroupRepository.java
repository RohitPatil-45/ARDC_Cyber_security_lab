package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Group;


@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, Integer> {
	
	@Query("SELECT DISTINCT g.id,g.group_name FROM Group g")
	List<Object[]> getAllGroups();
	
	@Query("SELECT g FROM Group g WHERE group_name=:groupName")
	List<Group> findByGroupName(String groupName);
	
	@Query("SELECT DISTINCT g.id,g.group_name FROM Group g WHERE group_name=:groupName")
	List<Object[]> getByGroups(String groupName);

}
