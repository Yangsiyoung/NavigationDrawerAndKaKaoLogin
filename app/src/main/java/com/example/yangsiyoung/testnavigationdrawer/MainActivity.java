package com.example.yangsiyoung.testnavigationdrawer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.helper.Base64;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private Button btnShowNavigationDrawer;

    private Button btnShowToast;

    private Intent intent;

    //ActionBarDrawerToggle 요녀석 android.support.v7꺼 써야한다.
    //android.support.v4는 이제 사용 안하니까... deprecated대써...
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private SessionCallback callBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        if(intent.hasExtra("userProfile")){
            UserProfile userProfile = intent.getParcelableExtra("userProfile");
            Toast.makeText(MainActivity.this, "User Nick Name is " + userProfile.getNickname(), Toast.LENGTH_SHORT).show();
        }

        //Style에서 NoActionBar로 ActionBar를 Disable 시켰으니
        //우리가 ActionBar처럼 사용 할 toolbar를 ActionBar처럼
        //사용하기위해 setSupportActionBar에 설정해준다.
        //주의 할 점은 xml에 <include>의 id를 가져와서 설정하는것에 유의
        toolbar = (Toolbar) findViewById(R.id.toolbarInclude);
        setSupportActionBar(toolbar);

        //여기서 setContentView로 설정되어있는건 activity_main이므로
        //toolbar에 구현해둔 컴포넌트를 findViewById로 가져오기위해
        //toolbar.findViewById로 찾아준다
        btnShowNavigationDrawer = (Button) toolbar.findViewById(R.id.btnShowNavigationDrawer);
        btnShowNavigationDrawer.setOnClickListener(onClickListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = setUpActionBarToggle();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        setUpDrawerContent(navigationView);

        btnShowToast = (Button) navigationView.findViewById(R.id.btnShowToast);
        btnShowToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "클릭클릭!!!",Toast.LENGTH_SHORT).show();
            }
        });

        try{
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures){
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("aaaa", Base64.encodeBase64URLSafeString(messageDigest.digest()));
            }
        } catch (Exception e){
            Log.d("error", "PackageInfo error is " + e.toString());
        }
        callBack = new SessionCallback();
        Session.getCurrentSession().addCallback(callBack);
        Session.getCurrentSession().checkAndImplicitOpen();

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnShowNavigationDrawer:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            }
        }
    };

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.first_navigation_item:
                        Toast.makeText(MainActivity.this,"첫번째 Navigation Item 입니다", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.second_navigation_item:
                        Toast.makeText(MainActivity.this,"두번째 Navigation Item 입니다", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.third_navigation_item:
                        Toast.makeText(MainActivity.this,"세번째 Navigation Item 입니다", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private ActionBarDrawerToggle setUpActionBarToggle(){
        return new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            request();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            setContentView(R.layout.activity_main);
            Log.d("error", "연결실패 에러 코드는 " + exception.getErrorType().toString());
            Log.d("error", "연결실패 에러 코드는 " + exception.getMessage());
        }

    }

    public void request(){
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("error", "Session Closed Error is " + errorResult.toString());
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                Toast.makeText(MainActivity.this, "사용자 이름은 " + result.getNickname() , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
