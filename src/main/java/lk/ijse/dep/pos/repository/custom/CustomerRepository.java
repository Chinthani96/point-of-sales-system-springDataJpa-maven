package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.SQLException;

public interface CustomerRepository extends JpaRepository<Customer,String> {
    Customer getFirstLastCustomerIdByOrderByIdDesc() throws SQLException;
}
