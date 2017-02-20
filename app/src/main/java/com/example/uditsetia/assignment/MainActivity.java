package com.example.uditsetia.assignment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "MainActivity";
  private static final String TWITTER_KEY = "S5hu2UMsm7sbiIbXfAXYF3hmO";
  private static final String TWITTER_SECRET = "Sc26Xv4Oiyd4zsQ0H7jadElIw3khKMWHWoS7em4AazXes1mZL8";
  private static final String Query = "CREATE TABLE IF NOT EXISTS " + DatabaseNomenclature.Table_Name + "(" + DatabaseNomenclature.Table_Column_PhoneNumber + " text," + DatabaseNomenclature.Table_Column_ReferralNumber + " text," + DatabaseNomenclature.Table_Column_Referral_Used + " text)";
  public static boolean success = false;
  private EditText et;
  public String referal_used;
  SQLiteDatabase database;
  String phoneNo;
  Handler handler;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
    Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
    setContentView(R.layout.activity_main);

    Database db = new Database(this);
    database = db.getWritableDatabase();
    et = (EditText) findViewById(R.id.et_code);

    findViewById(R.id.submit).setOnClickListener(this);
    DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
    digitsButton.setCallback(new AuthCallback() {
      @Override
      public void success (DigitsSession session, String phoneNumber) {
        // TODO: associate the session userID with your user model
        Toast.makeText(getApplicationContext(), "Authentication successful for "
                + phoneNumber, Toast.LENGTH_LONG).show();

        success = true;
        phoneNo = phoneNumber;
        createDB();
      }

      @Override
      public void failure (DigitsException exception) {
        Log.d("Digits", "Sign in with Digits failure", exception);
      }
    });
    digitsButton.setText("Authenticate Phone No");

  }

  private void createDB () {
    database.execSQL(Query);
  }


  @Override
  public void onClick (View v) {
    switch (v.getId()) {

      case R.id.submit:
        logIn();
        et.getText().clear();
        break;


    }
  }

  private void logIn () {

    Log.d(TAG, "logIn: ");
    handler = new Handler(Looper.getMainLooper());

    if (success) {
      Log.d(TAG, "success");
      referal_used = et.getText().toString();
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run () {
          Cursor cursor2 = database.query(DatabaseNomenclature.Table_Name, new String[]{DatabaseNomenclature.Table_Column_Referral_Used}, DatabaseNomenclature.Table_Column_PhoneNumber + "=?", new String[]{phoneNo}, null, null, null);
          cursor2.moveToFirst();
          if (cursor2.getCount() != 0) {

            Log.d("", "running: ");

            if (referal_used.isEmpty()) {
              Intent intent = new Intent(MainActivity.this, LoginComplete.class);
              intent.putExtra("phone", phoneNo);
              intent.putExtra("referal_used", referal_used);
              startActivity(intent);
            } else {
              handler.post(new Runnable() {
                @Override
                public void run () {

                  Toast.makeText(MainActivity.this, "Referral code only for new users", Toast.LENGTH_LONG).show();

                }
              });

            }

          } else if (!referal_used.isEmpty() && cursor2.getCount() == 0) {

            Log.d("", "running2: ");


            AsyncTask async = new AsyncTask() {
              @Override
              protected Object doInBackground (Object[] objects) {
                Cursor cursor3 = database.query(DatabaseNomenclature.Table_Name, new String[]{DatabaseNomenclature.Table_Column_Referral_Used}, DatabaseNomenclature.Table_Column_ReferralNumber + "=?", new String[]{referal_used}, null, null, null);

                if (cursor3.getCount() != 0 || referal_used.equals("1111")) {
                  Log.d(TAG, "doInBackground22222222");
                  Intent intent = new Intent(MainActivity.this, LoginComplete.class);
                  intent.putExtra("phone", phoneNo);
                  intent.putExtra("referal_used", referal_used);
                  startActivity(intent);
                  cursor3.close();
                  return true;
                } else {
                  cursor3.close();
                  return false;
                }

              }

              @Override
              protected void onPostExecute (Object o) {
                super.onPostExecute(o);
                if (!(Boolean) o) {
                  Toast.makeText(MainActivity.this, "Wrong Referral Code", Toast.LENGTH_LONG).show();
                }
              }
            };
            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

          } else if (referal_used.isEmpty() && cursor2.getCount() == 0) {
            Log.d("", "running3: ");
            handler.post(new Runnable() {
              @Override
              public void run () {

                Toast.makeText(MainActivity.this, "Referral code is required for new users ", Toast.LENGTH_LONG).show();


              }
            });

          }

          cursor2.close();

        }
      });
      thread.start();

    } else {
      Toast.makeText(this, "Authenticate your number", Toast.LENGTH_LONG).show();
    }

  }
}
