package com.kvelinskiy.ua.statisticsAutomation.helper.uploadingfiles;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String locationDocx = "upload-dir";

    public String getLocationDocx() {
        return locationDocx;
    }

    public void setLocationDocx(String locationDocx) {
        this.locationDocx = locationDocx;
    }
}
