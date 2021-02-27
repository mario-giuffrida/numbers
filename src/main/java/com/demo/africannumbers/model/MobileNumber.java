package com.demo.africannumbers.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "numbers")
public class MobileNumber {
  @Id
  @GeneratedValue
  private Long id;

  @NonNull
  private String importedId;

  @NonNull
  @NotBlank
  private String number;

  @ElementCollection
  private List<String> corrections = new ArrayList<>();

  @JsonIgnore
  private MobileNumberState state = MobileNumberState.INITIAL;

  public MobileNumber(@NonNull @NotBlank String number) {
    this.number = number;
  }
}

