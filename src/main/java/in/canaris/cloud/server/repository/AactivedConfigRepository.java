package in.canaris.cloud.server.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.ADConfig;
import in.canaris.cloud.server.entity.AactiveDConfig;


@Repository
@Transactional
public interface AactivedConfigRepository extends JpaRepository<AactiveDConfig, Integer> {
	
	List<AactiveDConfig> findByusername(String username);

}
