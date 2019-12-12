package app.user

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class UserDaoImpl(db: Database) extends UserDao {

  lazy val users = TableQuery[UserTable]

  override def add(user: User): Future[Int] = db.run(users += user)
}
