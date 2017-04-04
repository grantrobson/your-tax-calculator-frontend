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

package controllers

import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import uk.gov.hmrc.yourtaxcalculator.binders.NativeOS
import uk.gov.hmrc.yourtaxcalculator.controllers._


class TaxCreditRenewalsSpec extends UnitSpec with WithFakeApplication with ScalaFutures{

  "TaxCreditRenewalsController" should {

    "return the debug HTML block when not in debug mode" in new TaxCreditRenewalsSuccess {
      val result = await(controller.buildRenewalsContent(NativeOS("ios"), Some(true))(emptyRequest))

      status(result) shouldBe 200
      contentAsString(result) should include ("debugShow")
    }

    "not return the debug HTML block when not in debug mode" in new TaxCreditRenewalsSuccess {
      val result = await(controller.buildRenewalsContent(NativeOS("ios"), Some(false))(emptyRequest))

      status(result) shouldBe 200
      contentAsString(result) should include ("debugHidden")
    }

  }


}
