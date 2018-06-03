package com.fitzguru.mfaauction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.parse.LogInCallback;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;


public class LoginActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        checkPlayServices();

		setContentView(R.layout.login);
        findViewById(R.id.base_tint_darken).setVisibility(View.GONE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.gobutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(((TextView) findViewById(R.id.email)).getText().toString(),
                        ((TextView) findViewById(R.id.name)).getText().toString(),
                        ((TextView) findViewById(R.id.telephone)).getText().toString());
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            doLogin(currentUser.getEmail(), currentUser.getString("fullname"), currentUser.getString("telephone"));
        } else {
            findViewById(R.id.loginLayout).setVisibility(1);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("checkPlayServices", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

  void doLogin(final String email, final String name, final String telephone) {
    if (hasRegistrationError(email, name, telephone)) {
        findViewById(R.id.loginLayout).setVisibility(1);
        Toast.makeText(getApplicationContext(), "Please enter your first and last name, email address, and telephone number.", Toast.LENGTH_LONG).show();
        return;
    } else {
        ParseUser.logInInBackground(email, "noP@ssword!", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.i("doLogin", "User successfully logged in.");
                    updateIdentityAndInstallation(email, name, telephone);
                    goToStartPage();
                } else {
                    Log.i("doLogin", "Error logging user in.", e);
                    doRegister(email, name, telephone);
                }
            }
        });
    }
  }

  void doRegister (final String email, final String name, final String telephone) {
      if (hasRegistrationError(email, name, telephone)) {
          Toast.makeText(getApplicationContext(), "Please enter your first and last name, email address, and telephone number.", Toast.LENGTH_LONG).show();
          return;
      } else {
          ParseUser currentUser = new ParseUser();
          currentUser.setUsername(email);
          currentUser.setEmail(email);
          currentUser.setPassword("noP@ssword!");
          currentUser.put("telephone", telephone);
          currentUser.put("fullname", name);

          currentUser.signUpInBackground(new SignUpCallback() {
              public void done(ParseException e) {
                  if (e == null) {
                      OneSignal.syncHashedEmail(email);
                      OneSignal.setSubscription(true);
                      updateIdentityAndInstallation(email, name, telephone);
                      goToStartPage();
                  } else {
                      // Sign up didn't succeed. Look at the ParseException
                      // to figure out what went wrong
                      Log.e("doRegister", "Signup failed", e);
                  }
              }
          });
      }
  }

  private boolean hasRegistrationError(String email, String name, String telephone) {
      return ((email == null || email.length() < 5) || (name == null || name.length() < 2) || (telephone == null || telephone.length() < 10 || telephone.length() > 14));
  }

  private void goToStartPage () {
      Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
      startActivity(itemListIntent);
  }

  private void updateIdentityAndInstallation(String email, String name, String telephone) {
      final OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
      final ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();

      IdentityManager.setEmail(email, this);
      IdentityManager.setName(name, this);
      IdentityManager.setTelephone(telephone, this);

      //parseInstallation.put("deviceToken", status.getSubscriptionStatus().getPushToken());

      parseInstallation.put("email", email);
      parseInstallation.put("GCMSenderId", "592875681538");
      parseInstallation.saveInBackground();
  }
}
