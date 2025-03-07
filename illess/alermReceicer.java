package com.example.illess;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class alermReceicer extends BroadcastReceiver {

    private ActivityManager manager;
    private String ID;
    private Dialog dialog;
    private View viewDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get the manager and the top running activity
        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = manager.getRunningTasks(1).get(0).topActivity;

        //check the user is use the app
        if(componentName.getPackageName().equals("com.example.illess")){
            //user is use
            Intent i = new Intent(context,dialogActivity.class);
            //use share preference
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else{
            //user is not use
            //reconnect to the logIn page
            Intent intentNotify = new Intent(context,dialogActivity.class);
            //put extra for indety
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intentNotify,0);

            //set the notufication
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"illessChannelID")
                    .setSmallIcon(R.drawable.ic_launcher_background) //set the background
                    .setContentTitle("illess用藥通知")
                    .setContentText("用藥時間到了，要記得吃藥，不要忘記了喔~")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            //build the notification
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify(123, builder.build());
        }
    }
}
