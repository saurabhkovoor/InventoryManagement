package com.inventory.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import com.inventory.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import scalafx.Includes._ //can remove
import com.inventory.view.UserController

class User(val usernameS: String, val passwordS: String){
    var userID = ObjectProperty[Int](-1) 
    var firstName = StringProperty("FirstName")
    var lastName = StringProperty("LastName") 
    var username = new StringProperty(usernameS) 
    var password = new StringProperty(passwordS)
    var userPhoneNumber = ObjectProperty[Int](999999999) 
    var userAddress = StringProperty("Null")

	//save function to add or update user records in table
    def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				val result: Long = sql"""
					insert into "user" (firstName, lastName, username, password, userPhoneNumber, userAddress) values 
						(${firstName.value}, ${lastName.value}, ${username.value}, ${password.value}, ${userPhoneNumber.value}, ${userAddress.value} )
				""".updateAndReturnGeneratedKey.apply() 
				userID.value = result.toInt
				result.toInt
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update "user" 
				set 
				firstName = ${firstName.value} ,
				lastName = ${lastName.value},
				username = ${username.value},
                password = ${password.value},
                userPhoneNumber = ${userPhoneNumber.value},
                userAddress = ${userAddress.value}
				 where userID = ${userID.value} 
				""".update.apply()
			}) 
		}
			
	}
	
	//delete function to delete product records in table
	def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from "user" where  
					userID = ${userID.value}
				""".update.apply()
			})
		} else 
			throw new Exception("User doesn't exist in the Database")	 	
	}
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from "user" where 
				userID = ${userID()}
			""".map(rs => rs.string("username")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}
	
}
object User extends Database{
	def apply (
        userIDI: Int = -1,
        firstNameS: String,
        lastNameS: String,
        usernameS: String,
        passwordS: String,
        userPhoneNumberI: Int, 
        userAddressS: String
        ) : User = {

		new User(usernameS, passwordS) {
			userID.value = userIDI
			firstName.value = firstNameS
            lastName.value = lastNameS
            username.value = usernameS
            password.value = passwordS
			userPhoneNumber.value = userPhoneNumberI
			userAddress.value = userAddressS
		}
		
	}
	//function to initialise the user table
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table "user" (
			  userID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
			  firstName varchar(64),
              lastName varchar(64),
              username varchar(64),
              password varchar(64), 
			  userPhoneNumber int, 
			  userAddress varchar(100)
			)
			""".execute.apply() 
		}
	}
	
	//function that returns list of all users in the table
	def allUsers : List[User] = {
		DB readOnly { implicit session =>
			sql"""
            select * from "user"
            """.map(rs => User(rs.int("userID"),rs.string("firstName"),rs.string("lastName"),rs.string("username"),rs.string("password"),
				rs.int("userPhoneNumber"),rs.string("userAddress") )).list.apply()
		}
	}

	//function to truncate user table or completely reset and remove its data
	def deleteAll() : Unit = {
		DB autoCommit { implicit session => 
			sql"""
				truncate table user
				""".update.apply()
			}
	} 

	//function to check if a username exists in the user table, to ensure every username is unique
	def usernameExist(_username: String) : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from "user" where 
				username = ${_username}
			""".map(rs => rs.string("username")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
	//overloaded function to obtain user record based on entered username string input
	def getUserWithUserData(username: String) : Option[User] = {
		DB readOnly { implicit session =>
			sql"""
            select * from "user" where
			username = ${username}
            """.map(rs =>  User(rs.int("userID"),rs.string("firstName"),rs.string("lastName"),rs.string("username"),rs.string("password"),
				rs.int("userPhoneNumber"),rs.string("userAddress") )).single.apply()
		}
	}

	//overloaded function to obtain user record based on entered userID int input
	def getUserWithUserData(userID: Int) : Option[User] = {
		DB readOnly { implicit session =>
			sql"""
            select * from "user" where
			userID = ${userID}
            """.map(rs =>  User(rs.int("userID"),rs.string("firstName"),rs.string("lastName"),rs.string("username"),rs.string("password"),
				rs.int("userPhoneNumber"),rs.string("userAddress") )).single.apply()
		}
	}
}