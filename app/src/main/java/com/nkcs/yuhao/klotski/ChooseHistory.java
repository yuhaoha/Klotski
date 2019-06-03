package com.nkcs.yuhao.klotski;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChooseHistory extends AppCompatActivity {
    public static ChooseHistory context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_history);
        context = this;
    }

    public static ChooseHistory getActivity()
    {
        return context;
    }
}
