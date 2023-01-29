package com.example.toynjoy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toynjoy.R;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.entity.Product_Photo_Gallery;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class ProductInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_info);

        context = getApplicationContext();

        //获取页面元素（会多次使用的）
        Buy_InProductInfo = findViewById(R.id.Buy_InProductInfo);
        Image_InProjectInfo = findViewById(R.id.Image_InProjectInfo);
        Mini_Image_InProjectInfo = findViewById(R.id.Mini_Image_InProjectInfo);
        ImageList_InProjectInfo = findViewById(R.id.ImageList_InProjectInfo);
        isLove = findViewById(R.id.isLove);

        MyUlits.SystemNotificationDIY(this);
        MyUlits.setToLastPageClick(ProductInfo.this);

        getPageData();
        RenderButtonStyle();
        setBuy_InProductInfoClick();
    }

    private Context context;
    private String ProductJSON;
    private Product product;
    private List<ShapeableImageView> miniImageList;
    private boolean inLibrary;
    private boolean inWishList;

    private Button Buy_InProductInfo;
    private ShapeableImageView Image_InProjectInfo;
    private ShapeableImageView Mini_Image_InProjectInfo;
    private LinearLayout ImageList_InProjectInfo;
    private ImageButton isLove;

    //获取上个页面通过Intent传递的值，并向服务器请求更多相关数据 and 渲染页面数据
    private void getPageData() {
        //获取Intent的参数
        Intent intent = getIntent();
        ProductJSON = intent.getStringExtra("ProductJSON");
        if (ProductJSON != null && !ProductJSON.equals("")){
            product = MyUlits.gson.fromJson(ProductJSON, Product.class);
            //渲染基础数据
            ((TextView)findViewById(R.id.Price_InProjectInfo)).setText(MyUlits.getPriceText(product.getPrice()));
            ((TextView)findViewById(R.id.Title_InProjectInfo)).setText(product.getName());
            ((TextView)findViewById(R.id.Brief_InProductInfo)).setText(product.getIntro());
            MyVolleyHelper.LoadingImage(context,Image_InProjectInfo, product.getImage());
            MyVolleyHelper.LoadingImage(context,Mini_Image_InProjectInfo, product.getImage());
            //小图
            miniImageList = new ArrayList<>();
            miniImageList.add(Mini_Image_InProjectInfo);
            //获取更多小图
            Map<String, String> params = new HashMap<>();
            params.put("product_id", product.getId().toString());
            MyVolleyHelper.HttpResponsePost(context, "getListByProductID", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {

                    List<Product_Photo_Gallery> list = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product_Photo_Gallery>>() {}.getType());

                    for (Product_Photo_Gallery item:list) {
                        ShapeableImageView image = new ShapeableImageView(context);
                        image.setAdjustViewBounds(true);//不设置宽高无效
                        image.setMaxWidth(Mini_Image_InProjectInfo.getWidth());
                        image.setMaxHeight(Mini_Image_InProjectInfo.getHeight());
                        image.setScaleType(Mini_Image_InProjectInfo.getScaleType());
                        image.setBackground(Mini_Image_InProjectInfo.getBackground());
                        image.setPadding(20, 0, 0, 0);
                        MyVolleyHelper.LoadingImage(context, image, item.getImage());
                        miniImageList.add(image);
                        ImageList_InProjectInfo.addView(image);
                    }

                    for (ShapeableImageView image:miniImageList) {
                        //点击时替换大图的图片
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Image_InProjectInfo.setImageBitmap((Bitmap)v.getTag());
                            }
                        });
                    }

                    return null;
                }
            });
        }
        else{
            Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    //渲染底部按钮
    private void RenderButtonStyle(){
        if (MyUlits.getloginUser(ProductInfo.this) != null){
            Map<String, String> params = new HashMap<>();
            params.put("username", MyUlits.getloginUser(ProductInfo.this).getUsername());
            params.put("productId", String.valueOf(product.getId()));
            MyVolleyHelper.HttpResponsePost(context, "getLibraryDataByProductId", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {

                    inLibrary = serviceResult.getState();
                    Buy_InProductInfo.setText("已在库中");
                    Buy_InProductInfo.setEnabled(false);

                    return null;
                }
            });
            MyVolleyHelper.HttpResponsePost(context, "getWishListDataByProductId", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {

                    inWishList = serviceResult.getState();
                    if (inWishList){
                        isLove.setImageDrawable(getDrawable(R.drawable.ic_xin_pink));
                    }

                    return null;
                }
            });
        }
    }

    private void setBuy_InProductInfoClick(){
        Buy_InProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (MyUlits.getloginUser(ProductInfo.this) != null){
                    intent = new Intent(context, Alipay.class);
                    intent.putExtra("ProductJSON", ProductJSON);
                    finish();
                }
                else {
                    intent = new Intent(context, Login.class);
                }
                startActivity(intent);
            }
        });
    }

}