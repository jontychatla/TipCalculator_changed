package com.calculator.tipcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.dao.DbQuery;
import com.calculator.tipcalculator.model.Tip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TipCalculator extends Activity {
    /**
     * Called when the activity is first created.
     */
    private static final String APP_URL = "http://market.android.com/details?id=com.calculator.tipcalculator";
    private EditText billEditText;
    private Double billTotal = 0.0;
    private Double customAmount = 0.0;
    private Double customTip = 0.0;
    private SeekBar seekBar;
    private SeekBar noOfPeopleSeekBar;
    private int noOfPeopleSliderValue = 0;
    private TextView noOfPeopleTextView;
    private TextView customTipTextView;
    private int customTipSliderValue = 15;

    private EditText finalTipEditText;
    private EditText finalTotalEditText;
    private double finalTotal;

    private EditText perPersonAmountEditText;
    //private EditText customAmountEditText;
    //private EditText customTipPercentEditText;
    private GridLayout mainView;
    private MenuItem viewTipsMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainView = (GridLayout) findViewById(R.id.mainGridLayoutId);

        billEditText = (EditText) findViewById(R.id.billEditText);

        noOfPeopleSeekBar = (SeekBar) findViewById(R.id.noOfPeopleSeekBar);
        noOfPeopleTextView = (TextView) findViewById(R.id.noOfPeopleTextView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        customTipTextView = (TextView) findViewById(R.id.customTipTextView);


        finalTipEditText = (EditText) findViewById(R.id.finalTipEditText);
        finalTotalEditText = (EditText) findViewById(R.id.finalTotalEditText);

        perPersonAmountEditText = (EditText) findViewById(R.id.perPersonEditText);

        //customAmountEditText = (EditText) findViewById(R.id.customAmountEditText);
        //customTipPercentEditText = (EditText) findViewById(R.id.customTipPercentEditText);

        billEditText.addTextChangedListener(new BillTotalTextWatchListener());
        seekBar.setOnSeekBarChangeListener(new MySeekChangeListener());
        noOfPeopleSeekBar.setOnSeekBarChangeListener(new MySeekChangeListener());
        //customAmountEditText.addTextChangedListener(new CustomAmountTextWatchListener());

        mainView.setOnTouchListener(new TouchListener());
        //saveTestData();
//        try {
//            exportDb();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void saveTestData() {
        for (int i = 1; i < 11; i++) {
            saveTip(i);
        }
    }

    int years[] = new int[]{2012,2013,2014};

    private String getRandomDate(){
        Random random = new Random();
        int year = years[random.nextInt(2)];
        int month = random.nextInt(12)+1;
        int day = random.nextInt(27)+1;
        return month +"/"+day+"/"+year;
    }

    private void saveTip(int i) {
        DatabaseConnector databaseConnector = new DatabaseConnector(TipCalculator.this);
        Tip tip = new Tip();
        tip.withPlace("test " + i)
                .withDate(getRandomDate())
                .withBillAmount(Double.valueOf(14 * i*0.33))
                .withTipAmount(14D);
        databaseConnector.insertTip(tip);
    }


    private void exportDb() throws Exception {
            //Open your local db as the input stream
            String inFileName = "/data/data/com.calculator.tipcalculator/databases/UserTips";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory()+"/UserTips";
            //Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            //Close the streams
            output.flush();
            output.close();
            fis.close();
    }

    private void updateTipValue() {
        customTip = (billTotal * customTipSliderValue * 0.01);
        finalTotal = customTip + billTotal;

        finalTipEditText.setText(getFormattedString(customTip));
        finalTotalEditText.setText(getFormattedString(finalTotal));
    }

    private String getFormattedString(double value) {
        return String.format("%.2f", value);
    }

    private void updateCustomTipValue() {
        customTipTextView.setText(customTipSliderValue + "%");
        if (billTotal > 0) {
            updateTipValue();
            calculatePerPersonTotal();
        }
    }

    private void updatePerPersonTotal() {
        noOfPeopleTextView.setText(noOfPeopleSliderValue+"");
        calculatePerPersonTotal();
    }

    private void calculatePerPersonTotal() {
        if (billTotal > 0 && finalTotal > 0 && noOfPeopleSliderValue > 0) {
            perPersonAmountEditText.setText(getFormattedString(finalTotal / noOfPeopleSliderValue));
        }else {
            perPersonAmountEditText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        viewTipsMenuItem =  menu.getItem(0);
        try {
            Boolean aBoolean = new RowExist().execute((Object[]) null).get();
            if(aBoolean) {
                viewTipsMenuItem.setEnabled(true);
            }else{
                viewTipsMenuItem.setEnabled(false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTip:
                startSaveTipActivity();
                return true;
            case R.id.viewTips:
                startViewTipActivity();
                return true;
            case R.id.emailApp:
                shareAppViaEmail();
                return true;
            case R.id.rateApp:
                rateApp();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSaveTipActivity() {
        Intent intent = new Intent(this, SaveTipActivity.class);
        if (billTotal > 0) {
            intent.putExtra("billAmount", getFormattedString(billTotal));
        }
        if (customTip > 0) {
            intent.putExtra("tipAmount", getFormattedString(customTip));
        }
        startActivity(intent);
    }

    private void startViewTipActivity() {
        Intent intent = new Intent(this, ViewTipActivity.class);
        startActivity(intent);
    }

    private void shareAppViaEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, "Tip Calculator App");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app " + APP_URL);

        Intent chooser = Intent.createChooser(intent, "Tell a friend about Tip Calculator");
        startActivity(chooser);
    }

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(APP_URL));
        startActivity(intent);
    }

    class BillTotalTextWatchListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                billTotal = Double.parseDouble(charSequence.toString());
            } catch (NumberFormatException e) {
                billTotal = 0.0;
            }
            updateTipValue();
            calculatePerPersonTotal();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

//    class NoOfPeopleTextWatchListener implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            try {
//                noOfPeople = Integer.parseInt(charSequence.toString());
//                calculatePerPersonTotal();
//            } catch (NumberFormatException e) {
//
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//        }
//    }

//    class CustomAmountTextWatchListener implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            try {
//                customAmount = Double.parseDouble(charSequence.toString());
//                if (billTotal < customAmount) {
//                    double customTipPercentValue = (customAmount - billTotal) * 100 / billTotal;
//                    customTipPercentEditText.setText(getFormattedString(customTipPercentValue));
//                } else {
//                    customTipPercentEditText.setText("");
//                }
//            } catch (NumberFormatException e) {
//                customAmount = 0.0;
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//        }
//    }

    class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    customTipSliderValue = seekBar.getProgress();
                    updateCustomTipValue();
                    break;
                case R.id.noOfPeopleSeekBar:
                    noOfPeopleSliderValue = seekBar.getProgress();
                    updatePerPersonTotal();
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return false;
        }
    }

    class RowExist extends AsyncTask<Object, Object, Boolean> {
        DbQuery dbQuery = new DbQuery(TipCalculator.this);

        @Override
        protected Boolean doInBackground(Object... objects) {
            dbQuery.open();
            return dbQuery.rowExists();
        }

        @Override
        protected void onPostExecute(Boolean strings) {
            dbQuery.close();
        }
    }

}
