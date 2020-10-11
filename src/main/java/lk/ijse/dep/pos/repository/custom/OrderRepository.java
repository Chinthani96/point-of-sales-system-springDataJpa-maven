package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.SQLException;

public interface OrderRepository extends JpaRepository<Order,String> {
    Order getFirstLastOrderIdByOrderByIdDesc() throws SQLException;
}
