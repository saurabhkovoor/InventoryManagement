package com.inventory
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}
import com.inventory.view.LoginDialogController
import scalafx.collections.ObservableBuffer
import scalafx.stage.{Stage, Modality}
import scalafx.scene.image.Image
import scalafx.application.Platform
import javafx.event.EventHandler
import scalafx.beans.property.{StringProperty}

//Other classes I created
import com.inventory.model.Product
import com.inventory.view.ProductEditDialogController
import com.inventory.view.ProductController
import com.inventory.model.ProductCategory
import com.inventory.model.PhoneCategory
import com.inventory.view.ProductCategoryEditDialogController
import com.inventory.view.ProductCategoryController
import com.inventory.util.Database
import com.inventory.model.Supplier
import com.inventory.view.SupplierController
import com.inventory.view.SupplierEditDialogController
import com.inventory.model.User
import com.inventory.view.UserController
import com.inventory.view.UserEditDialogController
import com.inventory.model.Order
import com.inventory.view.OrderController
import com.inventory.view.OrderEditDialogController
import com.inventory.view.RegistrationDialogController
import com.inventory.view.HomePageController

object MainApp extends JFXApp {

    Database.setupDB()
    //for creating value and storing value, then inserting that data to corresponding table
    //for Product
    val productData = new ObservableBuffer[Product]()
    productData ++= Product.allProducts

    //for ProductCategory
    val productCategoryData = new ObservableBuffer[ProductCategory]()
    productCategoryData ++= ProductCategory.allProductCategory
    // productCategoryData += new PhoneCategory(0, "Samsung", "Samsung Phones")
    // productCategoryData += new PhoneCategory(1, "Apple", "Apple Phones")

    //for Supplier
    val supplierData = new ObservableBuffer[Supplier]()
    supplierData ++= Supplier.allSuppliers

    //for User
    val userData = new ObservableBuffer[User]()
    userData ++= User.allUsers

    //for Order
    val orderData = new ObservableBuffer[Order]()
    orderData ++= Order.allOrders

    //variable that holds the username that the user logged in with
    var loggedInUsername:String = ""

    //value that holds the fxml file for app root which has menu consistent to every page
    val rootResource = getClass.getResourceAsStream("view/RootMenu.fxml")
    
    //value to hold loader for loading fxml file
    val loader = new FXMLLoader(null, NoDependencyResolver)

    //loading root page
    loader.load(rootResource)

    val roots: javafx.scene.layout.BorderPane = loader.getRoot[jfxs.layout.BorderPane]

    //main stage
    stage = new PrimaryStage {
        title = "TuringTech Inventory Manager"
        scene = new Scene {
            root = roots
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
    }

    //Function to show and display the homepage
    def showHomePage() = {
        val resource = getClass.getResourceAsStream("view/HomePage.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the login dialog page
    def showLoginDialog() ={
        val resource = getClass.getResourceAsStream("view/Login.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[LoginDialogController#Controller]

        val dialog = new Stage() {
            title = "TuringTech User Login"
            initModality(Modality.APPLICATION_MODAL)
            initOwner(stage)
            //initStyle(StageStyle.Undecorated) //to remove windows border
            scene = new Scene {
                root = roots2
                stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
                }
            icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
            }
        
        //method so that when login dialog is closed, entire application is ended and closed
        dialog.onCloseRequest_=(e => {
            Platform.exit()
        }) //to close dialog window

        control.dialogStage = dialog
        dialog.showAndWait()

    control.loginClicked
    control.dialogStage.close()
    }

    //Function to show and display the registration dialog page
    def showRegistrationDialog(user : User) : Boolean = {
        val resource = getClass.getResourceAsStream("view/RegistrationDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[RegistrationDialogController#Controller]

        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "TuringTech User Register"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
        }
        control.dialogStage = dialog
        control.user = user
        dialog.showAndWait()
        control.confirmClicked
    } 
    
    //Function to show and display the order page
    def showOrderPage() = {
        val resource = getClass.getResourceAsStream("view/Order.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the dialog page for adding and editing order table items
    def showOrderEditDialog(order: Order) : Boolean = {
        val resource = getClass.getResourceAsStream("view/OrderEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[OrderEditDialogController#Controller]
        
        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "Add/Update Orders"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
        }
        
        control.dialogStage = dialog
        control.order = order
        dialog.showAndWait()
        control.confirmClicked
    }

    //Function to show and display the product page
    def showProductPage() = {
        val resource = getClass.getResourceAsStream("view/Product.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the dialog page for adding and editing product table items
    def showProductEditDialog(product: Product) : Boolean = {
        val resource = getClass.getResourceAsStream("view/ProductEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[ProductEditDialogController#Controller]

        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "Add/Update Products"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))

        }
        control.dialogStage = dialog
        control.product = product
        dialog.showAndWait()
        control.confirmClicked
    }
    //Function to show and display the product category page
    def showProductCategoryPage() = {
        val resource = getClass.getResourceAsStream("view/ProductCategory.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the dialog page for adding and editing product category table items
    def showProductCategoryEditDialog(productCategory: ProductCategory) : Boolean = {
        val resource = getClass.getResourceAsStream("view/ProductCategoryEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[ProductCategoryEditDialogController#Controller]

        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "Add/Update Categories"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))

        }
        control.dialogStage = dialog
        control.productCategory = productCategory
        dialog.showAndWait()
        control.confirmClicked
    }

    //Function to show and display the supplier page
    def showSupplierPage() = {
        val resource = getClass.getResourceAsStream("view/Supplier.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the dialog page for adding and editing supplier table items
    def showSupplierEditDialog(supplier: Supplier) : Boolean = {
        val resource = getClass.getResourceAsStream("view/SupplierEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[SupplierEditDialogController#Controller]

        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "Add/Update Suppliers"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
        }
        control.dialogStage = dialog
        control.supplier = supplier
        dialog.showAndWait()
        control.confirmClicked
    }

    //Function to show and display the user page
    def showUserPage() = {
        val resource = getClass.getResourceAsStream("view/User.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource)
        val roots = loader.getRoot[jfxs.layout.BorderPane]
        this.roots.setCenter(roots)
    }

    //Function to show and display the dialog page for adding and editing supplier table items
    def showUserEditDialog(user: User) : Boolean = {
        val resource = getClass.getResourceAsStream("view/UserEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent]
        val control = loader.getController[UserEditDialogController#Controller]

        val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
            root = roots2
            stylesheets = List(getClass.getResource("view/InventoryApp.css").toExternalForm())
        }
        title = "Add/Update Users"
        icons += new Image(getClass.getResourceAsStream("/images/smartphone-logo.png"))
        }
        control.dialogStage = dialog
        control.user = user
        dialog.showAndWait()
        control.confirmClicked
    }

    //calls the function which displays login dialog page when app starts
    showLoginDialog()
    
}