package xyz.tomd.cameldemos.springboot.callrest;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelCallRestRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        // We need something to trigger this route which calls the REST API.
        // So I'm using a Timer, which is triggered only once.
        // In your application, you might use a different component
        // at the start of the route, such as a File component or Direct component.
        // This route will get the current GitHub user, and then star a repo
        from("timer:mytimer?repeatCount=1")
                .to("direct:get-user")
                .to("direct:create-gist");


        // GET USER

        from("direct:get-user")
                .log("Getting user info from GitHub...")
                // Set the body to null, as we're not posting anything to this API
                .setBody(simple("${null}"))

                // First let's just get the current user's info
                .to("https://api.github.com/user" +
                        "?httpMethod=GET" +
                        "&authMethod=Basic" +
                        "&authUsername={{github.username}}" +
                        "&authPassword={{github.token}}" +
                        "&authenticationPreemptive=true")

                .log("Response from the Get User operation was: ${body}");


        // ADD STAR

        from("direct:create-gist")
                .log("Creating a Gist on GitHub...")

                // Set the body to null, because it is required by the GitHub API.
                // But you can set the body here to anything you like.

                // This is the content of our Gist.
                // See: https://docs.github.com/en/rest/reference/gists#create-a-gist
                .transform(simple("{\"files\":{\"README.md\":{\"content\":\"Hello from Camel!\"}}}"))

                // Now invoke the GitHub API with the Gist JSON in the body
                // POST /gists
                .to("https://api.github.com/gists" +
                        "?httpMethod=POST" +
                        "&authMethod=Basic" +
                        "&authUsername={{github.username}}" +
                        "&authPassword={{github.token}}" +
                        "&authenticationPreemptive=true")

                .log("Response code from the Create Gist operation was: ${header.CamelHttpResponseCode}")
                .log("Response body from the Create Gist operation was: ${body}");


    }
}
