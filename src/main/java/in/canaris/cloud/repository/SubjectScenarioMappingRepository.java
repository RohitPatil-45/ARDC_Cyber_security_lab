package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SubjectScenarioMapping;

public interface SubjectScenarioMappingRepository extends JpaRepository<SubjectScenarioMapping, Integer> {

	@Query("SELECT DISTINCT s.scenarioId FROM SubjectScenarioMapping s WHERE s.subject IN :subjectIds")
	List<Integer> findScenarioIdsBySubjectIds(@Param("subjectIds") List<Integer> subjectIds);

	

	@Query("SELECT s.scenarioId FROM SubjectScenarioMapping s WHERE s.subject IN :subjectIds")
	List<Integer> findSubject(@Param("subjectIds") Integer subjectIds);

	

	

}
