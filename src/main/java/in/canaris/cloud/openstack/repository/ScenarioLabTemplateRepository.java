package in.canaris.cloud.openstack.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.ScenarioLabTemplate;



@Repository
@Transactional
public interface ScenarioLabTemplateRepository extends JpaRepository<ScenarioLabTemplate, Integer> {

	List<ScenarioLabTemplate> findByScenarioId(int scenarioId);

}
