package app.task

import java.util.UUID

import slick.jdbc.PostgresProfile.api._

case class Task(id: UUID, title: String, priority: Option[Short], userId: Option[Long])

final class TaskTable(tag: Tag) extends Table[Task](tag, "task") {
  def id = column[UUID]("id", O.PrimaryKey)

  def title = column[String]("title")

  def priority = column[Option[Short]]("priority")

  def userId = column[Option[Long]]("user_id")

  def * = (id, title, priority, userId).mapTo[Task]
}
