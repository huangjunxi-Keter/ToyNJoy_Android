package com.example.toynjoy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toynjoy.R;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        context = getApplicationContext();

        MyUlits.SystemNotificationDIY(this);

        GetPageElement();
        SetGetVerificationCodeClick();
        SetRegisterNowClick();
    }

    private Context context;

    private EditText username;
    private EditText password;
    private EditText confirm_password;
    private EditText email;
    private EditText verification;

    private List<EditText> UserInfoEdit;
    private String verificationCode;

    private void GetPageElement(){

        username = findViewById(R.id.register_username);
        password = findViewById(R.id.register_password);
        confirm_password = findViewById(R.id.register_confirm_password);
        email = findViewById(R.id.register_email);
        verification = findViewById(R.id.register_verification_code);

        UserInfoEdit = new ArrayList<>();
        UserInfoEdit.add(username);
        UserInfoEdit.add(password);
        UserInfoEdit.add(confirm_password);
        UserInfoEdit.add(email);
        UserInfoEdit.add(verification);

    }

    private void SetGetVerificationCodeClick(){
        findViewById(R.id.get_verification_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                if (!emailStr.equals("")){
                    verificationCode = MyUlits.GenerateVerificationCode();
                    Map<String, String> params = new HashMap<>();
                    params.put("email", emailStr);
                    params.put("title", "ToyNJoy???????????????");
                    params.put("content", "?????????????????????" + verificationCode);
                    MyVolleyHelper.HttpResponsePost(context, "sendEmail", params, new Function1<ServiceResult, Void>() {
                        @Override
                        public Void invoke(ServiceResult serviceResult) {
                            Toast.makeText(context, serviceResult.getMessage(), Toast.LENGTH_SHORT).show();

                            return null;
                        }
                    });
                }
                else
                    Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetRegisterNowClick(){

        findViewById(R.id.register_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean userinfostate = true;
                String message = "";

                for (EditText item:UserInfoEdit) {
                    if (item.getText().toString().equals("")){
                        userinfostate = false;
                        switch (item.getId()){
                            case R.id.register_username:
                                message = "??????????????????";
                                break;
                            case R.id.register_password:
                                message = "???????????????";
                                break;
                            case R.id.register_confirm_password:
                                message = "?????????????????????";
                                break;
                            case R.id.register_email:
                                message = "???????????????";
                                break;
                            case R.id.register_verification_code:
                                message = "??????????????????";
                                break;
                        }
                        break;
                    }
                    else{
                        if (!password.getText().toString().equals(confirm_password.getText().toString())){
                            message = "?????????????????????";
                            userinfostate = false;
                            break;
                        }
                        if (verificationCode == null || verificationCode.equals("") || !verificationCode.equals(verification.getText().toString())){
                            message = "???????????????";
                            userinfostate = false;
                            break;
                        }
                    }
                }

                if (userinfostate){
                    Map<String, String> params = new HashMap<>();
                    params.put("name", username.getText().toString());
                    params.put("password", password.getText().toString());
                    params.put("email", email.getText().toString());
                    MyVolleyHelper.HttpResponsePost(context, "CreateUser", params, new Function1<ServiceResult, Void>() {
                        @Override
                        public Void invoke(ServiceResult serviceResult) {

                            Toast.makeText(Register.this, serviceResult.getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();

                            return null;
                        }
                    });
                }
                else
                    Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}