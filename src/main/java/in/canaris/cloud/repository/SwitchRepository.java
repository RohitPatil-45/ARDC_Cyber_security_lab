package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.Switch;

@Repository
@Transactional
public interface SwitchRepository extends JpaRepository<Switch, Integer> {

	@Query("SELECT DISTINCT s.id,s.switch_name FROM Switch s")
	List<Object[]> getAllSwitch();

	@Query("SELECT g FROM Switch g WHERE switch_name=:switch_name")
	List<Switch> findBySwitchName(String switch_name);
	
	@Query("SELECT s.switch_name, s.id FROM Switch s WHERE s.physicalServerIP=:serverIP")
	List<Object[]> findByphysicalServerIP(String serverIP);

}
