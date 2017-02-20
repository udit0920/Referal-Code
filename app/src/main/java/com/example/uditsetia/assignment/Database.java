package com.example.uditsetia.assignment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by uditsetia on 03/02/17.
 */

public class Database extends SQLiteOpenHelper {
  public Database (Context context) {
    super(context, "Database", null, 1);
  }

  @Override
  public void onCreate (SQLiteDatabase db) {

  }

  @Override
  public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
