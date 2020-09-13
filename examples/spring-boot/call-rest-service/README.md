# spring-boot-example-call-rest-service

Demonstrates how to invoke a REST service using Apache Camel, once using GET and once using POST.

This app uses the GitHub API to get your user info, and then [creates a private Gist][1] in your GitHub account.

Camel will start, then invoke the GitHub API to perform these 2 actions. You can then press Ctrl+C to terminate the app.

## To run

To run this demo, first [create a Personal Access Token][2] in your GitHub account and grant it some permissions to be able to modify your `user` object.

Then, set your GitHub auth details in environment variables and run the app:

    $ export GITHUB_USERNAME=xxxxx
    $ export GITHUB_TOKEN=yyyyy
    $ mvn clean spring-boot:run

Then, check [GitHub Gists][3] to see your newly created Gist, where you can also delete it.

[1]: https://docs.github.com/en/rest/reference/gists#create-a-gist
[2]: https://github.com/settings/tokens
[3]: https://gist.github.com