package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Customer;


@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	@Query("SELECT  c.id,c.customer_name FROM Customer c")
	List<Object[]> getCustomerName();
	
	@Query("SELECT  c FROM Customer c WHERE c.customer_name=:customer")
	List<Customer> findByCustomerName(String customer);
	
	@Query("SELECT  c FROM Customer c WHERE c.email_id=:email")
	List<Customer> findByEmail(String email);

}
