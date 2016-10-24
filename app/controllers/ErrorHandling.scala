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
import play.api.{Logger, mvc}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.api.controllers.{ErrorInternalServerError, ErrorResponse}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object ErrorBadRequest extends ErrorResponse(400, "BAD_REQUEST" ,"Invalid Request")
case object ErrorTaxCalculatorConfig extends ErrorResponse(500, "INTERNAL_SERVER_ERROR" ,"Error Tax Calculator Config")

class BadRequestException(message:String) extends uk.gov.hmrc.play.http.HttpException(message, 400)
class TaxCalculatorConfigException(message: String) extends uk.gov.hmrc.play.http.HttpException(message, 500)

trait ErrorHandling {
  self: BaseController =>
  val app:String

  def log(message:String) = Logger.info(s"$app $message")

  def errorWrapper(func: => Future[mvc.Result])(implicit hc: HeaderCarrier) = {
    func.recover {
      case ex:BadRequestException =>
        log("BadRequest!")
        Status(ErrorBadRequest.httpStatusCode)(Json.toJson(ErrorBadRequest))
      case ex: TaxCalculatorConfigException =>
        Logger.error(s"TaxCalculatorConfigException : ${ex.getMessage}", ex)
        Status(ErrorTaxCalculatorConfig.httpStatusCode)(Json.toJson(ErrorTaxCalculatorConfig))
      case e: Throwable =>
        Logger.error(s"$app Internal server error: ${e.getMessage}", e)
        Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }
}
