package my.redis.employee.repository;

import jakarta.annotation.Resource;
import my.redis.employee.domain.Employee;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository{

    private final String hashReference = "Employee";

    @Resource(name = "redisTemplate")
    private HashOperations<String, Integer, Employee> hashOperations;

    @Override
    public void saveEmployee(Employee emp) {
        hashOperations.putIfAbsent(hashReference, emp.getEmpId(), emp);
    }

    @Override
    public void saveAllEmployees(Map<Integer, Employee> map) {
        hashOperations.putAll(hashReference, map);
    }

    @Override
    public Employee getOneEmployee(Integer id) {
        return hashOperations.get(hashReference, id);
    }

    @Override
    public void updateEmployee(Employee emp) {
        hashOperations.put(hashReference, emp.getEmpId(), emp);
    }

    @Override
    public Map<Integer, Employee> getAllEmployees() {
        return hashOperations.entries(hashReference);
    }

    @Override
    public void deleteEmployee(Integer id) {
        hashOperations.delete(hashReference, id);
    }
}
