package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import com.inventory.model.Supplier
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, TextField, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.beans.property.{StringProperty} 
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.collections.transformation.{FilteredBuffer, SortedBuffer}

@sfxml
class SupplierController(
    private val supplierTable : TableView[Supplier],
    private val supplierIDColumn : TableColumn[Supplier, Int],
    private val supplierNameColumn : TableColumn[Supplier, String],
    private val supplierPhoneNumberColumn : TableColumn[Supplier, Int],
    private val supplierAddressColumn : TableColumn[Supplier, String],
    private val searchField : TextField
){
  //initialise tableview with additions from MainApp class
  supplierTable.items = MainApp.supplierData
  
  // initialize columns's cell values
  supplierIDColumn.cellValueFactory = (x) => {x.value.supplierID}
  supplierNameColumn.cellValueFactory  = {_.value.supplierName}
  supplierPhoneNumberColumn.cellValueFactory  = {_.value.supplierPhoneNumber}
  supplierAddressColumn.cellValueFactory  = {_.value.supplierAddress}

  //function to delete supplier from table
  def handleDeleteSupplier(action : ActionEvent) = {
    val selectedIndex = supplierTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
        supplierTable.items()(selectedIndex).delete()//deleting from the database
        supplierTable.items().remove(selectedIndex);
    } else {
        //When no suppliers are selected
        new Alert(AlertType.Information) {
          initOwner(MainApp.stage)
          title       = "No Selected Supplier"
          headerText  = "No Supplier Selected"
          contentText = "Please select a supplier in the table."
        }.showAndWait()
    }
  } 

  //function to add supplier to table
  def handleNewSupplier(action : ActionEvent) = {
    val supplier = new Supplier("")
    val okClicked = MainApp.showSupplierEditDialog(supplier); //change OK clicked
        if (okClicked) {
            supplier.save() //saving supplier to the database
            MainApp.supplierData += supplier //adding data to the supplier table
        }
  }

  //function to update information of supplier in table
  def handleUpdateSupplier(action : ActionEvent) = {
    val selectedSupplier = supplierTable.selectionModel().selectedItem.value 
    if (selectedSupplier != null) {
        val okClicked = MainApp.showSupplierEditDialog(selectedSupplier)

        if (okClicked) {
          selectedSupplier.save() //saving supplier information in database
        }

    } else {
        //when no suppliers are selected
        val alert = new Alert(Alert.AlertType.Warning){
          initOwner(MainApp.stage)
          title       = "No Selected Supplier"
          headerText  = "No Supplier Selected"
          contentText = "Please select a supplier in the table."
        }.showAndWait()
    }
  }

  //Function to clear all entries in the Supplier table
  def handleClearButton() {
        //If supplier table is empty or has no entries, show information message to users
        if (MainApp.supplierData.isEmpty) {
            val emptyTableAlert: Alert = new Alert(Alert.AlertType.Information)
            emptyTableAlert.setTitle("Supplier Table is Empty!")
            emptyTableAlert.setHeaderText("Supplier Table is Empty, No Supplier Data Found!")
            emptyTableAlert.setContentText("You can only clear the Supplier table if there are supplier data in it.")
            emptyTableAlert.showAndWait()
        } 
        
        // Else shows confirmation message to user before clearing the table
        else {
            val alert : Alert = new Alert(Alert.AlertType.Confirmation)
            alert.setTitle("Clear All Suppliers?")
            alert.setHeaderText("Are you sure you want to clear all the supplier data?")
            alert.setContentText("Note: Clicking OK will completely delete the supplier data from this table and the database. This change is irreversible.")

            val result = alert.showAndWait();
            //After confirmation is accepted or clicked OK, proceed to clear supplier data
            if (result.get == ButtonType.OK){
                MainApp.supplierData.clear() //clear data displayed in table
                Supplier.deleteAll() //clear data from table in database as well
                MainApp.showSupplierPage() //refresh supplier page
            }
        }
    }

}