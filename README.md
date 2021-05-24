# react-native-foreground-service

Android only foreground service with JS code support, This is a library which can help to run headless js task while your app is in background, such as geolocation update, play music, fetch data,

Thanks to the following repos to provide ideas and insight about how to do this.

- https://github.com/voximplant/react-native-foreground-service
- https://github.com/zo0r/react-native-push-notification/

# Installation

- package.json

```
"react-native-foreground-service": "github:greifmatthias/react-native-foreground-service"
```

- AndroidManifest.xml

```
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- Inside <application> -->

<meta-data android:name="be.greifmatthias.foregroundservice.notification_channel_name" android:value="Foreground Service" />
<meta-data android:name="be.greifmatthias.foregroundservice.notification_channel_description" android:value="A foreground Service" />

<service android:name="be.greifmatthias.foregroundservice.ForegroundService" />
<service android:name="be.greifmatthias.foregroundservice.ForegroundServiceTask" />
```

# Usage:

TODO, but basically: register headless task (at module level) and then call the methods from index.js with the notification config

```
import ForegroundService from 'react-native-foreground-service';


// register task with a given name and function
let foregroundTask = async (data) => {
    await myTask();
}
ForegroundService.registerForegroundTask("myTaskName", foregroundTask);


// then later, start service, and send tasks

let notificationConfig = {
    id: 3,
    title: 'Service',
    message: `blah message`,
    visibility: 'public',
    importance: 'low',
    number: String(1)
};

await ForegroundService.startService(notificationConfig);

await ForegroundService.runTask({
    taskName: 'myTaskName',
    delay: 0
});


// stop service when no longer needed
await ForegroundService.stopServiceAll();
// or await ForegroundService.stopService();


```
