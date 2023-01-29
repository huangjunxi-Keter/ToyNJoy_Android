package com.example.toynjoy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.toynjoy.R;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.entity.User_Info;
import com.example.toynjoy.entity.Users;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class UserInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        context = getApplicationContext();

        intent = new Intent(UserInfo.this, ToyNJoyMain.class);

        MyUlits.SystemNotificationDIY(this);
        MyUlits.setToLastPageClick(this);
        LoadingUserInfo();
        setLogoutClick();
    }

    private Context context;
    private Intent intent;

    private Users loginUser;
    private User_Info loginUserInfo;

    private void LoadingUserInfo() {

        loginUser = MyUlits.getloginUser(this);

        if (loginUser != null) {
            Map<String, String> params = new HashMap<>();
            params.put("username", loginUser.getUsername());


            MyVolleyHelper.HttpResponsePost(context, "getInfoByUsername", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {

                    loginUserInfo = MyUlits.gson.fromJson(serviceResult.getData(), User_Info.class);

                    ShapeableImageView userPortrait = findViewById(R.id.info_portrait);
                    MyVolleyHelper.LoadingImage(context, userPortrait, loginUser.getVirtualImage());
                    ((TextView) findViewById(R.id.info_nickname)).setText(loginUser.getNickname());
                    ((TextView) findViewById(R.id.info_gender)).setText(loginUserInfo.getGender());
                    ((TextView) findViewById(R.id.info_birthday)).setText(loginUserInfo.getBirthday());
                    ((TextView) findViewById(R.id.info_signature)).setText(loginUserInfo.getSignature());

                    return null;
                }
            });
        }
    }

    private void setLogoutClick() {
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("loginUser", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();

                startActivity(intent);
                finish();
            }
        });
    }

}