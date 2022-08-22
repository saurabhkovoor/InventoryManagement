package com.inventory.view

import com.inventory.model.User
import com.inventory.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert, PasswordField}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class RegistrationDialogController(
    private val firstNameField: TextField,
    private val lastNameField: TextField,
    private val usernameField: TextField,
    private val passwordField: TextField,
    private val userPhoneNumberField: TextField,
    private val userAddressField: TextField
){
    var dialogStage : Stage = null
    private var _user : User = null
    var confirmClicked = false 
    
    def user = _user
    def user_=(x : User) {
        _user = x
        firstNameField.text = _user.firstName.value
        lastNameField.text = _user.lastName.value
        usernameField.text = _user.username.value
        passwordField.text = _user.password.value
        userPhoneNumberField.text = _user.userPhoneNumber.value.toString
        userAddressField.text = _user.userAddress.value
    }
    //function to handle confirm button by checking and validating form inputs and entering values to new user record 
    def handleConfirmButton(action: ActionEvent){

        if(isInputValid()) {
            _user.firstName.value = firstNameField.text.value
            _user.lastName.value = lastNameField.text.value
            _user.username.value = usernameField.text.value
            _user.password.value = passwordField.text.value
            _user.userPhoneNumber.value = userPhoneNumberField.text().toInt
            _user.userAddress.value = userAddressField.text.value
            
            confirmClicked = true;
            dialogStage.close()
        }
    }

    //handle cancel button by closing registration dialog stage
    def handleCancelButton(action :ActionEvent) {
        dialogStage.close()
        }

    //function to perform checking if the field is blank or input is null or has a length of 0
    def nullChecking (x : String) = x == null || x.length == 0
    
    //function to validate inputs
    def isInputValid() : Boolean = {
        var errorMessage = ""

        if (nullChecking(firstNameField.text.value))
        errorMessage += "No valid first name!\n"
        if (nullChecking(lastNameField.text.value))
        errorMessage += "No valid last name!\n"
        if (nullChecking(usernameField.text.value))
        errorMessage += "No valid username!\n"
        if (User.usernameExist(usernameField.text.value)) //check if username is already taken
        errorMessage+="Username is already taken\n"
        if (nullChecking(passwordField.text.value))
        errorMessage += "No valid password!\n"
        else {
        try {
            userPhoneNumberField.text.value.toInt
        } catch {
            case e : NumberFormatException =>
                errorMessage += "No valid phone number (must be a valid phone number)!\n"
        }
        }
        if (nullChecking(userAddressField.text.value))
        errorMessage += "No valid address!\n"

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