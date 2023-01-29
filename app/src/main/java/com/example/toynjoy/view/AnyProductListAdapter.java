package com.example.toynjoy.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toynjoy.R;
import com.example.toynjoy.activity.ProductInfo;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AnyProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Product> data;
    private Activity activity;

    public AnyProductListAdapter(List<Product> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    //获取页面元素
    static class ViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView WishListProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            WishListProductImage = itemView.findViewById(R.id.WishListProductImage);
        }
    }

    //获取xml模板
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_any_product_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myholder = (ViewHolder)holder;
        MyVolleyHelper.LoadingImage(activity.getApplicationContext(), myholder.WishListProductImage, data.get(position).getImage());
        final int current_position = position;
        myholder.WishListProductImage.setOnClickListener(new View.OnClickListener() {
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
