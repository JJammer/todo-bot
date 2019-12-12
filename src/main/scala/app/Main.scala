package app

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


object Main extends App {
    // To run spawn the bot
    val bot = new TodoBot(sys.env("BOT_TOKEN"))
    val eol = bot.run()
    println("Press [ENTER] to shutdown the bot, it may take a few seconds...")
    scala.io.StdIn.readLine()
    bot.shutdown() // initiate shutdown
    // Wait for the bot end-of-life
    Await.result(eol, Duration.Inf)
}
