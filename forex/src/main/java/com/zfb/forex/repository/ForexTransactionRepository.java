package com.zfb.forex.repository;

import com.zfb.forex.domain.ForexTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexTransactionRepository extends JpaRepository<ForexTransaction, Long> {

  Optional<ForexTransaction> findByUuid(String uuid);

  Optional<ForexTransaction> findByClientRequestId(String clientRequestId);
}
