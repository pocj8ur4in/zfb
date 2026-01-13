package com.zfb.forex.domain;

import com.zfb.domain.BaseColumn;
import com.zfb.domain.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "forex_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForexAccount extends BaseColumn {

  @Column(nullable = false, unique = true, length = 20)
  private String accountNumber;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Currency currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AccountStatus status;

  @Builder
  public ForexAccount(
      String accountNumber,
      Long userId,
      BigDecimal balance,
      Currency currency,
      AccountStatus status) {
    this.accountNumber = accountNumber;
    this.userId = userId;
    this.balance = balance != null ? balance : BigDecimal.ZERO;
    this.currency = currency;
    this.status = status != null ? status : AccountStatus.ACTIVE;
  }

  public void withdraw(BigDecimal amount) {
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("account is not active");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    if (this.balance.compareTo(amount) < 0) {
      throw new IllegalStateException("insufficient balance");
    }
    this.balance = this.balance.subtract(amount);
  }

  public void deposit(BigDecimal amount) {
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("account is not active");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    this.balance = this.balance.add(amount);
  }

  public void updateStatus(AccountStatus status) {
    this.status = status;
  }

  public enum AccountStatus {
    ACTIVE,
    DORMANT,
    SUSPENDED,
    CLOSED
  }
}
