package xyz.tomd.cameldemos.springboot.fileupload;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class FileUploadRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .host("localhost").port("8080")
                .contextPath("/services")
                .apiContextPath("/api-doc")
                .enableCORS(true);

        // This will configure a 'servlet:/upload' consumer
        rest("/upload")
                .description("File upload service")
                .consumes("text/plain")

            .put().description("Upload a file")
                .outType(String.class)
                .param().name("filename")
                    .type(RestParamType.header)
                    .description("The filename of the new file")
                    .dataType("string")
                    .endParam()
                .responseMessage().code(200).message("Upload OK").endResponseMessage()

                .route()
                .to("log:mylogger?showAll=true")
                .to("file:target/output?fileName=${header.filename}");
    }
}
