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

package uk.gov.hmrc.yourtaxcalculator.controllers

import play.api.Play
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.yourtaxcalculator.domain.PreFlightCheckResponse
import uk.gov.hmrc.yourtaxcalculator.services.{LiveVersionCheckService, VersionCheckService}

trait VersionCheckController extends FrontendController with ErrorHandling {

  val service: VersionCheckService
  val maxAgeForVersionCheck: Long

  def preFlightCheck(journeyId: Option[String] = None) = Action.async(BodyParsers.parse.json) {
    implicit request => {
      errorWrapper {
        def buildResponse(response:PreFlightCheckResponse) = Ok(Json.toJson(response))

        service.preFlightCheck(request.body, journeyId).map {
          response => if (!response.upgradeRequired) addCacheHeader(maxAgeForVersionCheck, buildResponse(response)) else buildResponse(response)
        }
      }
    }
  }

  def addCacheHeader(maxAge:Long, result:Result):Result = {
    result.withHeaders(HeaderNames.CACHE_CONTROL -> s"max-age=$maxAge")
  }
}

trait ConfigLoad {
  val versionCheckMaxAge = "version.check.maxAge"
  def getConfigForVersionCheckMaxAge:Option[Long]

  lazy val maxAgeForVersionCheck: Long = getConfigForVersionCheckMaxAge
    .getOrElse(throw new Exception(s"Failed to resolve config key $versionCheckMaxAge"))
}

object LiveVersionCheckController extends VersionCheckController with ConfigLoad {
  override val service: VersionCheckService = LiveVersionCheckService
  override val app: String = "LiveVersionCheckController"

  override def getConfigForVersionCheckMaxAge: Option[Long] = Play.current.configuration.getLong(versionCheckMaxAge)
}
