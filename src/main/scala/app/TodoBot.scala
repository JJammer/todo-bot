package app

import java.util.UUID

import app.task.{Task, TaskDao, TaskDaoImpl}
import app.user.{User, UserDaoImpl}
import cats.instances.future._
import cats.syntax.functor._
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.{Callbacks, Commands}
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.models.{InlineKeyboardButton, InlineKeyboardMarkup}
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class TodoBot(token: String) extends TelegramBot
  with Polling
  with Commands[Future]
  with Callbacks[Future] {

  implicit val backend: SttpBackend[Future, Nothing] = OkHttpFutureBackend()
  override val client: RequestHandler[Future] = new FutureSttpClient(token)

  val db = Database.forConfig("database")
  val tasks: TaskDao = new TaskDaoImpl(db)
  val users = new UserDaoImpl(db)

  onCommand("start") { implicit msg =>
    (msg.from.map(_.id), msg.from.flatMap(_.username)) match {
      case (Some(id), Some(username)) => users.add(User(id, username)).void
      case _ => reply("Go Away Usernameless Slug").void
    }
  }

  onCommand("show") { implicit msg =>
    msg.from.map(_.id) match {
      case Some(id) =>
        tasks.get(id)
          .flatMap(tasks =>
            reply(
              tasks.sortBy(_.priority)
                .map(x => s"${x.priority.getOrElse("No priority")} - ${x.title}").mkString("\n")
            )
          ).andThen {
          case Failure(e) => Logger.log.error(s"Show error for user $id", e)
        }.void
      case _ => {
        Logger.log.error("Can't complete show operation: No user id")
        reply("Operation is no successful").void
      }
    }
  }

  onCommand("add") { implicit msg =>
    withArgs { args =>
      val title = args.mkString(" ")
      val uuid = UUID.randomUUID()
      tasks.count(msg.from.map(_.id).get).flatMap { n =>
          if (n >= 6)
            reply("Woah...You already have 6 tasks to do")
          else {
            tasks.add(Task(uuid, title, None, msg.from.map(_.id)))
              .andThen {
                case Failure(e) => Logger.log.error(s"Can't add task for user ${msg.from.map(_.id)} ", e)
              }
            reply(text = "Please choose the priority of your task", replyMarkup = Some(priorityButtons(uuid.toString)))
          }
      }.void
    }
  }

  onCommand("clear") { implicit msg =>
    tasks.clear(msg.from.map(_.id).get)
      .andThen { case Failure(e) =>
        Logger.log.error(s"Clear error for user ${msg.from.map(_.id)}:", e)
      }
      .void
  }

  onCommand("remove") { implicit msg =>
    withArgs { args =>
      tasks.remove(UUID.fromString(args.head), msg.from.map(_.id).get)
        .andThen { case Failure(e) =>
          Logger.log.error(s"Remove error for user ${msg.from.map(_.id)}:", e)
        }
        .void
    }
  }


  def priorityButtons(uuid: String): InlineKeyboardMarkup =
    InlineKeyboardMarkup.singleRow(
      for (i <- 1 to 6)
        yield InlineKeyboardButton.callbackData(i.toString, i.toString + " " + uuid)
    )


  onCallbackQuery {
    implicit cbq =>
      cbq.data match {
        case Some(data) =>
          val priority = data.charAt(0).asDigit.toShort
          val uuid = UUID.fromString(data.substring(2))

          tasks.get(cbq.from.id) onComplete {
            case Success(taskList) =>
              if (taskList.flatMap(_.priority).contains(priority))
                ackCallback(Some("Sorry, this priority is already chosen. Please choose another one"))
              else
                tasks.updatePriority(uuid, Some(priority))
                  .andThen {
                    case Failure(e) => Logger.log.error(s"Change priority for task $uuid", e)
                  }

            case Failure(e) => {
              Logger.log.error("Priority error", e)
              ackCallback(Some(s"Something went wrong"))
            }
          }
          Future.successful()
        case _ =>
          ackCallback(Some(s"Something went wrong")).void
      }
  }

}

object Logger extends StrictLogging {
  val log = logger
}