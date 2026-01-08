package com.zfb.customer.domain;

import com.zfb.domain.BaseColumn;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "customers",
    indexes = {
      @Index(name = "idx_customer_email", columnList = "email", unique = true),
      @Index(name = "idx_customer_username", columnList = "username", unique = true),
      @Index(name = "idx_customer_status", columnList = "status")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseColumn {

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 20)
  private String phoneNumber;

  @Column(length = 10)
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CustomerStatus status;

  @Builder
  public Customer(
      String email,
      String username,
      String password,
      String name,
      String phoneNumber,
      LocalDate dateOfBirth) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.dateOfBirth = dateOfBirth;
    this.status = CustomerStatus.ACTIVE;
  }

  /**
   * update password
   * @param newPassword the new password
   */
  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }

  /**
   * update profile
   * @param name the new name
   * @param phoneNumber the new phone number
   */
  public void updateProfile(String name, String phoneNumber) {
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  /**
   * deactivate customer
   */
  public void deactivate() {
    this.status = CustomerStatus.INACTIVE;
  }

  /**
   * suspend customer
   */
  public void suspend() {
    this.status = CustomerStatus.SUSPENDED;
  }

  /**
   * activate customer
   */
  public void activate() {
    this.status = CustomerStatus.ACTIVE;
  }
}
