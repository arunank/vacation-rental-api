package com.homeaway.vacationrental

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.homeaway.vacationrental.RentalListingActor.{ActionPerformed, GetRentalListing}

import scala.concurrent.Future
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.server.Directives._

/**
  * Created by arunankannan on 16/08/2018.
  */


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import akka.pattern.ask

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val contactFormat = jsonFormat2(Contact)
  implicit val addressFormat = jsonFormat6(Address)
  implicit val locationFormat = jsonFormat2(Location)
  implicit val listingFormat = jsonFormat4(RentalListing)
  implicit val actionFormat = jsonFormat1(ActionPerformed)
}

trait RentalRoute extends JsonSupport {

  implicit def system: ActorSystem
  import com.homeaway.vacationrental.RentalListingActor._

  lazy val log = Logging(system, classOf[RentalRoute])

  def rentalListingActor : ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  // GET, POST, DELETE API for rental listing

  def rentalListingRoute(implicit mat: Materializer): Route = {
    pathPrefix("listings"){
      concat(
        pathEnd {
          concat(
            post {
              entity(as[RentalListing]) { rentalListing: RentalListing =>
                val rentalListingCreated: Future[ActionPerformed] =
                  (rentalListingActor ? CreateRentalListing(rentalListing)).mapTo[ActionPerformed]
                onSuccess(rentalListingCreated) { performed =>
                  log.info("Created listing [{}]: {}", rentalListing.id)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { id =>
          println(s"ak->$id")
          concat(
            get {
              val futureListing : Future[Option[RentalListing]] =
                            (rentalListingActor ? GetRentalListing(id))
                                .mapTo[Option[RentalListing]]
              rejectEmptyResponse {
                complete(futureListing)
              }
            },
            delete {
              val rentalListingDeleted: Future[ActionPerformed] =
                (rentalListingActor ? DeleteListing(id)).mapTo[ActionPerformed]

              onSuccess(rentalListingDeleted) { performed =>
                log.info("Deleted listing [{}]: {}", id, performed.description)
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
  }

}

