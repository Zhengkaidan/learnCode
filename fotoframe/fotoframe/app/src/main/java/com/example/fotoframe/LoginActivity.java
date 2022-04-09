package com.example.fotoframe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fotoframe.Adapter.utitls.CountDownTimerUtils;
import com.example.fotoframe.view.gvAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private com.example.fotoframe.view.gvAdapter gvAdapter;
    private TextView tv_vlogin, tv_plogin, tv_tips, getvcode;
    private ImageButton delete1, delete2;
    private EditText etphone, et_vcode, et_password;
    private RelativeLayout rl_plogin, rl_vlogin;
    private ImageView imageView;
    private Button btlogin;
    private GridView gridView;
    private LinearLayout ll;
    private String inputText1="",inputText2="";
    private RelativeLayout login_tips;
    private Button bt_check;
    private boolean isvlogin = true;
    private boolean issend = false,ischeck = false,isHidden = true;
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

        login_tips = (RelativeLayout)findViewById(R.id.login_tips);
        bt_check = (Button)findViewById(R.id.bt_check);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHidden) {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isHidden = !isHidden;
            }
        });
        bt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ischeck){
                    bt_check.setBackground(getApplicationContext().getDrawable(R.drawable.cb_login));
                    btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                    login_tips.setVisibility(View.VISIBLE);
                    issend = true;
                }else{
                    bt_check.setBackground(getApplicationContext().getDrawable(R.drawable.chechked_login));
                    login_tips.setVisibility(View.INVISIBLE);
                    if (isvlogin){
                        if (!et_vcode.getText().toString().isEmpty() && !etphone.getText().toString().isEmpty()){
                            btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                            issend = false;
                        }
                    }else{
                        if (!et_password.getText().toString().isEmpty() && !etphone.getText().toString().isEmpty()){
                            btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                            issend = false;
                        }
                    }
                }
                ischeck = !ischeck;
            }
        });

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
                    getVcode();
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
        etphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isvlogin){
                    if (etphone.getText().toString().isEmpty() || et_vcode.getText().toString().isEmpty() || !ischeck) {
                        issend = true;
                        btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                    } else {
                        issend = false;
                        btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                    }
                }else{
                    if (etphone.getText().toString().isEmpty() || et_password.getText().toString().isEmpty() || !ischeck) {
                        issend = true;
                        btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                    } else {
                        issend = false;
                        btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                    }
                }
            }
        });
        et_vcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etphone.getText().toString().isEmpty() || et_vcode.getText().toString().isEmpty() || !ischeck) {
                    issend = true;
                    btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                } else {
                    issend = false;
                    btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etphone.getText().toString().isEmpty() || et_password.getText().toString().isEmpty() || !ischeck) {
                    issend = true;
                    btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                } else {
                    issend = false;
                    btlogin.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }
            }
        });
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!issend){
                    toLogin();
                }
            }
        });
        gridView = (GridView) findViewById(R.id.gv_icon);
        setGridView(gridView,images.length);
        gvAdapter = new gvAdapter(getApplicationContext(),images);
        gridView.setAdapter(gvAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toDisplayToast("该功能暂未开放！敬请期待....");
            }
        });
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
    public final static String PHONE_PATTERN = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$"/*"^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"*/;
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
        Log.d("dddd","是否符合 = "+isMatchered(PHONE_PATTERN,inputText1));
        if (!isMatchered(PHONE_PATTERN,inputText1)){
            toDisplayToast("手机号格式错误");
        }else{
            handler.sendEmptyMessage(0);
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
                                    }

                                    @Override
                                    public void uploadProgress(Progress progress) {
                                        super.uploadProgress(progress);
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);

                                    }
                                });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void toLogin(){
        issend = true;
        inputText1 = etphone.getText().toString();
        inputText2 = et_vcode.getText().toString();
        if (isvlogin) {
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
                                            String code = json.getString("code");
                                            if (!code.equals("0")){
                                                handler.sendEmptyMessage(1);
                                                return;
                                            }
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
                                            editor.putString("masterDevice",data.getString("masterDevice"));
                                            editor.putString("token",token);
                                            editor.apply();
                                            Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                            it.putExtra("token", token);
                                            it.putExtra("userSign", inputText1);
                                            it.putExtra("masterDevice",data.getString("masterDevice"));
                                            Log.d("dddd"," masterDevice = "+data.getString("masterDevice"));
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

                                    }
                                });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            inputText1 = etphone.getText().toString();
            inputText2 = et_password.getText().toString();
            if (!isMatchered(PHONE_PATTERN, inputText1)) {
                toDisplayToast("手机号格式错误");
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
                                                String code = json.getString("code");
                                                if (!code.equals("0")){
                                                    handler.sendEmptyMessage(2);
                                                    return;
                                                }
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
                                                editor.putString("masterDevice",data.getString("masterDevice"));
                                                editor.apply();
                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                it.putExtra("token", token);
                                                it.putExtra("userSign", inputText1);
                                                it.putExtra("masterDevice",data.getString("masterDevice"));
                                                Log.d("dddd"," masterDevice = "+data.getString("masterDevice"));
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

    public void toDisplayToast(String text){
        Log.d("dddd","into toDisplayToast");
        Toast toast=new Toast(getApplicationContext());
        //创建一个填充物,用于填充Toast
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View view = inflater.inflate(R.layout.toast,null);
        TextView textView = view.findViewById(R.id.tv_toast);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,500);
        toast.show();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(getApplicationContext(),getvcode,60000,1000);
                    countDownTimerUtils.start();
                    break;
                case 1:
                    toDisplayToast("验证码错误");
                    issend = false;
                    break;
                case 2:
                    toDisplayToast("密码错误");
                    issend = false;
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),"登录失败,请检查您的手机号和密码",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
