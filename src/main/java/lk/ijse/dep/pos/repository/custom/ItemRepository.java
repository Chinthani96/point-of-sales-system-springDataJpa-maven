package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.Item;

import java.sql.SQLException;

public interface ItemRepository extends CrudDAO<Item,String> {
    String getLastItemId() throws SQLException;
}
