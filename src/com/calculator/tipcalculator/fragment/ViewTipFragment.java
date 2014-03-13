package com.calculator.tipcalculator.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.calculator.tipcalculator.R;
import com.calculator.tipcalculator.TipFragmentActivity;
import com.calculator.tipcalculator.dao.DatabaseConnector;
import com.calculator.tipcalculator.model.Tip;
import com.calculator.tipcalculator.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bharatkc on 2/22/14.
 */
public class ViewTipFragment extends ListFragment implements AdapterView.OnItemClickListener {


    private FragmentListAdapter fragmentListAdapter;

    public static ViewTipFragment getInstance() {
        return new ViewTipFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListHeader();
        fragmentListAdapter = new FragmentListAdapter(getActivity(), getTips());
        setListAdapter(fragmentListAdapter);
    }

    private void setupListHeader() {
        getListView().addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.header_view_tips, null), null, false);
        getListView().setDrawSelectorOnTop(true);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getActivity(), TipFragmentActivity.class).putExtra("test",fragmentListAdapter.getItem(position-1)));
    }

    private static class FragmentListAdapter extends ArrayAdapter<Tip> {
        static final int layoutId = R.layout.item_view_tips;
        LayoutInflater layoutInflater;
        int evenBackgroundColor;

        public FragmentListAdapter(Context context, List<Tip> objects) {
            super(context, layoutId, objects);
            layoutInflater = LayoutInflater.from(context);
            evenBackgroundColor = context.getResources().getColor(R.color.gray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(layoutId, null);
            }
            Tip data = getItem(position);
            TextView place = (TextView) convertView.findViewById(R.id.placeFragment);
            TextView date = (TextView) convertView.findViewById(R.id.dateFragment);
            TextView billAmount = (TextView) convertView.findViewById(R.id.billFragment);
            TextView tipAmount = (TextView) convertView.findViewById(R.id.tipFragment);
            place.setText(data.getPlace());
            date.setText(data.getDate());
            billAmount.setText(Utility.getFormattedMoneyString(data.getBillAmount()));
            tipAmount.setText(Utility.getFormattedMoneyString(data.getTipAmount()));
            convertView.setBackgroundColor((position %2 ==0) ? Color.TRANSPARENT : evenBackgroundColor);
            return convertView;
        }
    }

    private List<Tip> getTips() {
        DisplayTip displayTip = new DisplayTip();
        displayTip.execute((Object[]) null);
        try {
            return displayTip.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<Tip>();
    }

    class DisplayTip extends AsyncTask<Object, Object, List<Tip>> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        @Override
        protected List<Tip> doInBackground(Object... objects) {
            databaseConnector.open();
            return databaseConnector.getAllTips();
        }

        @Override
        protected void onPostExecute(List<Tip> tips) {
            databaseConnector.close();
        }
    }
}
