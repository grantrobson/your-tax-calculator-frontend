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

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpPost, HttpResponse}
import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.yourtaxcalculator.connectors.VersionCheckConnector

import scala.concurrent.{ExecutionContext, Future}


class TestVersionCheckConnector(requiresUpgrade: Boolean) extends VersionCheckConnector {
  override def customerProfileConnectorUrl: String = "someUrl"

  override def http: HttpPost = new HttpPost {
    private def notImplemented = {
      throw new RuntimeException("not implemented")
    }
    override protected def doPostString(url: String, body: String, headers: Seq[(String, String)])(implicit hc: HeaderCarrier): Future[HttpResponse] = notImplemented
    override protected def doFormPost(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier): Future[HttpResponse] = notImplemented
    override protected def doPost[A](url: String, body: A, headers: Seq[(String, String)])(implicit wts: Writes[A], hc: HeaderCarrier): Future[HttpResponse] = notImplemented
    override protected def doEmptyPost[A](url: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = notImplemented
    override val hooks: Seq[HttpHook] = NoneRequired
  }

  override def requiresUpgrade(inputRequest: JsValue, hc: HeaderCarrier)(implicit ec: ExecutionContext): Future[Boolean] =  {
    Future(requiresUpgrade)
  }
}

class TestVersionCheckService(versionCheckConnector: VersionCheckConnector) extends VersionCheckService {
  override val connector = versionCheckConnector
}

trait Setup {
  implicit val hc = HeaderCarrier()
  val newJourneyId = UUID.randomUUID().toString
  val existingJourneyId = Some("123-foo-456-bar")
  val emptyRequest = FakeRequest()
  val request: JsValue = Json.parse("""{"os":"unicorn", "version":"1.2.3"}""")
}

trait TestVersionCheckServiceNoUpgradeRequired extends Setup {
  val connector = new TestVersionCheckConnector(false)
  val service = new TestVersionCheckService(connector)
}

trait TestVersionCheckServiceUpgradeRequired extends Setup {
  val connector = new TestVersionCheckConnector(true)
  val service = new TestVersionCheckService(connector)
}
