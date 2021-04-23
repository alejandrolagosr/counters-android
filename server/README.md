# Cornershop Android Development Test

## Before you begin
You will need to create a private GitHub repository using the information that we provided in this README and invite as collaborators: @DevPicon @jhonnyx2012 and @coyarzun89. The correct collaborators will be confirmed in the email that contains this file.

If you have any questions, please reach joaquin.munoz@cornershopapp.com or cristopher@cornershopapp.com. Specially if they are related to UI design.

## The test
Create an Android app for counting things. You'll need to meet high expectations for quality and functionality. It must meet at least the following:

* Add a named counter to a list of counters.
* Increment any of the counters.
* Decrement any of the counters.
* Delete a counter.
* Show a sum of all the counter values.
* Search counters.
* Enable sharing counters.
* Handle batch deletion.
* Persist data back to the server.
* States are important, consider handling each state transition properly.
* Must **not** feel like a learning exercise. Think you're building it to publish for the Google Play Store.

#### Build this app using the following spec: https://www.figma.com/file/qBcG5Poxunyct1HEyvERXN/Counters-for-Android

Some other important notes:

* Showing off the knowledge of mobile architectures is essential.
* Offer support to Android API >= 21.
* Unreliable networks are a thing. Error handling is **expected**.
* The app should persist the counter list if the network is not available (i.e Airplane Mode).
* Create incremental commits instead of a single commit with the whole project
* **Test your app to the latest Android API**

Bonus points:

* Don't bloat your Activities/Fragments.
* Minimal use of external dependencies.
* Tests are good: Unit, Instrumented, and UI. 
* Handle orientation changes.


**Remember**: The UI is super important. Don't build anything that doesn't feel right for Android.


## Install and start the server

```
$ npm install
$ npm start
```

## API endpoints / examples

> The following endpoints are expecting a `Content-Type: application/json`

```
GET /api/v1/counters
# []

POST {title: "bob"} /api/v1/counter
# [
#   {id: "asdf", title: "bob", count: 0}
# ]

POST {title: "steve"} /api/v1/counter
# [
#   {id: "asdf", title: "bob", count: 0},
#   {id: "qwer", title: "steve", count: 0}
# ]

POST {id: "asdf"} /api/v1/counter/inc
# [
#   {id: "asdf", title: "bob", count: 1},
#   {id: "qwer", title: "steve", count: 0}
# ]

POST {id: "qwer"} /api/v1/counter/dec
# [
#   {id: "asdf", title: "bob", count: 1},
#   {id: "qwer", title: "steve", count: 2}
# ]

DELETE {id: "qwer"} /api/v1/counter
# [
#   {id: "asdf", title: "bob", count: 1}
# ]

GET /api/v1/counters
# [
#   {id: "asdf", title: "bob", count: 1},
# ]
```

> **NOTE:* Each request returns the current state of all counters.

