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

package uk.gov.hmrc.yourtaxcalculator.services

import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global

class VersionCheckServiceSpec  extends UnitSpec with WithFakeApplication with ScalaFutures {

  "VersionCheckService preFlightCheck" should {
    "return a new journeyId given a version that does not require an upgrade and no journeyId is provided" in new TestVersionCheckServiceNoUpgradeRequired {
      val result = service.preFlightCheck(request).futureValue

      result.upgradeRequired shouldBe false
      result.journeyId.isDefined shouldBe true
      result.journeyId shouldNot be (existingJourneyId)
    }

    "return the journeyId provided given a version that does not require an upgrade" in new TestVersionCheckServiceNoUpgradeRequired {
      val result = service.preFlightCheck(request, existingJourneyId).futureValue

      result.upgradeRequired shouldBe false
      result.journeyId shouldBe existingJourneyId
    }

    "not return a new journeyId given a version that does require an upgrade" in new TestVersionCheckServiceUpgradeRequired {
      val result = service.preFlightCheck(request).futureValue

      result.upgradeRequired shouldBe true
      result.journeyId shouldBe None
    }
  }
}
