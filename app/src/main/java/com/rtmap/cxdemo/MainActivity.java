package com.rtmap.cxdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rtmap.library.net.CNet;
import com.rtmap.library.net.NetTask;
import com.rtmap.library.net.Response;
import com.rtmap.library.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public static final String NEW_BASE_URL = "http://www.weather.com.cn/adat/sk/101010100.html";
    public static final String DITU = "http://gc.ditu.aliyun.com/regeocoding";
    public static final String GEO = "http://gc.ditu.aliyun.com/geocoding";
    public static final String TAO = "http://ip.taobao.com/service/getIpInfo.php?ip=63.223.108.42";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        setListener();
    }


    public void setListener() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetToString();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPostToString();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPostToString();
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void doPostToString() {
        Map map = new HashMap();
        map.put("l", "39.938133,116.395739");
        map.put("type", "001");
        map.put("a", "北京市");
        CNet net = new CNet();
//        net.useCache(CachePolicy.POLICY_CACHE_AndRefresh);
        net.setUrl(GEO);
        net.addParams(map);
        net.post(new NetTask(this) {
            @Override
            public void onSuccess(Response response, Integer transfer) {
                textView.setText("result=" + response.getResult());
                LogUtils.e(response.getResult());
            }
        });
    }

    public void doGetToString() {
        CNet net = new CNet();
        net.setUrl(TAO);
//        net.useCache(CachePolicy.POLICY_CACHE_AndRefresh);
        net.get(new NetTask(this) {
            @Override
            public void onSuccess(Response response, Integer transfer) {
                textView.setText("result=" + response.getResult());
                LogUtils.e(response.getResult());
//                LogUtils.e(response.getObjectData());
//                LogUtils.e(response.getObject(TestBean.class).getData());
            }

            @Override
            public void doInBackground(Response response) {
                super.doInBackground(response);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
