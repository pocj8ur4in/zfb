package com.zfb.forex.repository;

import com.zfb.forex.domain.ForexAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexAccountRepository extends JpaRepository<ForexAccount, Long> {

  Optional<ForexAccount> findByUuid(String uuid);

  List<ForexAccount> findByUserUuid(String userUuid);
}
