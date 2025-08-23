package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.ADConfig;
import in.canaris.cloud.entity.Discount;

@Repository
@Transactional
public interface AdConfigRepository extends JpaRepository<ADConfig, Integer> {
	
	List<ADConfig> findByusername(String username);

}
