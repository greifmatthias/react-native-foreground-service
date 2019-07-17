package be.greifmatthias.foregroundservice;

import static be.greifmatthias.foregroundservice.Constants.NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

class NotificationHelper {

    private static NotificationHelper instance = null;
    private final NotificationManager mNotificationManager;

    private final NotificationConfig config;

//    Get instance
    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }

        return instance;
    }

//    Constructor
    private NotificationHelper(Context context) {
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.config = new NotificationConfig(context);
    }

//    Create notification
    Notification buildNotification(Context context, Bundle bundle) {
        if (bundle == null) {
            Log.e("NotificationHelper", "buildNotification: invalid config");
            return null;
        }

        Class mainActivityClass = getMainActivityClass(context);
        if (mainActivityClass == null) {
            return null;
        }

        Intent notificationIntent = new Intent(context, mainActivityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        String title = bundle.getString("title");
        String description = bundle.getString("message");

        int visibility = Notification.VISIBILITY_PRIVATE;
        String visibilityString = bundle.getString("visibility");

        if (visibilityString != null) {
            switch(visibilityString.toLowerCase()) {
                case "private":
                    visibility = Notification.VISIBILITY_PRIVATE;
                    break;
                case "public":
                    visibility = Notification.VISIBILITY_PUBLIC;
                    break;
                case "secret":
                    visibility = Notification.VISIBILITY_SECRET;
                    break;
                default:
                    visibility = Notification.VISIBILITY_PRIVATE;
            }
        }

//        Create channel
        checkOrCreateChannel(mNotificationManager, bundle);

//        Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setVisibility(visibility)
                .setContentIntent(pendingIntent)
                .setOngoing(bundle.getBoolean("ongoing", false));


        String iconName = bundle.getString("icon");
        if(iconName == null){
            iconName = "ic_notification";
        }
        notificationBuilder.setSmallIcon(getResourceIdForResourceName(context, iconName));


        String largeIconName = bundle.getString("largeIcon");
        if(largeIconName == null){
            largeIconName = "ic_launcher";
        }

        int largeIconResId = getResourceIdForResourceName(context, largeIconName);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), largeIconResId);

        if (largeIconResId != 0) {
            notificationBuilder.setLargeIcon(largeIconBitmap);
        }

//        Badge count
        String numberString = bundle.getString("number");
        if (numberString != null) {
            int numberInt = Integer.parseInt(numberString);
            if(numberInt > 0){
                notificationBuilder.setNumber(numberInt);
            }
        }

        return notificationBuilder.build();
    }

    private Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e("NotificationHelper", "Failed to get launch intent or component");
            return null;
        }

        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e("NotificationHelper", "Failed to get main activity class");
            return null;
        }
    }

    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }

        return resourceId;
    }

    private static boolean channelCreated = false;
    private void checkOrCreateChannel(NotificationManager manager, Bundle bundle) {
        if (channelCreated) return;
        if (manager == null) return;

        int importance = NotificationManager.IMPORTANCE_HIGH;
        final String importanceString = bundle.getString("importance");

        if (importanceString != null) {
            switch(importanceString.toLowerCase()) {
                case "default":
                    importance = NotificationManager.IMPORTANCE_DEFAULT;
                    break;
                case "max":
                    importance = NotificationManager.IMPORTANCE_MAX;
                    break;
                case "high":
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    break;
                case "low":
                    importance = NotificationManager.IMPORTANCE_LOW;
                    break;
                case "min":
                    importance = NotificationManager.IMPORTANCE_MIN;
                    break;
                case "none":
                    importance = NotificationManager.IMPORTANCE_NONE;
                    break;
                case "unspecified":
                    importance = NotificationManager.IMPORTANCE_UNSPECIFIED;
                    break;
                default:
                    importance = NotificationManager.IMPORTANCE_HIGH;
            }
        }

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, this.config.getChannelName(), importance);
        channel.setDescription(this.config.getChannelDescription());
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setShowBadge(false);

        manager.createNotificationChannel(channel);
        channelCreated = true;
    }
}