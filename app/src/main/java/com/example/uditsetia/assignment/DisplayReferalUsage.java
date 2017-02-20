package com.example.uditsetia.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uditsetia on 04/02/17.
 */

public class DisplayReferalUsage extends DialogFragment {
  private static final String TAG = "DisplayReferalUsage";
  private ArrayAdapter<String> adapter;
  private ListView lv;

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View layout = inflater.inflate(R.layout.display_referal_usage, container, false);

    String list[] = getArguments().getStringArray("list");

    if(list!=null) {
      adapter = new ArrayAdapter<String>(getActivity(), R.layout.array_adapter_view, R.id.tv_adapter, list);
      lv = (ListView) layout.findViewById(R.id.lv_1);
      lv.setAdapter(adapter);
    }
    return layout;
  }

}
