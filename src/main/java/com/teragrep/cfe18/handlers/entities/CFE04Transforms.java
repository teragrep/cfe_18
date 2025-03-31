package com.teragrep.cfe18.handlers.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CFE04Transforms {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private int id;
    private int cfe04Id;
    private String name;
    private boolean writeMeta;
    private boolean writeDefault;
    private String defaultValue;
    private String destinationKey;
    private String regex;
    private String format;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCfe04Id() {
        return cfe04Id;
    }

    public void setCfe04Id(int cfe04Id) {
        this.cfe04Id = cfe04Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWriteMeta() {
        return writeMeta;
    }

    public void setWriteMeta(boolean writeMeta) {
        this.writeMeta = writeMeta;
    }

    public boolean isWriteDefault() {
        return writeDefault;
    }

    public void setWriteDefault(boolean writeDefault) {
        this.writeDefault = writeDefault;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDestinationKey() {
        return destinationKey;
    }

    public void setDestinationKey(String destinationKey) {
        this.destinationKey = destinationKey;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
