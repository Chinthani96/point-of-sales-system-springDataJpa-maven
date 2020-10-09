package lk.ijse.dep.pos.controller;

import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.business.custom.ItemBO;
import lk.ijse.dep.pos.business.custom.OrderBO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep.pos.util.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class OrderDetailsFormController {
    public AnchorPane root;
    public TextField txtOrderID;
    public TextField txtQtyOnHand;
    public TextField txtOrderDate;
    public TextField txtCustomerName;
    public TextField txtQuantity;
    public TextField txtUnitPrice;
    public TextField txtDescription;
    public ComboBox<CustomerTM> cmbCustomerId;
    public ComboBox<ItemTM> cmbItemID;
    public Button btnDelete;
    public TableView<OrderDetailsTM> tblOrderDetails;
    public Label lblTotal;
    public Button btnPlaceOrder;
    public Button btnBack;
    public Button btnAdd;
    public Button btnAddOrder;
    private OrderBO orderBO = AppInitializer.getApplicationContext().getBean(OrderBO.class);
    private ItemBO itemBO= AppInitializer.getApplicationContext().getBean(ItemBO.class);
    private CustomerBO customerBO= AppInitializer.getApplicationContext().getBean(CustomerBO.class);

    public void initialize() {

        loadAllCustomers();
        loadAllItems();

        //Basic Initializations
        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnAdd.requestFocus();

        //map columns
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemId"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        //when a customer id is selected
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM selectedCustomer) {
                if(selectedCustomer==null){
                    return;
                }
                txtCustomerName.setText(selectedCustomer.getCustomerName());
            }
        });

        //when an item code is selected
        cmbItemID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM selectedItem) {
                if(selectedItem==null){
                    cmbItemID.getSelectionModel().clearSelection();
                    txtDescription.clear();
                    txtUnitPrice.clear();
                    txtQtyOnHand.clear();
                    return;
                }

                btnAdd.setDisable(false);
                txtDescription.setText(selectedItem.getDescription());
                txtQtyOnHand.setText(String.valueOf(selectedItem.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
            }
        });
        //When a row in the table is selected
        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailsTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailsTM> observable, OrderDetailsTM oldValue, OrderDetailsTM selectedOrderDetails) {
                btnDelete.setDisable(false);
                btnAdd.setText("Update");

                if(selectedOrderDetails==null){
                    return;
                }

                String itemId = selectedOrderDetails.getItemId();
                ObservableList<ItemTM> items = cmbItemID.getItems();

                for(ItemTM item:items){
                    if(itemId==item.getItemId()){
                        txtDescription.setText(item.getDescription());
                        txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
//                        txtQuantity.setText();
                    }
                }

                txtDescription.setText(selectedOrderDetails.getDescription());

            }
        });
    }
    public void btnAddOrder_OnAction(ActionEvent actionEvent) {
        generateId();
        LocalDate today = LocalDate.now();
        txtOrderDate.setText(today.toString());
        btnAdd.setDisable(true);
    }

    @SuppressWarnings("Duplicates")
    public void btnDelete_OnAction(ActionEvent actionEvent) {
        ObservableList<OrderDetailsTM> orderDetails = tblOrderDetails.getItems();
        OrderDetailsTM selectedDetail = tblOrderDetails.getSelectionModel().getSelectedItem();
        orderDetails.remove(selectedDetail);
        btnDelete.setDisable(true);
        btnAdd.setDisable(true);
        txtQuantity.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        cmbItemID.getSelectionModel().clearSelection();
    }

    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {
        //add to orders table
        String orderId = txtOrderID.getText();
        String orderDate = txtOrderDate.getText();
        CustomerTM selectedCustomer= cmbCustomerId.getSelectionModel().getSelectedItem();
//        String unitPrice = txtUnitPrice.getText();
        String customerId = selectedCustomer.getCustomerId();
        addToOrders(orderId,orderDate,new CustomerTM(selectedCustomer.getCustomerId(),selectedCustomer.getCustomerName(),selectedCustomer.getCustomerAddress()));

        ObservableList<OrderDetailsTM> items = tblOrderDetails.getItems();
        for(OrderDetailsTM item:items) {
            String itemCode = item.getItemId();
            System.out.println(itemCode);
            int qty = item.getQty();
            System.out.println(qty);
            addToOrderDetail(orderId,itemCode,qty,item.getUnitPrice());
        }
        calculateOrderTotal();
        loadAllItems();
    }

    private void generateId() {
        String newOrderId = null;
        try {
            newOrderId = orderBO.generateNewOrderId();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        txtOrderID.setText(newOrderId);
    }
    public void btnAdd_OnAction(ActionEvent actionEvent) {
        ItemTM item = cmbItemID.getSelectionModel().getSelectedItem();

        //table columns
        String itemId = item.getItemId();
        String description = item.getDescription();
        double unitPrice  = item.getUnitPrice();
        int qty = Integer.parseInt(txtQuantity.getText());
        double total = unitPrice * qty;

        ObservableList<OrderDetailsTM> orderDetails = tblOrderDetails.getItems();
        orderDetails.add(new OrderDetailsTM(itemId,description,qty,unitPrice,total));
        cmbItemID.getSelectionModel().clearSelection();
        txtQuantity.clear();

    }

    public void txtQuantity_OnAction(ActionEvent actionEvent) {
        btnAdd_OnAction(actionEvent);
    }

    @SuppressWarnings("Duplicates")
    private void addToOrders(String orderId, String date, CustomerTM customer){
        try {
            orderBO.saveOrder(orderId, Date.valueOf(date),customer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    private void addToOrderDetail(String orderId,String itemCode,int orderQty,double unitPrice){
        try {
            orderBO.saveOrderDetail(orderId,itemCode,orderQty,unitPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void loadAllCustomers(){
        List<CustomerTM> allCustomers = null;
        try {
            allCustomers = customerBO.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmbCustomerId.getItems().clear();
        ObservableList<CustomerTM> customerTMS = FXCollections.observableArrayList(allCustomers);
        cmbCustomerId.setItems(customerTMS);
    }
    private void loadAllItems(){
        List<ItemTM> allItems = null;
        try {
            allItems = itemBO.getAllItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmbItemID.getItems().clear();
        ObservableList<ItemTM> itemTMS = FXCollections.observableArrayList(allItems);
        cmbItemID.setItems(itemTMS);
    }
    private void calculateOrderTotal(){
        ObservableList<OrderDetailsTM> orderDetails = tblOrderDetails.getItems();
        double netTotal = 0;

        for(OrderDetailsTM order : orderDetails){
            netTotal+=order.getTotal();
        }
        lblTotal.setText(String.valueOf(netTotal));
    }

    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        @SuppressWarnings("Duplicates")
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage) this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }
}

