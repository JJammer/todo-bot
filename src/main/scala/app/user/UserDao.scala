package app.user

import scala.concurrent.Future

trait UserDao {

  def add(user: User): Future[Int]
}
