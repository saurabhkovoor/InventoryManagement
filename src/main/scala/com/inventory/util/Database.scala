package com.inventory.util
import scalikejdbc._
import com.inventory.model.Product
import com.inventory.model.ProductCategory
import com.inventory.model.Supplier
import com.inventory.model.User
import com.inventory.model.Order
trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  
  ConnectionPool.singleton(dbURL, "me", "mine")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession


}
object Database extends Database{
  //function to intially setup the databases with tables 
  def setupDB() = {
  	if (!hasDBInitialize){
  		Product.initializeTable()
      ProductCategory.initializeTable()
      Supplier.initializeTable()
      User.initializeTable()
      Order.initializeTable()
      }
  }
  //check if the table has been initialised
  def hasDBInitialize : Boolean = {

    DB getTable "Product" match {
      case Some(x) => true
      case None => false
    }

    DB getTable "ProductCategory" match {
      case Some(x) => true
      case None => false
    }

    DB getTable "Supplier" match {
      case Some(x) => true
      case None => false
    }

    DB getTable "User" match {
      case Some(x) => true
      case None => false
    }

    DB getTable "Order" match {
      case Some(x) => true
      case None => false
    }
  }
}