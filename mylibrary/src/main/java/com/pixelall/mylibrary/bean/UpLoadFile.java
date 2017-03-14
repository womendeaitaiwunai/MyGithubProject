package com.pixelall.mylibrary.bean;

import java.io.File;

/**
 * Created by lxl on 2017/3/4.
 */

public class UpLoadFile {
    private String fileName;
    private String upName;
    private File upFile;

    public File getUpFile() {
        return upFile;
    }

    public void setUpFile(File upFile) {
        this.upFile = upFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUpName() {
        return upName;
    }

    public void setUpName(String upName) {
        this.upName = upName;
    }

}
