package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import in.canaris.cloud.entity.Product;
import in.canaris.cloud.entity.SubProduct;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface SubProductRepository extends JpaRepository<SubProduct, Integer> {
	
	@Query("SELECT  s.id,s.sub_product_name FROM SubProduct s WHERE s.product_id=:id")
	List<Object[]> getAllSubProductByProduct(Product id);
	
	@Query("SELECT iso_file_path FROM SubProduct s WHERE s.id=:id")
	String getISOFilePath(int id);
	
	@Query("SELECT variant FROM SubProduct s WHERE s.id=:id")
	String getVARIANT(int id);
	
	@Modifying
	@Transactional
	@Query("UPDATE SubProduct c SET c.isSourceCreated = 'created' WHERE c.id = :labId")
	int updateSourceImage(int labId);

}
