package com.inventory.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
//class that inherits from the productCategory class and holds information unique to phone category (discount and tax amount) 
class PhoneCategory(productCategoryIDI: Int, productCategoryNameS: String, descriptionS: String, discountD: Double = 20, taxD: Double = 6.5) extends ProductCategory(productCategoryIDI: Int, productCategoryNameS: String, descriptionS: String, discountD: Double, taxD: Double){
}