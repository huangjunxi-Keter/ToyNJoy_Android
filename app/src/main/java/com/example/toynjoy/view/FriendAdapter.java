package com.example.toynjoy.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toynjoy.R;
import com.example.toynjoy.entity.Friend;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.entity.User_Info;
import com.example.toynjoy.entity.Users;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Friend> data;
    private Activity activity;

    public FriendAdapter(List<Friend> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    //获取页面元素
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView FriendName;
        private TextView FriendSignature;
        private ShapeableImageView FriendImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            FriendName = itemView.findViewById(R.id.FriendName);
            FriendSignature = itemView.findViewById(R.id.FriendSignature);
            FriendImage = itemView.findViewById(R.id.FriendImage);
        }
    }

    //获取xml模板
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myholder = (ViewHolder)holder;
        Map<String, String> params = new HashMap<>();
        params.put("username",data.get(position).getFriendName());
        int index = position;
        //获取用户基本信息
        MyVolleyHelper.HttpResponsePost(activity.getApplicationContext(), "getUserByUserName", params, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                Users user = MyUlits.gson.fromJson(serviceResult.getData(), Users.class);
                MyVolleyHelper.LoadingImage(activity.getApplicationContext(), myholder.FriendImage, user.getVirtualImage());
                myholder.FriendName.setText(data.get(index).getNickname());
                if (myholder.FriendName.getText().equals(""))
                    myholder.FriendName.setText(user.getNickname());

                return null;
            }
        });
        //获取用户详情
        MyVolleyHelper.HttpResponsePost(activity.getApplicationContext(), "getInfoByUsername", params, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                User_Info userInfo = MyUlits.gson.fromJson(serviceResult.getData(), User_Info.class);
                myholder.FriendSignature.setText(userInfo.getSignature());

                return null;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //暴露接口，实现数据更新
    public void UpdateData(List<Friend> newData) {
        for (Friend item : newData) {
            data.add(item);
        }

        notifyDataSetChanged();
    }

    //暴露接口，将数据源置为空
    public void ResetData() {
        data = new ArrayList<>();
    }
}
