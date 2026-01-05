package com.zfb.current.repository;

import com.zfb.current.domain.CurrentAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {

  Optional<CurrentAccount> findByUuid(String uuid);

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM CurrentAccount a WHERE a.uuid = :uuid")
  Optional<CurrentAccount> findByUuidWithLock(@Param("uuid") String uuid);

  List<CurrentAccount> findByUserUuid(String userUuid);
}
