package io.scalac.wsakka.chat

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl._
import akka.stream.{FlowShape, OverflowStrategy}

class ChatRoom(actorSystem: ActorSystem) {

  private[this] val chatRoomActor = actorSystem.actorOf(Props[ChatRoomActor])

  def sendMessage(message: ChatMessage): Unit = chatRoomActor ! message

  def websocketFlow(user: String): Flow[Message, Message, Any] = ???

}

object ChatRoom {
  private var chatRoomOpt: Option[ChatRoom] = None

  def chatRoom()(implicit actorSystem: ActorSystem): ChatRoom = chatRoomOpt match {
    case Some(room) => room
    case None => chatRoomOpt = Some(new ChatRoom(actorSystem))
      chatRoomOpt.get
  }

}
