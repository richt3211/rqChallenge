package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployee implements Cloneable {
  private String name;
  private String salary;
  private String age;
  private String id;

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
