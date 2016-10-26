/*
 * Copyright 2016 HM Revenue & Customs
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

import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers}
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.yourtaxcalculator.services.{LiveVersionCheckService, VersionCheckService}

trait VersionCheckController extends BaseController with ErrorHandling {
  import scala.concurrent.ExecutionContext.Implicits.global

  val service: VersionCheckService

  def preFlightCheck(journeyId: Option[String] = None) = Action.async(BodyParsers.parse.json) {
    implicit request => {
      errorWrapper {
        service.preFlightCheck(request.body, journeyId).map {
          response => Ok(Json.toJson(response))
        }
      }
    }
  }
}

object LiveVersionCheckController extends VersionCheckController {
  override val service: VersionCheckService = LiveVersionCheckService
  override val app: String = "LiveVersionCheckController"
}
