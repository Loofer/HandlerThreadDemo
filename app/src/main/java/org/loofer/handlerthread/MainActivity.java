package org.loofer.handlerthread;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //后台线程
    private HandlerThread mCheckMsgThread;
    //后台线程handler
    private Handler mCheckMsgHandler;
    private boolean isUpdateInfo;

    private static final int MSG_UPDATE_INFO = 0x110;

    private Handler mHandler = new Handler();
    private TextView mTvInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        initBackInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUpdateInfo = true;
        mCheckMsgHandler.sendEmptyMessage(MSG_UPDATE_INFO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUpdateInfo = false;
        mCheckMsgHandler.removeMessages(MSG_UPDATE_INFO);
    }

    //初始化后台线程
    private void initBackInfo() {
        mCheckMsgThread = new HandlerThread("start-to-check-info");
        mCheckMsgThread.start();
        mCheckMsgHandler = new Handler(mCheckMsgThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                checkForUpdate();
                if (isUpdateInfo) {
                    mCheckMsgHandler.sendEmptyMessageDelayed(MSG_UPDATE_INFO, 1000);
                }
            }
        };

    }

    private void checkForUpdate() {
        SystemClock.sleep(1000);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String result = "实时更新中，当前大盘指数：<font color='red'>%d</font>";
                result = String.format(result, (int) (Math.random() * 3000 + 1000));
                mTvInfo.setText(Html.fromHtml(result));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mCheckMsgThread != null) {
            mCheckMsgThread.quit();
            mCheckMsgThread = null;
        }
    }

}
