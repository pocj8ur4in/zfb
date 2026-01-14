package com.zfb.exchange.repository;

import com.zfb.exchange.domain.ExchangeSaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeSagaRepository extends JpaRepository<ExchangeSaga, Long> {}
