package app.task

import java.util.UUID

import scala.concurrent.Future

trait TaskDao {

    def get(userId: Long): Future[Seq[Task]]

    def find(id: UUID): Future[Option[Task]]

    def add(t: Task): Future[Int]

    def remove(id: UUID, userId: Long): Future[Int]

    def clear(userId: Long): Future[Int]

    def updatePriority(id: UUID, priority: Option[Short]): Future[Int]

    def count(userId: Long): Future[Int]
}
