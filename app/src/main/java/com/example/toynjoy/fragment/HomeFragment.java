package com.example.toynjoy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.toynjoy.activity.ProductInfo;
import com.example.toynjoy.databinding.FragmentHomeBinding;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.example.toynjoy.view.GameStoreAdapter;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function1;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = getActivity().getApplicationContext();

        LoadBanner();
        LoadBrowseProduct();
        LoadPurchasesProduct();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //-------------------------------自定义变量
    private Context context;
    private List<Product> newGameList;
    private List<String> BannerImage;
    private List<String> BannerTitle;

    private Banner banner;
    //---------------------------------------

    //加载轮播图
    private void LoadBanner(){
        MyVolleyHelper.HttpResponsePost(context, "getNewProduct", null, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                newGameList = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product>>(){}.getType());
                BannerImage = new ArrayList<>();
                BannerTitle = new ArrayList<>();
                for (Product item:newGameList) {
                    BannerImage.add(MyVolleyHelper.httpAddress + "Image/" + item.getImage());
                    BannerTitle.add(item.getName());
                }
                banner = binding.homeBanner;
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                //加载网络图片
                banner.setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        Glide.with(context.getApplicationContext())
                                .load((String) path)
                                .into(imageView);
                    }
                });
                banner.setBannerAnimation(Transformer.Default);
                banner.setBannerTitles(BannerTitle);
                banner.setDelayTime(3000);
                banner.isAutoPlay(true);
                banner.setIndicatorGravity(BannerConfig.CENTER);
                banner.setImages(BannerImage);
                //轮播图点击监听
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent intent = new Intent(context, ProductInfo.class);
                        intent.putExtra("ProductJSON", MyUlits.gson.toJson(newGameList.get(position)));
                        startActivity(intent);
                    }
                });
                banner .start();

                return null;
            }
        });
    }

    private void LoadBrowseProduct(){
        MyVolleyHelper.HttpResponsePost(context, "getBrowseProduct", null, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                List<Product> data = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product>>(){}.getType());
                GameStoreAdapter adapter = new GameStoreAdapter(data, getActivity());
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                binding.browseGame.setLayoutManager(layoutManager);
                binding.browseGame.setAdapter(adapter);

                return null;
            }
        });
    }

    private void LoadPurchasesProduct(){
        MyVolleyHelper.HttpResponsePost(context, "getPurchasesProduct", null, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {

                List<Product> data = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product>>(){}.getType());
                GameStoreAdapter adapter = new GameStoreAdapter(data, getActivity());
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                binding.purchasesGame.setLayoutManager(layoutManager);
                binding.purchasesGame.setAdapter(adapter);

                return null;
            }
        });
    }

}