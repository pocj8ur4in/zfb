package com.zfb.current.domain;

import com.zfb.domain.BaseColumn;
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
@Table(name = "current_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentAccount extends BaseColumn {

  @Column(nullable = false, unique = true, length = 20)
  private String accountNumber;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AccountStatus status;

  @Builder
  public CurrentAccount(
      String accountNumber, Long userId, BigDecimal balance, AccountStatus status) {
    this.accountNumber = accountNumber;
    this.userId = userId;
    this.balance = balance != null ? balance : BigDecimal.ZERO;
    this.status = status != null ? status : AccountStatus.ACTIVE;
  }

  /**
   * withdraw money from the account
   *
   * @param amount the amount to withdraw
   */
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

  /**
   * deposit money into the account
   *
   * @param amount the amount to deposit
   */
  public void deposit(BigDecimal amount) {
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("account is not active");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    this.balance = this.balance.add(amount);
  }

  /**
   * update account status
   *
   * @param status
   */
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
