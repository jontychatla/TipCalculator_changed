package com.calculator.tipcalculator.fragment;
import static com.calculator.tipcalculator.util.Utility.*;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.calculator.tipcalculator.R;
import com.calculator.tipcalculator.model.Tip;
import com.calculator.tipcalculator.util.Utility;
import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bharatkc on 2/23/14.
 */
public class TipDetailFragment extends Fragment {

    private Tip data;
    private TextView placeTextView;
    private TextView dateTextView;
    private BarGraph tipBarGraph;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat osdf = new SimpleDateFormat("MMM, dd yyyy");

    public TipDetailFragment(Tip data) {
        this.data = data;

    }

    public static TipDetailFragment getInstance(Tip tip) {
        return new TipDetailFragment(tip);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tip_detail, null);
        placeTextView = (TextView) view.findViewById(R.id.place_detail);
        dateTextView = (TextView) view.findViewById(R.id.date_detail);
        tipBarGraph = (BarGraph) view.findViewById(R.id.tip_bar_graph);
        bindViewData();
        return view;
    }

    private void bindViewData() {
        placeTextView.setText(data.getPlace());
        try {
            Date d = sdf.parse(data.getDate());
            dateTextView.setText(osdf.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<Bar> points = new ArrayList<Bar>();
        Bar barBillAmount = new Bar();
        barBillAmount.setColor(getResources().getColor(R.color.green));
        barBillAmount.setName("Bill Amount");
        barBillAmount.setValue(data.getBillAmount().floatValue());
        barBillAmount.setValueString(getFormattedMoneyString(data.getBillAmount()));
        points.add(barBillAmount);

        Bar barTipAmount = new Bar();
        barTipAmount.setColor(getResources().getColor(R.color.red));
        barTipAmount.setName("Tip Amount");
        barTipAmount.setValue(data.getTipAmount().floatValue());
        barTipAmount.setValueString(getFormattedMoneyString(data.getTipAmount()));
        points.add(barTipAmount);

        Bar totalAmount = new Bar();
        totalAmount.setColor(getResources().getColor(R.color.red));
        totalAmount.setName("Total Amount");
        double totalAmt = data.getBillAmount() + data.getTipAmount();
        totalAmount.setValue((float)totalAmt);
        totalAmount.setValueString(getFormattedMoneyString(totalAmt));
        points.add(totalAmount);

        Bar barPercentage = new Bar();
        barPercentage.setColor(getResources().getColor(R.color.darker_gray));
        barPercentage.setName("Tip %");
        if (data.getTipAmount() > 0) {
            Double tipPercentage = data.getTipAmount() * 100 / data.getBillAmount();

            barPercentage.setValue(tipPercentage.floatValue());
            barPercentage.setValueString(getFormattedMoneyString(tipPercentage) + "%");
            points.add(barPercentage);
        }

        tipBarGraph.setScaleX(0.8f);
        tipBarGraph.setBars(points);

    }

}
