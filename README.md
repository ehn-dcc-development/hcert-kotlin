# Electronic Health Certificate Service Kotlin

Run the service with `./gradlew bootRun`, browse it at <http://localhost:8080/certservice>, fill in some data (use the provided data!) and click on "Generate COSE" to view the result.

## Endpoints

Get the certificate for verification at `/cert/{kid}`: Set your `Accept` to `text/plain` to get the certificate in Base64, or set it to `application/octet-stream` to get binary data.

## TODO

- Use the JSON schema for data classes
