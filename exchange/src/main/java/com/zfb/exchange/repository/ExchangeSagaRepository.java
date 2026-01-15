package com.zfb.exchange.repository;

import com.zfb.exchange.domain.ExchangeSaga;
import com.zfb.exchange.domain.ExchangeSaga.SagaStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeSagaRepository extends JpaRepository<ExchangeSaga, Long> {

  Optional<ExchangeSaga> findBySagaId(String sagaId);

  Page<ExchangeSaga> findByStatus(SagaStatus status, Pageable pageable);

  @Query(
      "SELECT es FROM ExchangeSaga es "
          + "WHERE es.status IN :statuses "
          + "AND es.createdAt < :threshold "
          + "ORDER BY es.createdAt ASC")
  List<ExchangeSaga> findStaleSagas(
      @Param("statuses") List<SagaStatus> statuses, @Param("threshold") LocalDateTime threshold);
}
