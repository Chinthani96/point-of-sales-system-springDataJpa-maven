package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.Customer;

import java.sql.SQLException;

public interface CustomerRepository extends CrudDAO<Customer,String> {
    String getLastCustomerId() throws SQLException;
}
