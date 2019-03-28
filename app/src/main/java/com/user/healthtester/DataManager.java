package com.user.healthtester;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class DataManager {

    boolean isback = true;
    private TextView TvSteps;
    private TextView TvSpeeds;
    private TextView TvStatus;
    private SensorManager mSensroMgr;
    private Context context;

    public DataManager(Context context, TextView TvStatus, TextView TvSteps, TextView TvSpeeds, SensorManager mSensroMgr) {
        Parameters.init(context);
        Parameters.context = context;
        this.TvSteps = TvSteps;
        this.TvStatus = TvStatus;
        this.TvSpeeds = TvSpeeds;
        this.mSensroMgr = mSensroMgr;
    }

    public void sendData(String status, int stepers, double speeds) {
        if (!isback) {
            return;
        }
        isback = false;

        String HostUrl = Parameters.getString("serverurl");

        RequestParams params = new RequestParams();
        //Integer.toString(1);
        params.addQueryStringParameter("stepers", stepers+"");
        params.addQueryStringParameter("macid", Parameters.getMacIP()+"");
        params.addQueryStringParameter("status", status+"");
        params.addQueryStringParameter("speeds", speeds+"");
        //Log.e(newHostUrl,"newhosturl");
        Log.e(Parameters.getMacIP()+"","macid");
        Log.e(stepers+"","stepers");
        Log.e(status+"","status");
        Log.e(speeds+"","speeds");
        //String newURL;

        //newURL = HostUrl+"?stepers="+stepers+"&macid="+Parameters.getMacIP();

        //Log.e(newURL, "newURL");

        HttpUtils http = new HttpUtils();

        //http.configCurrentHttpCacheExpiry(1000 * 10);
       http.send(HttpMethod.POST, HostUrl,params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        isback = true;
                        Log.e("falied","failed");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> textInfo)  {
                        Log.e("success","success");
                        try {
                            Log.e(textInfo.locale.toString(),"locale");
                            Log.e(textInfo.result,"textInfor");

                            /*JSONObject json = new JSONObject(textInfo.result+"");
                            //Log.e(json.toString(),"json");
                            if (json.getString("msgtype").equals("suc")) {
                            } else {
                            }*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        isback = true;
                    }
                });
    }
}
