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

import play.api.{Logger, mvc}
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

class TaxCalculatorConfigException(message: String) extends uk.gov.hmrc.http.HttpException(message, 500)

trait ErrorHandling {
  self: FrontendController =>
  val app:String

  def log(message:String) = Logger.info(s"$app $message")

  def errorWrapper(func: => Future[mvc.Result])(implicit hc: HeaderCarrier) = {
    func.recover {
      case e:BadRequestException =>
        log("BadRequest")
        BadRequest
      case e:Upstream4xxResponse =>
        log("UpstreamBadRequest")
        BadRequest
      case ex: TaxCalculatorConfigException =>
        Logger.error(s"TaxCalculatorConfigException : ${ex.getMessage}", ex)
        InternalServerError
      case e: Throwable =>
        Logger.error(s"$app Internal server error: ${e.getMessage}", e)
        InternalServerError
    }
  }
}
