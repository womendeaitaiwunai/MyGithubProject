package com.pixelall.mylibrary.okhttp;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lxl on 2017/3/4.
 * 回调类重写
 */

 class HttpCallBack<V> extends Callback<V> {
    private V v;
    private CallBackInterface<V> callBackInterface;

     HttpCallBack(V v,CallBackInterface<V> callBackInterface){
        this.v=v;
        this.callBackInterface=callBackInterface;
    }

    @Override
    public V parseNetworkResponse(Response response, int id) throws Exception {
        V v = null;
        try {
            String string = response.body().string();
            v= (V) new Gson().fromJson(string,this.v.getClass());
        }
        catch (JsonIOException jsonIo){
            onError(null,jsonIo,0);
        }
        return v;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (call==null&&id==0){
            callBackInterface.onFail("数据解析错误");
        }else if (call!=null){
            Log.i("请求发生错误","URL："+call.request().url().url().toString());
            callBackInterface.onFail("请求发生错误");
            call.cancel();
        }

    }

    @Override
    public void onResponse(V response, int id) {
        callBackInterface.onSuccess(v,id);
    }
}
