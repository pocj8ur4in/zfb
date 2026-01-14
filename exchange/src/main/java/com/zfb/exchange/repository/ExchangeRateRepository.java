package com.zfb.exchange.repository;

import com.zfb.domain.Currency;
import com.zfb.exchange.domain.ExchangeRate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

  @Query(
      "SELECT er FROM ExchangeRate er "
          + "WHERE er.sourceCurrency = :source "
          + "AND er.targetCurrency = :target "
          + "AND er.active = true "
          + "ORDER BY er.effectiveAt DESC "
          + "LIMIT 1")
  Optional<ExchangeRate> findLatestRate(
      @Param("source") Currency source, @Param("target") Currency target);
}
