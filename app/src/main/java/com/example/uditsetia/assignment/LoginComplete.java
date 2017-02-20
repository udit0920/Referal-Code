package com.example.uditsetia.assignment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;

import java.util.Random;


/**
 * Created by uditsetia on 03/02/17.
 */

public class LoginComplete extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = "LoginComplete";
  int generated_reference_code;
  TextView tv;
  boolean right_post=false;
  int refer_code;
  String projection[];
  public Random random;
  boolean code_generated = false;
  String list[];
  String phone;
  String ref_code;
  String referal_used;
  SQLiteDatabase database;

  @Override
  protected void onCreate (@Nullable Bundle savedInstanceState) {

    setContentView(R.layout.login_complete);
    Database db = new Database(this);
    database = db.getWritableDatabase();
    tv = (TextView) findViewById(R.id.tv_generated);
    Intent intent = getIntent();
    this.phone = intent.getStringExtra("phone");
    this.referal_used = intent.getStringExtra("referal_used");
    generateCode();
    findViewById(R.id.Refer_IT).setOnClickListener(this);
    findViewById(R.id.generate_code).setOnClickListener(this);
    findViewById(R.id.logout).setOnClickListener(this);
    findViewById(R.id.usageReference).setOnClickListener(this);
    super.onCreate(savedInstanceState);
  }

  private void generateCode () {

    random = new Random();
    AsyncTask async = new AsyncTask() {
      @Override
      protected Object doInBackground (Object[] params) {

        Cursor cursor = database.query(DatabaseNomenclature.Table_Name, new String[]{DatabaseNomenclature.Table_Column_ReferralNumber}, DatabaseNomenclature.Table_Column_PhoneNumber + "=?", new String[]{(String.valueOf(phone))}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
          generated_reference_code = 10000 + random.nextInt(20000);
        } else {
          generated_reference_code = cursor.getInt(0);
        }
        cursor.close();

        return null;
      }

      @Override
      protected void onPostExecute (Object o) {
        super.onPostExecute(o);
        right_post=true;

      }
    };
    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




  }

  @Override
  public void onClick (View v) {

    switch (v.getId()) {

      case R.id.generate_code:
        generateReferalCode();
        break;

      case R.id.logout:
        function_logOut();
        finish();
        break;

      case R.id.Refer_IT:
        refer();
        break;

      case R.id.usageReference:
        usageData();
        break;
    }


  }

  private void usageData () {
    projection = new String[]{
            DatabaseNomenclature.Table_Column_PhoneNumber
    };

    AsyncTask async = new AsyncTask() {

      @Override
      protected Object doInBackground (Object[] params) {

        Cursor cursor = database.query(DatabaseNomenclature.Table_Name, projection, DatabaseNomenclature.Table_Column_Referral_Used + "=?", new String[]{(String.valueOf(generated_reference_code))}, null, null, null);
        cursor.moveToFirst();
        //
        int size = cursor.getCount();
        ////
        //    int a[] = new int[size];
        if (cursor.getCount() <= 0) {
          return false;

        } else {
          list = new String[size];
          int i = 0;
          do {
            list[i] = cursor.getString(0);
            //      list[i] = String.valueOf(a[i]);
            Log.d(TAG, "usageData: " + list[i]);
            i++;
          }
          while (cursor.moveToNext());
          cursor.close();
        }
        return true;
      }

      @Override
      protected void onPostExecute (Object o) {
        super.onPostExecute(o);
        if (!(Boolean) o)
          Toast.makeText(LoginComplete.this, "No referreral found!", Toast.LENGTH_SHORT).show();
        DisplayReferalUsage displayReferalUsage = new DisplayReferalUsage();
        Bundle bundle = new Bundle();
        bundle.putStringArray("list", list);
        displayReferalUsage.setArguments(bundle);
        displayReferalUsage.show(getSupportFragmentManager(), "dialog");

      }
    };
    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

     /* Intent intent = new Intent(this, DisplayReferalUsage.class);
    intent.putExtra("list", list);
    startActivity(intent);*/


  }

  private void refer () {

    if (code_generated) {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(generated_reference_code));
      startActivity(Intent.createChooser(intent, "Share Code via:"));
    } else {
      Toast.makeText(this, "Firstly generate the code please", Toast.LENGTH_LONG).show();
    }
  }

  private void function_logOut () {
    Digits.logout();
    MainActivity.success = false;
  }

  private void generateReferalCode () {
    if(right_post) {
      String gen_code = String.valueOf(generated_reference_code);
      tv.setText(gen_code);
      storeData(generated_reference_code);
      code_generated = true;
    }else{
      Toast.makeText(this,"Please Press again after few seconds",Toast.LENGTH_LONG);

    }
  }

  private void storeData (int reference_code) {
    refer_code = reference_code;
    AsyncTask async = new AsyncTask() {
      @Override
      protected Object doInBackground (Object[] params) {
        ref_code = String.valueOf(refer_code);
        Cursor cursor = database.query(DatabaseNomenclature.Table_Name, new String[]{DatabaseNomenclature.Table_Column_PhoneNumber}, DatabaseNomenclature.Table_Column_PhoneNumber + "=?", new String[]{phone}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
          ContentValues values = new ContentValues();
          values.put(DatabaseNomenclature.Table_Column_ReferralNumber, ref_code);
          values.put(DatabaseNomenclature.Table_Column_PhoneNumber, phone);
          values.put(DatabaseNomenclature.Table_Column_Referral_Used, referal_used);
          database.insert(DatabaseNomenclature.Table_Name, null, values);
        }
        cursor.close();
        return null;
      }
    };

    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

  }


}
