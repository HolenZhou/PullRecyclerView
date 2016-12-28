package com.holenzhou.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_common_list).setOnClickListener(this);
        findViewById(R.id.btn_empty_list).setOnClickListener(this);
        findViewById(R.id.btn_footer_list).setOnClickListener(this);
        findViewById(R.id.btn_grid_list).setOnClickListener(this);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.app_name);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_common_list:
                intent = new Intent(this, CommonListActivity.class);
                break;
            case R.id.btn_empty_list:
                intent = new Intent(this, EmptyListActivity.class);
                break;
            case R.id.btn_footer_list:
                intent = new Intent(this, FooterListActivity.class);
                break;
            case R.id.btn_grid_list:
                intent = new Intent(this, GridListActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
