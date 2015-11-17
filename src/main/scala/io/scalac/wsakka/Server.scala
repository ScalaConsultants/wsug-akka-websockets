package io.scalac.wsakka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Server extends App {

  implicit val actorSystem = ActorSystem("as-websockets")
  implicit val flowMaterializer = ActorMaterializer()

  val HOST = "localhost"
  val PORT = 8080

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http().bind(interface = HOST, PORT)

  val binding: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from {}", connection.remoteAddress)
      connection handleWith route
    }).run()

  println(s"Server is now online at http://$HOST:$PORT\n")

  //Execution context for Future processing
  import actorSystem.dispatcher

  //shutdown Hook
  scala.sys.addShutdownHook {
    println("Terminating...")
    binding
      .flatMap(_.unbind())
      .onComplete { _ =>
        flowMaterializer.shutdown()
        actorSystem.terminate()
      }
    Await.result(actorSystem.whenTerminated, 60 seconds)
    println("Terminated... Bye")
  }

  /**
    * Route of last chance
    */
  def catchAllRoute = complete("Welcome to websocket server!!")

  /**
    * Echo Websocket Route
    */
  def echoRoute = ???

  /**
    * Websocket chat route
    */
  def chatRoute = ???

  /**
    * Composite route for server logic
    */
  def route = catchAllRoute

}
