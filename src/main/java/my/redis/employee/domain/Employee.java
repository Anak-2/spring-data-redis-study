package my.redis.employee.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
// No @Entity concept here
public class Employee implements Serializable {

    private static final long serialVersionUID = -7817224776021728682L;

    private Integer empId;
    private String empName;
    private Double empSalary;
}
