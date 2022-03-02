package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.model.CreateEmployee;
import com.example.rqchallenge.employees.model.CreateEmployeeResponse;
import com.example.rqchallenge.employees.model.DeleteEmployeeByIdResponse;
import com.example.rqchallenge.employees.model.DeleteEmployeeByNameResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.GetEmployeeResponse;
import com.example.rqchallenge.employees.model.GetEmployeesResponse;
import com.example.rqchallenge.employees.model.GetHighestSalaryOfEmployeeResponse;
import com.example.rqchallenge.employees.model.GetTopTenHighestEarningEmployeeNamesResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Named
@Slf4j
public class EmployeeService {
    WebClient webClient;

    @Autowired
    public EmployeeService(EmployeeServiceConfig employeeServiceConfig) {
        webClient = employeeServiceConfig.buildWebClient();
    }

    public GetEmployeesResponse getEmployees() throws NullPointerException {
        GetEmployeesResponse response = makeSynchronousGetRequest("/employees", GetEmployeesResponse.class);
        log.debug("Got /employees response from dummy rest api {}", response);
        return response;
    }

    public GetEmployeeResponse getEmployeeById(String id) {
        GetEmployeeResponse response = makeSynchronousGetRequest("/employees/" + id, GetEmployeeResponse.class);
        log.debug("Got /employees/{} response from dummy rest api {}", id, response);
        return response;
    }

    public CreateEmployeeResponse createEmployee(CreateEmployee createEmployee) {
        CreateEmployeeResponse response = makeSynchronousPostRequest("/create", createEmployee, CreateEmployeeResponse.class);
        log.debug("Got /create response for employee {} from dummy rest api {}", createEmployee, response);
        return response;
    }

    public GetHighestSalaryOfEmployeeResponse getHighestSalaryOfEmployees() {
        GetEmployeesResponse getEmployeesResponse = getEmployees();
        Employee highestSalaryEmployee = Collections.max(getEmployeesResponse.getData());
        return GetHighestSalaryOfEmployeeResponse.builder()
            .highestSalary(highestSalaryEmployee.getEmployeeSalary())
            .status("success")
            .build();
    }

    public GetTopTenHighestEarningEmployeeNamesResponse getTopTenHighestEarningEmployeeNames() {
        GetEmployeesResponse getEmployeesResponse = getEmployees();
        Collections.sort(getEmployeesResponse.getData(), Collections.reverseOrder());
        Collections.reverse(getEmployeesResponse.getData());
        List<String> employeeNames = getEmployeesResponse
            .getData()
            .subList(0,10)
            .stream()
            .map(Employee::getEmployeeName)
            .collect(Collectors.toList());
        return GetTopTenHighestEarningEmployeeNamesResponse.builder()
            .data(employeeNames)
            .status("success")
            .build();
    }

    public DeleteEmployeeByNameResponse deleteEmployeeByName(String name) {
        GetEmployeesResponse getEmployeesResponse = getEmployees();
        List<Employee> employeesWithName = getEmployeesResponse.getData()
            .stream()
            .filter(e -> e.getEmployeeName().equals("name"))
            .collect(Collectors.toList());
        int deletedCount = 0;
        for (Employee employee : employeesWithName) {
            try {
                DeleteEmployeeByIdResponse response = makeSynchronousDeleteRequest(
                    "/delete" + employee.getId(), DeleteEmployeeByIdResponse.class);
                if (response.getStatus().equals("success")) {
                    deletedCount += 1;
                }
            }
            catch (Exception e) {
                log.error(String.format("Couldn't delete user % with name %", employee.getId(), employee.getEmployeeName()), e);
            }
        }
        DeleteEmployeeByNameResponse deleteEmployeeByNameResponse = DeleteEmployeeByNameResponse.builder()
            .deletedCount(deletedCount)
            .build();
        if (deletedCount != 0) {
            deleteEmployeeByNameResponse.setStatus("failure");
        }
        return deleteEmployeeByNameResponse;
    }

    // nifty way to wrap common code for making requests
    // https://stackoverflow.com/a/450874
    private <T> T makeSynchronousGetRequest(String url, Class<T> clazz) {
        Object response = webClient.get()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(clazz)
            .block();
        return clazz.cast(response);
    }

    private <T> T makeSynchronousPostRequest(String url, Object body, Class<T> clazz) {
        Object response = webClient.post()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(body), clazz)
            .retrieve()
            .bodyToMono(clazz)
            .block();
        return clazz.cast(response);
    }

    private <T> T makeSynchronousDeleteRequest(String url, Class<T> clazz) {
        Object response = webClient.delete()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(clazz)
            .block();
        return clazz.cast(response);
    }
}
