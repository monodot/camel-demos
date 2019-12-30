# spring-boot-example-call-rest-service

Demonstrates how to invoke a REST service using Apache Camel.

This will use the GitHub API to add the `apache/camel` repository to your [Starred repositories][1].

Camel will start, invoke the GitHub API and star the repository. You can then press Ctrl+C to terminate the app.

## To run

To run this demo, first [create a Personal Access Token][2] in your GitHub account and grant it some permissions to be able to modify your account.

Then, run the app, replacing the `XXX`s with your GitHub credentials:

    $ mvn clean spring-boot:run \
        -Dspring-boot.run.arguments=--github.username=XXX,--github.token=XXX

[1]: https://developer.github.com/v3/activity/starring/#star-a-repository
[2]: https://github.com/settings/tokens
