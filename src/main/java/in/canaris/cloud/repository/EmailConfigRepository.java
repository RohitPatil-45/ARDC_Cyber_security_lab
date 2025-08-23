package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.EmailConfig;

@Repository
@Transactional
public interface EmailConfigRepository extends JpaRepository<EmailConfig, Integer> {
	
	
	@Query("SELECT ec FROM EmailConfig ec WHERE ec.email_id=:email")
	List<EmailConfig> findByEmail(String email);

}
