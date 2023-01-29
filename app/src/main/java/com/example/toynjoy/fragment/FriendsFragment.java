package com.example.toynjoy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.toynjoy.activity.Login;
import com.example.toynjoy.databinding.FragmentFriendsBinding;
import com.example.toynjoy.entity.Friend;
import com.example.toynjoy.entity.ServiceResult;
import com.example.toynjoy.entity.Users;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.example.toynjoy.view.FriendAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class FriendsFragment extends Fragment {
    private FragmentFriendsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LoadFriend();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Users loginuser;

    private RecyclerView Friend_Content;

    private void LoadFriend(){
        loginuser = MyUlits.getloginUser(getActivity());
        if (loginuser != null) {
            Map<String, String> params = new HashMap<>();
            params.put("username",loginuser.getUsername());
            MyVolleyHelper.HttpResponsePost(getActivity(), "getFriendByUserName", params, new Function1<ServiceResult, Void>() {
                @Override
                public Void invoke(ServiceResult serviceResult) {
                    Friend_Content = binding.FriendContent;
                    List<Friend> data = MyUlits.gson.fromJson(serviceResult.getData(), new TypeToken<List<Friend>>() {}.getType());
                    FriendAdapter adapter = new FriendAdapter(data, getActivity());
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
                    Friend_Content.setLayoutManager(layoutManager);
                    Friend_Content.setAdapter(adapter);

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