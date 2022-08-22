package com.inventory.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate
import com.inventory.util.DateUtil._
import com.inventory.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Order() extends Database{
    var orderID = ObjectProperty[Int](-1) 
    var productName = StringProperty("product")
    var productCategory = StringProperty("category")
	var date = ObjectProperty[LocalDate](LocalDate.of(2021, 3, 2))
    var quantity = ObjectProperty[Int](0)
    var unitPrice = ObjectProperty[Double](0.00)
    var comments = StringProperty("Null")
    var buyerName = StringProperty("Buyer")
    var buyerEmail = StringProperty("buyer@example.com")

	//save function to add or update order records in table
   	def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				val result: Long = sql"""
					insert into "order" (productName, productCategory, date, quantity, unitPrice, comments, buyerName, buyerEmail) values 
						(${productName.value},${productCategory.value},${date.value.asString},${quantity.value},${unitPrice.value},${comments.value},${buyerName.value},${buyerEmail.value})
				""".updateAndReturnGeneratedKey.apply()
				orderID.value = result.toInt
				result.toInt
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update "order" 
				set 
				productName = ${productName.value},
				productCategory = ${productCategory.value},
                date = ${date.value.asString},
                quantity = ${quantity.value} ,
				unitPrice = ${unitPrice.value},
				comments = ${comments.value},
                buyerName = ${buyerName.value},
                buyerEmail = ${buyerEmail.value}
				 where orderID = ${orderID.value} 
				""".update.apply()
			})
		}
			
	}

	//delete function to delete order records in table
	def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from "order" where  
					orderID = ${orderID.value}
				""".update.apply()
			})
		} else 
			throw new Exception("Order doesn't exist in the Database")		
	}
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from "order" where 
				orderID = ${orderID()}
			""".map(rs => rs.string("productName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
}

object Order extends Database{
	def apply (
        orderIDI: Int = -1,
        productNameS: String, 
        productCategoryS: String, 
        dateS: String, 
        quantityI: Int, 
        unitPriceD: Double,  
        commentsS: String, 
        buyerNameS: String, 
        buyerEmailS: String 
        ) : Order = {

		new Order() {
			orderID.value = orderIDI
            productName.value = productNameS
			productCategory.value = productCategoryS
			date.value = dateS.parseLocalDate
            quantity.value = quantityI
			unitPrice.value = unitPriceD
			comments.value = commentsS
			buyerName.value = buyerNameS
            buyerEmail.value = buyerEmailS
		}
		
	}

	//function to initialise the order table
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table "order" (
			  orderID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
              productName varchar(64),
              productCategory varchar(64),
              date varchar(64),
              quantity int,
			  unitPrice varchar(20), 
			  comments varchar(100),
			  buyerName varchar(64),
			  buyerEmail varchar(64)
			)
			""".execute.apply()
		}
	}

	//function that returns list of all orders in the table
	def allOrders : List[Order] = {
		DB readOnly { implicit session =>
			sql"""
			select * from "order"
			""".map(rs => Order(rs.int("orderID"),rs.string("productName"),
				rs.string("productCategory"),rs.string("date"),rs.int("quantity"), 
				rs.double("unitPrice"), rs.string("comments"), rs.string("buyerName"), rs.string("buyerEmail") )).list.apply()
		}
	}

	//function to truncate order table or completely reset and remove its data
	def deleteAll() : Unit = {
		DB autoCommit { implicit session => 
			sql"""
				truncate table order
				""".update.apply()
			}
	} 
}