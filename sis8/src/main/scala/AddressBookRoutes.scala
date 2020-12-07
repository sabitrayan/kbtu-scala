package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import scala.concurrent.Future
import com.example.AddressBookRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import JsonFormats._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
class AddressBookRoutes(addressbookRegistry: ActorRef[AddressBookRegistry.Command])(implicit val system: ActorSystem[_]) {
  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getAddressBooks(): Future[AddressBooks] =
    addressbookRegistry.ask(GetAddressBooks)
  def getAddressBook(name: String): Future[GetAddressBookResponse] =
    addressbookRegistry.ask(GetAddressBook(name, _))
  def createAddressBook(addressbook: AddressBook): Future[ActionPerformed] =
    addressbookRegistry.ask(CreateAddressBook(addressbook, _))
  def deleteAddressBook(name: String): Future[ActionPerformed] =
    addressbookRegistry.ask(DeleteAddressBook(name, _))
  def updateAddressBook(name: String, user: AddressBook): Future[ActionUpdate] =
    addressbookRegistry.ask(UpdateAddressBook(user, name, _))

  val addressbookRoutes: Route =
  pathPrefix("address") {
    concat(
      pathEnd {
        concat(
          get {
            complete(getAddressBooks())
          },
          post {
            entity(as[AddressBook]) { addressbook =>
              onSuccess(createAddressBook(addressbook)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          })
      },

      path(Segment) { name =>
        concat(
          get {
            rejectEmptyResponse {
              onSuccess(getAddressBook(name)) { response =>
                complete(response.maybeAddressBook)
              }
            }
          },
          delete {
            onSuccess(deleteAddressBook(name)) { performed =>
              complete((StatusCodes.OK, performed))
            }
          },
          put {
            rejectEmptyResponse {
              entity(as[AddressBook]) { user =>
                onSuccess(updateAddressBook(name, user)) { response =>
                  complete((StatusCodes.OK), response.description)
                }
              }
            }
          }
        )
      }
    )
  }
}
