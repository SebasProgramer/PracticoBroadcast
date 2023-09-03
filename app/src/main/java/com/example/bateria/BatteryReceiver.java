package com.example.bateria;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.TextView;

public class BatteryReceiver extends BroadcastReceiver {

    private final TextView batteryStatusTextView;

    public BatteryReceiver(TextView batteryStatusTextView) {
        this.batteryStatusTextView = batteryStatusTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        String message = "";

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            message = "La batería se está cargando";
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            message = "La batería se está descargando";
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            message = "La batería está completamente cargada";
        }

        if (batteryPct <= 0.53) {  // 16% o menos
            message = "Batería baja";
        }

        if (batteryStatusTextView != null) {
            batteryStatusTextView.setText(message);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "battery_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Battery Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, channelId);
        } else {
            builder = new Notification.Builder(context);
        }

        Notification notification = builder
                .setContentTitle("Estado de la batería")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_battery)
                .build();

        notificationManager.notify(1, notification);
    }
}
