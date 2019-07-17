package be.greifmatthias.foregroundservice;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import javax.annotation.Nullable;


public class ForegroundServiceTask extends HeadlessJsTaskService {

    @Nullable
    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {

        // check null intents that may rarely happen
        // return null so react knows it should skip this request
        if(intent == null){
            return null;
        }

        Bundle extras = intent.getExtras();

        return new HeadlessJsTaskConfig(
          extras.getString("taskName"), //headless function to call
          Arguments.fromBundle(extras),
          0,
          true);
    }
}