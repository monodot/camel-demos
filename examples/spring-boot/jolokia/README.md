# spring-boot-example-jolokia

Demonstrates using Jolokia to monitor your Camel on Spring Boot app. A couple of things to note:

- This demo has `hawtio.authenticationEnabled` set to `false`. Don't do this in production obviously!

## To run

To run this demo:

    $ mvn clean spring-boot:run

Then you can access Jolokia using:

    $ curl http://localhost:8081/actuator/jolokia

Or using httpie:

    $ http -b GET http://localhost:8081/actuator/jolokia

You can use [HTTP GET requests to Jolokia][jolokia-get] to fetch stats. For example, to get all stats for all routes in the Camel Context:

    $ curl http://localhost:8081/actuator/jolokia/read/org.apache.camel:context\=\*,type\=routes,name\=\*

This will give you stats on the two routes:

    {
      "request": {
        "mbean": "org.apache.camel:context=*,name=*,type=routes",
        "type": "read"
      },
      "value": {
        "org.apache.camel:context=camel-1,name=\"TimerRoute1sec\",type=routes": {
          "StatisticsEnabled": true,
          "CamelManagementName": "camel-1",
          "EndpointUri": "timer://mytimer?period=1000",
          "ExchangesCompleted": 2162,
          ...
      },
      "org.apache.camel:context=camel-1,name=\"TimerRoute5sec\",type=routes": {
        "StatisticsEnabled": true,
        "CamelManagementName": "camel-1",
        "EndpointUri": "timer://mytimer2?period=5000",
        "ExchangesCompleted": 433,
        ...
      },
      "timestamp": 1573983712,
      "status": 200
    }

[jolokia-get]: https://jolokia.org/reference/html/protocol.html#get-request

