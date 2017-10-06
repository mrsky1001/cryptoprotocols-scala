package akkahttp

/**
  * Created by Nikita on 06.10.2017.
  */

import spray.json.DefaultJsonProtocol._

final case class Item(name: String, id: Long)

final case class Order(items: List[Item])

object DomainEntitiesJsonFormats {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)
}