package com.calculator.tipcalculator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.fragment.ViewTipFragment;
import com.calculator.tipcalculator.model.Tip;
import com.calculator.tipcalculator.util.DateUtil;
import com.calculator.tipcalculator.util.Utility;
import com.echo.holographlibrary.BarGraph;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bharatkc on 2/2/14.
 */
public class ViewTipActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = getFragmentManager().findFragmentByTag("list");
        if(null == fragment){
            fragment = ViewTipFragment.getInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, "list");
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_to_launch_data_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.dataFilterMenuItem :
               showDataFilterActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDataFilterActivity() {
        Intent intent = new Intent(this, DataFilterActivity.class);
        startActivity(intent);
    }
}
