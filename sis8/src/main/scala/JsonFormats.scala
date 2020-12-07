package com.example

import com.example.AddressBookRegistry.ActionPerformed

import spray.json.DefaultJsonProtocol._

object JsonFormats  {
  implicit val userJsonFormat = jsonFormat3(AddressBook)
  implicit val usersJsonFormat = jsonFormat1(AddressBooks)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
