package my.redis.employee.repository;

import my.redis.employee.domain.Employee;

import java.util.Map;

public interface EmployeeRepository {

    void saveEmployee(Employee emp);
    Employee getOneEmployee(Integer id);
    void updateEmployee(Employee emp);
    Map<Integer, Employee> getAllEmployees();
    void deleteEmployee(Integer id);
    void saveAllEmployees(Map<Integer, Employee> map);
}
