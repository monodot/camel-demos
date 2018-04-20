package com.cleverbuilder.cameldemos.beans;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleFileFilter<T> implements GenericFileFilter<T> {

    @Override
    public boolean accept(GenericFile<T> genericFile) {
        Pattern pattern = Pattern.compile(".*\\.csv");
        Matcher matcher = pattern.matcher(genericFile.getFileName());
        return matcher.find() ? true : false;
    }
}
