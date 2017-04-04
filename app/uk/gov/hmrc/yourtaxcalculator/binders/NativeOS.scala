/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.yourtaxcalculator.binders

import play.api.mvc.PathBindable

case class NativeOS(os:String)

object NativeOS {
  val ios = "ios"
  val android = "android"
  val windows = "windows"

  val validOS: Seq[String] = Seq(ios, android, windows)

  def valid(os:String): Boolean = validOS.contains(os)
  def checkOS(osId:String): Either[String, NativeOS] = if (valid(osId)) Right(NativeOS(osId)) else Left("Invalid OS!")

  implicit def pathBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[NativeOS] {

    override def bind(key: String, value: String): Either[String, NativeOS] = {
      for {
        osId <- stringBinder.bind(key, value).right
        nativeOS <- checkOS(osId).right
      } yield nativeOS
    }

    override def unbind(key: String, nativeOS: NativeOS): String = {
      nativeOS.os
    }
  }
}

