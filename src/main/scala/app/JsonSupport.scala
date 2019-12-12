package app

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import app.task.Task
import app.user.User
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
        def write(x: UUID) = JsString(x.toString) //Never execute this line
        def read(value: JsValue) = value match {
            case JsString(x) => UUID.fromString(x)
            case x           => throw DeserializationException("Expected UUID as JsString, but got " + x)
        }
    }

    implicit val taskJsonFormat4: RootJsonFormat[Task] = jsonFormat4(Task.apply)
    implicit val userJsonFormat2: RootJsonFormat[User] = jsonFormat2(User.apply)
}

