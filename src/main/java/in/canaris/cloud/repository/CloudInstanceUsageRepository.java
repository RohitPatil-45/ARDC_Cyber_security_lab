package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface CloudInstanceUsageRepository extends JpaRepository<CloudInstanceUsage, Integer> {
	
	

}
