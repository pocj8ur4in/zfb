package com.zfb.current.repository;

import com.zfb.current.domain.CurrentAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {

  Optional<CurrentAccount> findByUuid(String uuid);
}
