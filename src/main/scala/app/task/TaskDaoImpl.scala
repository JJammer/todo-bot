package app.task

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskDaoImpl(db: Database) extends TaskDao {

    lazy val tasks = TableQuery[TaskTable]

    override def add(t: Task): Future[Int] = {db.run(tasks += t)}

    override def get(userId: Long): Future[Seq[Task]] = db.run(tasks.filter(_.userId === userId).result)

    override def find(id: UUID): Future[Option[Task]] = db.run(tasks.filter(_.id === id).result).map(_.headOption)

    override def remove(id: UUID, userId: Long): Future[Int] = db.run(tasks.filter(x => x.id === id && x.userId === userId).delete)

    override def clear(userId: Long): Future[Int] = db.run(tasks.filter(_.userId === userId).delete)

    override def updatePriority(id: UUID, priority: Option[Short]): Future[Int] =
        db.run(tasks.filter(_.id === id).map(_.priority).update(priority))

    override def count(userId: Long): Future[Int] = db.run(tasks.filter(_.userId === userId).length.result)
}
