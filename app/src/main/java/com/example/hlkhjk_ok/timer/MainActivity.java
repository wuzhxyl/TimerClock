package com.example.hlkhjk_ok.timer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "Timer";
    private static String pkg = "";
    private List<appModelInfo> appList = new ArrayList<>();
    private List<appModelInfo> systemList = new ArrayList<>();

    private ListView listview;
    private applistAdapater adapater;
    private TimePicker timer;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.start);
        startBtn.setOnClickListener(this);
        timer = (TimePicker) findViewById(R.id.timer);
        adapater = new applistAdapater(this, R.layout.application_item, appList);
        listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapater);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int lastposition = adapater.getPosition();
                //相同则跳过
                if (position == lastposition) {return ;}

                //取消上一个
                if (lastposition >= 0) { appList.get(lastposition).setSel(false); }

                //保存当前位置
                adapater.setPosition(position);

                //缓冲值
                appModelInfo info = appList.get(position);
                info.setSel(true);

                //通知改变
                adapater.notifyDataSetChanged();
            }
        });

        timer.setIs24HourView(false);
        getApplicationList();
    }

    public void setFullScreen() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = this.getSupportActionBar();
            if (actionBar != null) actionBar.hide();
        }
    }

    public void initData() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int hour = pref.getInt("hour", 8);
        int minute = pref.getInt("minute", 0);
        int second = 60;

        timer.setHour(hour);
        timer.setMinute(minute);

        pkg = pref.getString("pkg", "");

        if (pkg.length() > 0) {
            for(appModelInfo info:appList) {
                if (pkg.equals(info.getPkg())) {
                    info.setSel(true);
                    break;
                }
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void getApplicationList() {
        List<ApplicationInfo> applicationInfos;
        PackageManager manager = getPackageManager();
        applicationInfos = manager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);

        for(ApplicationInfo info: applicationInfos) {
            if((info.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) { //非系统程序
                appList.add(new appModelInfo(manager.getApplicationLabel(info).toString(), false, drawableToBitmap(manager.getApplicationIcon(info)), info.packageName));
            } else {
                systemList.add(new appModelInfo(manager.getApplicationLabel(info).toString(), false, drawableToBitmap(manager.getApplicationIcon(info)), info.packageName));
            }
        }

        initData();
        adapater.notifyDataSetChanged();
    }

    public long getTargetMS(int hour, int minute) {
        long currentmills = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        int currHour = c.get(Calendar.HOUR_OF_DAY);
        int currMinute = c.get(Calendar.MINUTE);
        int currSeconds = c.get(Calendar.SECOND);

        long currentMS = currentmills-(currHour*60*60*1000+currMinute*60*1000+(currSeconds)*1000);
        long targetMS  = currentMS + hour*3600*1000 + minute*60*1000;

        if (targetMS < currentmills) { targetMS = targetMS + 24 * 60 * 60 * 1000; }

        return targetMS;
    }

    public long getDelayMills(int hour, int minute) {
        long targetMills = getTargetMS(hour, minute);
        return (targetMills-System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            int position = adapater.getPosition();
            if (position < 0 || !appList.get(position).isSel()) { return; }

            int hour = timer.getHour();
            int minute = timer.getMinute();
            pkg = appList.get(adapater.getPosition()).getPkg();

            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putInt("hour", hour);
            editor.putInt("minute", minute);
            editor.putString("pkg", pkg);
            editor.commit();

            // 定时器部分
            long delayMills = getDelayMills(hour, minute);
            Log.d(TAG, "onClick: delayTime(s) " + delayMills/1000);
            Timer tmSchedule = new Timer(true); //变身守护线程
            tmSchedule.schedule(new MyTimer(), delayMills, 24 * 60 * 60 * 1000); // TODO: 间隔多少后执行， 频率为24小时
        }
    }

    public class MyTimer extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "run: 哟， 要打开了 ..");
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (null != intent) { startActivity(intent); }
        }
    }
}