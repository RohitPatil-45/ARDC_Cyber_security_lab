package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.UserScenarioMapping;

public interface UserScenarioMappingRepository extends JpaRepository<UserScenarioMapping, Integer> {

}
