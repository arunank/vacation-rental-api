package com.homeaway.vacationrental


import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.homeaway.vacationrental.RentalRestService.system
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by arunankannan on 17/08/2018.
  */
class RentalRouteSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with RentalRoute {

  override val rentalListingActor: ActorRef =  system.actorOf(RentalListingActor.props, "rentalListingActor")

  lazy val routes = rentalListingRoute

  "rental listing Route" should {

    "return 1 user if  present (GET /listings/id)" in {

      val request = HttpRequest(uri = "/listings/7e22a83a-6f4f-11e6-8b77-86f30ca893d3")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"id":"7e22a83a-6f4f-11e6-8b77-86f30ca893d3","contact":{"phone":"35126841100","formattedPhone":"+1 512-684-1100"},"address":{"city":"Austin","state":"TX","country":"United States","postalCode":"1011","countryCode":"US","address":"1011 W 5th St"},"location":{"lat":"40.4255485534668","lng":"-3.7075681686401367"}}""")
      }
    }

    "be able to add listing (POST /listings)" in {
      val listing = RentalListing(contact = Contact("4", "+4"), address = Address("1", "2", "3", "4", "5", "6"), location = Location("1", "2"))
      val listingEntity = Marshal(listing).to[MessageEntity].futureValue

      // using the RequestBuilding DSL:
      val request = Post("/listings").withEntity(listingEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should (startWith("""{"description":"Listing"""))
        entityAs[String] should (endWith("""created."}"""))

      }
    }

    "be able to remove listing (DELETE /listings)" in {
      val request = Delete(uri = "/listings/7e22a83a-6f4f-11e6-8b77-86f30ca893d3")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)


        entityAs[String] should (startWith("""{"description":"Listing"""))
        entityAs[String] should (endWith("""deleted."}"""))      }
    }

  }

}
