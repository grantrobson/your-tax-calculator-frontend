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

package uk.gov.hmrc.yourtaxcalculator.connectors

import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.yourtaxcalculator.config.WSHttp

import scala.concurrent.{ExecutionContext, Future}

trait VersionCheckConnector {

  def customerProfileConnectorUrl: String

  def http: CorePost

  def requiresUpgrade(inputRequest: JsValue, hc: HeaderCarrier)(implicit ec: ExecutionContext): Future[Boolean] =  {
    implicit val hcHeaders = hc.withExtraHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

    http.POST[JsValue, JsValue](s"$customerProfileConnectorUrl/profile/native-app/version-check", inputRequest)
      .map(r => (r \ "upgrade").as[Boolean])
      .recover {
        case e:BadRequestException =>
          Logger.error(s"BadRequestException processing /profile/native-app/version-check: ${e.getMessage}", e)
          throw Upstream4xxResponse(e.message, e.responseCode, e.responseCode)
        case e:InternalServerException =>
          Logger.error(s"InternalServerException processing /profile/native-app/version-check: ${e.getMessage}", e)
          throw Upstream5xxResponse(e.message, e.responseCode, e.responseCode)
        case e:Exception =>
          Logger.error(s"Exception processing /profile/native-app/version-check: ${e.getMessage}", e)
          throw new InternalServerException(e.getMessage)
      }
  }
}

object VersionCheckConnector extends  VersionCheckConnector with ServicesConfig {
  override lazy val customerProfileConnectorUrl = baseUrl("customer-profile")

  override lazy val http = WSHttp
}
