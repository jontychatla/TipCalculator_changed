package com.calculator.tipcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.calculator.tipcalculator.button.CustomButton;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.dao.DbQuery;
import com.calculator.tipcalculator.model.Tip;
import com.echo.holographlibrary.Line;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

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
    private Double customTip = 0.0;
    private int noOfPeopleValue = 0;
    private TextView noOfPeopleTextView;
    private TextView customTipTextView;
    private int customTipValue = 15;

    private EditText finalTipEditText;
    private EditText finalTotalEditText;
    private double finalTotal;

    private EditText perPersonAmountEditText;
    private GridLayout mainView;
    private MenuItem viewTipsMenuItem;
    private CustomButton tipDownButton;
    private CustomButton tipUpButton;
    private CustomButton noOfPeopleDownButton;
    private CustomButton noOfPeopleUpButton;
    private Switch roundSwitch;
    private boolean roundTotal;

    private AdView adView;

    private static final String TIP_PREF = "tipPref";
    private static final String ROUND_PREF = "roundPref";
    private static final String AD_UNIT_ID="ca-app-pub-3069079687424454/8745700010";

    private LinearLayout hideCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainView = (GridLayout) findViewById(R.id.mainGridLayoutId);

        billEditText = (EditText) findViewById(R.id.billEditText);

        noOfPeopleTextView = (TextView) findViewById(R.id.noOfPeopleTextView);
        customTipTextView = (TextView) findViewById(R.id.customTipTextView);

        tipDownButton = (CustomButton) findViewById(R.id.tipDownButtonId);
        tipDownButton.setOnClickListener(new UpDownButtonListener());
        tipDownButton.setOnTouchListener(new UpDownTouchListener());
        tipDownButton.setOnLongClickListener(new UpDownLongClickListener());


        tipUpButton = (CustomButton) findViewById(R.id.tipUpButtonId);
        tipUpButton.setOnClickListener(new UpDownButtonListener());
        tipUpButton.setOnTouchListener(new UpDownTouchListener());
        tipUpButton.setOnLongClickListener(new UpDownLongClickListener());


        noOfPeopleDownButton = (CustomButton) findViewById(R.id.noOfPeopleDownButtonId);
        noOfPeopleDownButton.setOnClickListener(new UpDownButtonListener());
        noOfPeopleDownButton.setOnTouchListener(new UpDownTouchListener());
        noOfPeopleDownButton.setOnLongClickListener(new UpDownLongClickListener());

        noOfPeopleUpButton = (CustomButton) findViewById(R.id.noOfPeopleUpButtonId);
        noOfPeopleUpButton.setOnClickListener(new UpDownButtonListener());
        noOfPeopleUpButton.setOnTouchListener(new UpDownTouchListener());
        noOfPeopleUpButton.setOnLongClickListener(new UpDownLongClickListener());

        finalTipEditText = (EditText) findViewById(R.id.finalTipEditText);
        finalTotalEditText = (EditText) findViewById(R.id.finalTotalEditText);

        perPersonAmountEditText = (EditText) findViewById(R.id.perPersonEditText);


        billEditText.addTextChangedListener(new BillTotalTextWatchListener());

        mainView.setOnTouchListener(new TouchListener());

        billEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        roundSwitch = (Switch)findViewById(R.id.togglebutton);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences.contains(TIP_PREF)) {
            customTipValue = preferences.getInt(TIP_PREF, 15);
        }
        customTipTextView.setText(customTipValue + "");
        if(preferences.contains(ROUND_PREF)) {
            roundTotal = preferences.getBoolean(ROUND_PREF, false);
            roundSwitch.setChecked(roundTotal);
        }


        hideCursor = (LinearLayout) findViewById(R.id.focusId);

        // Create an ad.
//        adView = new AdView(this);
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(AD_UNIT_ID);
//
//        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutId);
//        layout.addView(adView);
//
//        // Create an ad request. Check logcat output for the hashed device ID to
//        // get test ads on a physical device.
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
//                .build();
//
//        // Start loading the ad in the background.
//        adView.loadAd(adRequest);


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

    int years[] = new int[]{2012, 2013, 2014};

    private String getRandomDate() {
        Random random = new Random();
        int year = years[random.nextInt(2)];
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(27) + 1;
        return month + "/" + day + "/" + year;
    }

    private void saveTip(int i) {
        DatabaseConnector databaseConnector = new DatabaseConnector(TipCalculator.this);
        Tip tip = new Tip();
        tip.withPlace("test " + i)
                .withDate(getRandomDate())
                .withBillAmount(Double.valueOf(14 * i * 0.33))
                .withTipAmount(14D);
        databaseConnector.insertTip(tip);
    }


    private void exportDb() throws Exception {
        //Open your local db as the input stream
        String inFileName = "/data/data/com.calculator.tipcalculator/databases/UserTips";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory() + "/UserTips";
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    private void updateTipValue() {
        customTip = (billTotal * customTipValue * 0.01);
        finalTotal = customTip + billTotal;

        System.out.println("Round total "+Math.round(finalTotal ));
        System.out.println("Round Tip "+Math.round(customTip));
        System.out.println("new Round Tip "+ (Math.round(finalTotal)-billTotal ));
        if(roundTotal) {
            finalTotal = Math.round(finalTotal);
            customTip = finalTotal - billTotal;
        }

        finalTipEditText.setText(getFormattedString(customTip));
        finalTotalEditText.setText(getFormattedString(finalTotal));
    }

    private String getFormattedString(double value) {
        return String.format("%.2f", value);
    }

    private void updateCustomTipValue() {
        customTipTextView.setText(customTipValue + "");
        if (billTotal > 0) {
            updateTipValue();
            calculatePerPersonTotal();
        }
    }

    private void updatePerPersonTotal() {
        noOfPeopleTextView.setText(noOfPeopleValue + "");
        calculatePerPersonTotal();
    }

    private void calculatePerPersonTotal() {
        if (billTotal > 0 && finalTotal > 0 && noOfPeopleValue > 0) {
            perPersonAmountEditText.setText(getFormattedString(finalTotal / noOfPeopleValue));
        } else {
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
        viewTipsMenuItem = menu.getItem(0);
        try {
            Boolean aBoolean = new RowExist().execute((Object[]) null).get();
            if (aBoolean) {
                viewTipsMenuItem.setEnabled(true);
            } else {
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
            case R.id.tipPreference:
                createPreferenceDialog();
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

    private void createPreferenceDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.tip_pref, null);

        final EditText input = (EditText) view.findViewById(R.id.prefEditTextId);
        final Switch sw = (Switch) view.findViewById(R.id.togglePreferenceButton);
        sw.setChecked(roundTotal);
        AlertDialog.Builder builder = new AlertDialog.Builder(TipCalculator.this);
        builder.setTitle(R.string.tipPrefTitle);
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = TipCalculator.this.getPreferences(MODE_PRIVATE);
                if (input.getText().length() > 0) {
                    customTipValue = Integer.parseInt(input.getText().toString());
                    preferences.edit().putInt(TIP_PREF, customTipValue).commit();
                    customTipTextView.setText(input.getText().toString());
                    updateCustomTipValue();
                }
                if(sw.isChecked()) {
                    roundTotal = true;
                    preferences.edit().putBoolean(ROUND_PREF,roundTotal).commit();
                    roundSwitch.setChecked(roundTotal);
                    updateCustomTipValue();
                }else {
                    roundTotal = false;
                    roundSwitch.setChecked(roundTotal);
                    preferences.edit().putBoolean(ROUND_PREF,roundTotal).commit();
                    updateCustomTipValue();
                }
            }
        }).setNegativeButton("Cancel", null).show();

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


    public void onToggleClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            roundTotal = on;
            updateCustomTipValue();
        } else {
            roundTotal = on;
            updateCustomTipValue();
        }
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

    class UpDownButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tipDownButtonId:
                    customTipValue -= 1;
                    updateCustomTipValue();
                    break;
                case R.id.tipUpButtonId:
                    customTipValue += 1;
                    updateCustomTipValue();
                    break;
                case R.id.noOfPeopleDownButtonId:
                    noOfPeopleValue -= 1;
                    updatePerPersonTotal();
                    break;
                case R.id.noOfPeopleUpButtonId:
                    noOfPeopleValue += 1;
                    updatePerPersonTotal();
                    break;
            }
        }
    }

    private Handler repeatUpdateHandler = new Handler();

    private boolean tipAutoIncrement = false;
    private boolean tipAutoDecrement = false;
    private boolean peopleAutoIncrement = false;
    private boolean peopleAutoDecrement = false;
    private long REP_DELAY = 100;

    class RepeatUpdater implements Runnable {
        public void run() {
            if (tipAutoIncrement) {
                customTipValue += 1;
                updateCustomTipValue();
                repeatUpdateHandler.postDelayed(new RepeatUpdater(), REP_DELAY);
            } else if (tipAutoDecrement) {
                customTipValue -= 1;
                updateCustomTipValue();
                repeatUpdateHandler.postDelayed(new RepeatUpdater(), REP_DELAY);
            } else if (peopleAutoIncrement) {
                noOfPeopleValue += 1;
                updatePerPersonTotal();
                repeatUpdateHandler.postDelayed(new RepeatUpdater(), REP_DELAY);
            } else if (peopleAutoDecrement) {
                noOfPeopleValue -= 1;
                updatePerPersonTotal();
                repeatUpdateHandler.postDelayed(new RepeatUpdater(), REP_DELAY);
            }
        }
    }

    class UpDownLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.tipDownButtonId:
                    tipAutoDecrement = true;
                    break;
                case R.id.tipUpButtonId:
                    tipAutoIncrement = true;
                    break;
                case R.id.noOfPeopleDownButtonId:
                    peopleAutoDecrement = true;
                    break;
                case R.id.noOfPeopleUpButtonId:
                    peopleAutoIncrement = true;
                    break;
            }
            repeatUpdateHandler.post(new RepeatUpdater());
            return false;
        }
    }

    class UpDownTouchListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyboard();
            if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    && (tipAutoIncrement || tipAutoDecrement || peopleAutoIncrement || peopleAutoDecrement)) {
                tipAutoIncrement = false;
                tipAutoDecrement = false;
                peopleAutoIncrement = false;
                peopleAutoDecrement = false;
            }
            return false;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hideKeyboard();
            billEditText.setNextFocusDownId(R.id.focusId);
            billEditText.clearFocus();
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
