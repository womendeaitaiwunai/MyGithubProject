package com.pixelall.mylibrary.okhttp;

import android.util.Log;

import com.google.gson.Gson;
import com.pixel.photostudiob.bean.UpLoadFile;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by lxl on 2017/3/4.
 */

public  class PixelOkHttpUtil<T,V> {
    private T requestBean;
    private V responseBeam;

    /**
     * post 请求数据
     * @param url 服务器地址
     * @param params 请求参数
     * @param files 是否有文件
     * @param responseBeam 请求的类
     * @param backInterface 结果返回类
     */
    public void post(String url, Map<String, String> params, List<UpLoadFile> files, V responseBeam, CallBackInterface<V> backInterface){
        PostFormBuilder postFormBuilder= OkHttpUtils.post();
        postFormBuilder.url(checkUrl(url));
        if (params!=null){
            for (Map.Entry<String,String> entry:params.entrySet()){
                postFormBuilder.addParams(entry.getKey(),entry.getValue());
            }
        }
        if (files!=null){
            for (UpLoadFile upLoadFile:files){
                postFormBuilder.addFile(upLoadFile.getUpName(),upLoadFile.getFileName(),upLoadFile.getUpFile());
            }
        }

        RequestCall requestCall=postFormBuilder.build();
        requestCall.execute(new HttpCallBack<>(responseBeam,backInterface));
    }

    /**
     * 提交一个Gson字符串到服务器端
     * @param url 服务器地址
     * @param requestBean 请求的类
     * @param responseBeam 回调的类
     * @param backInterface 回调方法
     */
    public void postJson(String url,T requestBean,V responseBeam,CallBackInterface<V> backInterface){
        PostStringBuilder postStringBuilder= OkHttpUtils.postString();
        postStringBuilder.url(checkUrl(url)).content(new Gson().toJson(requestBean));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        RequestCall requestCall=postStringBuilder.build();
        requestCall.execute(new HttpCallBack<>(responseBeam,backInterface));
    }

    public void get(){
        OkHttpUtils.get().url("http://www.baidu.com").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i("得到的数据是",e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("得到的数据是",response);
            }
        });
    }

    private static String checkUrl(String url){
        if (url.startsWith("http://")||url.startsWith("https://")){
            return url;
        }else {
            return "https://"+url;
        }
    }
}
