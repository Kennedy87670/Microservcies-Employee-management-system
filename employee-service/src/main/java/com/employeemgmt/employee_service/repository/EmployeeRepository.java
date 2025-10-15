package com.employeemgmt.employeeservice.repository;

import com.employeemgmt.employeeservice.entity.Employee;
import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByStatus(EmployeeStatus status);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.department.id = ?1 AND e.status = ?2")
    List<Employee> findByDepartmentIdAndStatus(Long departmentId, EmployeeStatus status);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Employee> searchByName(String name);
}