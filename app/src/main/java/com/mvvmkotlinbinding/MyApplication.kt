package com.mvvmkotlinbinding;

import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication
{
    @Override
    public void onCreate() {
        super.onCreate();
        /*FirebaseApp.initializeApp(getApplicationContext());
        getDeviceToken();*/
    }

    /*private void getDeviceToken() {

        Task<InstanceIdResult> instanceIdResultTask = FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            // Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        PrefUtils.setPrefString(getApplicationContext(), PrefKeys.PREF_FCM_TOKEN, task.getResult().getToken());


                    }
                });
    }*/
}
