package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.AssessmentUserLab;
import in.canaris.cloud.openstack.entity.UserLab;

public interface AssessmentUserLabRepository extends JpaRepository<AssessmentUserLab, Integer> {

	  // Find max lab_id
    @Query("SELECT MAX(a.labId) FROM AssessmentUserLab a")
    Integer findMaxLabId();

    // Find by guacamoleId (since there's no proxmoxId in AssessmentUserLab)
    List<AssessmentUserLab> findByGuacamoleId(Integer guacamoleId);

    // Find by scenarioId and username
    List<AssessmentUserLab> findByScenarioIdAndUsername(Integer scenarioId, String username);

    // Find by username
    List<AssessmentUserLab> findByUsername(String username);

    // Custom query for getByProxmoxId - since there's no proxmoxId field, 
    // I assume you want to use guacamoleId instead
    @Query("SELECT a FROM AssessmentUserLab a WHERE a.guacamoleId = ?1")
    AssessmentUserLab getByProxmoxId(Integer guacamoleId);

    // Update mandatory command status
    @Query("UPDATE AssessmentUserLab a SET a.mandatoryCommandExecuted = ?2 WHERE a.guacamoleId = ?1")
    void updateMandatoryCommandStatus(Integer guacamoleId, Boolean status);

    // Update status by labId
    @Query("UPDATE AssessmentUserLab a SET a.Status = 'Completed' WHERE a.labId = ?1")
    int updateStatusById(Long labId);

	List<AssessmentUserLab> findByguacamoleId(int temp);

	
	

}
