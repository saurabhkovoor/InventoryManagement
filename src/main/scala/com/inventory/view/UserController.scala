package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import com.inventory.model.User
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.beans.property.{StringProperty} 
import scalafx.Includes._
import scalafx.event.ActionEvent
@sfxml
class UserController(
    private val userTable : TableView[User],
    private val userIDColumn : TableColumn[User, Int],
    private val firstNameColumn : TableColumn[User, String],
    private val lastNameColumn : TableColumn[User, String],
    private val usernameColumn : TableColumn[User, String],
    private val passwordColumn : TableColumn[User, String],
    private val userPhoneNumberColumn : TableColumn[User, Int],
    private val userAddressColumn : TableColumn[User, String]
){
  //initialise tableview with additions from MainApp class
  userTable.items = MainApp.userData

  // initialize columns's cell values
  userIDColumn.cellValueFactory = (x) => {x.value.userID}
  firstNameColumn.cellValueFactory  = {_.value.firstName}
  lastNameColumn.cellValueFactory  = {_.value.lastName}
  usernameColumn.cellValueFactory  = {_.value.username}
  passwordColumn.cellValueFactory  = {_.value.password}
  userPhoneNumberColumn.cellValueFactory  = {_.value.userPhoneNumber}
  userAddressColumn.cellValueFactory  = {_.value.userAddress} 
  
  //function to delete user from table
  def handleDeleteUser(action : ActionEvent) = {
    val selectedIndex = userTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
        userTable.items()(selectedIndex).delete() //deleting from the database
        userTable.items().remove(selectedIndex);
    } else {
        //When no users are selected
        new Alert(AlertType.Information) {
          initOwner(MainApp.stage)
          title       = "No Selected User"
          headerText  = "No User Selected"
          contentText = "Please select a user in the table."
        }.showAndWait()
    }
  } 

  //function to add user to table
  def handleNewUser(action : ActionEvent) = {
    val user = new User("","")
    val okClicked = MainApp.showUserEditDialog(user); //change OK clicked
        if (okClicked) {
            user.save() //saving user to the database
            MainApp.userData += user  //adding data to the user table
        }
  }
  //function to update information of user in table
  def handleUpdateUser(action : ActionEvent) = {
    val selectedUser = userTable.selectionModel().selectedItem.value 
    if (selectedUser != null) {
        val okClicked = MainApp.showUserEditDialog(selectedUser)

        if (okClicked) {
          selectedUser.save() //saving user information in database
        }

    } else {
        //when no user is selected
        val alert = new Alert(Alert.AlertType.Warning){
          initOwner(MainApp.stage)
          title       = "No Selected User"
          headerText  = "No User Selected"
          contentText = "Please select a user in the table."
        }.showAndWait()
    }
  }
  
  //Function to clear all entries in the User table
  def handleClearButton() {
        //If user table is empty or has no entries, show information message to users
        if (MainApp.userData.isEmpty) {
            val emptyTableAlert: Alert = new Alert(Alert.AlertType.Information)
            emptyTableAlert.setTitle("User Table is Empty!")
            emptyTableAlert.setHeaderText("User Table is Empty, No User Data Found!")
            emptyTableAlert.setContentText("You can only clear the User table if there are user data in it.")
            emptyTableAlert.showAndWait()
        } 
        
        // Else shows confirmation message to user before clearing the table
        else {
            val alert : Alert = new Alert(Alert.AlertType.Confirmation)
            alert.setTitle("Clear All Users?")
            alert.setHeaderText("Are you sure you want to clear all the user data?")
            alert.setContentText("Note: Clicking OK will completely delete the user data from this table and the database. This change is irreversible.")

            val result = alert.showAndWait();
            //After confirmation is accepted or clicked OK, proceed to clear user data
            if (result.get == ButtonType.OK){
                MainApp.userData.clear() //clear data displayed in table
                User.deleteAll() //clear data from table in database as well
                MainApp.showUserPage() //refresh user page
            }
        }
    }
}