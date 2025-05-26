package com.binhbkfx02295.cshelpdesk.employee_management.employee.repository;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.StatusLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUsername(String username);
    void deleteByUsername(String username);

    List<Employee> findByUserGroup_GroupId(int groupId);
    @Query("""
    SELECT e FROM Employee e
    JOIN FETCH e.userGroup ug
    JOIN FETCH ug.permissions
    WHERE e.username = :username
""")
    Optional<Employee> findWithUserGroupAndPermissionsByUsername(@Param("username") String username);

    @Query("""
    SELECT e FROM Employee e
    JOIN FETCH e.statusLogs l
    JOIN FETCH l.status
    WHERE l.timestamp = (
        SELECT MAX(l2.timestamp)
        FROM StatusLog l2
        WHERE l2.employee = e
    )
""")
    @EntityGraph(attributePaths = "userGroup")
    List<Employee> findAllWithTop1EmployeeStatusLog();

    @Query("""
    SELECT e FROM Employee e
    LEFT JOIN FETCH e.statusLogs
    WHERE e.username = :username
""")
    Optional<Employee> findWithAllStatusLog(@Param("username") String username);

    boolean existsByUsername(String username);

    @Query("""
            SELECT e
            FROM Employee e
            LEFT JOIN FETCH e.statusLogs l
            LEFT JOIN FETCH l.status s
            WHERE l.timestamp = (
                SELECT MAX(l2.timestamp)
                FROM StatusLog l2
                WHERE l2.employee = e
            ) AND e.username = :username
            """)
    Optional<Employee> findWithTop1StatusLog(@Param("username") String username);
}
