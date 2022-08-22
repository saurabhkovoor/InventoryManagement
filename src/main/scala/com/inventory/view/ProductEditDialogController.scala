package com.inventory.view

import com.inventory.model.Product
import com.inventory.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class ProductEditDialogController(
    private val productNameField: TextField,
    private val productCategoryField: TextField,
    private val unitPriceField: TextField,
    private val stockField: TextField,
    private val descriptionField: TextField,
    private val supplierField: TextField,
){
    var dialogStage : Stage = null
    private var _product : Product = null
    var confirmClicked = false 
    
    def product = _product
    def product_=(x : Product) {
        _product = x
        productNameField.text = _product.productName.value
        productCategoryField.text = _product.productCategory.value
        unitPriceField.text = _product.unitPrice.value.toString
        stockField.text = _product.stock.value.toString
        descriptionField.text = _product.description.value
        supplierField.text = _product.supplier.value
    }
    //function to handle confirm button by checking and validating form inputs and entering values to new product record 
    def handleConfirmButton(action: ActionEvent){

        if(isInputValid()) {
            _product.productName.value = productNameField.text.value
            _product.productCategory.value = productCategoryField.text.value
            _product.unitPrice.value = unitPriceField.text().toDouble
            _product.stock.value = stockField.text().toInt
            _product.description.value = descriptionField.text.value
            _product.supplier.value = supplierField.text.value

            confirmClicked = true;
            dialogStage.close()
        }
    }
    //handle cancel button by closing product edit dialog stage
    def handleCancelButton(action: ActionEvent) {
        dialogStage.close();
        }
    //function to perform checking if the field is blank or input is null or has a length of 0
    def nullChecking (x : String) = x == null || x.length == 0

    //function to validate input fields/textfields
    def isInputValid() : Boolean = {
        var errorMessage = ""

        if (nullChecking(productNameField.text.value))
        errorMessage += "No valid product name!\n"
        if (nullChecking(productCategoryField.text.value))
        errorMessage += "No valid category!\n"
        if (nullChecking(descriptionField.text.value))
        errorMessage += "No valid description!\n"
        if (nullChecking(supplierField.text.value))
        errorMessage += "No valid supplier!\n"
        if (nullChecking(unitPriceField.text.value))
        errorMessage += "No valid unit price (must be a number, decimal is optional)!\n"
        else {
        try {
            unitPriceField.text.value.toDouble
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid unit price (must be a number, decimal is optional)!\n"
        }
        }
        if (nullChecking(stockField.text.value))
        errorMessage += "No valid stock (must be a number, decimal is optional)!\n"
        else {
        try {
            stockField.text.value.toInt 
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid stock (must be a whole number)!\n"
        }
        }

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