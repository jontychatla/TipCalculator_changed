package com.calculator.tipcalculator;

import android.app.*;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.model.Tip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.OnClickListener;

/**
 * Created by bharatkc on 2/2/14.
 */
public class SaveTipActivity extends Activity {

    private EditText placeEditText;
    private EditText dateEditText;
    private EditText billAmountEditText;
    private EditText tipAmountEditText;
    private Button saveButton;
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-mm-dd");
    private SimpleDateFormat viewFormat = new SimpleDateFormat("dd/mm/yyyy");
    private Spinner ethnicSpinner;
    private GridLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_tip_layout);
        mainView = (GridLayout)findViewById(R.id.saveTipMainViewTableLayoutId);
        placeEditText = (EditText) findViewById(R.id.placeEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        //ethnicSpinner = (Spinner) findViewById(R.id.ethnicSpinner);
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        tipAmountEditText = (EditText) findViewById(R.id.tipAmountEditText);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonListener);
        Bundle extras = getIntent().getExtras();
        setDefaultDate();
        setBillAmounts(extras);
        //loadEthnicSpinner();
        mainView.setOnTouchListener(new TouchListener());
    }

    private void loadEthnicSpinner() {
        List<String> ethnic = new ArrayList<String>();
        ethnic.add("American");
        ethnic.add("Indian");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ethnic);
        ethnicSpinner.setAdapter(adapter);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_add_to_save_screen,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        dateEditText.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
    }

    private void setBillAmounts(Bundle extras) {
        if (null != extras && extras.containsKey("billAmount") && extras.containsKey("tipAmount")) {
            billAmountEditText.setText(extras.getString("billAmount"));
            tipAmountEditText.setText(extras.getString("tipAmount"));
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragement datePickerFragement = new DatePickerFragement();
        datePickerFragement.show(getFragmentManager(), "datePicker");
    }

    public void setDateTextField(int year, int month, int day) {
        dateEditText.setText((month + 1) + "/" + day + "/" + year);
    }

    public class DatePickerFragement extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDateTextField(year, month, day);
        }
    }

    OnClickListener saveButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != placeEditText.getText() && null != dateEditText.getText() && (placeEditText.getText().length() > 0 && dateEditText.getText().length() > 0)) {
                AsyncTask<Object, Object, Object> saveTipTask = new AsyncTask<Object, Object, Object>() {

                    @Override
                    protected Object doInBackground(Object... objects) {
                        saveTip();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        finish();
                    }
                };

                saveTipTask.execute((Object[]) null);
            } else {
                if(placeEditText.getText().length()==0) {
                    checkEmptyFields(R.string.placeTitle, R.string.placeMessage);
                }else if(dateEditText.getText().length() == 0) {
                    checkEmptyFields(R.string.dateTitle, R.string.dateMessage);
                }
            }
        }
    };

    private void checkEmptyFields(int title, int message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SaveTipActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", null);
        dialog.show();
    }


    private void saveTip() {
        DatabaseConnector databaseConnector = new DatabaseConnector(SaveTipActivity.this);
        Tip tip = new Tip();
        tip.withPlace(getString(placeEditText))
                .withDate(getString(dateEditText))
                .withBillAmount(getDouble(billAmountEditText))
                .withTipAmount(getDouble(tipAmountEditText));
        databaseConnector.insertTip(tip);
    }

    private String getString(EditText editText) {
        if (null != editText.getText())
            return editText.getText().length() > 0 ? editText.getText().toString() : null;
        return null;
    }

    private Double getDouble(EditText editText) {
        return (null != getString(editText)) ? Double.parseDouble(editText.getText().toString()) : 0.0;
    }

    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return false;
        }
    }

}
