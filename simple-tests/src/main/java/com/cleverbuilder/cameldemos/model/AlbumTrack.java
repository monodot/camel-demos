package com.cleverbuilder.cameldemos.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", skipFirstLine = true)
public class AlbumTrack {

    @DataField(pos = 1)
    private String artist;

    @DataField(pos = 2)
    private String title;

    @DataField(pos = 3)
    private String remix;

    @DataField(pos = 4)
    private String length;


}