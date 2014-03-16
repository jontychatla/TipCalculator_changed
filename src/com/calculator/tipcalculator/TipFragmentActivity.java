package com.calculator.tipcalculator;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.calculator.tipcalculator.fragment.TipDetailFragment;
import com.calculator.tipcalculator.model.Tip;

public class TipFragmentActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_view);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_profile, TipDetailFragment.getInstance((Tip) getIntent().getSerializableExtra("test")))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

//
//        Fragment fragment = getFragmentManager().findFragmentByTag("list");
//        if(null == fragment){
//            fragment = TestFragment.getInstance();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.add(android.R.id.content, fragment, "list");
//            ft.commit();
//        }
    }
}
