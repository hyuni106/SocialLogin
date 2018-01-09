package com.example.idealidea_03.sociallogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;

public class LoginActivity extends AppCompatActivity {
    //    Facebook
    CallbackManager callbackManager;
    ProfileTracker pt;
    private com.facebook.login.widget.LoginButton loginButton;

    //    Kakao
    private SessionCallback callback;

    //    Naver
    OAuthLogin mOAuthLoginModule;
    OAuthLoginButton mOAuthLoginButton;
    String OAUTH_CLIENT_ID = "c22GAqGZYm2OqfIFnk9W";
    String OAUTH_CLIENT_SECRET = "1qf0ldoKYA";
    String OAUTH_CLIENT_NAME = "SocialLoginTest";

//    Google
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Facebook Login
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        pt = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                로그인 프로필 작업

                if (currentProfile != null) {
//                    누군가 로그인 했다.

//                    ContextUtil.login(mContext,
//                            currentProfile.getId(),
//                            currentProfile.getName(),
//                            currentProfile.getProfilePictureUri(500,500).toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginInfo", "1");
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, currentProfile.getName() + "님 로그인 완료", Toast.LENGTH_SHORT).show();
                }
            }
        };

//        Kakao Login
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

//        Naver Login
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                , OAUTH_CLIENT_ID
                , OAUTH_CLIENT_SECRET
                , OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginHandler = new NaverHandler(this, mOAuthLoginModule, this);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
//        mOAuthLoginButton.setBgResourceId(R.drawable.img_loginbtn_usercustom);

        mOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });

//        Google
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_login);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Kakao
        Session.getCurrentSession().removeCallback(callback);
//        Facebook
        pt.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Kakao
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
//        Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result != null) {
                if (result.isSuccess()) {

// 로그인 성공 했을때
                    GoogleSignInAccount acct = result.getSignInAccount();

                    String personName = acct.getDisplayName();
                    String personEmail = acct.getEmail();
                    String personId = acct.getId();
                    String tokenKey = acct.getServerAuthCode();

                    mGoogleApiClient.disconnect();


                    Log.e("GoogleLogin", "personName=" + personName);
                    Log.e("GoogleLogin", "personEmail=" + personEmail);
                    Log.e("GoogleLogin", "personId=" + personId);
                    Log.e("GoogleLogin", "tokenKey=" + tokenKey);


                } else {
                    Log.e("GoogleLogin", "login fail cause=" + result.getStatus().getStatusMessage());
                    // 로그인 실패 했을때
                }
            }
        } else {

        }
    }

    //        Kakao
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }

    //    Kakao
    protected void redirectSignupActivity() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
//                ContextUtil.login(mContext,
//                        result.getId()+"",
//                        "없음",
//                        result.getNickname(),
//                        result.getProfileImagePath());

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("loginInfo", "2");
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this, result.getNickname() + "님 로그인 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    Google
    private void setGoogleLogin() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("web client id")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            }
                        } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
}
