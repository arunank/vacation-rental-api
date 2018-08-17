# Vacation Rental Listing API

Tech stack used
    - Scala
    - Akka
    - Akka stream
    - Actor system
    - Spray JSON
    - ScalaTest

## Instruction to run this api server

Prerequisites
    - SBT
    - Scala

Unzip "vacation-rental-api"

From root project folder 'vacation-rental-api'
On commond prompt ->
$sbt run

RentalRestService is main class

Assumptions:
lat, longitude assumed as string for convenience - validation differed to later stage
List of RentalListing is store, though storage not necessary for test, have used simple file as storage for test and dev convenience.

Example:
vacation-rental-api arunankannan$ sbt run
[info] Loading project definition from /Users/arunankannan/src/vacation-rental-api/project
[info] Loading settings for project vacation-rental-api from build.sbt ...
[info] Set current project to vacation-rental-api (in build file:/Users/arunankannan/src/vacation-rental-api/)
[info] Running com.homeaway.vacationrental.RentalRestService
Server online at http://localhost:8080/

Access URL:

GET - On any browser
http://localhost:8080/listings/7e22a83a-6f4f-11e6-8b77-86f30ca893d3

POST
- please refer test case RentalRouteSpec

DELETE
- please refer test case RentalRouteSpec

