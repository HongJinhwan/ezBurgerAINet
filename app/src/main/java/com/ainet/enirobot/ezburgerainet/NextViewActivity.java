package com.ainet.enirobot.ezburgerainet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jsjs4 on 2016-04-12.
 */
public class NextViewActivity extends AppCompatActivity {
    private CloseProcess backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView txt_code;

        setContentView(R.layout.code_main);
        backPressCloseHandler = new CloseProcess(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbartitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFF));

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.RED);
        }

        Intent intent = getIntent();
        String resultCode = intent.getStringExtra("code");
        resultCode = resultCode + "    ";
        txt_code = (TextView)findViewById(R.id.resultcode);
        txt_code.setText(resultCode);

        Button btnMy = (Button) findViewById(R.id.againBt);
        btnMy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NextViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
