package com.teragrep.cfe18.handlers.entities;

public class ApplicationMeta {

    public String application;
    public String application_meta_key;
    public String application_meta_value;


    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication_meta_key() {
        return application_meta_key;
    }

    public void setApplication_meta_key(String application_meta_key) {
        this.application_meta_key = application_meta_key;
    }

    public String getApplication_meta_value() {
        return application_meta_value;
    }

    public void setApplication_meta_value(String application_meta_value) {
        this.application_meta_value = application_meta_value;
    }

    @Override
    public String toString() {
        return "ApplicationMeta{" +
                "application='" + application + '\'' +
                ", application_meta_key='" + application_meta_key + '\'' +
                ", application_meta_value='" + application_meta_value + '\'' +
                '}';
    }
}
