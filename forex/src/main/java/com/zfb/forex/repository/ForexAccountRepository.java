package com.zfb.forex.repository;

import com.zfb.forex.domain.ForexAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexAccountRepository extends JpaRepository<ForexAccount, Long> {}
