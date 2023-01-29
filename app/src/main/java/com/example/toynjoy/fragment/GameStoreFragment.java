package com.example.toynjoy.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.toynjoy.R;
import com.example.toynjoy.databinding.FragmentGamestoreBinding;
import com.example.toynjoy.entity.Product;
import com.example.toynjoy.entity.Product_Type;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.example.toynjoy.view.GameStoreAdapter;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class GameStoreFragment extends Fragment {

    private FragmentGamestoreBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGamestoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GameStore_Content = binding.GameStoreContent;
        smartRefreshLayout = binding.refreshlayout;
        LoadTopTitle();
        LoadGameStore_Content();
        setSmartRefreshLayoutControl();
        setOrderTypeClick();
        setSelect();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //-------------------------------自定义变量
    private LinearLayout GameTypes;
    private List<TextView> GameTypeList;
    private List<TextView> OrderTypeList;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView GameStore_Content;
    private GameStoreAdapter adapter;

    private int DataCount = 0;
    private int PageItemNum = 8;
    private int CurrentPage = 1;
    private int PageCount = 0;
    private boolean ResetPage = false;

    private String orderColumn = "";
    private String name = "";
    private String type_id = "";
    //---------------------------------------

    //动态加载游戏类型
    private void LoadTopTitle() {
        MyVolleyHelper.HttpResponsePost(getActivity(), "getAllProductType", null, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {
                List<Product_Type> list = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product_Type>>() {}.getType());
                GameTypes = binding.GameType;
                GameTypeList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    int RightPadding = 70;
                    if (i == list.size() - 1) {
                        RightPadding = 0;
                    }

                    TextView textView = new TextView(getActivity());
                    textView.setText(list.get(i).getTypeName());
                    textView.setTag(list.get(i).getId());
                    textView.setPadding(0, 0, RightPadding, 0);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (type_id == null || !type_id.equals(v.getTag().toString())) {
                                for (TextView item:GameTypeList) {
                                    item.setTextColor(getResources().getColor(R.color.grey));
                                }
                                type_id = v.getTag().toString();
                                ((TextView) v).setTextColor(getResources().getColor(R.color.System));
                                ResetPage = true;
                                CurrentPage = 1;
                                LoadGameStore_Content();
                            }
                        }
                    });
                    GameTypes.addView(textView);
                    GameTypeList.add(textView);
                }

                return null;
            }
        });
    }

    //获取分页所需的数据
    private void getPageData(Map<String, String> params){
        MyVolleyHelper.HttpResponsePost(getActivity(), "getCount", params, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {
                Map<String, Double> map = MyUlits.gson.fromJson(serviceResult.getData(), Map.class);

                DataCount = map.get("count").intValue();
                PageCount = DataCount / PageItemNum;
                if (DataCount % PageItemNum > 0) {
                    PageCount++;
                }
                return null;
            }
        });
    }

    //加载页面内容
    private void LoadGameStore_Content() {

        Map<String, String> params = new HashMap<>();
        params.put("top", String.valueOf((CurrentPage - 1) * PageItemNum));
        params.put("num", String.valueOf(PageItemNum));
        params.put("orderColumn", orderColumn);
        params.put("name", name);
        params.put("type_id", type_id);

        getPageData(params);
        MyVolleyHelper.HttpResponsePost(getActivity(), "getProductListPaging", params, new Function1<ServiceResult, Void>() {
            @Override
            public Void invoke(ServiceResult serviceResult) {
                adapter = (GameStoreAdapter) GameStore_Content.getAdapter();
                List<Product> data = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Product>>() {}.getType());

                if (adapter == null) {
                    adapter = new GameStoreAdapter(data, getActivity());
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    GameStore_Content.setLayoutManager(layoutManager);
                    GameStore_Content.setAdapter(adapter);
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

    //下拉刷新和上拉加载
    private void setSmartRefreshLayoutControl() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                ResetPage = true;
                CurrentPage = 1;
                LoadGameStore_Content();
                refreshLayout.finishRefresh(true);//刷新完成
            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (CurrentPage < PageCount) {
                    CurrentPage++;
                    if (CurrentPage == PageCount) {
                        int num = DataCount % PageCount;
                        PageItemNum = num == 0 ? PageItemNum : num;
                    }
                    else {
                        PageItemNum = 8;
                    }
                    LoadGameStore_Content();
                }
                else {
                    Toast.makeText(GameStoreFragment.this.getActivity(), "没有更多内容了噢", Toast.LENGTH_SHORT).show();
                }
                refreshLayout.finishLoadMore(true);//加载完成
            }
        });
    }

    //设置排序类型点击事件
    private void setOrderTypeClick() {
        OrderTypeList = new ArrayList<>();
        OrderTypeList.add(binding.orderByBrowse);
        OrderTypeList.add(binding.orderByName);
        OrderTypeList.add(binding.orderByPrice);
        OrderTypeList.add(binding.orderByPurchases);
        OrderTypeList.add(binding.orderByReleaseDate);

        for (int i = 0; i < OrderTypeList.size(); i++) {
            OrderTypeList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderColumn == null || !orderColumn.equals(v.getTag().toString())) {
                        for (TextView item:OrderTypeList) {
                            item.setTextColor(getResources().getColor(R.color.grey));
                        }
                        orderColumn = v.getTag().toString();
                        ((TextView) v).setTextColor(getResources().getColor(R.color.System));
                        ResetPage = true;
                        CurrentPage = 1;
                        LoadGameStore_Content();
                    }
                }
            });
        }
    }

    private void selectProductByName(){
        String nameStr = binding.selectProductName.getText().toString();
        if (nameStr != null && !nameStr.equals("")){
            name = nameStr;
            ResetPage = true;
            CurrentPage = 1;
            type_id = "";
            orderColumn = "";
            LoadGameStore_Content();
        }
    }

    private void setSelect(){
        binding.selectProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProductByName();
            }
        });

        binding.selectProductName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean result = false;
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN){
                    selectProductByName();
                    result = true;
                }
                return result;
            }
        });
    }

}