package com.qq.e.union.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qq.e.union.demo.adapter.test.activity.MediationTestActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class DemoListActivity extends AppCompatActivity {

    /**
     * key : view id
     * pair.first: button content
     * pair.second: intent action
     */
    private static Map<String, Pair<String, String>> launcherMap = new HashMap<>();

    public static void register(String action, String id, String content) {
        launcherMap.put(action, new Pair<>(id, content));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
        // 如果targetSDKVersion >= 23，建议动态申请权限。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "广点通，结盟而赢", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_muid) {
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_mediation_tool) {
            Intent intent = new Intent(this, MediationTestActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private View createContentView() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
        linearLayout.setPadding(padding, padding, padding, padding);
        for (Map.Entry<String, Pair<String, String>> entry : launcherMap.entrySet()) {
            final String action = entry.getKey();
            final Pair<String, String> pair = entry.getValue();
            Button button = new Button(this);
            button.setId(getResources().getIdentifier(pair.first, "id", getPackageName()));
            button.setText(pair.second);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(action);
                intent.setPackage(getPackageName());
                DemoListActivity.this.startActivity(intent);
            });
            linearLayout.addView(button,
                    new LinearLayout.LayoutParams(
                            MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(linearLayout, new FrameLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT));
        return scrollView;
    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么建议动态申请相关权限，再调用广点通SDK
     * <p>
     * SDK不强制校验下列权限（即:无下面权限sdk也可正常工作），但建议开发者申请下面权限，尤其是READ_PHONE_STATE权限
     * <p>
     * READ_PHONE_STATE权限用于允许SDK获取用户标识,
     * 针对单媒体的用户，允许获取权限的，投放定向广告；不允许获取权限的用户，投放通投广告，媒体可以选择是否把用户标识数据提供给优量汇，并承担相应广告填充和eCPM单价下降损失的结果。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (lackedPermission.size() != 0) {
            // 建议请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && !hasAllPermissionsGranted(grantResults)) {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
}
