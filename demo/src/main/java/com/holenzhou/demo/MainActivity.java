package com.holenzhou.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_common_list).setOnClickListener(this);
        findViewById(R.id.btn_header_list).setOnClickListener(this);
        findViewById(R.id.btn_empty_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_common_list:
                intent = new Intent(this, CommonListActivity.class);
                break;
            case R.id.btn_header_list:
                intent = new Intent(this, HeaderListActivity.class);
                break;
            case R.id.btn_empty_list:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
