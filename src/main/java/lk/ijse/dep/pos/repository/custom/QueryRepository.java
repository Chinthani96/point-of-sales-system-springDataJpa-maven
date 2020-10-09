package lk.ijse.dep.pos.repository.custom;

import lk.ijse.dep.pos.entity.CustomEntity;

import java.io.Serializable;
import java.util.List;

public interface QueryRepository extends Serializable {
    public List<CustomEntity> getOrderDetails() throws Exception;
}
