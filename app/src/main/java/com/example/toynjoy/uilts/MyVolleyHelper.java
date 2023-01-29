package com.example.toynjoy.uilts;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.toynjoy.entity.ServiceResult;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Map;

import kotlin.jvm.functions.Function1;

public class MyVolleyHelper {

//    public static String httpAddress = "http://127.0.0.1:8000/";
    public static String httpAddress = "http://192.168.0.141:8000/";

    public static void LoadingImage(Context context, ShapeableImageView imageView, String ImageName) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        ImageRequest imageRequest = new ImageRequest(
                httpAddress + "Image/" + ImageName,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setTag(response);
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(imageRequest);
    }

    public static void HttpResponsePost(Context context, String ControllerName, Map<String, String> params, Function1<ServiceResult,Void> function1) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                MyVolleyHelper.httpAddress + ControllerName,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        ServiceResult result = MyUlits.gson.fromJson(response.toString(), ServiceResult.class);
                        if (result.getState()){
                            function1.invoke(result);
                        }
                        else {
                            //Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpResponsePost---ErrorResponse:", error.getMessage(), error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue.add(request);
    }

}
