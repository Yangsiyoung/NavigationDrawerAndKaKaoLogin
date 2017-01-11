package com.example.yangsiyoung.testnavigationdrawer.application;

import android.app.Activity;
import android.app.Application;

import com.example.yangsiyoung.testnavigationdrawer.adapter.KakaoLoginAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by Yang Si Young on 2017-01-10.
 */

public class GlobalApplication extends Application {

    private static volatile  GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoLoginAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext(){
        return instance;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity = currentActivity;
    }
}
