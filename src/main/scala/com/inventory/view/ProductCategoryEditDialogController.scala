package com.inventory.view

import com.inventory.model.ProductCategory
import com.inventory.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import com.inventory.util.DateUtil._ //don't need
import scalafx.event.ActionEvent
@sfxml
class ProductCategoryEditDialogController(
    private val productCategoryNameField : TextField,
    private val descriptionField : TextField
){
    var dialogStage : Stage = null //reference to the window
    private var _productCategory: ProductCategory = null //data model
    var confirmClicked = false //keep track user click ok/cancel

    def productCategory = _productCategory
    def productCategory_=(x : ProductCategory) {
        _productCategory = x

        productCategoryNameField.text = _productCategory.productCategoryName.value
        descriptionField.text  = _productCategory.description.value  
        }

    //function to handle confirm button by checking and validating form inputs and entering values to new product category record 
    def handleConfirmButton(action :ActionEvent){

     if (isInputValid()) {
        _productCategory.productCategoryName.value = productCategoryNameField.text.value
        _productCategory.description.value  = descriptionField.text.value

        confirmClicked = true;
        dialogStage.close() //closing dialog stage
    }
  }
  
  //handle cancel button by closing product category edit/add dialog stage
  def handleCancelButton(action :ActionEvent) {
        dialogStage.close();
  }
  
  //function to perform checking if the field is blank or input is null or has a length of 0
  def nullChecking (x : String) = x == null || x.length == 0

  //function to validate inputs
  def isInputValid() : Boolean = {
    var errorMessage = ""

    if (nullChecking(productCategoryNameField.text.value))
      errorMessage += "No valid product category name!\n"
    if (nullChecking(descriptionField.text.value))
      errorMessage += "No valid description!\n"
    if (errorMessage.length() == 0) {
        return true;
    } else {
        // Show the error message if there are errors detected in any input fields/textfields            
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