package com.example.toynjoy.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toynjoy.R;
import com.example.toynjoy.activity.Login;
import com.example.toynjoy.activity.ProductInfo;
import com.example.toynjoy.activity.ToyNJoyMain;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class GameStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Product> data;
    private Activity activity;

    public GameStoreAdapter(List<Product> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    //获取页面元素
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView GameName;
        private TextView GamePrice;
        private ShapeableImageView GameImage;
        private LinearLayout ProductBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            GameName = itemView.findViewById(R.id.GameName);
            GamePrice = itemView.findViewById(R.id.GamePrice);
            GameImage = itemView.findViewById(R.id.GameImage);
            ProductBox = itemView.findViewById(R.id.ProductBox);
        }
    }

    //获取xml模板
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_game_store,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //绑定数据&Item的点击事件
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myholder = (ViewHolder)holder;
        myholder.GameName.setText(data.get(position).getName());
        myholder.GamePrice.setText(MyUlits.getPriceText(data.get(position).getPrice()));
        MyVolleyHelper.LoadingImage(myholder.GameImage.getContext(), myholder.GameImage, data.get(position).getImage());

        //position error: Do not treat position as fixed; only use immediately and call holder.getAdapterPosition() to look it up later
        final int current_position = position;
        myholder.ProductBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ProductInfo.class);
                intent.putExtra("ProductJSON", MyUlits.gson.toJson(data.get(current_position)));
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //暴露接口，实现数据更新
    public void UpdateData(List<Product> newData) {
        for (Product item : newData) {
            data.add(item);
        }

        notifyDataSetChanged();
    }

    //暴露接口，将数据源置为空
    public void ResetData() {
        data = new ArrayList<>();
    }
}
