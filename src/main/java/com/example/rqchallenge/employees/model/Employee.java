package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Employee implements Comparable<Employee>, Cloneable{
  @JsonProperty("id")
  private String id;
  @JsonProperty("employee_name")
  private String employeeName;
  @JsonProperty("employee_salary")
  private String employeeSalary;
  @JsonProperty("employee_age")
  private String employeeAge;
  @JsonProperty("profile_image")
  private String profileImage;

  @Override
  public int compareTo(Employee o) {
    if (Integer.parseInt(this.getEmployeeSalary()) > Integer.parseInt(o.getEmployeeSalary())) {
      return 1;
    } else if (Integer.parseInt(o.getEmployeeSalary()) < Integer.parseInt(this.getEmployeeSalary())) {
      return -1;
    }
    return 0;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
