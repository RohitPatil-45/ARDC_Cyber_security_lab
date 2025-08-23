package in.canaris.cloud.repository;


import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Discount;

@Repository
@Transactional
public interface DiscountRepository extends JpaRepository<Discount, Integer> {

	@Query("SELECT id,discount_percentage FROM Discount d WHERE d.discount_type=:discountType")
	String getDiscount(String discountType);

}
