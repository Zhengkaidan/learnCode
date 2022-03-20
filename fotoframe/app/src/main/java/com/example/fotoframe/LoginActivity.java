package com.example.fotoframe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private gvAdapter gvAdapter;
    private TextView tv_vlogin, tv_plogin, tv_tips, getvcode;
    private ImageButton delete1, delete2;
    private EditText etphone, et_vcode, et_password;
    private RelativeLayout rl_plogin, rl_vlogin;
    private ImageView imageView;
    private Button btlogin;
    private GridView gridView;
    private LinearLayout ll;
    private String inputText1="",inputText2="";
    private boolean isvlogin = true;
    private boolean issend = false;
    public static String service_url = "http://43.154.235.89:8888";
    private String msgId = "";
    private String token = "";
    private int[] images = {R.drawable.weixin, R.drawable.facebook, R.drawable.google};
    private int s = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {

        tv_vlogin = (TextView) findViewById(R.id.tv_vlogin);
        tv_plogin = (TextView) findViewById(R.id.tv_plogin);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        rl_vlogin = (RelativeLayout) findViewById(R.id.rl_vlogin);
        rl_plogin = (RelativeLayout) findViewById(R.id.rl_plogin);
        etphone = (EditText) findViewById(R.id.etphone);
        et_vcode = (EditText) findViewById(R.id.et_vcode);
        getvcode = (TextView) findViewById(R.id.getvcode);
        et_password = (EditText) findViewById(R.id.et_password);
        imageView = (ImageView) findViewById(R.id.imageView);
        delete1 = (ImageButton) findViewById(R.id.delete1);
        delete2 = (ImageButton) findViewById(R.id.delete2);
        btlogin = (Button) findViewById(R.id.btlogin);
        tv_vlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isvlogin = true;
                tv_vlogin.setTextSize(20);
                tv_vlogin.setTextColor(getApplicationContext().getResources()
                        .getColor(R.color.tv_focus));
                tv_plogin.setTextSize(18);
                tv_plogin.setTextColor(getApplicationContext().getResources().getColor(R.color.tv_nomarl));
                tv_tips.setText(R.string.tip_vlogin);
                rl_vlogin.setVisibility(View.VISIBLE);
                rl_plogin.setVisibility(View.INVISIBLE);
            }
        });
        tv_plogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isvlogin = false;
                tv_plogin.setTextSize(20);
                tv_plogin.setTextColor(getApplicationContext().getResources()
                        .getColor(R.color.tv_focus));
                tv_vlogin.setTextSize(18);
                tv_vlogin.setTextColor(getApplicationContext().getResources().getColor(R.color.tv_nomarl));
                tv_tips.setText(R.string.tip_glogin);
                rl_vlogin.setVisibility(View.INVISIBLE);
                rl_plogin.setVisibility(View.VISIBLE);
            }
        });
        getvcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dddd","issend = "+issend);
                if (!issend){
                    getVcode();
                }
            }
        });
        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etphone.setText("");
            }
        });
        delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isvlogin){
                    et_vcode.setText("");
                }else {
                    et_password.setText("");
                }
            }
        });
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin();
            }
        });
        gridView = (GridView) findViewById(R.id.gv_icon);
        setGridView(gridView,images.length);
        gvAdapter = new gvAdapter(getApplicationContext(),images);
        gridView.setAdapter(gvAdapter);
    }

    private void setGridView(GridView gridView,int numColunms){
        Log.d("dddd","INTO setGridView() numColumns=" + numColunms);
        //int size = mpalistdata.size();
        int length = 50;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int gvWidth = (int)(numColunms * length * density);
        int itemWidth = (int)(length * density);
        Log.d("dddd","gvWidth=" + gvWidth + "\titemWidth" + itemWidth);
        Log.d("dddd","getWindowManager().getDefaultDisplay().getWidth() ="+getWindowManager().getDefaultDisplay().getWidth());
        LinearLayout.LayoutParams params;
        if(gvWidth >= getWindowManager().getDefaultDisplay().getWidth()){
            params = new LinearLayout.LayoutParams(
                    gvWidth, LinearLayout.LayoutParams.FILL_PARENT);
        }else{
            params = new LinearLayout.LayoutParams(
                    getWindowManager().getDefaultDisplay().getWidth(), LinearLayout.LayoutParams.FILL_PARENT);
        }
        gridView.setLayoutParams(params);
        gridView.setColumnWidth(itemWidth);
//      gridView.setStretchMode(GridView.NO_STRETCH);

        gridView.setNumColumns(numColunms);
    }
    public final static String PHONE_PATTERN = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    /**
     * 正则表达式匹配判断
     * @param patternStr 匹配规则
     * @param input 需要做匹配操作的字符串
     * @return true if matched, else false
     */
    public static boolean isMatchered(String patternStr, CharSequence input) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
    public void getVcode(){
        inputText1 = etphone.getText().toString();
        Log.d("dddd", "inputText1 = " + inputText1);
        if(inputText1.isEmpty()){
            Toast.makeText(getApplicationContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
        }else{
            Log.d("dddd","是否符合 = "+isMatchered(PHONE_PATTERN,inputText1));
            if (!isMatchered(PHONE_PATTERN,inputText1)){
                Toast.makeText(getApplicationContext(),"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sendcodeurl = service_url+"/user/sendCode";
                        try {
                            OkGo.<String>get(sendcodeurl)
                                    .tag(this)
                                    .headers("Content-Type", "application/x-www-form-urlencoded")
                                    .params("phoneNumber", inputText1)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Log.e("dddd", response.body());
                                            try {
                                                JSONObject json = new JSONObject(response.body());
                                                JSONObject data= new JSONObject(json.getString("data"));
                                                Log.d("dddd","msgId = "+data.getString("msgId"));

                                                msgId = data.getString("msgId");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            handler.sendEmptyMessage(1);
                                        }

                                        @Override
                                        public void uploadProgress(Progress progress) {
                                            super.uploadProgress(progress);
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);
                                            handler.sendEmptyMessage(2);

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
    private void toLogin(){
        inputText1 = etphone.getText().toString();
        if (isvlogin) {
            inputText2 = et_vcode.getText().toString();
            if (inputText1.isEmpty() || inputText2.isEmpty() || !isMatchered(PHONE_PATTERN, inputText1)) {
                if (inputText1.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                }else if(inputText2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phoneLoginurl = service_url + "/user/phoneLogin";
                        try {
                            OkGo.<String>post(phoneLoginurl)
                                    .tag(this)
                                    .headers("Content-Type", "application/x-www-form-urlencoded\n")
                                    .params("code", inputText2)
                                    .params("msgId", msgId)
                                    .params("phoneNumber", inputText1)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Log.e("dddd", response.body());
                                            try {
                                                JSONObject json = new JSONObject(response.body());
                                                JSONObject data = new JSONObject(json.getString("data"));
                                                Log.d("dddd", "token = " + data.getString("token"));
                                                token = data.getString("token");
                                                String expirationDate = data.getString("expirationDate");
                                                Log.d("dddd","expirationDate = "+expirationDate);
                                                SharedPreferences mySharedPreferences = getSharedPreferences(
                                                        "cun",LoginActivity.MODE_WORLD_READABLE);
                                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                                editor.putString("expirationDate",expirationDate);
                                                editor.putString("phoneNumber", data.getString("phoneNumber"));
                                                editor.putString("token",token);
                                                editor.apply();
                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                it.putExtra("token", token);
                                                it.putExtra("userSign", inputText1);
                                                startActivity(it);
                                                LoginActivity.this.finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void uploadProgress(Progress progress) {
                                            super.uploadProgress(progress);
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);
                                            handler.sendEmptyMessage(3);

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else {
            inputText2 = et_password.getText().toString();
            if (inputText1.isEmpty() || inputText2.isEmpty() || !isMatchered(PHONE_PATTERN, inputText1)) {
                if (inputText1.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                }else if(inputText2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phoneLoginurl = service_url + "/user/login";
                        try {
                            OkGo.<String>post(phoneLoginurl)
                                    .tag(this)
                                    .headers("Content-Type", "application/x-www-form-urlencoded\n")
                                    .params("password", inputText2)
                                    .params("phoneNumber", inputText1)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Log.e("dddd", response.body());
                                            try {
                                                JSONObject json = new JSONObject(response.body());
                                                JSONObject data = new JSONObject(json.getString("data"));
                                                Log.d("dddd", "token = " + data.getString("token"));
                                                token = data.getString("token");
                                                String expirationDate = data.getString("expirationDate");
                                                Log.d("dddd","expirationDate = "+expirationDate);
                                                SharedPreferences mySharedPreferences = getSharedPreferences(
                                                        "cun", SplashActivity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                                editor.putString("expirationDate",expirationDate);
                                                editor.putString("phoneNumber", data.getString("phoneNumber"));
                                                editor.putString("token",token);
                                                editor.apply();
                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                it.putExtra("token", token);
                                                it.putExtra("userSign", inputText1);
                                                startActivity(it);
                                                LoginActivity.this.finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void uploadProgress(Progress progress) {
                                            super.uploadProgress(progress);
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);
                                            handler.sendEmptyMessage(4);

                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"发送失败",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"登录失败,请检查您的手机号和验证码",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),"登录失败,请检查您的手机号和密码",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    issend = false;
                    getvcode.setText(R.string.getvcode);
                    getvcode.setTextColor(getApplicationContext().getResources().getColor(R.color.tv_focus));
                    break;
            }
        }
    };
}
