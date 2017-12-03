package com.ainet.enirobot.ezburgerainet;

import android.app.Activity;
import android.widget.Toast;

/**
 * 그냥 뒤로누르는거
 */
public class CloseProcess {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public CloseProcess(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
