package my.redis.employee;

import my.redis.employee.domain.Employee;
import my.redis.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisOperationsRunner implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {

        //saving one employee
        employeeRepository.saveEmployee(new Employee(500, "Emp0", 2150.0));

        //saving multiple employees
        employeeRepository.saveAllEmployees(
                Map.of( 501, new Employee(501, "Emp1", 2396.0),
                        502, new Employee(502, "Emp2", 2499.5),
                        503, new Employee(503, "Emp4", 2324.75)
                )
        );

        //modifying employee with empId 503
        employeeRepository.updateEmployee(new Employee(503, "Emp3", 2325.25));

        //deleting employee with empID 500
        employeeRepository.deleteEmployee(500);

        //retrieving all employees
        employeeRepository.getAllEmployees().forEach((k,v)-> System.out.println(k +" : "+v));

        //retrieving employee with empID 501
        System.out.println("Emp details for 501 : "+employeeRepository.getOneEmployee(501));
    }
}