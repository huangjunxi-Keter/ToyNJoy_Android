package com.example.toynjoy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.toynjoy.R;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.example.toynjoy.uilts.alipay.AuthResult;
import com.example.toynjoy.uilts.alipay.OrderInfoUtil2_0;
import com.example.toynjoy.uilts.alipay.PayResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class Alipay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay);

        MyUlits.SystemNotificationDIY(this);
        MyUlits.setToLastPageClick(Alipay.this);

        getPageData();
        setAlipaySubmitClick();
    }

    private Product product;
    private Button AliPaySubmit;

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016101700709747";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。商家uid
     */
    public static final String PID = "2088102180022561";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。商家账号
     */
    public static final String TARGET_ID = "hbshhp2548@sandbox.com";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEowIBAAKCAQEAnojobm27Pk3vlG3nt2XIyXBo2rXLWL7" +
            "Lftn5DNTAEQ++7fJdRUx3qnkaMXKn9TojqamLDMX4SRrth4QNjuqvWdFQCiaRCMbEA4HtBVVefQZoKK+X" +
            "gslbsiczVtW3BGrx9BitTr3IQg0avnOyBE11p+I3xeYLWCwL5TsOM4zmhHi+dyEGLE4iOmyXUjeYqcg4U" +
            "qcFgG24713ypK+zzLgbKZE38Onvo1MIvjMxNQSH1+zeOenjlExU2JZ3l+PaPUURIlY4qC9wurNbLYUO5Y" +
            "GUn4OoibNMGEbBA7htbHjdWvhnxqUnmypm3Ano4BLQijIJhHK/kDLUBI4LEHAZNCKYtwIDAQABAoIBAHo" +
            "edgTSbCDTQhCxFIQ2WJOrDmojDY+/8Ns3JtxWadj6qxV505UVETz06lNawbxp25zOp/jf6qDNqFjyRMtp" +
            "RkfGr4QSLzh2e/lDtQOdvhpKvCNTFz+8wfCat7ZVDBTQGK7x71YvZLpUg9xfHKqpzE7VOCcuTGDQFR2v/" +
            "wGAsPUrHAvdWqcymkjMBSW1Wl/lbJRZGCK7wtzM7NYcaaILQ3UTTv5+W4wwAorx51mcBI5xW8p5cDeLKh" +
            "QdjXMHqD1v+z3j5TngbQCPk2Q2RplJA3h/EcvKOikueqMb6rQTqI+DXgMqoZkx2maNvgRE825DW9upWn4" +
            "XjuIAvSGfO1g+WPECgYEA+S3fYMlSXjlYjtmkUdasQGng1wr6wTKLkXqKVfWP/Vlx0uoLJjKurd3HuJVx" +
            "4ntJFcK4KVSZ4qdHP6yzRqWN36evkGXVRkHozZwkbIStA1FQt0OOKe1k5yh575GUYRW0ELcmi9M+FjKxd" +
            "m6FsQoxIv6ZfL0s4A6bEZ6G1fmcthkCgYEAot/X3fyMNLnLIJ2PjI4Nnr3hTcsf2ICaxcx5EezQxp18E8" +
            "PMm2moCbSEjQlgr3s1UeqRnKENs2y6d68Kuu2SYmjz1RpzPiCNelrZvb+s7HRQqDoM8eosP685kt1CQ/n" +
            "z3t1ZX42rij2lhghnaVPsGTueO9BcV8fizWBDIE/wf08CgYBEqZJTLkanNjAj9O8lqfz/Ju3Q8/KTCCWT" +
            "aevysd8ClgIad2mpFfAyctmVEIE4QnaqK2Tp5qkc3rFwZ1tjTT1h8uga5yS03naTKcKTsJ+oOWD/jvr+r" +
            "K7QT8QB8uCrO/rJXF6fyw7huQhTtTLbzQ4rMXMD/3D9MKkkWsWW8thvCQKBgBPcqD5x5ccoQRUhIbhKOm" +
            "75SNhrxN5qEHW+kaUV8//EhLUEU5dAMzW7xc5NLnU32TC8IjWvjjQrNjISLoTNI+TMV6/NIfCZl6csHRF" +
            "+pl/Pb2aUba+yluLNQ4Ada09O0+aBp7x3UkvxaJYHwFSf31LUal9w8VHjFk1lR8pQ9UsRAoGBAOGmeMwE" +
            "skSc/zLYftyiZqdWHHqFMaw14eVSszutreKpIYG0kx/oJErcD0TjsKdedPas8ngMOlH3ZRXNnGCRJ2xh5" +
            "N36i3WpyD/+E6m3ag0L8vzfBCoIhXSeKnparUvtRuSIzXIQP0yEtFCT1Ooi0N+maAz+GGSKpqsXWrHUmk2b";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    //获取产品数据
    private void getPageData(){

        Intent intent = getIntent();
        product = MyUlits.gson.fromJson(intent.getStringExtra("ProductJSON"), Product.class);
        AliPaySubmit = findViewById(R.id.alipay_submit);

        AliPaySubmit.setText("支付宝支付￥" + product.getPrice());
        ((TextView)findViewById(R.id.alipay_money)).setText("" + product.getPrice());

    }

    //支付成功后将产品加入用户的库存
    private void AddToLibrary(){
        Map<String, String> params = new HashMap<>();
        params.put("username", MyUlits.getloginUser(Alipay.this).getUsername());
        params.put("productId", String.valueOf(product.getId()));
        MyVolleyHelper.HttpResponsePost(getApplicationContext(), "CreateLibraryByUsernmae", params, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProductInfo.class);
                intent.putExtra("ProductJSON", MyUlits.gson.toJson(product));
                startActivity(intent);
                finish();

                return null;
            }
        });
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.d("支付结果", getString(R.string.pay_failed) + payResult);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        AddToLibrary();
                    } else {
                        showAlert(Alipay.this, "支付失败");
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(Alipay.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(Alipay.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    private void setAlipaySubmitClick(){
        findViewById(R.id.alipay_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
                    showAlert(Alipay.this, getString(R.string.error_missing_appid_rsa_private));
                    return;
                }

                /*
                 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
                 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
                 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
                 *
                 * orderInfo 的获取必须来自服务端；
                 */
                boolean rsa2 = (RSA2_PRIVATE.length() > 0);
                //支付订单参数
                Map<String, String> params =
                        OrderInfoUtil2_0.buildOrderParamMap(
                                APPID,
                                rsa2,
                                product.getId().toString(),
                                String.valueOf(product.getPrice()),
                                "[ToyNJoy] 购买" + product.getName(),
                                "感谢您使用ToyNJoy [时间]" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                                        "[商品]" + product.getName() +
                                        "[价格]￥" + product.getPrice());
                String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

                String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
                String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
                final String orderInfo = orderParam + "&" + sign;

                final Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(Alipay.this);
                        Map<String, String> result = alipay.payV2(orderInfo, true);
                        Log.i("msp", result.toString());

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        });
    }
}