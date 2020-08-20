package com.cleverbuilder.cameldemos.components;

import com.cleverbuilder.cameldemos.model.AlbumTrack;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

public class BindyCsvValidationTest extends CamelTestSupport {

    @Test
    public void testCsv() throws Exception {
        template.sendBody("direct:start",
                new File("src/test/data/album_tracks.csv"));
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                BindyCsvDataFormat bindy = new BindyCsvDataFormat(AlbumTrack.class);
//                BindyDataFormat bindy = new BindyDataFormat(AlbumTrack.class);
//                bindy.setClassType(AlbumTrack.class);

                from("direct:start")
                        .unmarshal(bindy)
                        .split().body()
                        .to("bean-validation:")
                        .to("log:myoutput")
                        .to("mock:output");

            }
        };
    }


}

