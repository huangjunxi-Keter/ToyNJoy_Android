package com.example.toynjoy.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.toynjoy.R;
import com.example.toynjoy.databinding.ToynjoyMainBinding;
import com.example.toynjoy.entity.Users;
import com.example.toynjoy.uilts.MyUlits;
import com.example.toynjoy.uilts.MyVolleyHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

public class ToyNJoyMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ToynjoyMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //取消消息栏占位
        MyUlits.SystemNotificationDIY(this);
        //加载侧边栏
        MakeDrawer();
        //获取登录用户设置头像等，获取个别元素
        LoadPageData();
        //侧边栏控制
        ControlDrawer();
        //SetNavigationButtonClick();
        PortraitClick();
    }

    private AppBarConfiguration mAppBarConfiguration;
    //代替findViewById
    private ToynjoyMainBinding binding;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private View headerLayout;

    private ShapeableImageView portrait_mini;

    private Users loginUser;

    private void LoadPageData() {
        loginUser = MyUlits.getloginUser(this);
        portrait_mini = findViewById(R.id.portrait_mini);
        if (loginUser != null) {
            MyVolleyHelper.LoadingImage(this.getApplicationContext(), headerLayout.findViewById(R.id.user_portrait), loginUser.getVirtualImage());
            MyVolleyHelper.LoadingImage(this.getApplicationContext(), portrait_mini, loginUser.getVirtualImage());
            String name = loginUser.getNickname();
            if (name.equals(""))
                name = loginUser.getUsername();
            ((TextView)headerLayout.findViewById(R.id.username)).setText(name);
        }
    }

    //获取Drawer相关的元素
    private void MakeDrawer() {
        //将toolbar设为头部工具栏
        //setSupportActionBar(binding.appBarMain.toolbar);
        //隐藏项目名
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //实例化侧边栏
        drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_Home, R.id.nav_GameStore, R.id.nav_Friends)
                .setOpenableLayout(drawer)
                .build();

        navigationView = binding.navView;
        bottomNavigationView = binding.appBarMain.navBottom;

        headerLayout = navigationView.getHeaderView(0);
    }

    //使用系统自带头部（overflow？themes？）时，加载隐藏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 拓展工具填充
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //使用系统自带头部（overflow？themes？）时，侧边栏触发事件加载隐藏菜单
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    //设置fragment的操控元素
    private void ControlDrawer() {
        //悬浮按钮
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("WrongConstant")
//            @Override
//            public void onClick(View view) {
//                //提示信息
//                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                drawer.openDrawer(Gravity.START);
//            }
//        });

        portrait_mini.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //侧边栏menu控制
        NavigationUI.setupWithNavController(navigationView, navController);
        //底部导航控制
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //使用系统自带头部（overflow？themes？）时，侧边栏触发按钮
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    }

//    private class NavigationButtonClick implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            //int shakDegrees = v.getId() == R.id.Home ? 15 : 10;
//            //new MyAnimation().startShakeByViewAnim(v,1, 1, shakDegrees, 20);
//        }
//    }
//
//    private void SetNavigationButtonClick(){
//        //((ImageButton) findViewById(R.id.GameStore)).setOnClickListener(new NavigationButtonClick());
//        //((ImageButton) findViewById(R.id.Home)).setOnClickListener(new NavigationButtonClick());
//        //((ImageButton) findViewById(R.id.Friends)).setOnClickListener(new NavigationButtonClick());
//    }

    //抽屉头部头像的点击事件
    private void PortraitClick() {
        headerLayout.findViewById(R.id.user_portrait).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(MyUlits.getloginUser(ToyNJoyMain.this) != null){
                    intent = intent = new Intent(ToyNJoyMain.this, UserInfo.class);
                }
                else{
                    intent = new Intent(ToyNJoyMain.this, Login.class);
                }
                startActivity(intent);
            }
        });
    }

    //重写返回监听
    @Override
    public void onBackPressed() {

    }
}