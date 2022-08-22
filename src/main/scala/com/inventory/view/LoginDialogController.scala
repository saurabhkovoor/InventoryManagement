package com.inventory.view
import scalafxml.core.macros.sfxml
import com.inventory.MainApp
import scalafx.stage.Stage
import scalafx.scene.control.{TextField, PasswordField, TableColumn, Label, Alert}
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.application.Platform

import scalikejdbc._
import com.inventory.model.User

@sfxml
class LoginDialogController(
    private val usernameField: TextField,
    private val passwordField: PasswordField
){
    var dialogStage : Stage = null
    var loginClicked: Boolean = false //keep track user click login

    //function to handle cancel button and close the stage
    def cancel()={
        dialogStage.close()
    }
    //function to handle log in button to submit login credentials for input validation and login authorisation
    def handleLoginButton()={
        if(checkLogin()){
            MainApp.loggedInUsername = usernameField.text.value.toString
            MainApp.showHomePage()
            loginClicked = true
            dialogStage.close()
        }
        else{
            val alert = new Alert(Alert.AlertType.Error){
            initOwner(dialogStage)
            title = "Invalid Login"
            headerText = "Invalid Login Credentials"
            contentText = "Wrong Username and Password Entry!"
            }.showAndWait()
        }
    }
    //function to handle sign up button by requesting to load and open registration dialog and create new user
    def handleSignUpButton(action : ActionEvent) ={
        val user = new User("","")
        val okClicked = MainApp.showRegistrationDialog(user)
        if (okClicked) {
            user.save() //saving newly registered user record to database
            MainApp.userData += user
        }
    }
    //function to check entered login credentials
    def checkLogin():Boolean={
        val enteredUsername = usernameField.text.value
        val enteredPassword = passwordField.text.value

        //check if combination of username and password exists in the user table, if so authorise login, else return false to show error message
        if(validateLogin()){
            DB readOnly { implicit session =>
                sql"""
                    select * from "user" where 
                    username = ${enteredUsername} and
                    password = ${enteredPassword}
                """.map(rs => rs.string("username")).single.apply()
                
            } match {
                case Some(x) => true
                case None => false
            }
        }
        else{
            return false
        }
    }
    
    //function to validate entered login credentials, if field is blank
    def validateLogin() : Boolean ={
        var errorMessage=""
        //if username field is blank
        if (usernameField.text.value == null || usernameField.text.value.length == 0){
            errorMessage += "ERROR: Username is blank!\n"
        }
        //if password field is blank
        if (passwordField.text.value == null || passwordField.text.value.length == 0){
            errorMessage += "ERROR: Password is blank!\n"
        }

        if (errorMessage.length() == 0){
            return true
        }  else {
            // Show the error message if there are errors detected in either username or password field.
            val alert = new Alert(Alert.AlertType.Error){
            initOwner(dialogStage)
            title = "Invalid Fields"
            headerText = "Please correct invalid fields"
            contentText = errorMessage
            }.showAndWait()

            return false;
        }
        
    }
    //function to handle cancel button by closing entire application
    def handleCancelButton(): Unit = {
        dialogStage.close()
        Platform.exit()
    }
}