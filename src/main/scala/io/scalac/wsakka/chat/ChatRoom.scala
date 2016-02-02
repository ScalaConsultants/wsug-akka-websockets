package io.scalac.wsakka.chat

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl._
import akka.stream.{FlowShape, OverflowStrategy}

class ChatRoom(actorSystem: ActorSystem) {

  private[this] val chatRoomActor = actorSystem.actorOf(Props[ChatRoomActor])

  def websocketFlow(user: String): Flow[Message, Message, Any] =
    Flow.fromGraph(

      FlowGraph.create(Source.actorRef[ChatMessage](bufferSize = 5, OverflowStrategy.fail)) {
        implicit builder =>
          chatSource =>

            import FlowGraph.Implicits._

            //input flow, all Messages
            val fromWebsocket = builder.add(
              Flow[Message].collect {
                case TextMessage.Strict(txt) => IncomingMessage(user, txt)
              })

            //          //output flow, it returns Message's
            val backToWebsocket = builder.add(
              Flow[ChatMessage].map {
                case ChatMessage(author, text) => TextMessage(s"[$author]: $text")
              }
            )

            //          //send messages to the actor, if send also UserLeft(user) before stream completes.
            val chatActorSink = Sink.actorRef[ChatEvent](chatRoomActor, UserLeft(user))

            //          //merges both pipes
            val merge = builder.add(Merge[ChatEvent](2))

            //          //Materialized value of Actor who sit in chatroom
            val actorAsSource = builder.materializedValue.map(actor => UserJoined(user, actor))

            //          //Message from websocket is converted into IncommingMessage and should be send to each in room
            fromWebsocket ~> merge.in(0)

            //          //If Source actor is just created should be send as UserJoined and registered as particiant in room
            actorAsSource ~> merge.in(1)

            //          //Merges both pipes above and forward messages to chatroom Represented by ChatRoomActor
            merge ~> chatActorSink

            //          //Actor already sit in chatRoom so each message from room is used as source and pushed back into websocket
            chatSource ~> backToWebsocket

            //          // expose ports
            FlowShape(fromWebsocket.inlet, backToWebsocket.outlet)
      }

    )


}

object ChatRoom {
  private var chatRoomOpt: Option[ChatRoom] = None

  def chatRoom()(implicit actorSystem: ActorSystem): ChatRoom = chatRoomOpt match {
    case Some(room) => room
    case None => chatRoomOpt = Some(new ChatRoom(actorSystem))
      chatRoomOpt.get
  }

}
