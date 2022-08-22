package com.inventory.view

import com.inventory.model.Order
import com.inventory.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import com.inventory.util.DateUtil._
import java.time.format.DateTimeFormatter;
import scalafx.event.ActionEvent

@sfxml
class OrderEditDialogController(
    private val productNameField: TextField,
    private val productCategoryField: TextField,
    private val orderDateField: TextField,
    private val quantityField: TextField,
    private val unitPriceField: TextField,
    private val commentsField: TextField,
    private val buyerNameField: TextField,
    private val buyerEmailField: TextField
){
    var dialogStage : Stage = null
    private var _order : Order = null
    var confirmClicked = false //to track if confirm button has been clicked
    
    def order = _order
    def order_=(x : Order) {
        _order = x
        productNameField.text = _order.productName.value
        productCategoryField.text = _order.productCategory.value
        orderDateField.setPromptText("dd.mm.yyyy")
        quantityField.text = _order.quantity.value.toString
        unitPriceField.text = _order.unitPrice.value.toString
        commentsField.text = _order.comments.value
        buyerNameField.text = _order.buyerName.value
        buyerEmailField.text = _order.buyerEmail.value
        orderDateField.text  = _order.date.value.asString
    }
    //function to handle confirm button by checking and validating form inputs and entering values to new order record 
    def handleConfirmButton(action: ActionEvent){

        if(isInputValid()) {
            _order.productName.value = productNameField.text.value
            _order.productCategory.value  = productCategoryField.text.value
            _order.date.value = orderDateField.text.value.parseLocalDate;
            _order.quantity.value = quantityField.text().toInt
            _order.unitPrice.value = unitPriceField.text().toDouble
            _order.comments.value = commentsField.text.value
            _order.buyerName.value = buyerNameField.text.value
            _order.buyerEmail.value = buyerEmailField.text.value
            
            confirmClicked = true;
            dialogStage.close()
        }
    }

    //handle cancel button by closing order edit dialog stage
    def handleCancelButton(action :ActionEvent) {
        dialogStage.close();
        }
    //function to perform checking if the field is blank or input is null or has a length of 0
    def nullChecking (x : String) = x == null || x.length == 0
    
    //function to validate inputs
    def isInputValid() : Boolean = {
        var errorMessage = ""

        if (nullChecking(productNameField.text.value))
        errorMessage += "No valid product name!\n"
        if (nullChecking(productCategoryField.text.value))
        errorMessage += "No valid category!\n"

        if (nullChecking(orderDateField.text.value))
        errorMessage += "No valid order date!\n"
        else {
            if (!orderDateField.text.value.isValid) {
            errorMessage += "No valid order date. Use the format dd.mm.yyyy!\n";
            }
        }
        if (nullChecking(unitPriceField.text.value))
        errorMessage += "No valid quantity!\n"
        else {
        try {
            unitPriceField.text.value.toDouble
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid unit price (must be a number, decimal is optional)!\n"
        }
        }
        if (nullChecking(quantityField.text.value))
        errorMessage += "No valid quantity!\n"
        else {
        try {
            quantityField.text.value.toInt 
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid quantity (must be a whole number)!\n"
        }
        }

        if (nullChecking(commentsField.text.value))
        errorMessage += "No valid comments!\n"
        if (nullChecking(buyerNameField.text.value))
        errorMessage += "No valid buyer name!\n"
        if (nullChecking(buyerEmailField.text.value))
        errorMessage += "No valid buyer email!\n"
        

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message if there are errors detected in any input fields
            val alert = new Alert(Alert.AlertType.Error){
            initOwner(dialogStage)
            title = "Invalid Fields"
            headerText = "Please correct invalid fields"
            contentText = errorMessage
            }.showAndWait()

            return false;
        }
    } 
}