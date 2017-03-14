package com.pixelall.mylibrary.okhttp;

import com.pixel.photostudiob.bean.UpLoadFile;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lxl on 2017/3/4.
 */

public class OkhttpParams {
    private enum Mothod{
        POST,GET
    }
    private String url;
    private HashMap<String,String> params=new HashMap<>();
    private List<UpLoadFile> upFiles;

    public class GsonPost<T,V>{
        T requestBean;
        V responseBean;
    }
}
