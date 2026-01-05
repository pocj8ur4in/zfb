package com.zfb.current.repository;

import com.zfb.current.domain.CurrentAccountTransaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentTransactionRepository
    extends JpaRepository<CurrentAccountTransaction, Long> {

  Optional<CurrentAccountTransaction> findByUuid(String uuid);

  Page<CurrentAccountTransaction> findByAccountUuidOrderByCreatedAtDesc(
      String accountUuid, Pageable pageable);
}
