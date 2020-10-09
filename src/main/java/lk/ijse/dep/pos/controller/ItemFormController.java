package lk.ijse.dep.pos.controller;

import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.ItemBO;
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
import lk.ijse.dep.pos.util.ItemTM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ItemFormController {
    public TextField txtItemID;
    public TextField txtDescription;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TableView<ItemTM> tblItemDetails;
    public Button btnAddItem;
    public Button btnSave;
    public Button btnDelete;
    public Button btnBack;
    public AnchorPane root;

    private ItemBO itemBO = AppInitializer.getApplicationContext().getBean(ItemBO.class);

    public void initialize(){
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnDelete.requestFocus();

        //load all items
        loadAllItems();

        //mapping columns
        tblItemDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemId"));
        tblItemDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItemDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItemDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        tblItemDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM selectedItem) {
                if(selectedItem==null){
                    return;
                }
                else{
                    btnSave.setText("Update");
                    btnSave.setDisable(false);
                    btnDelete.setDisable(false);
                    btnAddItem.setDisable(true);

                    txtItemID.setText(selectedItem.getItemId()+"");
                    txtDescription.setText(selectedItem.getDescription()+"");
                    txtUnitPrice.setText(selectedItem.getUnitPrice()+"");
                    txtQtyOnHand.setText(selectedItem.getQtyOnHand()+"");
                }
            }
        });
    }

    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        ItemTM selectedItem = tblItemDetails.getSelectionModel().getSelectedItem();

        if(selectedItem==null){
            return;
        }
        try {
            itemBO.deleteItem(selectedItem.getItemId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadAllItems();

        btnSave.setText("Save");
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        txtDescription.clear();
        txtItemID.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();

    }

    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        //TODO validations
        String itemId = txtItemID.getText();
        String description = txtDescription.getText();
        double price = Double.parseDouble(txtUnitPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());

        if(btnSave.getText().equals("Update")){
            try {
                itemBO.updateItem(itemId,description,price,qtyOnHand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                itemBO.saveItem(itemId,description,price,qtyOnHand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnAddItem.setDisable(false);
        btnAddItem.requestFocus();
        txtItemID.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        loadAllItems();
    }
    public void btnAdd_OnAction(ActionEvent actionEvent) {
        btnAddItem.setDisable(true);
        txtDescription.requestFocus();
        btnSave.setDisable(false);

        generateId();
    }

    public void loadAllItems(){
        tblItemDetails.getItems().clear();
        List<ItemTM> allItems = null;
        try {
            allItems = itemBO.getAllItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<ItemTM> itemTMS = FXCollections.observableArrayList(allItems);
        tblItemDetails.setItems(itemTMS);
    }
    private void generateId(){
        String newItemId = null;
        try {
            newItemId = itemBO.generateNewItemId();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        txtItemID.setText(newItemId);
    }

}
