package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.Order;

import java.sql.SQLException;

public interface OrderRepository extends CrudDAO<Order,String> {
    String lastOrderId() throws SQLException;
}
