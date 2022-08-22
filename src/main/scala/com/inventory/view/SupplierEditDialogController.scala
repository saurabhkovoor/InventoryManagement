package com.inventory.view

import com.inventory.model.Supplier
import com.inventory.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import com.inventory.util.DateUtil._ //don't need
import scalafx.event.ActionEvent

@sfxml
class SupplierEditDialogController(
    private val supplierNameField: TextField,
    private val supplierPhoneNumberField: TextField,
    private val supplierAddressField: TextField
){
    var dialogStage : Stage = null
    private var _supplier : Supplier = null
    var confirmClicked = false 
    
    def supplier = _supplier
    def supplier_=(x : Supplier) {
        _supplier = x
        supplierNameField.text = _supplier.supplierName.value
        supplierPhoneNumberField.text = _supplier.supplierPhoneNumber.value.toString
        supplierAddressField.text = _supplier.supplierAddress.value
    }

    //function to handle when user clicks confirm button on form to submit form data
    def handleConfirmButton(action: ActionEvent){

        if(isInputValid()) {
            _supplier.supplierName.value = supplierNameField.text.value
            _supplier.supplierPhoneNumber.value = supplierPhoneNumberField.text().toInt
            _supplier.supplierAddress.value = supplierAddressField.text.value
            
            confirmClicked = true;
            dialogStage.close()
        }
    }
    //function to handle when user clicks cancel button on form
    def handleCancelButton(action :ActionEvent) {
        dialogStage.close();
        }

    //function for checking null entry
    def nullChecking (x : String) = x == null || x.length == 0
    
    //function for validating user form input
    def isInputValid() : Boolean = {
        var errorMessage = ""

        if (nullChecking(supplierNameField.text.value))
        errorMessage += "No valid supplier name!\n"
        if (nullChecking(supplierAddressField.text.value))
        errorMessage += "No valid supplier address!\n"
        else {
        try {
            supplierPhoneNumberField.text.value.toInt
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid phone number (must be a valid number)!\n"
        }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
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