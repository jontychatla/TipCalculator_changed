package com.calculator.tipcalculator;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.dao.DbQuery;
import com.calculator.tipcalculator.model.FilterData;
import com.calculator.tipcalculator.model.Tip;
import com.calculator.tipcalculator.util.DateUtil;
import com.calculator.tipcalculator.util.Utility;
import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.calculator.tipcalculator.util.Utility.getFormattedMoneyString;

/**
 * Created by bharatkc on 2/2/14.
 */
public class DataFilterActivity extends Activity {

    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Button dataFilterShowButton;
    private LinearLayout filterGraphLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_filter_layout);
        yearSpinner = (Spinner) findViewById(R.id.yearFilterSpinner);
        monthSpinner = (Spinner) findViewById(R.id.monthFilterSpinner);
        filterGraphLayout = (LinearLayout) findViewById(R.id.filterGraphLayoutId);
        dataFilterShowButton = (Button) findViewById(R.id.dataFilterButtonId);
        dataFilterShowButton.setOnClickListener(new ShowButtonListener());
        loadYearSpinner();
        loadMonthSpinner();
    }

    private void loadYearSpinner() {
        List<String> years = new ArrayList<String>();
        Year year = new Year();
        year.execute((Object[]) null);
        try {
            years = year.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        yearSpinner.setAdapter(adapter);
    }

    private void loadMonthSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.months));
        monthSpinner.setAdapter(adapter);
    }

    private void showData(FilterData data) {
        filterGraphLayout.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.data_filter_data, null);
        TextView billAmountTextView = (TextView)linearLayout.findViewById(R.id.dataFilterBillAmountTV);
        billAmountTextView.setText(Utility.getFormattedMoneyString(data.getBillAmount()));
        TextView tipAmountTextView = (TextView)linearLayout.findViewById(R.id.dataFilterTipAmountTV);
        tipAmountTextView.setText(Utility.getFormattedMoneyString(data.getTipAmount()));
        TextView finalAmountTextView = (TextView)linearLayout.findViewById(R.id.dataFilterFinalAmountTV);
        finalAmountTextView.setText(Utility.getFormattedMoneyString(data.getTotalAmount()));
        filterGraphLayout.addView(linearLayout);
    }

    class ShowButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String selectedYear = (String) yearSpinner.getSelectedItem();
            String selectedMonth = (String) monthSpinner.getSelectedItem();
            String monthNo = Utility.getMonthNumber(selectedMonth);
            DataFilter dataFilter = new DataFilter();
            dataFilter.execute(selectedYear, monthNo);
            try {
                List<FilterData> filterDatas = dataFilter.get();
                System.out.println("=========data size=======" + filterDatas.size());
                if (filterDatas.size() == 1) {
                    showData(filterDatas.get(0));
                    for (FilterData data : filterDatas) {
                        System.out.println(data.getBillAmount() + " " + data.getTipAmount() + " " + data.getTotalAmount());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Selected ======= " + selectedYear + "  " + selectedMonth);
        }
    }

    class Year extends AsyncTask<Object, Object, List<String>> {
        DbQuery dbQuery = new DbQuery(DataFilterActivity.this);

        @Override
        protected List<String> doInBackground(Object... objects) {
            dbQuery.open();
            return dbQuery.getYear();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            dbQuery.close();
        }
    }

    class DataFilter extends AsyncTask<String, String, List<FilterData>> {
        DbQuery dbQuery = new DbQuery(DataFilterActivity.this);

        @Override
        protected List<FilterData> doInBackground(String... objects) {
            dbQuery.open();
            return dbQuery.getYearAndMonthTips(objects[0], objects[1]);
        }

        @Override
        protected void onPostExecute(List<FilterData> filterDatas) {
            dbQuery.close();
        }
    }
}
