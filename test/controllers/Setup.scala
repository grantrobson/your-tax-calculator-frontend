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

import java.util.UUID

import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.yourtaxcalculator.connectors.VersionCheckConnector
import uk.gov.hmrc.yourtaxcalculator.domain.PreFlightCheckResponse
import uk.gov.hmrc.yourtaxcalculator.services.VersionCheckService

import scala.concurrent.{ExecutionContext, Future}

trait Setup {
  implicit val hc = HeaderCarrier()
  val journeyId = UUID.randomUUID
  val expectedJourneyId = journeyId.toString
  val emptyRequest = FakeRequest()
  val deviceRequest = FakeRequest().withBody(Json.parse("""{"os":"android", "version":"7.8.9"}"""))
    .withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")
  val maxAgeVersionCheckConfig:Long = 900
}

class TestVersionCheckService(updateRequired: Boolean, testJourneyId: Option[String]) extends VersionCheckService {
  override val connector = VersionCheckConnector
  override def preFlightCheck(inputRequest:JsValue, journeyId:Option[String])(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[PreFlightCheckResponse] = {
    val id = journeyId.orElse(testJourneyId)
    Future(PreFlightCheckResponse(updateRequired, id))
  }
}

class TaxCreditRenewalsSuccess extends Setup {

  val controller = new TaxCreditRenewals{}
}


class ThrowingVersionCheckService(exception: Exception) extends VersionCheckService {
  override val connector = VersionCheckConnector
  override def preFlightCheck(inputRequest:JsValue, journeyId:Option[String])(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[PreFlightCheckResponse] = {
    Future{ throw exception }
  }
}

trait VersionCheckControllerNoUpgradeRequired extends Setup {
  val controller = new VersionCheckController {
    override val service: VersionCheckService = new TestVersionCheckService(false, Option(expectedJourneyId))
    override val app: String = "TestVersionCheckController"
    override val maxAgeForVersionCheck = maxAgeVersionCheckConfig
  }
}

trait VersionCheckControllerUpgradeRequired extends Setup {
  val controller = new VersionCheckController {
    override val service: VersionCheckService = new TestVersionCheckService(true, None)
    override val app: String = "TestVersionCheckController"
    override val maxAgeForVersionCheck = maxAgeVersionCheckConfig
  }
}

trait VersionCheckControllerProblem extends Setup {
  val controller = new VersionCheckController {
    override val service: VersionCheckService = new ThrowingVersionCheckService(new Exception("something bad has happened..."))
    override val app: String = "TestVersionCheckController"
    override val maxAgeForVersionCheck = maxAgeVersionCheckConfig
  }
}

trait VersionCheckBadRequest extends Setup {
  val controller = new VersionCheckController {
    override val service: VersionCheckService = new ThrowingVersionCheckService(new BadRequestException("a really bad request"))
    override val app: String = "TestVersionCheckController"
    override val maxAgeForVersionCheck = maxAgeVersionCheckConfig
  }
}
