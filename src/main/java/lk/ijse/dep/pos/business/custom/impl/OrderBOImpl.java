package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.OrderBO;
import lk.ijse.dep.pos.repository.custom.ItemRepository;
import lk.ijse.dep.pos.repository.custom.OrderRepository;
import lk.ijse.dep.pos.repository.custom.OrderDetailRepository;
import lk.ijse.dep.pos.repository.custom.QueryRepository;
import lk.ijse.dep.pos.entity.*;
import lk.ijse.dep.pos.util.CustomerTM;
import lk.ijse.dep.pos.util.SearchOrderTM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class OrderBOImpl implements OrderBO {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailDAO;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private QueryRepository queryDAO;

    public void saveOrder(String id, Date date, CustomerTM customer) throws Exception {
        orderRepository.save(new Order(id, date, new Customer(customer.getCustomerId(), customer.getCustomerName(), customer.getCustomerAddress())));
    }

    public void saveOrderDetail(String orderId, String itemCode, int qty, double unitPrice) throws Exception {
        orderDetailDAO.save(new OrderDetail(orderId, itemCode, qty, BigDecimal.valueOf(unitPrice)));
        Item item = itemRepository.find(itemCode);
        item.setQtyOnHand(item.getQtyOnHand() - qty);
    }

    public String generateNewOrderId() throws SQLException {
        String lastOrderId = orderRepository.lastOrderId();

        int lastNumber = Integer.parseInt(lastOrderId.substring(2, 5));
        if (lastNumber <= 0) {
            lastNumber++;
            return "OD001";
        } else if (lastNumber < 9) {
            lastNumber++;
            return "OD00" + lastNumber;
        } else if (lastNumber < 99) {
            lastNumber++;
            return "OD0" + lastNumber;
        } else {
            lastNumber++;
            return "OD" + lastNumber;
        }
    }

    @Transactional(readOnly = true)
    public List<SearchOrderTM> getOrderDetails() throws Exception {
        List<CustomEntity> orderDetails = null;
        List<SearchOrderTM> searchOrderTMS = new ArrayList<>();

        orderDetails = queryDAO.getOrderDetails();
        for (CustomEntity orderDetail : orderDetails) {
            searchOrderTMS.add(new SearchOrderTM(orderDetail.getOrderId(), orderDetail.getOrderDate().toString(), orderDetail.getCustomerId(), orderDetail.getCustomerName(), orderDetail.getTotal().doubleValue()));
            return searchOrderTMS;
        }
        return null;
    }
}
