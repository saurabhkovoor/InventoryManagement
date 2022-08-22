package com.inventory.view

import scalafxml.core.macros.sfxml
import scalafx.application.Platform
import com.inventory.MainApp
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.control.Alert
import scalafx.stage.Stage
@sfxml
//to consistently display the menu bar and side menu for every page except for the opening login dialog
class RootMenuController() {

    var dialogStage : Stage = null
    //function for handling the close button
    def handleClose(): Unit = {
        Platform.exit()
    }
    //function for handling the home button - redirect to homepage
    def handleHomeButton(){
        MainApp.showHomePage()
    }
    //function for handling the order button - redirect to order page
    def handleOrderButton(){
            MainApp.showOrderPage()
        }
    //function for handling the product category button - redirect to product category page
    def handleProductCategoryButton(){
            MainApp.showProductCategoryPage()
        }
    //function for handling the product button - redirect to product page
    def handleProductButton(){
            MainApp.showProductPage()
        }
    //function for handling the user button - redirect to user page
    def handleUserButton(){
            MainApp.showUserPage()
        }
    //function for handling the supplier button - redirect to supplier page
    def handleSupplierButton(){
            MainApp.showSupplierPage()
        }
    //function for handling the log out button
     def handleLogOutButton(){
         MainApp.stage.hide()
         MainApp.showLoginDialog()
         MainApp.stage.show()
         }

    //function for displaying about dialog
    def handleHelpButton(){
         new Alert(Alert.AlertType.Information){
            initOwner(dialogStage)
            title = "About Us"
            headerText = "What is this app?"
            contentText = "This is a simple inventory management platform for the TuringTech Company!\nWith this, you can record the company's products, categories, suppliers, orders, and system users."
         }.showAndWait()
         }    
}