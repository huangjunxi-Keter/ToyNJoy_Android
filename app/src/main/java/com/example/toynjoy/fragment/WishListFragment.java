package com.example.toynjoy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toynjoy.R;
import com.example.toynjoy.activity.Login;
import com.example.toynjoy.databinding.FragmentWishlistBinding;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.entity.Users;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.example.toynjoy.view.AnyProductListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.functions.Function1;

public class WishListFragment extends Fragment {
    private FragmentWishlistBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPageData();

        return root;
    }

    private Users loginuser;

    private RecyclerView WishList_Content;
    private AnyProductListAdapter adapter;
    private boolean ResetPage = true;

    private void getPageData(){
        loginuser = MyUlits.getloginUser(getActivity());
        if (loginuser != null){
            Map<String, String> params = new HashMap<>();
            params.put("username", loginuser.getUsername());
            MyVolleyHelper.HttpResponsePost(getActivity(), "getProductInWishListByUsername", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {

                    WishList_Content = binding.WishListContent;

                    adapter = (AnyProductListAdapter) WishList_Content.getAdapter();
                    List<Product> data = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product>>(){}.getType());

                    if (adapter == null){
                        adapter = new AnyProductListAdapter(data, getActivity());
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        WishList_Content.setLayoutManager(layoutManager);
                        WishList_Content.setAdapter(adapter);
                    }
                    else {
                        if (ResetPage) {
                            adapter.ResetData();
                            ResetPage = false;
                        }
                        adapter.UpdateData(data);
                    }

                    return null;
                }
            });
        }
        else {
            startActivity(new Intent(this.getActivity(), Login.class));
            this.getActivity().finish();
        }
    }
}