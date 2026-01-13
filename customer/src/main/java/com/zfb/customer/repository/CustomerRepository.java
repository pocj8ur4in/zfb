package com.zfb.customer.repository;

import com.zfb.customer.domain.Customer;
import com.zfb.customer.domain.CustomerStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByEmailAndStatus(String email, CustomerStatus status);

  Optional<Customer> findByUuid(String uuid);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
