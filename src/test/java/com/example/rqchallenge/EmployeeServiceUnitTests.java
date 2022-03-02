package com.example.rqchallenge;

import com.example.rqchallenge.employees.EmployeeService;
import com.example.rqchallenge.employees.model.CreateEmployee;
import com.example.rqchallenge.employees.model.CreateEmployeeResponse;
import com.example.rqchallenge.employees.model.DeleteEmployeeByIdResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.GetEmployeeResponse;
import com.example.rqchallenge.employees.model.GetEmployeesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeServiceUnitTests {
  @Autowired
  EmployeeService employeeService;

  @Autowired
  MockMvc mockMvc;

  private static Employee employee;
  private static final Logger log = LoggerFactory.getLogger(EmployeeServiceUnitTests.class);
  public static MockWebServer mockWebServer;
  private static ObjectMapper objectMapper;

  private static String id;
  private static String name;
  private static String age;
  private static String salary;

  // dyanmically load the base url to work with MockWebServers random port assignment
  // https://stackoverflow.com/a/64241361
  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry r) throws IOException {
    r.add("employee.service.baseUrl", () -> "http://localhost:" + mockWebServer.getPort());
  }

  @BeforeAll
  static void setup() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    objectMapper = new ObjectMapper();
    id = "test";
    name = "first last";
    age = "20";
    salary = "10000";
    employee = Employee.builder()
        .id(id)
        .employeeAge(age)
        .employeeName(name)
        .employeeSalary(salary)
        .build();
  }

  @Test
  public void contextLoads() {
    assertThat(employeeService).isNotNull();
  }

  @Test
  public void testGetEmployees() throws Exception {
    List<Employee> employeeList = new ArrayList<>();
    employeeList.add(employee);
    GetEmployeesResponse response = GetEmployeesResponse.builder()
        .data(employeeList)
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(response);
    mockMvc.perform(get("/api/v1/employees"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].employee_age").value(age))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].employee_salary").value(salary))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].employee_name").value(name));
  }

  @Test
  public void testGetEmployee() throws Exception {
    GetEmployeeResponse response = GetEmployeeResponse.builder()
        .data(employee)
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(response);
    mockMvc.perform(get("/api/v1/employees/test"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.employee_age").value(age))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.employee_salary").value(salary))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.employee_name").value(name));
  }

  @Test
  public void testCreateEmployee() throws Exception {
    CreateEmployee createEmployee = CreateEmployee.builder()
        .name(name)
        .age(age)
        .salary(salary)
        .build();
    CreateEmployee createEmployeeWithId = (CreateEmployee) createEmployee.clone();
    createEmployeeWithId.setId("test");

    CreateEmployeeResponse response = CreateEmployeeResponse.builder()
        .status("success")
        .data(createEmployeeWithId)
        .build();

    enqueueMockResponseWithResponseObject(response);

    mockMvc.perform(
        post("/api/v1/create")
            .content(objectMapper.writeValueAsString(createEmployee))
            .contentType("application/json")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.age").value(age))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.salary").value(salary))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(name));
  }

  @Test
  public void testGetHighestSalaryOfEmployees() throws Exception {
    List<Employee> employeeList = new ArrayList<>();
    Employee employee1 = (Employee)employee.clone();
    employee1.setEmployeeSalary("1000");
    Employee employee2 = (Employee)employee.clone();
    employee2.setEmployeeSalary("1");
    Employee employee3 = (Employee)employee.clone();
    employee3.setEmployeeSalary("100000");

    employeeList.add(employee);
    employeeList.add(employee1);
    employeeList.add(employee2);
    employeeList.add(employee3);
    GetEmployeesResponse response = GetEmployeesResponse.builder()
        .data(employeeList)
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(response);
    mockMvc.perform(get("/api/v1/employees/highest-salary"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.highest_salary").value("100000"));
  }

  @Test
  public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
    List<Employee> employeeList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      Employee employeeX = (Employee)employee.clone();
      employeeX.setEmployeeSalary(String.valueOf(i * 10));
      employeeX.setEmployeeName("employee" + String.valueOf(i));
      employeeList.add(employeeX);
    }
    GetEmployeesResponse response = GetEmployeesResponse.builder()
        .data(employeeList)
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(response);
    mockMvc.perform(get("/api/v1/employees/top-ten-salaries"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0]").value("employee19"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[1]").value("employee18"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[2]").value("employee17"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[3]").value("employee16"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[4]").value("employee15"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[5]").value("employee14"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[6]").value("employee13"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[7]").value("employee12"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[8]").value("employee11"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[9]").value("employee10"));
  }

  @Test
  public void testDeleteEmployeeByName() throws Exception {
    List<Employee> employeeList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Employee employeeX = (Employee)employee.clone();
      employeeX.setEmployeeName("name");
      employeeList.add(employeeX);
    }
    for (int i = 0; i < 3; i++) {
      Employee employeeX = (Employee)employee.clone();
      employeeX.setEmployeeName("other_name");
      employeeList.add(employeeX);
    }
    GetEmployeesResponse response = GetEmployeesResponse.builder()
        .data(employeeList)
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(response);
    DeleteEmployeeByIdResponse deleteEmployeeByIdResponse = DeleteEmployeeByIdResponse.builder()
        .message("successfully! deleted Records")
        .status("success")
        .build();
    enqueueMockResponseWithResponseObject(deleteEmployeeByIdResponse);
    enqueueMockResponseWithResponseObject(deleteEmployeeByIdResponse);
    enqueueMockResponseWithResponseObject(deleteEmployeeByIdResponse);

    mockMvc.perform(delete("/api/v1/delete/name"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_count").value("3"));
  }

  private void enqueueMockResponseWithResponseObject(Object response) throws Exception {
    MockResponse mockResponse =  new MockResponse()
        .setBody(objectMapper.writeValueAsString(response))
        .addHeader("Content-Type", "application/json");
    mockWebServer.enqueue(mockResponse);
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

//  @AfterEach
//  static void cleanQueue() throws IOException {
//    mockWebServer.
//  }
}
