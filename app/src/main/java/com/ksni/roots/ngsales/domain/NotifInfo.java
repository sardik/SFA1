package com.ksni.roots.ngsales.domain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;

/**
 * Created by #roots on 09/11/2015.
 */
public class NotifInfo {

    public static int notifId = 555666;

    public static Notification getNotification(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash2);

        Notification n = null;
        n = new Notification.Builder(context)
                .setContentTitle("SFA App")
                .setContentText("Tap here for quick launcher.")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_notif)
                .setLargeIcon(largeIcon)
                //.setColor(context.getResources().getColor(R.color.bg_color_toolbar))

                .build();



        return n;
    }

    public static void cancel(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notifId);
    }

}
