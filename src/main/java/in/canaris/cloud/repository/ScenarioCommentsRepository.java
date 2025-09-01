package in.canaris.cloud.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.ScenarioComments;

public interface ScenarioCommentsRepository extends JpaRepository<ScenarioComments, Long> {

//	List<ScenarioComments> findByscenarioId(Long scenarioId);
	// You can add custom queries here later if needed

	List<ScenarioComments> findByScenarioId(Long scenarioId);

	@Modifying
	@Transactional
	@Query("UPDATE ScenarioComments c SET c.comment = :comment, c.createBy = :updatedBy, c.createdAt = :updatedAt WHERE c.id = :commentId")
	int updateCommentById(@Param("commentId") Long commentId, @Param("comment") String comment,
			@Param("updatedBy") String updatedBy, @Param("updatedAt") Timestamp updatedAt);

	@Modifying
	@Transactional
	@Query("DELETE FROM ScenarioComments c WHERE c.id = :commentId")
	int deleteByIdCustom(@Param("commentId") Long commentId);

}
