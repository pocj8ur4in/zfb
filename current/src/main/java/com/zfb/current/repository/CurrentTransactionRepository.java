package com.zfb.current.repository;

import com.zfb.current.domain.CurrentAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentTransactionRepository
    extends JpaRepository<CurrentAccountTransaction, Long> {}
