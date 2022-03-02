package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.model.CreateEmployee;
import com.example.rqchallenge.employees.model.CreateEmployeeResponse;
import com.example.rqchallenge.employees.model.DeleteEmployeeByNameResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.GetEmployeeResponse;
import com.example.rqchallenge.employees.model.GetEmployeesResponse;
import com.example.rqchallenge.employees.model.GetTopTenHighestEarningEmployeeNamesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public ResponseEntity<GetEmployeesResponse> getAllEmployees() {
        log.info("Getting all employees");
        return ResponseEntity.ok(employeeService.getEmployees());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<GetEmployeeResponse> getEmployeeById(@PathVariable String id) {
        log.info("Getting employee with id {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@RequestBody CreateEmployee createEmployee) {
        log.info("Creating employee {}", createEmployee);
        return ResponseEntity.ok(employeeService.createEmployee(createEmployee));
    }

    @GetMapping("/employees/highest-salary")
    public ResponseEntity<Object> getHighestSalaryOfEmployees() {
        log.info("Getting employees with highest status");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @GetMapping("/employees/top-ten-salaries")
    public ResponseEntity<GetTopTenHighestEarningEmployeeNamesResponse> getTopTenHighestEarningEmployeeNames() {
        log.info("Getting employees with top ten salaries");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<DeleteEmployeeByNameResponse> deleteEmployeeByName(@PathVariable String name) {
        log.info("Deleting all employees with name {}", name);
        return ResponseEntity.ok(employeeService.deleteEmployeeByName(name));
    }
}
