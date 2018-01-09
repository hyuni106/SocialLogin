package com.example.idealidea_03.sociallogin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    Button logoutBtn;
//    OAuthLogin mOAuthLoginModule;
    String loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginInfo = getIntent().getStringExtra("loginInfo");

        this.logoutBtn = (Button) findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                로그아웃할때, 사용자 정보를 제거
//                ContextUtil.logout(mContext);

//                로그아웃버튼이 눌리면, 페이스북에서도 강제로 로그아웃
                if (loginInfo.equals("1")) {
                    LoginManager.getInstance().logOut();
                } else if (loginInfo.equals("2")) {
//                카카오톡도 강제로 로그아웃.
//                UserManagement.requestLogout(null);
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
//                            redirectLoginActivity();
                        }
                    });
                } else if (loginInfo.equals("3")) {
//                네이버 로그아웃
                    OAuthLogin.getInstance().logout(MainActivity.this);
                } else {
                    FirebaseAuth.getInstance().signOut();
                }

                redirectLoginActivity();

//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.idealidea_03.sociallogin",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
