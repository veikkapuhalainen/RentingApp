package Classes

/**
 * Class for categories
 * @param categoryName name of category
 */
case class Category(categoryName: String):
  
  override def toString: String = this.categoryName
end Category