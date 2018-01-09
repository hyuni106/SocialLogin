package com.example.idealidea_03.sociallogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IdealIdea_03 on 2018-01-09.
 */

public class NaverHandler extends OAuthLoginHandler {

    private Context mContext;
    private LoginActivity activity;
    private OAuthLogin mOAuthLoginModule;


    NaverHandler(Context mContext, OAuthLogin mOAuthLoginModule, LoginActivity activity) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
        this.activity = activity;
    }

    @Override
    public void run(boolean success) {
        if (success) {
            final String accessToken = mOAuthLoginModule.getAccessToken(mContext);
            // 얘가 있어야 유저 정보를 가져올 수 있습니다.
            ProfileTask task = new ProfileTask();
            // 이 클래스가 유저정보를 가져오는 업무를 담당합니다.
            task.execute(accessToken);
        } else {
            String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
            Toast.makeText(mContext, "errorCode:" + errorCode
                    + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
        }
    }

    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
                // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(result);
                if(object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Log.d("jsonObject", jsonObject.toString());
//                    SharedPreferences.Editor editor = activity.userData.edit();
//                    editor.putString("email", jsonObject.getString("email"));
//                    editor.putString("name", jsonObject.getString("name"));
//                    editor.putString("nickname", jsonObject.getString("nickname"));
//                    editor.putString("profile", jsonObject.getString("profile_image"));
//                    editor.apply();
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("loginInfo", "3");
                    activity.startActivity(intent);
                    activity.finish();
                    Toast.makeText(mContext, jsonObject.getString("nickname") + "님 로그인", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}