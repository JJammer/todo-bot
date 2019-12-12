package app.user

import slick.jdbc.PostgresProfile.api._

case class User(id: Long, name: String)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey)

  def name = column[String]("name")

  def * = (id, name).mapTo[User]
}
