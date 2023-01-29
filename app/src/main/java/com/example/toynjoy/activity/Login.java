package com.example.toynjoy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.toynjoy.R;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;

import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        MyUlits.SystemNotificationDIY(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        context = Login.this.getApplicationContext();

        setLoginButtonClick();
        SetSigninClick();
    }

    private EditText username;
    private EditText password;
    private Context context;

    private void setLoginButtonClick() {
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();

                if (!usernameStr.equals("") && !passwordStr.equals("")) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", usernameStr);
                    params.put("password", passwordStr);

                    MyVolleyHelper.HttpResponsePost(context, "login", params, new Function1<ServiceResult, Void>() {
                        @Override
                        public Void invoke(ServiceResult serviceResult) {

                            SharedPreferences settings = getSharedPreferences("loginUser", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("jsonData",serviceResult.getData().toString());
                            editor.commit();

                            startActivity(new Intent(Login.this, ToyNJoyMain.class));

                            //使登录界面无法被返回
                            finish();

                            return null;
                        }
                    });
                }
                else {
                    Toast.makeText(Login.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetSigninClick(){
        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ToyNJoyMain.class));
        finish();
    }
}