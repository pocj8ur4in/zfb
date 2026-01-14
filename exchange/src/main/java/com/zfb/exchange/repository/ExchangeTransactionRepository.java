package com.zfb.exchange.repository;

import com.zfb.exchange.domain.ExchangeTransaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeTransactionRepository extends JpaRepository<ExchangeTransaction, Long> {

  Optional<ExchangeTransaction> findByClientRequestId(String clientRequestId);

  Page<ExchangeTransaction> findByAccountUuid(String accountUuid, Pageable pageable);
}
