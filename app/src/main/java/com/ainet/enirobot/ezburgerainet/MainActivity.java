package com.ainet.enirobot.ezburgerainet;

// Author : Moon
// Version 1.0.1

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private CloseProcess backPressCloseHandler;
    Button btnMy;
    Button btnStartCam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new CloseProcess(this);

        // 액션바 커스텀
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbartitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFF));

        // 상태바 색 바꾸는 코드
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.RED);
        }


        final EditText editText=(EditText)findViewById(R.id.editText);
        btnMy = (Button) findViewById(R.id.btn_my);
        btnStartCam = (Button) findViewById(R.id.btn_startCam);

        btnMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inPutText = editText.getText().toString();
                ConnectThread connect = new ConnectThread(inPutText);

                // 입력 키보드 내리는 코드
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // 웹 파싱 및 웹 요청 스레드 시작
                if (editText.length() == 16) {
                    connect.execute();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<big>코드의 길이를 확인해주세요.<big>"), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btnStartCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    // 웹에 접속하는 스레드 클래스
    class ConnectThread extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressCyle = new ProgressDialog(MainActivity.this);
        ProgressDialog progressBar = new ProgressDialog(MainActivity.this);

        private int check = 0;

        private String url = "https://kor.tellburgerking.com/";
        private Elements textview;

        private Map<String, String> cookies;
        private int i = 0;

        private String et;

        public ConnectThread(String label){
            et = label;
        }

        @Override
        protected void onPreExecute() { //실행 딱 되면 이게 실행됨
            progressCyle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressCyle.setMessage(Html.fromHtml("<big>입력 코드 확인 중입니다..<big>"));
            progressCyle.setCanceledOnTouchOutside(false);
            progressCyle.setCancelable(false);

            // show dialog
            progressCyle.show();

            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArrayList<String> arraytemp = new ArrayList<String>();
                ArrayList<String> array = new ArrayList<String>();
                int count = 0;
                String plusUrl = null;

                // 쿠키 얻는 요청
                Connection.Response res = Jsoup.connect(url).method(Connection.Method.GET).execute();
                cookies = res.cookies();

                // 쿠키를 사용해서 다음으로 넘어간다.
                plusUrl = init();
                array = inputCupon(plusUrl);

                // 제대로된 코드 입력시 실행 예외처리
                if (array.get(0) != "-1") {
                    publishProgress(0);

                    plusUrl = array.get(0);

                    while (check == 0) {
                        arraytemp = submit(plusUrl, array);

                        if (i == 1) {
                            break;
                        } else {
                            array.clear();
                            plusUrl = arraytemp.get(0);
                            array = (ArrayList<String>) arraytemp.clone();
                            arraytemp.clear();

                            // 진행바를 보여주는 곳
                            count += 3;
                            if (count < 90) {
                                progressBar.setProgress(count);
                            } else {
                                progressBar.setProgress(100);
                            }
                        }
                    }

                    if (check == 1) {
                        cancel(true);
                    }
                } else {
                    crashNum(0);
                }
            } catch (IOException e) {
                crashNum(1);
            }

            return null;
        }

        public String init() throws IOException { //계속 버튼 누르고 다음창으로 넘어간거
            String plusUrl;

            Document doc = Jsoup.connect(url)
                    .cookies(cookies)
                    .get();
            textview = doc.select("form#surveyEntryForm");
            plusUrl = textview.attr("action");

            return connect(plusUrl);
        }

        public String connect(String plusUrl) throws IOException {
            Document doc = Jsoup.connect(url + plusUrl)
                    .cookies(cookies)
                    .data("JavaScriptEnabled", "1")
                    .data("FIP", "True")
                    .data("AcceptCookies", "Y")
                    .post();
            textview = doc.select("form#surveyEntryForm");

            return textview.attr("action");
        }

        public ArrayList<String> inputCupon(String plusUrl) throws IOException {
            ArrayList<String> arrayValues = new ArrayList<String>();

            Document doc = Jsoup.connect(url + plusUrl)
                    .cookies(cookies)
                    .data("JavaScriptEnabled", "1")
                    .data("FIP", "True")
                    .data("FIP", "True")
                    .data("CN1", et.substring(0, 3))
                    .data("CN2", et.substring(3, 6))
                    .data("CN3", et.substring(6, 9))
                    .data("CN4", et.substring(9, 12))
                    .data("CN5", et.substring(12, 15))
                    .data("CN6", et.substring(15, 16))
                    .post();
            textview = doc.select("div.Error");
            if(!textview.isEmpty()) {
                arrayValues.add("-1");

                return arrayValues;
            }

            textview = doc.select("form#surveyForm");
            arrayValues.add(textview.attr("action"));

            textview = doc.select("input#IoNF");
            arrayValues.add(textview.attr("value"));

            textview = doc.select("input#PostedFNS");
            arrayValues.add(textview.attr("value"));

            return arrayValues;
        }

        public ArrayList<String> submit(String plusUrl, ArrayList<String> array) throws IOException {
            ArrayList<String> arrayValues = new ArrayList<String>();

            Document doc = Jsoup.connect(url + plusUrl)
                    .cookies(cookies)
                    .data("IoNF", array.get(1))
                    .data("PostedFNS", array.get(2))
                    .timeout(0)
                    .post();
            textview = doc.select("form#surveyForm");
            arrayValues.add(textview.attr("action"));

            textview = doc.select("input#IoNF");
            arrayValues.add(textview.attr("value"));

            textview = doc.select("input#PostedFNS");
            arrayValues.add(textview.attr("value"));

            textview = doc.select("p.ValCode");

            if(!textview.isEmpty()) {
                i = 1;
            }

            return arrayValues;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            // Back button push
            progressBar.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    check = 1;
                }
            });

            if(values[0] == 0) {
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setMessage(Html.fromHtml("<big>세트 업 중입니다..<big>"));
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.setCancelable(true);

                // show dialog
                progressBar.show();

                progressCyle.dismiss();
            }
            else if(values[0] == 1) {
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<big>코드를 다시 확인해주세요.<big>"), Toast.LENGTH_SHORT);
                toast.show();

                cancel(true);
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<big>데이터 연결을 확인해주세요.<big>"), Toast.LENGTH_SHORT);
                toast.show();

                cancel(true);
            }

            super.onProgressUpdate();
        }

        public void crashNum(int datacheck) {
            progressCyle.dismiss();

            if(datacheck == 0) {
                publishProgress(1);
            }
            else {
                publishProgress(-1);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(MainActivity.this, NextViewActivity.class);

            progressBar.dismiss();

            intent.putExtra("code", textview.text());
            startActivity(intent);
            finish();
        }
    }
}