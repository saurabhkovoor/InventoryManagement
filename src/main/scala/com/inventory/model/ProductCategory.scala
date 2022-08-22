package com.inventory.model

import com.inventory.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import com.inventory.view.ProductController
import scalafx.beans.property.{StringProperty, ObjectProperty}

abstract class ProductCategory(val productCategoryIDI: Int,  val productCategoryNameS: String, val descriptionS: String, val discountD: Double, val taxD: Double){
    var productCategoryID = ObjectProperty[Int](productCategoryIDI)
    var productCategoryName = new StringProperty(productCategoryNameS)
    var description = StringProperty(descriptionS)
	var discount = ObjectProperty[Double](discountD)
	var tax = ObjectProperty[Double](taxD)

	//save function to add or update product category records in table
    def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				val result: Long = sql"""
					insert into productCategory (productCategoryName, description, discount, tax) values 
						(${productCategoryName.value}, ${description.value}, ${discount.value}, ${tax.value})
				""".updateAndReturnGeneratedKey.apply()
				productCategoryID.value = result.toInt
				result.toInt
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update productCategory 
				set 
				productCategoryName = ${productCategoryName.value} ,
				description = ${description.value},
				discount = ${discount.value},
				tax = ${tax.value}
				 where productCategoryID = ${productCategoryID.value} 
				""".update.apply()
			})
		}
			
	}

	//delete function to delete product category records in table
    def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from productCategory where  
					productCategoryID = ${productCategoryID.value}
				""".update.apply()
			})
		} else 
			throw new Exception("product Category not Exists in Database")		
	}
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from productCategory where 
				productCategoryID = ${productCategoryID()}
			""".map(rs => rs.string("productCategoryName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}

}
object ProductCategory extends Database{
	def apply (
		productCategoryIDI: Int = -1,
		productCategoryNameS : String, 
		descriptionS : String,
		discountD: Double,
		taxD: Double
		) : ProductCategory = {

		new ProductCategory(productCategoryIDI, productCategoryNameS, descriptionS, discountD, taxD) {
			productCategoryID.value = productCategoryIDI
			productCategoryName.value = productCategoryNameS
			description.value = descriptionS
			discount.value = discountD
			tax.value = taxD
		}
		
	}
	//function to initialise the product category table
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table productCategory (
			  productCategoryID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
			  productCategoryName varchar(64), 
			  description varchar(100),
			  discount varchar(10),
			  tax varchar(10)
			)
			""".execute.apply()
		}
	}
	
	//function that returns list of all product categories in the table
	def allProductCategory : List[ProductCategory] = {
		DB readOnly { implicit session =>
			sql"select * from productCategory".map(rs => ProductCategory(rs.int("productCategoryID"),rs.string("productCategoryName"),rs.string("description"),rs.double("discount"),rs.double("tax") )).list.apply()
		}
	}
	//function to truncate product category table or completely reset and remove its data
	def deleteAll() : Unit = {
		DB autoCommit { implicit session => 
			sql"""
				truncate table productCategory
				""".update.apply()
			}
	}
}
