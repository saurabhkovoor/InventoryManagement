package com.inventory.view
import com.inventory.MainApp
import com.inventory.model.User
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.beans.property.{StringProperty}
import scalafx.scene.control.{Label}


@sfxml
class HomePageController(
    private val welcomeLabel : Label,
    private val nameLabel : Label,
    private val usernameLabel : Label,
    private val phoneLabel : Label,
    private val addressLabel : Label    
){
    val loggedinUser: Option[User] = User.getUserWithUserData(MainApp.loggedInUsername.toString)
    showWelcome(loggedinUser: Option[User])

    //function to display welcome message and the logged in user's data
    def showWelcome(user : Option[User]) = {
        user match {
            case Some(x) =>
            welcomeLabel.text = "Welcome " + x.firstName.value.toString + " " + x.lastName.value.toString + "!"
            nameLabel.text = x.firstName.value.toString + " " + x.lastName.value.toString
            usernameLabel.text = x.username.value.toString
            phoneLabel.text = x.userPhoneNumber.value.toString
            addressLabel.text = x.userAddress.value.toString

            case None =>
            welcomeLabel.text.unbind()
            welcomeLabel.text = ""
            nameLabel.text.unbind()
            nameLabel.text = ""
            usernameLabel.text.unbind()
            usernameLabel.text = ""
            phoneLabel.text.unbind()
            phoneLabel.text = ""
            addressLabel.text.unbind()
            addressLabel.text = ""
            }
        }

    //function for handling the order button - redirect to order page
    def handleOrderButton()={
            MainApp.showOrderPage()
        }

    //function for handling the product category button - redirect to product category page
    def handleProductCategoryButton() ={
            MainApp.showProductCategoryPage()
        }
        
    //function for handling the product button - redirect to product page
    def handleProductButton() = {
            MainApp.showProductPage()
        }
        
    //function for handling the supplier button - redirect to supplier page
    def handleSupplierButton() = {
            MainApp.showSupplierPage()
        }

}