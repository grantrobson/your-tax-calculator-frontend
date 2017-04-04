# your-tax-calculator-frontend

[![Build Status](https://travis-ci.org/hmrc/your-tax-calculator-frontend.svg)](https://travis-ci.org/hmrc/your-tax-calculator-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/your-tax-calculator-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/your-tax-calculator-frontend/_latestVersion)

  HMRC Tax Calculator Frontend - calculates income tax, as well as national insurance contributions for Employers and Employees
  
  This frontend project demonstrates the implementation of the tax calculator using scalaJS
    
* **URL**

  `/your-tax-calculator-frontend/calculator`
  
 API
 ---
 
 The frontend additionally exposes the following service:
 
 | *Task* | *Supported Methods* | *Description* |
 |--------|----|----|
 | ```/your-tax-calculator-frontend/version-check``` | POST | Validates the mobile application version [More...](docs/version-check.md) |
 | ```/tax-estimator/tax-credit-renewals/:os``` | GET | Return the tax-credit-renwals JS app [More...](docs/tax-credit-renewals.md) |

 
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")