package io.scalac.wsakka.chat

import akka.actor.{ActorRef, Actor}

class ChatRoomActor extends Actor {

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))
      println(s"User $name joined channel")

    case UserLeft(name) =>
      println(s"User $name left channel")
      broadcast(SystemMessage(s"User $name left channel"))
      participants -= name

    case msg: IncomingMessage =>
      broadcast(msg)
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)

}
