package com.homeaway.vacationrental

import java.io.{File, FileInputStream, InputStream, PrintWriter}
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.scaladsl.Source
import com.homeaway.vacationrental.RentalListingActor.{ActionPerformed, CreateRentalListing, DeleteListing, GetRentalListing}

import scala.io

/**
  * Created by arunankannan on 16/08/2018.
  */

// all plain object representing json listing
case class RentalListing(id: String = UUID.randomUUID().toString, contact: Contact, address: Address, location: Location)
case class Contact(phone: String, formattedPhone: String)
case class Address(address: String, postalCode: String, countryCode: String, city: String, state: String, country: String)
case class Location(lat: String, lng: String)

// main actor for CRUD operation with source
object RentalListingActor {

  final case class GetRentalListing(id:String)
  final case class CreateRentalListing(listing: RentalListing)
  final case class ActionPerformed(description: String)
  final case class DeleteListing(id: String)

  def props: Props = Props[RentalListingActor]
}

// main actor for CRUD operation with source - implementation
class RentalListingActor extends Actor with ActorLogging {

  var Rentals : List[RentalListing] = JsonRead.readJson

  override def receive: Receive = {
    case GetRentalListing(id) => sender() ! Rentals.find(_.id==id)

    case CreateRentalListing(listing) =>
      Rentals ::= listing
      JsonRead.writeJson(Rentals)
      sender() ! ActionPerformed(s"Listing ${listing.id} created.")

    case DeleteListing(id) =>
      Rentals = Rentals.filterNot(_.id == id)
      JsonRead.writeJson(Rentals)
      sender() ! ActionPerformed(s"Listing with ${id} deleted.")
  }
}


object JsonRead extends JsonSupport {
  import spray.json._

  def readJson : List[RentalListing] = {
    val is : InputStream = getClass.getResourceAsStream("/rentals.json")
    val jsonString = io.Source.fromInputStream(is).mkString
    is.close()
    jsonString.parseJson.convertTo[List[RentalListing]]
  }

  def writeJson(listings : List[RentalListing]) : Boolean = {
    val source = getClass.getResource("/rentals.json").getPath
    val printer = new PrintWriter(new File(source))
    printer.write(listings.toJson.prettyPrint)
    printer.close()
    true
  }
}