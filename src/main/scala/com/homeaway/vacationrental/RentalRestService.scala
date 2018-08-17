package com.homeaway.vacationrental

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * main class entry for rental api service
  *
  * Created by arunankannan on 16/08/2018.
  */
object RentalRestService extends App with RentalRoute {

  implicit val system: ActorSystem = ActorSystem("httpServer4RentalApi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val rentalListingActor: ActorRef = system.actorOf(RentalListingActor.props, "rentalListingActor")

  lazy val routes: Route = rentalListingRoute

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)

}
