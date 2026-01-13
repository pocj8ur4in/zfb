package com.zfb.forex.repository;

import com.zfb.forex.domain.ForexAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForexAccountRepository extends JpaRepository<ForexAccount, Long> {

  Optional<ForexAccount> findByUuid(String uuid);

  List<ForexAccount> findByUserUuid(String userUuid);

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM ForexAccount a WHERE a.uuid = :uuid")
  Optional<ForexAccount> findByUuidForUpdate(@Param("uuid") String uuid);
}
