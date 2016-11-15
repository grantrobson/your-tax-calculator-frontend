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

package uk.gov.hmrc.yourtaxcalculator.connectors

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VersionCheckConnectorSpec extends UnitSpec with ScalaFutures {

  private trait Setup {
    implicit lazy val hc = HeaderCarrier()
    lazy val request: JsValue = Json.parse("""{"os":"android", "version":"1.0.1"}""")
    lazy val upgradeTrue: JsValue = Json.parse("""{"upgrade":true}""")

    lazy val http500Response = Future.failed(new InternalServerException("Kaboom!"))
    lazy val http400Response = Future.failed(new BadRequestException("Bad request"))
    lazy val http429Response = Future.failed(new HttpException("Too many requests", 429))
    lazy val http200Response = Future.successful(HttpResponse(200, Some(upgradeTrue)))

    lazy val response: Future[HttpResponse] = http400Response

    val connector = new VersionCheckConnector {

      override lazy val  customerProfileConnectorUrl = "theUrl"
      override lazy val http: HttpPost = new HttpPost {
        override val hooks: Seq[HttpHook] = NoneRequired

        override protected def doFormPost(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier): Future[HttpResponse] = response
        override protected def doPost[A](url: String, body: A, headers: Seq[(String, String)])(implicit wts: Writes[A], hc: HeaderCarrier): Future[HttpResponse] = response
        override protected def doPostString(url: String, body: String, headers: Seq[(String, String)])(implicit hc: HeaderCarrier): Future[HttpResponse] = response
        override protected def doEmptyPost[A](url: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = response
      }
    }
  }

  "VersionCheckConnector" should {

    "return the value returned by version check given a 200 response from the customer profile service" in new Setup {
      override lazy val response = http200Response

      val requiresUpgrade = connector.requiresUpgrade(request, hc).futureValue

      requiresUpgrade shouldBe true
    }

    "throw a Upstream4xxResponse given a 400 response from the customer profile service" in new Setup {
      override lazy val response = http400Response

      whenReady(connector.requiresUpgrade(request, hc).failed) {
        case e: Upstream4xxResponse  => e.message shouldBe "Bad request"
      }
    }

    "throw a Upstream5xxResponse given a 500 response from the customer profile service" in new Setup {
      override lazy val response = http500Response

      whenReady(connector.requiresUpgrade(request, hc).failed) {
        case e: Upstream5xxResponse  => e.message shouldBe "Kaboom!"
      }
    }

    "throw an InternalServerError given a failure from the customer profile service" in new Setup {
      override lazy val response = http429Response

      whenReady(connector.requiresUpgrade(request, hc).failed) {
        case e: InternalServerException => e.message shouldBe "Too many requests"
      }
    }
  }
}
