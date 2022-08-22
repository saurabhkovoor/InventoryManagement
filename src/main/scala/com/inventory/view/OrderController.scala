package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import com.inventory.model.Order
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.beans.property.{StringProperty} 
import scalafx.Includes._
import scalafx.event.ActionEvent
import java.time.LocalDate;
@sfxml
class OrderController(
    private val orderTable : TableView[Order],
    private val orderIDColumn : TableColumn[Order, Int],
    private val orderIDLabel : Label,
    private val productNameLabel : Label,
    private val productCategoryLabel : Label,
    private val orderDateLabel : Label,
    private val quantityLabel : Label,
    private val unitPriceLabel : Label,
    private val commentsLabel : Label,
    private val buyerNameLabel : Label,
    private val buyerEmailLabel : Label
){
    //initialise tableview with additions from MainApp class
    orderTable.items = MainApp.orderData
    // initialize columns's cell values
    orderIDColumn.cellValueFactory = (x) => {x.value.orderID}

    //function to display selected record's data to labels in grid pane
    private def showOrderDetails (order : Option[Order]) = {
        order match {
            case Some(x) =>
            // Fill the labels with info from the order object.
            orderIDLabel.text = x.orderID.value.toString

            productNameLabel.text <== x.productName 
            productCategoryLabel.text <== x.productCategory

            import com.inventory.util.DateUtil._
            orderDateLabel.text = x.date.value.asString

            quantityLabel.text = x.quantity.value.toString
            unitPriceLabel.text = x.unitPrice.value.toString

            commentsLabel.text <== x.comments
            buyerNameLabel.text <== x.buyerName
            buyerEmailLabel.text <== x.buyerEmail
            
            case None =>
            productNameLabel.text.unbind()
            productCategoryLabel.text.unbind()
            commentsLabel.text.unbind()
            buyerNameLabel.text.unbind()
            buyerEmailLabel.text.unbind()
            
            // Order is null, remove all the text.
            orderIDLabel.text = ""
            productNameLabel.text = ""
            productCategoryLabel.text = ""
            orderDateLabel.text = ""
            quantityLabel.text = ""
            unitPriceLabel.text = ""
            commentsLabel.text = ""
            buyerNameLabel.text = ""
            buyerEmailLabel.text  = ""
            
        }    
    }

    showOrderDetails(None)

    //for selecting in table and showing details in gridpane
    orderTable.selectionModel().selectedItem.onChange(
        (_, _, newValue) => showOrderDetails(Option(newValue))
    )
    
    //Function to delete a order from the table
    def handleDeleteOrder(action : ActionEvent) = {
        val selectedIndex = orderTable.selectionModel().selectedIndex.value
        if (selectedIndex >= 0) {
            orderTable.items()(selectedIndex).delete() //remove from db
            orderTable.items().remove(selectedIndex);
        } else {
            //No order selected
            new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title       = "No Selection"
            headerText  = "No Order Selected"
            contentText = "Please select a order in the table."
            }.showAndWait()
        }
    } 
    //Function to add a phone product category to the table
    def handleNewOrder(action : ActionEvent) = {
        val order = new Order()
        val okClicked = MainApp.showOrderEditDialog(order);
            if (okClicked) {
                order.save() //saving to database
                MainApp.orderData += order
            }
    }
    //Function to update a order's information
    def handleUpdateOrder(action : ActionEvent) = {
        val selectedOrder = orderTable.selectionModel().selectedItem.value
        if (selectedOrder != null) {
            val okClicked = MainApp.showOrderEditDialog(selectedOrder)

            if (okClicked) {
            selectedOrder.save() //saving to database
            showOrderDetails(Some(selectedOrder))
            }

        } else {
            // Nothing selected.
            val alert = new Alert(Alert.AlertType.Warning){
            initOwner(MainApp.stage)
            title       = "No Selection"
            headerText  = "No Order Selected"
            contentText = "Please select a order in the table."
            }.showAndWait()
        }
    } 
    
    //Function to clear all entries in the Order table
    def handleClearButton() {
        //If order table is empty or has no entries, show information message to users
        if (MainApp.orderData.isEmpty) {
            val emptyTableAlert: Alert = new Alert(Alert.AlertType.Information)
            emptyTableAlert.setTitle("Order Table is Empty!")
            emptyTableAlert.setHeaderText("Order Table is Empty, No Order Data Found!")
            emptyTableAlert.setContentText("You can only clear the Order table if there are order data in it.")
            emptyTableAlert.showAndWait()
        } 
        
        // Else shows confirmation message to user before clearing the table
        else {
            val alert : Alert = new Alert(Alert.AlertType.Confirmation)
            alert.setTitle("Clear All Orders?")
            alert.setHeaderText("Are you sure you want to clear all the order data?")
            alert.setContentText("Note: Clicking OK will completely delete the order data from this table and the database. This change is irreversible.")

            val result = alert.showAndWait();
            //After confirmation is accepted or clicked OK, proceed to clear order data
            if (result.get == ButtonType.OK){
                MainApp.orderData.clear() //clear data displayed in table
                Order.deleteAll() //clear data from table in database as well
                MainApp.showOrderPage() //refresh order page
            }
        }
    }

}