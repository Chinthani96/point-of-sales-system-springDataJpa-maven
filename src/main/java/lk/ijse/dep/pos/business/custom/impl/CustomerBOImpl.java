package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.repository.custom.CustomerRepository;
import lk.ijse.dep.pos.entity.Customer;
import lk.ijse.dep.pos.util.CustomerTM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class CustomerBOImpl implements CustomerBO {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerTM> getAllCustomers() throws Exception {
        List<Customer> allCustomers = null;
        List<CustomerTM> customerTMS = new ArrayList<>();
            allCustomers = customerRepository.findAll();
        for (Customer customer : allCustomers) {
            customerTMS.add(new CustomerTM(customer.getId(), customer.getName(), customer.getAddress()));
        }
        return customerTMS;
    }

    public void saveCustomer(String id, String name, String address) throws Exception {
            customerRepository.save(new Customer(id, name, address));

    }

    public void updateCustomer(String id, String name, String address) throws Exception {
            customerRepository.save(new Customer(id, name, address));
    }

    public void deleteCustomer(String id) throws Exception {
            customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String generateNewCustomerId() throws SQLException {
            String lastCustomerId = customerRepository.getFirstLastCustomerIdByOrderByIdDesc().getId();
            int lastNumber = Integer.parseInt(lastCustomerId.substring(1, 4));
            if (lastNumber == 0) {
                return "C001";
            } else if (lastNumber < 9) {
                lastNumber++;
                return "C00" + lastNumber;
            } else if (lastNumber < 99) {
                lastNumber++;
                return "C0" + lastNumber;
            } else {
                lastNumber++;
                return "C" + lastNumber;
            }
    }
}
