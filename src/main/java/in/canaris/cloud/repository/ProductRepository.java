package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.Product;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query("SELECT DISTINCT p.id,p.product_name FROM Product p")
	List<Object[]> getAllProducts();
	
	@Query("SELECT p.id FROM Product p  WHERE p.product_name=:product_name")
	int getProductIDByName(String product_name);


}
