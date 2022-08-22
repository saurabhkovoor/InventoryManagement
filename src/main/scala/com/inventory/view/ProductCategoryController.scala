package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import com.inventory.model.ProductCategory
import com.inventory.model.PhoneCategory
import com.inventory.model.LaptopCategory
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.beans.property.{StringProperty} 
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class ProductCategoryController(
    private val productCategoryTable : TableView[ProductCategory],
    private val productCategoryIDColumn : TableColumn[ProductCategory, Int],
    private val productCategoryNameColumn : TableColumn[ProductCategory, String],
    private val descriptionColumn : TableColumn[ProductCategory, String],
    private val discountColumn : TableColumn[ProductCategory, Double],
    private val taxColumn : TableColumn[ProductCategory, Double]
    ){
        //initialise tableview with additions from MainApp class
        productCategoryTable.items = MainApp.productCategoryData
        //initialise columns's cell values
        productCategoryIDColumn.cellValueFactory  = {_.value.productCategoryID}
        productCategoryNameColumn.cellValueFactory  = {_.value.productCategoryName}
        descriptionColumn.cellValueFactory  = {_.value.description}
        discountColumn.cellValueFactory  = {_.value.discount}
        taxColumn.cellValueFactory  = {_.value.tax}
        
        //Function to delete a product category from the table
        def handleDeleteProductCategory(action : ActionEvent) = {
            val selectedIndex = productCategoryTable.selectionModel().selectedIndex.value
            if (selectedIndex >= 0) {
                productCategoryTable.items()(selectedIndex).delete() //delete from table in the database
                productCategoryTable.items().remove(selectedIndex);
            } else {
                //No product category selected
                new Alert(AlertType.Information) {
                initOwner(MainApp.stage)
                title       = "No Selected Product Category"
                headerText  = "No Product Category Selected"
                contentText = "Please select a Product Category in the table."
                }.showAndWait()
            }
        } 

        //Function to add a phone product category to the table
        def handleNewPhoneProductCategory(action : ActionEvent) = {
            val phoneCategory = new PhoneCategory(0, "", "")
            val okClicked = MainApp.showProductCategoryEditDialog(phoneCategory);
                if (okClicked) {
                    phoneCategory.save() //saving to database
                    MainApp.productCategoryData += phoneCategory
                }
        }

        //Function to add a laptop product category to the table
        def handleNewLaptopProductCategory(action : ActionEvent) = {
            val laptopCategory = new LaptopCategory(0, "", "")
            val okClicked = MainApp.showProductCategoryEditDialog(laptopCategory);
                if (okClicked) {
                    laptopCategory.save() //saving to database
                    MainApp.productCategoryData += laptopCategory
                }
        }

        //Function to update a product category's information
        def handleUpdateProductCategory(action : ActionEvent) = {
            val selectedProductCategory = productCategoryTable.selectionModel().selectedItem.value
            if (selectedProductCategory != null) {
                val okClicked = MainApp.showProductCategoryEditDialog(selectedProductCategory)

                if (okClicked) {
                selectedProductCategory.save() //saving to database
                }

            } else {
                // Nothing selected.
                val alert = new Alert(Alert.AlertType.Warning){
                initOwner(MainApp.stage)
                title       = "No Selection"
                headerText  = "No Product Category Selected"
                contentText = "Please select a Product Category in the table."
                }.showAndWait()
            }
        } 
        
        //Function to clear all entries in the Product Category table
        def handleClearButton() {
            //If productCategory table is empty or has no entries, show information message to users
            if (MainApp.productCategoryData.isEmpty) {
                val emptyTableAlert: Alert = new Alert(Alert.AlertType.Information)
                emptyTableAlert.setTitle("Product Category Table is Empty!")
                emptyTableAlert.setHeaderText("Product Category Table is Empty, No Product Category Data Found!")
                emptyTableAlert.setContentText("You can only clear the Product Category table if there are product categories in it.")
                emptyTableAlert.showAndWait()
            } 
            
            // Else shows confirmation message to user before clearing the table
            else {
                val alert : Alert = new Alert(Alert.AlertType.Confirmation)
                alert.setTitle("Clear All Product Categories?")
                alert.setHeaderText("Are you sure you want to clear all the product category data?")
                alert.setContentText("Note: Clicking OK will completely delete the product category data from this table and the database. This change is irreversible.")

                val result = alert.showAndWait();
                //After confirmation is accepted or clicked OK, proceed to clear Product Category data
                if (result.get == ButtonType.OK){
                    MainApp.productCategoryData.clear() //clear data displayed in table
                    ProductCategory.deleteAll() //clear data from table in database as well
                    MainApp.showProductCategoryPage() //refresh product category page
                }
            }
    }
}