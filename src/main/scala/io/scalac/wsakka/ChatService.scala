package io.scalac.wsakka

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import io.scalac.wsakka.chat.ChatRoom

object ChatService {

  def route(implicit actorSystem: ActorSystem): Route = pathPrefix("ws-chat") {
    parameter('name) { userName =>
      handleWebsocketMessages(ChatRoom.chatRoom().websocketFlow(userName))
    }
  }
}
