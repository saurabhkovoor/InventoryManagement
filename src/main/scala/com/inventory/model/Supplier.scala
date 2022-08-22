package com.inventory.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import com.inventory.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import com.inventory.view.SupplierController

class Supplier(val supplierNameS: String){
    var supplierID = ObjectProperty[Int](-1) 
    var supplierName = new StringProperty(supplierNameS)
    var supplierPhoneNumber = ObjectProperty[Int](999999999) 
    var supplierAddress = StringProperty("Null")

	//save function to add or update supplier records in table
   	def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				val result: Long = sql"""
					insert into supplier (supplierName, supplierPhoneNumber, supplierAddress) values 
						(${supplierName.value}, ${supplierPhoneNumber.value}, ${supplierAddress.value})
				""".updateAndReturnGeneratedKey.apply()
				supplierID.value = result.toInt
				result.toInt
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update supplier 
				set 
				supplierName  = ${supplierName.value} ,
				supplierPhoneNumber   = ${supplierPhoneNumber.value},
				supplierAddress     = ${supplierAddress.value}
				 where supplierID = ${supplierID.value} 
				""".update.apply()
			})
		}
			
	}

	//delete function to delete supplier records in table
	def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from supplier where  
					supplierID = ${supplierID.value}
				""".update.apply()
			})
		} else 
			throw new Exception("Supplier doesn't exist in the Database")		
	}
	

	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from supplier where 
				supplierID = ${supplierID()}
			""".map(rs => rs.string("supplierName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}
}
object Supplier extends Database{
	def apply (
        supplierIDI: Int = -1,
        supplierNameS: String,
        supplierPhoneNumberI: Int, 
        supplierAddressS: String
        ) : Supplier = {

		new Supplier(supplierNameS) {
			supplierID.value = supplierIDI
			supplierName.value = supplierNameS
			supplierPhoneNumber.value = supplierPhoneNumberI
			supplierAddress.value = supplierAddressS
		}
		
	}
	//function to initialise the supplier table
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table supplier (
			  supplierID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
			  supplierName varchar(64), 
			  supplierPhoneNumber int, 
			  supplierAddress varchar(100)
			)
			""".execute.apply()
		}
	}

	//function to truncate supplier table or completely reset and remove its data
	def deleteAll() : Unit = {
		DB autoCommit { implicit session => 
			sql"""
				truncate table supplier
				""".update.apply()
			}
	}
	//function that returns list of all suppliers in the table
	def allSuppliers : List[Supplier] = {
		DB readOnly { implicit session =>
			sql"select * from supplier".map(rs => Supplier(rs.int("supplierID"),rs.string("supplierName"),
				rs.int("supplierPhoneNumber"),rs.string("supplierAddress") )).list.apply()
		}
	} 
}