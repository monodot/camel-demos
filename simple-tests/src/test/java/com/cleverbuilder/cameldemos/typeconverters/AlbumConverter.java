package com.cleverbuilder.cameldemos.typeconverters;

import com.cleverbuilder.cameldemos.transform.JsonToJaxbTest;
import org.apache.camel.Converter;

import java.util.Map;

/**
 * A TypeConverter to be used with Camel's convertBodyTo()
 */
@Converter
public class AlbumConverter {

    @Converter
    public static JsonToJaxbTest.Album toAlbum(Map input) {
        JsonToJaxbTest.Album album = new JsonToJaxbTest.Album();

        album.setArtist((String) input.get("artist"));
        album.setTitle((String) input.get("title"));
        album.setRating((String) input.get("rating"));
        album.setAccolades((String) input.get("accolades"));

        return album;
    }

}
