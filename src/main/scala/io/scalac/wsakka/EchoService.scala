package io.scalac.wsakka

import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.Flow

object EchoService {
  def route: Route = path("ws-echo") {
    get {
      handleWebsocketMessages(echoService)
    }
  }

  val echoService: Flow[Message, Message, Any] = Flow[Message].map {
    case TextMessage.Strict(txt) => TextMessage("ECHO: " + txt)
    //BinaryMessage? :)
  }
}
