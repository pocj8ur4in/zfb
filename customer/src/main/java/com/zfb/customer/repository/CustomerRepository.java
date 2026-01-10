package com.zfb.customer.repository;

import com.zfb.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
