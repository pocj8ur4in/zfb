package com.zfb.forex.repository;

import com.zfb.forex.domain.ForexTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexTransactionRepository extends JpaRepository<ForexTransaction, Long> {}
