version-check
----
  Validate the mobile application version. The response to this service includes an upgrade status flag and, optionally, a Journey Id.

  The upgrade status is determined using the supplied POST data.
  
* **URL**

  `/tax-calc/version-check`

* **Method:**
  
  `POST`
  
*  **JSON**

Current version information of the application. The "os" attribute can be either ios, android or windows.

```json
{
    "os": "ios",
    "version" : "0.1.0"
}
```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 

```json
{
  "upgradeRequired": false,
  "journeyId": "c6a40291-ea84-432c-8381-25d9641e2166"
}
```

Please note that the "journeyId" attribute is optional, and will not be returned when an application upgrade is required.

* **Error Response:**

  In case of a server failure

  * **Code:** 500 INTERNAL_SERVER_ERROR <br/>
