package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;

public interface ChartBoatInstructionTemplateRepository extends JpaRepository<ChartBoatInstructionTemplate, Integer> {

	List<ChartBoatInstructionTemplate> findBytemplateId(int tempId);

	

	

}
