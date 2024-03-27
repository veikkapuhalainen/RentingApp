package Classes

import scala.collection.mutable
 
case class Category(val categoryName: String):

  val subCategories = mutable.Buffer[Category]()

  def addSubCategories(categoryName: String): Unit =
    this.subCategories += Category(categoryName)

  override def toString: String = this.categoryName
end Category
