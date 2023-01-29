package com.example.toynjoy.uilts;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.toynjoy.R;
import com.example.toynjoy.activity.Login;
import com.example.toynjoy.entity.Users;
import com.google.gson.Gson;

import java.util.Random;

public class MyUlits {

    public static Gson gson = new Gson();

    public static void SystemNotificationDIY(Activity activity) {
        //隐藏信息栏（不再占位）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                //window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                //attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static Users getloginUser(Activity activity) {
        Users loginUser = null;
        SharedPreferences settings = activity.getSharedPreferences("loginUser", MODE_PRIVATE);
        String settingsUserdata = settings.getString("jsonData", "").toString();
        if (!settingsUserdata.equals(""))
            loginUser = gson.fromJson(settingsUserdata, Users.class);

        return loginUser;
    }

    public static void MyStartActivity(Activity activity, Intent intent , boolean isNeedLogin) {
        if (isNeedLogin) {
            if (getloginUser(activity) == null) {
                intent = new Intent(activity, Login.class);
            }
        }
        activity.startActivity(intent);
    }

    public static String getPriceText(double price){
        String result = "";
        if (price > 0) {
            result = "￥" + price;
        }
        else {
            result = "免费";
        }
        return result;
    }

    public static void setToLastPageClick(Activity activity) {
        activity.findViewById(R.id.toLastPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    public static String GenerateVerificationCode(){
        String result = "";

        Random random = new Random();
        for (int i = 0; i < 5; i++){
            int num = random.nextInt(10);
            result += num;
        }

        return result;
    }

}
