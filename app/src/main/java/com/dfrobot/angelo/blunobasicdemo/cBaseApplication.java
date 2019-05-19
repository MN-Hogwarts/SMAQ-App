package com.dfrobot.angelo.blunobasicdemo;
import android.app.Application;

public class cBaseApplication extends Application {

    static BluetoothLeService mBluetoothLeService;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

}