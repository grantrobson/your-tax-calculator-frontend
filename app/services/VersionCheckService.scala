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

package uk.gov.hmrc.yourtaxcalculator.services

import java.util.UUID

import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.yourtaxcalculator.connectors.VersionCheckConnector
import uk.gov.hmrc.yourtaxcalculator.domain.PreFlightCheckResponse

import scala.concurrent.{ExecutionContext, Future}

trait VersionCheckService {
  val connector: VersionCheckConnector

  def preFlightCheck(inputRequest:JsValue, journeyId: Option[String] = None)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[PreFlightCheckResponse] = {
    val existingOrNewId = journeyId.getOrElse(UUID.randomUUID().toString)
    connector.requiresUpgrade(inputRequest, hc)
      .map(Option(_).collect{ case false => existingOrNewId })
      .map{id => PreFlightCheckResponse(id.isEmpty, id)}
  }
}

object LiveVersionCheckService extends VersionCheckService {
  override val connector = VersionCheckConnector
}
