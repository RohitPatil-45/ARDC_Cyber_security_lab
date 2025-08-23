package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AdditionalStorage;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Discount;

@Repository
@Transactional
public interface AdditionalStorageRepository extends JpaRepository<AdditionalStorage, Integer> {
	
	@Query("SELECT es FROM AdditionalStorage es WHERE es.status='Approved' AND es.instance_id=:id")
	List<AdditionalStorage> findByinstanceId(CloudInstance id);
	
	@Query("SELECT es FROM AdditionalStorage es WHERE es.instance_id=:id and es.status='Pending'")
	AdditionalStorage findByinstId(CloudInstance id);

}
