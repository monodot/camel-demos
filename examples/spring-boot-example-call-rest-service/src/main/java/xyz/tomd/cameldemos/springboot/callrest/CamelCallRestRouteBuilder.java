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
        // This route will first get the current GitHub user, and then add a Star
        from("timer:mytimer?repeatCount=1")
                .to("direct:getuser")
                .to("direct:addstar");


        // GET USER

        from("direct:getuser")
                // Set the body to null, as we're not posting anything to this API
                .setBody(simple("${null}"))

                // First let's just get the current user's info
                .to("http://api.github.com/user" +
                        "?httpMethod=GET" +
                        "&authMethod=Basic" +
                        "&authUsername={{github.username}}" +
                        "&authPassword={{github.token}}" +
                        "&authenticationPreemptive=true")

                .log("Response from the Get User operation was: ${body}");


        // ADD STAR

        from("direct:addstar")

                // Set up some parameters for our HTTP request
                .setHeader("repoOwner", constant("apache"))
                .setHeader("repoName", constant("camel"))

                // You could also set some query parameters here, if needed.
                //.setHeader(Exchange.HTTP_QUERY, constant("hello=true"))

                // Set the body to null, because it is required by the GitHub API.
                // But you can set the body here to anything you like.
                .setBody(simple("${null}"))

                // Now invoke the GitHub API with the null Body
                // PUT /user/starred/:owner/:repo
                // See: https://developer.github.com/v3/activity/starring/#star-a-repository
                // I'm using the 'toD' EIP, so that I can build the URL dynamically,
                // using the values of my repoOwner and repoName headers.
                // See: https://camel.apache.org/manual/latest/toD-eip.html
                .toD("https://api.github.com/user/starred/${header.repoOwner}/${header.repoName}" +
                        "?httpMethod=PUT" +
                        "&authMethod=Basic" +
                        "&authUsername={{github.username}}" +
                        "&authPassword={{github.token}}" +
                        "&authenticationPreemptive=true")

                .log("Response code from the Add Star operation was: ${header.CamelHttpResponseCode}");


    }
}
