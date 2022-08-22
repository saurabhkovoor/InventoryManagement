package com.inventory.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import com.inventory.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import com.inventory.view.ProductController

class Product (val productNameS: String){
    var productID = ObjectProperty[Int](-1)
    var productName = new StringProperty(productNameS)
    var productCategory = StringProperty("Category") 
    var unitPrice= ObjectProperty[Double]( 0.00 )
    var stock= ObjectProperty[Int](0) 
    var description= StringProperty("Null") 
    var supplier= StringProperty("Null")
    
	//save function to add or update product records in table
   	def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				val result: Long = sql"""
					insert into product (productName, productCategory, unitPrice, stock, description, supplier) values 
						(${productName.value}, ${productCategory.value}, ${unitPrice.value},
							${stock.value},${description.value}, ${supplier.value})
				""".updateAndReturnGeneratedKey.apply()
				productID.value = result.toInt
				result.toInt
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update product 
				set 
				productName  = ${productName.value} ,
				productCategory   = ${productCategory.value},
				unitPrice     = ${unitPrice.value},
				stock = ${stock.value},
				description       = ${description.value},
				supplier       = ${supplier.value}
				 where productID = ${productID.value} 
				""".update.apply()
			})
		}	
	}

	//delete function to delete product records in table
	def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from product where  
					productID = ${productID.value}
				""".update.apply()
			})
		} else 
			throw new Exception("Product not Exists in Database")		
	}

	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from product where 
				productID = ${productID()}
			""".map(rs => rs.string("productName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
}

object Product extends Database{
	def apply (
        productIDI: Int = -1,
        productNameS: String,
        productCategoryS: String, 
        unitPriceD: Double, 
        stockI: Int, 
        descriptionS: String, 
        supplierS: String
        ) : Product = {

		new Product(productNameS) {
			productID.value = productIDI
			productCategory.value = productCategoryS
			unitPrice.value = unitPriceD
			stock.value = stockI
			description.value = descriptionS
            supplier.value = supplierS
		}
		
	}
	//function to initialise the product table
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table product (
			  productID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
			  productName varchar(64), 
			  productCategory varchar(64), 
			  unitPrice varchar(20),
			  stock int,
			  description varchar(200),
			  supplier varchar(64)
			)
			""".execute.apply()
		}
	}
	//function that returns list of all products in the table
	def allProducts : List[Product] = {
		DB readOnly { implicit session =>
			sql"select * from product".map(rs => Product(rs.int("productID"),rs.string("productName"),
				rs.string("productCategory"),rs.double("unitPrice"), 
				rs.int("stock"),rs.string("description"), rs.string("supplier") )).list.apply()
		}
	} 
	//function to truncate product table or completely reset and remove its data
	def deleteAll() : Unit = {
		DB autoCommit { implicit session => 
			sql"""
				truncate table product
				""".update.apply()
			}
	}

}