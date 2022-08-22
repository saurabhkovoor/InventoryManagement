package com.inventory.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
//class that inherits from the productCategory class and holds information unique to laptop category (discount and tax amount)
class LaptopCategory(productCategoryIDI: Int, productCategoryNameS: String, descriptionS: String, discountD: Double= 14, taxD: Double= 5.5) extends ProductCategory(productCategoryIDI: Int, productCategoryNameS: String, descriptionS: String, discountD: Double, taxD: Double){
}