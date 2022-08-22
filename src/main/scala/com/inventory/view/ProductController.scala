package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import com.inventory.model.Product
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.beans.property.{StringProperty} 
import scalafx.Includes._
import scalafx.event.ActionEvent
@sfxml
class ProductController(
    private val productTable : TableView[Product],
    private val productIDColumn : TableColumn[Product, Int],
    private val productNameColumn : TableColumn[Product, String],
    private val productCategoryColumn : TableColumn[Product, String],
    private val unitPriceColumn : TableColumn[Product, Double],
    private val stockColumn : TableColumn[Product, Int],
    private val descriptionColumn : TableColumn[Product, String],
    private val supplierColumn : TableColumn[Product, String]
){
  // initialize Table View display contents model
  productTable.items = MainApp.productData
  // initialize columns's cell values
  productIDColumn.cellValueFactory = (x) => {x.value.productID}

  productNameColumn.cellValueFactory  = {_.value.productName}
  productCategoryColumn.cellValueFactory  = {_.value.productCategory}
  unitPriceColumn.cellValueFactory  = {_.value.unitPrice}
  stockColumn.cellValueFactory  = {_.value.stock}
  descriptionColumn.cellValueFactory  = {_.value.description}
  supplierColumn.cellValueFactory  = {_.value.supplier} 
  
  //function to delete product from table
  def handleDeleteProduct(action : ActionEvent) = {
    val selectedIndex = productTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
        productTable.items()(selectedIndex).delete() //deleting from the database
        productTable.items().remove(selectedIndex);
    } else {
        //When no products are selected
        new Alert(AlertType.Information) {
          initOwner(MainApp.stage)
          title       = "No Selected Product"
          headerText  = "No Product Selected"
          contentText = "Please select a product in the table."
        }.showAndWait()
    }
  } 

  //function to add product to table
  def handleNewProduct(action : ActionEvent) = {
    val product = new Product("")
    val okClicked = MainApp.showProductEditDialog(product);
        if (okClicked) {
            product.save() //saving product to the database
            MainApp.productData += product //adding data to the product table
        }
  }

  //function to update information of product in table
  def handleUpdateProduct(action : ActionEvent) = {
    val selectedProduct = productTable.selectionModel().selectedItem.value 
    if (selectedProduct != null) {
        val okClicked = MainApp.showProductEditDialog(selectedProduct)

        if (okClicked) {
          selectedProduct.save() //saving product information in database
        }

    } else {
        //when no product is selected
        val alert = new Alert(Alert.AlertType.Warning){
          initOwner(MainApp.stage)
          title       = "No Selected Product"
          headerText  = "No Product Selected"
          contentText = "Please select a product in the table."
        }.showAndWait()
    }
  }

  //Function to clear all entries in the Product table
  def handleClearButton() {
        //If product table is empty or has no entries, show information message to users
        if (MainApp.productData.isEmpty) {
            val emptyTableAlert: Alert = new Alert(Alert.AlertType.Information)
            emptyTableAlert.setTitle("Product Table is Empty!")
            emptyTableAlert.setHeaderText("Product Table is Empty, No Product Data Found!")
            emptyTableAlert.setContentText("You can only clear the Product table if there are product data in it.")
            emptyTableAlert.showAndWait()
        } 
        
        // Else shows confirmation message to user before clearing the table
        else {
            val alert : Alert = new Alert(Alert.AlertType.Confirmation)
            alert.setTitle("Clear All Products?")
            alert.setHeaderText("Are you sure you want to clear all the product data?")
            alert.setContentText("Note: Clicking OK will completely delete the product data from this table and the database. This change is irreversible.")

            val result = alert.showAndWait();
            //After confirmation is accepted or clicked OK, proceed to clear product data
            if (result.get == ButtonType.OK){
                MainApp.productData.clear() //clear data displayed in table
                Product.deleteAll() //clear data from table in database as well
                MainApp.showProductPage() //refresh product page
            }
        }
    }  
}