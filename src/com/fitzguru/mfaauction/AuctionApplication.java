package com.fitzguru.mfaauction;

import android.app.Application;
import android.util.Log;

import com.fitzguru.mfaauction.models.AuctionItem;
import com.fitzguru.mfaauction.models.Bid;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

public class AuctionApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(AuctionItem.class);
    ParseObject.registerSubclass(Bid.class);

    // Add your initialization code here
    Parse.initialize(this);

    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    // Push notification setup
    OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init();

    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
      @Override
      public void idsAvailable(String userId, String registrationId) {
        final OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        final ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();

        if (registrationId != null) {
          HashMap<String, Object> params = new HashMap<>(2);
          params.put("installationId", currentInstallation.getObjectId());
          params.put("deviceToken", status.getSubscriptionStatus().getPushToken());

          ParseCloud.callFunctionInBackground("setDeviceToken", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
              if (e != null) {
                Log.e("AuctionApplication", "Could not complete deviceToken ParseCloud function.", e);
              } else {
                Log.i("AuctionApplication", "Device Token successfully updated.");
              }
            }
          });
        }
      }
    });
  }
}
