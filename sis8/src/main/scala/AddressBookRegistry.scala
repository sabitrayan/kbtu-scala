package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

final case class AddressBook(name: String, age: Int, countryOfResidence: String)
final case class AddressBooks(addressbooks: immutable.Seq[AddressBook])

object AddressBookRegistry {
  sealed trait Command
  final case class GetAddressBooks(replyTo: ActorRef[AddressBooks]) extends Command
  final case class CreateAddressBook(addressbook: AddressBook, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetAddressBook(name: String, replyTo: ActorRef[GetAddressBookResponse]) extends Command
  final case class DeleteAddressBook(name: String, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class UpdateAddressBook(user: AddressBook, name: String, replyTo: ActorRef[ActionUpdate]) extends Command

  final case class GetAddressBookResponse(maybeAddressBook: Option[AddressBook])
  final case class ActionPerformed(description: String)
  final case class ActionUpdate(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(addressbooks: Set[AddressBook]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetAddressBooks(replyTo) =>
        replyTo ! AddressBooks(addressbooks.toSeq)
        Behaviors.same
      case CreateAddressBook(addressbook, replyTo) =>
        replyTo ! ActionPerformed(s"AddressBook ${addressbook.name} created.")
        registry(addressbooks + addressbook)
      case GetAddressBook(name, replyTo) =>
        replyTo ! GetAddressBookResponse(addressbooks.find(_.name == name))
        Behaviors.same
      case DeleteAddressBook(name, replyTo) =>
        replyTo ! ActionPerformed(s"AddressBook $name deleted.")
        registry(addressbooks.filterNot(_.name == name))
      case UpdateAddressBook(user, name,  replyTo) =>
        replyTo ! ActionUpdate(s"User $name has been updated")
        val temp_users = addressbooks.filterNot(_.name == name)
        registry(temp_users + user)
    }
}
