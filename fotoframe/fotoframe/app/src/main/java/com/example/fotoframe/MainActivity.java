package com.example.fotoframe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.fotoframe.Adapter.utitls.CountDownTimerUtils;
import com.example.fotoframe.view.AccountAdapter;
import com.example.fotoframe.view.DeviceAdapter;
import com.example.fotoframe.view.recyclerAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.example.fotoframe.LoginActivity.PHONE_PATTERN;

public class MainActivity extends AppCompatActivity {
    private boolean issend = false,ismine = true,isgetCode = false;
    private String inputText1="",inputText2="";
    private String msgId = "";
    private Uri imageUri,videoUrl;
    private ImageView showphoto,iv_upload;
    private Button takephoto, selectphoto, upphoto,btselect;
    private Bitmap bitmap;
    private String service_uri = "http://43.154.235.89:8888";
    private RoundProgressBar roundProgressBar;
    private LinearLayout ll_rpb,homelayout;
    private FrameLayout fl,fl_videoview,mylayout,fl_leavemain;
    private VideoView videoView;
    private ImageButton bt_home,bt_my;
    private RelativeLayout rl_nodevice,rl_main,rl_select,rl_mydevice,rl_networkerr,
            rl_bindphone,rl_changephone,rl_weixin,rl_password,rl_aboutus,
            rl_setdevice,rl_sharedeviceunbound,
            rl_changename,rl_sharemanager,rl_shareaccountunbound,rl_picmanager,rl_deviceshare,rl_deviceunbound;
    private TextView curr_device;

    //扫码绑定
    private Button bt_binddevice;

    //个人中心
    private RelativeLayout rl_tohome;
    private TextView tv_user;
    private RelativeLayout rl_tophone,rl_toweixin,rl_topassword,rl_toaboutus;
    private Button bt_scan,bt_device,to_phone,to_weixin,to_password,to_aboutus,btsignout;
    private FrameLayout signout_dialog;
    private Button signout_cancel,signout_confirm;

    //选择设备
    private Button select_back,select_confirm;
    private ListView list_select;
    private List<Device> allDevices,myDevices,shareDevices;
    private DeviceAdapter deviceAdapter;

    //绑定手机号
    private Button phone_back,phone_chang;
    private TextView tv_bindphone;
    private Button changephone_back,changephone_confirm;
    private ImageButton delete1;
    private EditText etphone, et_vcode;
    private TextView getvcode;

    //修改密码
    private Button changepassword_back,changepassword_confirm;
    private ImageButton delete_old,delete_new;
    private EditText etoldpassword,etnewpassword;

    //关于我们
    private Button aboutus_back,to_addversion,to_updateversion;
    private TextView app_version;

    //我的设备
    private Button mydevices_back,mydevices_add;
    private RelativeLayout havedevice,nodevice;
    private TextView tv_maindevice,tv_sharedevice;
    private View line_main,line_share;
    private ListView list_devices;

    //解绑共享设备
    private Button sharedevice_unbound_back,sharedevice_unbound_confirm;

    //管理设备
    private Button setdevices_back;
    private TextView tv_devicename1,tv_devicename2;
    private RelativeLayout set_devicename,set_sharemanager,set_picmanager,set_deviceshare,set_deviceunbound;


    //设备名称
    private Button changename_back,bt_savename;
    private EditText et_changename;

    //分享管理
    private Button sharemanager_back;
    private ListView list_shareaccount;
    private AccountAdapter accountAdapter;
    private List<Device> shareaccounts;
    private RelativeLayout haveshare,noshare;


    //解绑分享账号
    private Button shareaccount_unbound_back,shareaccount_unbound_confirm;

    //图片管理
    private Button picmanager_back,picmanager_delete;
    private List<DataBean> dataBeans;
    private recyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RelativeLayout havepic,nopic;
    private FrameLayout fl_dialog;
    private TextView dialog_msg;
    private Button dialog_cancal;
    private Button dialog_delete;

    //设备分享
    private Button deviceshare_back,bt_saveQRCode;
    private ImageView iv_QRCode;
    private String imgQRCode = "";
    private Bitmap decodedByte;

    //解绑设备
    private Button maindevice_unbound_back,maindevice_unbound_confirm;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String picturePath="",videoPath="";
    private String token="";
    private String userSign = "";
    private String deviceName = "",deviceId = ""
            ,curdeviceName = "",curdeviceId = ""
            ,deviceowner = "",shareName = "",shareId = "";
    private int progress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("dddd","into onCreate");

        Intent it = getIntent();
        token= it.getStringExtra("token");
        Log.d("dddd", "token = " + token);
        userSign=it.getStringExtra("userSign");
        Log.d("dddd", "userSign = " + userSign);
        String masterDevice = it.getStringExtra("masterDevice");
        Log.d("dddd","num = "+masterDevice);
        Log.d("dddd","!!!!!!!!!!!!!");

        allDevices = new ArrayList<>();
        myDevices = new ArrayList<>();
        shareDevices = new ArrayList<>();
        shareaccounts = new ArrayList<>();
        dataBeans = new ArrayList<>();


        toGetAllDevices();
        toGetMainDevices();
        toGetShareDevices();

        showphoto = (ImageView) findViewById(R.id.showphoto);
        takephoto = (Button) findViewById(R.id.takephoto);
        selectphoto = (Button) findViewById(R.id.selectphoto);
        ll_rpb = (LinearLayout)findViewById(R.id.ll_rpb);
        upphoto = (Button) findViewById(R.id.upphoto);
        iv_upload = (ImageView)findViewById(R.id.iv_upload);
        iv_upload.setVisibility(View.VISIBLE);
        iv_upload.setImageResource(R.drawable.iv_upload);
        fl = (FrameLayout)findViewById(R.id.fl);
        fl_videoview = (FrameLayout)findViewById(R.id.fl_videoview);
        videoView = (VideoView)findViewById(R.id.videoview);
        mylayout = (FrameLayout)findViewById(R.id.mylayout);
        homelayout = (LinearLayout)findViewById(R.id.homelayout);
        bt_home = (ImageButton)findViewById(R.id.bt_home);
        bt_my = (ImageButton)findViewById(R.id.bt_my);
        btselect = (Button)findViewById(R.id.btselect);
        curr_device = (TextView)findViewById(R.id.curr_device);

        rl_nodevice = (RelativeLayout)findViewById(R.id.rl_nodevice);
        rl_main = (RelativeLayout)findViewById(R.id.rl_main);
        fl_leavemain = (FrameLayout)findViewById(R.id.fl_leavemain);
        rl_select = (RelativeLayout)findViewById(R.id.rl_select);
        rl_mydevice = (RelativeLayout)findViewById(R.id.rl_mydevice);
        rl_bindphone = (RelativeLayout)findViewById(R.id.rl_bindphone);
        rl_changephone = (RelativeLayout)findViewById(R.id.rl_changephone);
        rl_password = (RelativeLayout)findViewById(R.id.rl_password);
        rl_aboutus = (RelativeLayout)findViewById(R.id.rl_aboutus);
        rl_setdevice = (RelativeLayout)findViewById(R.id.rl_setdevice);
        rl_sharedeviceunbound = (RelativeLayout)findViewById(R.id.rl_sharedeviceunbound);
        rl_changename = (RelativeLayout)findViewById(R.id.rl_changename);
        rl_sharemanager = (RelativeLayout)findViewById(R.id.rl_sharemanager);
        rl_shareaccountunbound = (RelativeLayout)findViewById(R.id.rl_shareaccountunbound);
        rl_picmanager = (RelativeLayout)findViewById(R.id.rl_picmanager);
        rl_deviceshare = (RelativeLayout)findViewById(R.id.rl_deviceshare);
        rl_deviceunbound = (RelativeLayout)findViewById(R.id.rl_deviceunbound);

        rl_networkerr = (RelativeLayout)findViewById(R.id.rl_networkerr);

        Log.d("dddd"," isEmpty = "+masterDevice.isEmpty());
        if (masterDevice.isEmpty() || masterDevice.equals("null")){
            Log.d("dddd","into no_device");
            rl_main.setVisibility(View.INVISIBLE);
            fl_leavemain.setVisibility(View.VISIBLE);
            rl_nodevice.setVisibility(View.VISIBLE);
            bt_binddevice = (Button)findViewById(R.id.bt_binddevice);

            bt_binddevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermission();
                    Intent it = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(it,3);
                }
            });
        }else{
            Log.d("dddd","masterDevice不为空");
            try {
                JSONObject json = new JSONObject(masterDevice);
                curdeviceId = json.getString("deviceId");
                curdeviceName = json.getString("deviceName");
                curr_device.setText(getApplicationContext().getString(R.string.curr_device)+curdeviceName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initmyLayoutView();
    }

    private void toGetAllDevices(){
        Log.d("dddd","into toGetAllDevices");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String allDeviceurl = LoginActivity.service_url + "/device/allDevice";
                    OkGo.<String>get(allDeviceurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        JSONObject json = new JSONObject(response.body());
                                        JSONArray data= json.getJSONArray("data");
                                        allDevices.clear();
                                        for(int i = 0;i<data.length();i++){
                                            JSONObject jsonObject = (JSONObject)data.get(i);
                                            Device device = new Device();
                                            device.setName(jsonObject.getString("deviceName"));
                                            device.setId(jsonObject.getString("deviceId"));
                                            Log.d("dddd","device.getName() = "+device.getName()+" device.getId() = "+device.getId());
                                            allDevices.add(device);
                                        }

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

    private void toGetMainDevices(){
        Log.d("dddd","into toGetMainDevices");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userDeviceurl = LoginActivity.service_url + "/device/userDevice";
                    OkGo.<String>get(userDeviceurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        JSONObject json = new JSONObject(response.body());
                                        JSONArray data= json.getJSONArray("data");
                                        myDevices.clear();
                                        for(int i = 0;i<data.length();i++){
                                            JSONObject jsonObject = (JSONObject)data.get(i);
                                            Device device = new Device();
                                            device.setName(jsonObject.getString("deviceName"));
                                            device.setId(jsonObject.getString("deviceId"));
                                            Log.d("dddd","device.getName() = "+device.getName()+" device.getId() = "+device.getId());
                                            myDevices.add(device);
                                        }
                                        /*handler.sendEmptyMessage(88);*/

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

    private void toGetShareDevices(){
        Log.d("dddd","into toGetShareDevices");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String shareDeviceurl = LoginActivity.service_url + "/device/sharedDevice";
                    OkGo.<String>get(shareDeviceurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        JSONObject json = new JSONObject(response.body());
                                        JSONArray data= json.getJSONArray("data");
                                        shareDevices.clear();
                                        for(int i = 0;i<data.length();i++){
                                            JSONObject jsonObject = (JSONObject)data.get(i);
                                            Device device = new Device();
                                            device.setName(jsonObject.getString("deviceName"));
                                            device.setId(jsonObject.getString("deviceId"));
                                            device.setShareUser(jsonObject.getString("userSign"));
                                            Log.d("dddd","device.getName() = "+device.getName()+" device.getId() = "+device.getId()+" device.getShareUser() = "+device.getShareUser());
                                            shareDevices.add(device);
                                        }

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

    private void initmyLayoutView(){
        curr_device.setText(getApplicationContext().getString(R.string.curr_device)+curdeviceName);
        Log.d("dddd","into initmyLayoutView()");
//        toGetAllDevices();
        rl_tohome = (RelativeLayout)findViewById(R.id.rl_tohome);
        tv_user = (TextView)findViewById(R.id.tv_user);
        bt_scan = (Button)findViewById(R.id.bt_scan);
        bt_device = (Button)findViewById(R.id.bt_device);
        to_phone = (Button)findViewById(R.id.to_phone);
        to_weixin = (Button)findViewById(R.id.to_weixin);
        to_password = (Button)findViewById(R.id.to_password);
        to_aboutus = (Button)findViewById(R.id.to_aboutus);
        btsignout = (Button)findViewById(R.id.btsignout);
        signout_dialog = (FrameLayout)findViewById(R.id.signout_dialog);
        signout_cancel = (Button)findViewById(R.id.signout_cancel);
        signout_confirm = (Button)findViewById(R.id.signout_confirm);
        rl_tophone = (RelativeLayout)findViewById(R.id.rl_tophone);
        rl_toweixin = (RelativeLayout)findViewById(R.id.rl_toweixin);
        rl_topassword = (RelativeLayout)findViewById(R.id.rl_topassword);
        rl_toaboutus = (RelativeLayout)findViewById(R.id.rl_toaboutus);

        tv_user.setText(userSign);
        rl_tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homelayout.setVisibility(View.VISIBLE);
                mylayout.setVisibility(View.INVISIBLE);
                bt_home.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.home_1));
                bt_my.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_2));
                toGetAllDevices();
            }
        });

        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                Intent it = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(it,3);
            }
        });
        bt_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_mydevice.setVisibility(View.VISIBLE);
                initMyDeviceView();
            }
        });
        to_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_bindphone.setVisibility(View.VISIBLE);
                initPhoneView();
            }
        });
        rl_tophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_bindphone.setVisibility(View.VISIBLE);
                initPhoneView();
            }
        });
        to_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDisplayToast("该功能暂未开放！！！");
            }
        });
        rl_toweixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDisplayToast("该功能暂未开放！！！");
            }
        });
        to_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_password.setVisibility(View.VISIBLE);
                initPasswordView();
            }
        });
        rl_topassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_password.setVisibility(View.VISIBLE);
                initPasswordView();
            }
        });
        to_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_aboutus.setVisibility(View.VISIBLE);
                initAboutUsView();
            }
        });
        rl_toaboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_aboutus.setVisibility(View.VISIBLE);
                initAboutUsView();
            }
        });
        btsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signout_dialog.setVisibility(View.VISIBLE);
                signout_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signout_dialog.setVisibility(View.INVISIBLE);
                    }
                });
                signout_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences mySharedPreferences = getSharedPreferences(
                                "cun",MainActivity.MODE_WORLD_READABLE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("expirationDate","");
                        editor.putString("phoneNumber", "");
                        editor.putString("token","");
                        editor.putString("masterDevice", "");
                        editor.apply();
                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(it);
                        MainActivity.this.finish();
                    }
                });

            }
        });
    }

    private void initSelectView(){
        select_back = (Button)findViewById(R.id.select_back);
        select_confirm = (Button)findViewById(R.id.select_confirm);
        list_select = (ListView)findViewById(R.id.list_select);

        select_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
        deviceAdapter = new DeviceAdapter(this,allDevices,0);
        list_select.setAdapter(deviceAdapter);

        select_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.VISIBLE);
                fl_leavemain.setVisibility(View.INVISIBLE);
                rl_select.setVisibility(View.INVISIBLE);
            }
        });
        select_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dddd","deviceAdapter.curposition = "+deviceAdapter.curposition);
                if(deviceAdapter.curposition != -1){
                    rl_main.setVisibility(View.VISIBLE);
                    fl_leavemain.setVisibility(View.INVISIBLE);
                    rl_select.setVisibility(View.INVISIBLE);
                    curdeviceName = deviceAdapter.getDevicename();
                    curdeviceId = deviceAdapter.getDeviceId();
                    curr_device.setText(getApplicationContext().getString(R.string.curr_device)+curdeviceName);
                }
            }
        });


        list_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("dddd","into itemOnClick");
                Log.d("dddd","position = "+position);
                deviceAdapter.setSelectFocus(position);
                select_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                list_select.setAdapter(deviceAdapter);
            }
        });
    }

    private void initPhoneView(){
        phone_back = (Button)findViewById(R.id.phone_back);
        phone_chang = (Button)findViewById(R.id.phone_change);
        tv_bindphone = (TextView)findViewById(R.id.tv_bindphone);

        tv_bindphone.setText(getApplicationContext().getString(R.string.bind_phone)+userSign);
        phone_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.VISIBLE);
                fl_leavemain.setVisibility(View.INVISIBLE);
                rl_bindphone.setVisibility(View.INVISIBLE);
            }
        });
        phone_chang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_bindphone.setVisibility(View.INVISIBLE);
                rl_changephone.setVisibility(View.VISIBLE);
                initChangephone();
            }
        });
    }

    private void initChangephone(){
        inputText1 = "";
        inputText2 = "";
        issend = false;
        changephone_back = (Button)findViewById(R.id.changephone_back);
        delete1 = (ImageButton) findViewById(R.id.delete1);
        etphone = (EditText) findViewById(R.id.etphone);
        et_vcode = (EditText) findViewById(R.id.et_vcode);
        getvcode = (TextView) findViewById(R.id.getvcode);
        changephone_confirm = (Button)findViewById(R.id.changephone_confirm);

        changephone_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_changephone.setVisibility(View.INVISIBLE);
                rl_bindphone.setVisibility(View.VISIBLE);
                initPhoneView();
            }
        });
        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etphone.setText("");
            }
        });
        getvcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dddd","isgetCode = "+isgetCode);
                getVcode();
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
                if (etphone.getText().toString().isEmpty() || et_vcode.getText().toString().isEmpty()){
                    issend = true;
                    changephone_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }else{
                    issend = false;
                    changephone_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
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
                if (et_vcode.getText().toString().isEmpty() || etphone.getText().toString().isEmpty()){
                    issend = true;
                    changephone_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }else{
                    issend = false;
                    changephone_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }
            }
        });
        changephone_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!issend) {
                    toChangePhone();
                }
            }
        });

    }

    private void initPasswordView(){
        issend = true;
        changepassword_back = (Button)findViewById(R.id.changepassword_back);
        delete_old = (ImageButton)findViewById(R.id.delete_old);
        delete_new = (ImageButton)findViewById(R.id.delete_new);
        etoldpassword = (EditText)findViewById(R.id.etoldpassword);
        etnewpassword = (EditText)findViewById(R.id.etnewpassword);
        changepassword_confirm = (Button)findViewById(R.id.changepassword_confirm);

        changepassword_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.VISIBLE);
                fl_leavemain.setVisibility(View.INVISIBLE);
                rl_password.setVisibility(View.INVISIBLE);
            }
        });
        delete_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etoldpassword.setText("");
            }
        });
        delete_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etnewpassword.setText("");
            }
        });
        etoldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etoldpassword.getText().toString().isEmpty() || etnewpassword.getText().toString().isEmpty()){
                    issend = true;
                    changepassword_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }else{
                    issend = false;
                    changepassword_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }
            }
        });
        etnewpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etnewpassword.getText().toString().isEmpty() || etoldpassword.getText().toString().isEmpty()){
                    issend = true;
                    changepassword_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }else{
                    issend = false;
                    changepassword_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }
            }
        });
        changepassword_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!issend){
                    toChangePassword();
                }
            }
        });
    }

    private void initAboutUsView(){
        aboutus_back = (Button)findViewById(R.id.aboutus_back);
        app_version = (TextView)findViewById(R.id.app_version);
        to_addversion = (Button)findViewById(R.id.to_addversion);
        to_updateversion = (Button)findViewById(R.id.to_updateversion);

        aboutus_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.VISIBLE);
                fl_leavemain.setVisibility(View.INVISIBLE);
                rl_aboutus.setVisibility(View.INVISIBLE);
            }
        });
        to_addversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        to_updateversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDisplayToast("当前已是最新版本");
            }
        });
    }

    private  void initMyDeviceView(){
        toGetMainDevices();
        toGetShareDevices();
        Log.d("dddd","into initMyDeviceView()");
        ismine = true;
        havedevice = (RelativeLayout)findViewById(R.id.havedevice);
        nodevice = (RelativeLayout)findViewById(R.id.nodevice);
        mydevices_back = (Button)findViewById(R.id.mydevices_back);
        mydevices_add = (Button)findViewById(R.id.mydevices_add);
        tv_maindevice = (TextView)findViewById(R.id.tv_maindevice);
        tv_sharedevice = (TextView)findViewById(R.id.tv_sharedevice);
        line_main = (View)findViewById(R.id.line_main);
        line_share = (View)findViewById(R.id.line_share);
        list_devices = (ListView)findViewById(R.id.list_device);

        tv_maindevice.setTextSize(18);
        tv_maindevice.setTextColor(getApplicationContext().getColor(R.color.tv_focus));
        tv_sharedevice.setTextSize(16);
        tv_sharedevice.setTextColor(getApplicationContext().getColor(R.color.tv_nomarl));
        line_main.setVisibility(View.VISIBLE);
        line_share.setVisibility(View.INVISIBLE);

        Log.d("Dddd"," mydevice.size = "+myDevices.size());
        if (myDevices.size()<= 0){
            havedevice.setVisibility(View.INVISIBLE);
            nodevice.setVisibility(View.VISIBLE);
        }else{
            havedevice.setVisibility(View.VISIBLE);
            nodevice.setVisibility(View.INVISIBLE);
            deviceAdapter = new DeviceAdapter(this,myDevices,1);
            list_devices.setAdapter(deviceAdapter);
        }

        mydevices_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_main.setVisibility(View.VISIBLE);
                fl_leavemain.setVisibility(View.INVISIBLE);
                rl_mydevice.setVisibility(View.INVISIBLE);
                initmyLayoutView();
            }
        });
        mydevices_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                Intent it = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(it,3);
            }
        });
        tv_maindevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ismine = true;
                tv_maindevice.setTextSize(18);
                tv_maindevice.setTextColor(getApplicationContext().getColor(R.color.tv_focus));
                tv_sharedevice.setTextSize(16);
                tv_sharedevice.setTextColor(getApplicationContext().getColor(R.color.tv_nomarl));
                line_main.setVisibility(View.VISIBLE);
                line_share.setVisibility(View.INVISIBLE);
                if (myDevices.size()<=0){
                    havedevice.setVisibility(View.INVISIBLE);
                    nodevice.setVisibility(View.VISIBLE);
                }else{
                    havedevice.setVisibility(View.VISIBLE);
                    nodevice.setVisibility(View.INVISIBLE);
                    deviceAdapter = new DeviceAdapter(MainActivity.this,myDevices,1);
                    list_devices.setAdapter(deviceAdapter);
                }
            }
        });
        tv_sharedevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ismine = false;
                tv_sharedevice.setTextSize(18);
                tv_sharedevice.setTextColor(getApplicationContext().getColor(R.color.tv_focus));
                tv_maindevice.setTextSize(16);
                tv_maindevice.setTextColor(getApplicationContext().getColor(R.color.tv_nomarl));
                line_main.setVisibility(View.INVISIBLE);
                line_share.setVisibility(View.VISIBLE);
                Log.d("dddd"," shareDevices.size() = "+shareDevices.size());
                if (shareDevices.size()<=0){
                    havedevice.setVisibility(View.INVISIBLE);
                    nodevice.setVisibility(View.VISIBLE);
                    return;
                }
                havedevice.setVisibility(View.VISIBLE);
                nodevice.setVisibility(View.INVISIBLE);
                deviceAdapter = new DeviceAdapter(MainActivity.this,shareDevices,2);
                list_devices.setAdapter(deviceAdapter);
            }
        });
        list_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                deviceAdapter.setSelectFocus(i);
                deviceName = deviceAdapter.getDevicename();
                deviceId = deviceAdapter.getDeviceId();
                deviceowner = deviceAdapter.getDeviceShareUser();
                Log.d("dddd","deviceName = "+deviceName+" deviceId"+deviceId+" deviceowner = "+deviceowner);
                if (ismine){
                    initSetDevices();
                }else{
                    initUnboundshareDevice();
                }
            }
        });
    }

    public void initUnboundshareDevice(){
        issend = false;
        rl_mydevice.setVisibility(View.INVISIBLE);
        rl_sharedeviceunbound.setVisibility(View.VISIBLE);
        sharedevice_unbound_back = (Button)findViewById(R.id.sharedevice_unbound_back);
        sharedevice_unbound_confirm = (Button)findViewById(R.id.sharedevice_unbound_confirm);

        sharedevice_unbound_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_mydevice.setVisibility(View.VISIBLE);
                rl_sharedeviceunbound.setVisibility(View.INVISIBLE);
            }
        });
        sharedevice_unbound_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!issend){
                    toUnboundshareDevice(userSign,deviceowner,2);//code = 2 指的是解绑共享设备
                }
            }
        });
    }

    public void initSetDevices(){
        toGetsharedList();
        togetDevicePicList();
        rl_mydevice.setVisibility(View.INVISIBLE);
        rl_setdevice.setVisibility(View.VISIBLE);
        setdevices_back = (Button)findViewById(R.id.setdevices_back);
        tv_devicename1 = (TextView)findViewById(R.id.tv_devicename1);
        tv_devicename2 = (TextView)findViewById(R.id.tv_devicename2);
        set_devicename = (RelativeLayout)findViewById(R.id.set_devicename);
        set_sharemanager = (RelativeLayout)findViewById(R.id.set_sharemanager);
        set_picmanager = (RelativeLayout)findViewById(R.id.set_picmanager);
        set_deviceshare = (RelativeLayout)findViewById(R.id.set_deviceshare);
        set_deviceunbound = (RelativeLayout)findViewById(R.id.set_deviceunbound);

        tv_devicename1.setText(deviceName);
        tv_devicename2.setText(deviceName);

        setdevices_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_mydevice.setVisibility(View.VISIBLE);
                rl_setdevice.setVisibility(View.INVISIBLE);
                initMyDeviceView();
            }
        });

        set_devicename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_setdevice.setVisibility(View.INVISIBLE);
                rl_changename.setVisibility(View.VISIBLE);
                initChangename();
            }
        });
        set_sharemanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_setdevice.setVisibility(View.INVISIBLE);
                rl_sharemanager.setVisibility(View.VISIBLE);
                initShareManager();
            }
        });

        set_picmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_setdevice.setVisibility(View.INVISIBLE);
                rl_picmanager.setVisibility(View.VISIBLE);
                initPicManager();
            }
        });
        set_deviceshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_deviceshare.setVisibility(View.VISIBLE);
                rl_setdevice.setVisibility(View.INVISIBLE);
                initDeviceShare();
            }
        });
        set_deviceunbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_deviceunbound.setVisibility(View.VISIBLE);
                rl_setdevice.setVisibility(View.INVISIBLE);
                initUnboundMainDevice();
            }
        });
    }

    public void toClickSet(String deviceName,String deviceId,String deviceowner){
        Log.d("dddd","into OnClickSet");
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceowner = deviceowner;
        if (ismine){
            initSetDevices();
        }else{
            initUnboundshareDevice();
        }
    }

    private void initChangename(){
        issend = false;
        changename_back = (Button)findViewById(R.id.changename_back);
        et_changename = (EditText)findViewById(R.id.et_changename);
        bt_savename = (Button)findViewById(R.id.bt_savename);

        et_changename.setText(deviceName);
        bt_savename.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));

        changename_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_changename.setVisibility(View.INVISIBLE);
                rl_setdevice.setVisibility(View.VISIBLE);
                initSetDevices();
            }
        });

        et_changename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                issend = true;
                bt_savename.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_changename.getText().toString().isEmpty()){
                    issend = true;
                    bt_savename.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btlogin_border));
                }else{
                    issend = false;
                    bt_savename.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_changename.getText().toString().isEmpty()){
                    bt_savename.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));
                }
            }
        });

        bt_savename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dddd","issend = "+issend);
                boolean issame = false;
                if(!et_changename.getText().toString().isEmpty() && issend){
                    if (deviceName.equals(curdeviceName)){
                        issame = true;
                    }
                    deviceName = et_changename.getText().toString();

                    if (issame){
                        curdeviceName = deviceName;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String updataDeviceNameurl = LoginActivity.service_url + "/device/updataDeviceName";

                                Log.d("dddd","deviceId = "+deviceId+" deviceName"+deviceName);
                                OkGo.<String>post(updataDeviceNameurl)
                                        .tag(this)
                                        .headers("Authorization", "Token"+token)
                                        .headers("Content-Type", "application/x-www-form-urlencoded")
                                        .params("deviceId",deviceId)
                                        .params("deviceName",deviceName)
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onSuccess(Response<String> response) {
                                                Log.e("dddd", response.body());
                                                try {
                                                    Log.d("dddd",response.body());
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
                    rl_changename.setVisibility(View.INVISIBLE);
                    rl_setdevice.setVisibility(View.VISIBLE);
                    initSetDevices();
                    issend = false;
                }
            }
        });
    }

    private void initShareManager(){
        toGetsharedList();
        sharemanager_back = (Button)findViewById(R.id.sharemanager_back);
        list_shareaccount = (ListView)findViewById(R.id.list_sharemanager);
        haveshare = (RelativeLayout)findViewById(R.id.haveshare);
        noshare = (RelativeLayout)findViewById(R.id.noshare);

        if (shareaccounts.size()<=0){
            haveshare.setVisibility(View.INVISIBLE);
            noshare.setVisibility(View.VISIBLE);

        }else {
            haveshare.setVisibility(View.VISIBLE);
            noshare.setVisibility(View.INVISIBLE);
            accountAdapter = new AccountAdapter(this, shareaccounts);
            list_shareaccount.setAdapter(accountAdapter);
        }

        sharemanager_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_sharemanager.setVisibility(View.INVISIBLE);
                rl_setdevice.setVisibility(View.VISIBLE);
                initSetDevices();
            }
        });
        list_shareaccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                rl_sharemanager.setVisibility(View.INVISIBLE);
                rl_shareaccountunbound.setVisibility(View.VISIBLE);
                accountAdapter.setSelectFocus(i);
                shareName = accountAdapter.getAccountName();
                shareId = accountAdapter.getAccountId();
                initshareAccountUnbound();
            }
        });
    }

    public void toClickDelete(String shareName,String shareId){
        Log.d("dddd","into toClickDelete");
        this.shareId = shareId;
        this.shareName = shareName;
        initshareAccountUnbound();
    }

    private void initshareAccountUnbound(){
        issend = false;
        shareaccount_unbound_back = (Button)findViewById(R.id.shareaccount_unbound_back);
        shareaccount_unbound_confirm = (Button)findViewById(R.id.shareaccount_unbound_confirm);

        shareaccount_unbound_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_sharemanager.setVisibility(View.VISIBLE);
                rl_shareaccountunbound.setVisibility(View.INVISIBLE);
                initShareManager();
            }
        });
        shareaccount_unbound_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!issend){
                    toUnboundshareDevice(shareId,userSign,1); //code = 1 指的是解绑分享账号
                }
            }
        });
    }

    private void initPicManager(){
        issend = false;
        picmanager_back = (Button)findViewById(R.id.picmanager_back);
        picmanager_delete = (Button)findViewById(R.id.picmanager_delete);
        recyclerView = (RecyclerView)findViewById(R.id.recycleview);

        havepic = (RelativeLayout)findViewById(R.id.havepic);
        nopic = (RelativeLayout)findViewById(R.id.nopic);
        fl_dialog = (FrameLayout)findViewById(R.id.fl_dialog);
        dialog_msg = (TextView)findViewById(R.id.dialog_msg);
        dialog_cancal = (Button)findViewById(R.id.dialog_cancel);
        dialog_delete = (Button)findViewById(R.id.dialog_delete);

        picmanager_delete.setBackground(getApplicationContext().getDrawable(R.drawable.btconfirm_border));


        Log.d("dddd","dataBeans.size() = "+dataBeans.size());
        if (dataBeans.size()<=0){
            havepic.setVisibility(View.INVISIBLE);
            nopic.setVisibility(View.VISIBLE);
        }else{
            havepic.setVisibility(View.VISIBLE);
            nopic.setVisibility(View.INVISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new recyclerAdapter(this,dataBeans);
            recyclerView.setAdapter(adapter);
        }


        picmanager_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dddd"," into picmanager_back");
                rl_picmanager.setVisibility(View.INVISIBLE);
                rl_setdevice.setVisibility(View.VISIBLE);
                initSetDevices();
            }
        });
        picmanager_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (issend){
                    fl_dialog.setVisibility(View.VISIBLE);
                    dialog_msg.setText("所选择的"+ picAdapter.x+"个内容将从云端以及本地设备");
                    dialog_cancal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fl_dialog.setVisibility(View.INVISIBLE);
                        }
                    });
                    dialog_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toDeletePic();
                        }
                    });
                }
            }
        });
    }

    public void toClickcheckbox(){
        Log.d("dddd","into toClickcheckbox()"+" todeletelist.size = "+picAdapter.toDeleteList.size());
        if(picAdapter.x > 0){
            issend = true;
            picmanager_delete.setBackground(getApplicationContext().getDrawable(R.drawable.btlogin_border));
        }else{
            issend = false;
            picmanager_delete.setBackground(getApplicationContext().getDrawable(R.drawable.btconfirm_border));
        }
    }

    private void toDeletePic(){
        Log.d("dddd","into toDeletePic");
        final String deleteImgApi = LoginActivity.service_url+"/image/deleteImg";
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0;i<picAdapter.toDeleteList.size();i++){
                    Log.d("dddd","ids = "+picAdapter.toDeleteList.get(i));
                    OkGo.<String>delete(deleteImgApi)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("ids",picAdapter.toDeleteList.get(i))
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.d("dddd"," reponse = "+response.body());
                                    handler.sendEmptyMessage(11);
                                }
                            });
                }
            }
        }).start();
    }

    private void initDeviceShare(){
        deviceshare_back = (Button)findViewById(R.id.deviceshare_back);
        bt_saveQRCode = (Button)findViewById(R.id.bt_saveQRCode);
        iv_QRCode = (ImageView)findViewById(R.id.iv_QRCode);

        toGetDeviceQRCode();

        deviceshare_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_setdevice.setVisibility(View.VISIBLE);
                rl_deviceshare.setVisibility(View.INVISIBLE);
                initSetDevices();
            }
        });
        bt_saveQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (decodedByte != null){
                    saveScreenShot(decodedByte,2);
                    toDisplayToast("保存成功！");
                }
            }
        });
    }

    /*private void moveData(File source, File target) {
        long start = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Path sourceP = source.toPath();
            Path targetP = target.toPath();
            if (target.exists()) {
                copyDir(source, target);
            } else {
                try {
                    Files.move(sourceP, targetP, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (target.exists()) {
                copyDir(source, target);
            } else {
                boolean result = source.renameTo(target);
            }
        }
        long end = System.currentTimeMillis();
        long val = end - start;
    }*/

    private void initUnboundMainDevice(){
        issend = false;
        maindevice_unbound_back = (Button)findViewById(R.id.maindevice_unbound_back);
        maindevice_unbound_confirm = (Button)findViewById(R.id.maindevice_unbound_confirm);

        maindevice_unbound_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_setdevice.setVisibility(View.VISIBLE);
                rl_deviceunbound.setVisibility(View.INVISIBLE);
                initSetDevices();
            }
        });
        maindevice_unbound_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dddd","into maindevice_unbound_confirm"+" issend = "+issend);
                if (!issend){
                    tounboundDevice(deviceId,userSign,1);
                }
            }
        });
    }

    private void toGetDeviceQRCode(){
        Log.d("dddd","into toGetDeviceQRCode()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String createQrCodeApi = LoginActivity.service_url+"/shared/createQrCode";
                Log.d("dddd","deviceId = "+deviceId+" userSign = "+userSign);
                OkGo.<String>post(createQrCodeApi)
                        .tag(this)
                        .headers("Authorization","Token"+token)
                        .params("deviceId",deviceId)
                        .params("userSign",userSign)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.e("dddd", response.body());
//                                GlideUtils.loadImage(MainActivity.this,response.body(),iv_QRCode);
//                                imgQRCode = response.body();
                                try {
                                    JSONObject json = new JSONObject(response.body());
                                    String data = json.getString("data");
                                    Log.d("dddd","data = "+data);
                                    byte[] bytes = Base64.decode(data.split(",")[1],Base64.DEFAULT);
                                    decodedByte = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    handler.sendEmptyMessage(10);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
            }
        }).start();
    }

    private void toUnboundshareDevice(final String friendId,final String userSign,final int code){
        issend = true;
        Log.d("dddd","into toUnboundshareDevice");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String deleteSharedurl = LoginActivity.service_url + "/shared/deleteShared";
                    Log.d("dddd","deviceId = "+deviceId+" userSign = "+userSign);
                    OkGo.<String>delete(deleteSharedurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("deviceId",deviceId)
                            .params("userSign",userSign)
                            .params("friend",friendId)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        if (code == 1) {
                                            handler.sendEmptyMessage(6);
                                        }else{
                                            handler.sendEmptyMessage(7);
                                        }
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

    private void tounboundDevice(final String deviceId,final String userSign,final int code) {
        issend = true;
        Log.d("dddd","into tounboundDevice");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String untieDeviceurl = LoginActivity.service_url + "/device/untieDevice";
                    Log.d("dddd","deviceId = "+deviceId+" userSign = "+userSign);
                    OkGo.<String>delete(untieDeviceurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("deviceId",deviceId)
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        if (code == 1){
                                            handler.sendEmptyMessage(4);
                                        }else{
                                            toDisplayToast("请重新扫码绑定");
                                        }
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

    private void toGetsharedList(){
        Log.d("dddd","into toGetsharedList");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String sharedListurl = LoginActivity.service_url + "/shared/sharedList";
                    OkGo.<String>get(sharedListurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded")
                            .params("deviceId",deviceId)
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        Log.d("dddd",response.body());
                                        JSONObject json = new JSONObject(response.body());
                                        JSONArray data= json.getJSONArray("data");
                                        shareaccounts.clear();
                                        for(int i = 0;i<data.length();i++){
                                            JSONObject jsonObject = (JSONObject)data.get(i);
                                            Device device = new Device();
                                            device.setName("手机号");
                                            device.setId(jsonObject.getString("friend"));
                                            Log.d("dddd","device.getName() = "+device.getName()+" device.getId() = "+device.getId());
                                            shareaccounts.add(device);
                                        }
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

    private void getVcode(){
        inputText1 = etphone.getText().toString();
        Log.d("dddd", "inputText1 = " + inputText1);
        Log.d("dddd","是否符合 = "+LoginActivity.isMatchered(PHONE_PATTERN,inputText1));
        if (!LoginActivity.isMatchered(PHONE_PATTERN,inputText1)){
            toDisplayToast("手机号格式错误");
        }else{
            handler.sendEmptyMessage(0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String sendcodeurl = LoginActivity.service_url+"/user/sendCode";
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

    private void toChangePhone(){
        issend = true;
        inputText1 = etphone.getText().toString();
        inputText2 = et_vcode.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phoneChangeurl = LoginActivity.service_url + "/user/replacePhone";
                try {

                    Log.d("dddd","toChange(),\ntoken = "+token+"\n code = "+inputText2+"\n msgid = "+msgId+"\n oldPhoneNumber = "+userSign+"\n newPhoneNumber = "+inputText1);
                    OkGo.<String>put(phoneChangeurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded\n")
                            .params("code", inputText2)
                            .params("msgId", msgId)
                            .params("oldPhoneNumber",userSign)
                            .params("newPhoneNumber", inputText1)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        JSONObject json = new JSONObject(response.body());
                                        String code = json.getString("code");
                                        if (!code.equals("0")){
                                            if((json.getString("msg")).equals("该手机号码已被绑定，请先解绑")){
                                                handler.sendEmptyMessage(3);
                                                return;
                                            }
                                            handler.sendEmptyMessage(2);
                                            return;
                                        }
                                        SharedPreferences mySharedPreferences = getSharedPreferences(
                                                "cun",MainActivity.MODE_WORLD_READABLE);
                                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                                        editor.putString("expirationDate","");
                                        editor.putString("phoneNumber", "");
                                        editor.putString("token","");
                                        editor.putString("masterDevice", "");
                                        editor.apply();
                                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(it);
                                        MainActivity.this.finish();
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

    public final static String PASSWORD_PATTERN = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,16}$";

    public boolean isok(String PatternStr,String input){
        Pattern pattern = Pattern.compile(PatternStr);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public void toDisplayToast(String text){
        Log.d("dddd","into toDisplayToast");
        Toast toast=new Toast(getApplicationContext());
        //创建一个填充物,用于填充Toast
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.toast,null);
        TextView textView = view.findViewById(R.id.tv_toast);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,500);
        toast.show();
    }
    public void toDisplayUploadToast(String text){
        Log.d("dddd","into toDisplayToast");
        Toast toast=new Toast(getApplicationContext());
        //创建一个填充物,用于填充Toast
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.toast,null);
        TextView textView = view.findViewById(R.id.tv_toast);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,-500);
        toast.show();
    }

    private void toChangePassword(){
        Log.d("dddd","into toChangePassword");
        final String oldPassword = etoldpassword.getText().toString();
        final String newPassword = etnewpassword.getText().toString();
        Log.d("dddd"," "+isok(PASSWORD_PATTERN,oldPassword)+" "+(8>oldPassword.length() || oldPassword.length()>32)+" "+isok(PASSWORD_PATTERN,newPassword)+" "+(8>newPassword.length() || newPassword.length()>32));
        if((!isok(PASSWORD_PATTERN,newPassword)) || (8>newPassword.length() || newPassword.length()>32)){
            toDisplayToast("密码设置不规范请重试");
            return;
        }
        issend = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String passwordChangeurl = LoginActivity.service_url + "/user/updataPw";
                try {

                    Log.d("dddd","toChange(),\ntoken = "+token+"\n newPassword = "+newPassword+"\n oldPassword = "+oldPassword+"\n phoneNumber = "+userSign);
                    OkGo.<String>put(passwordChangeurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded\n")
                            .params("newPassword", newPassword)
                            .params("oldPassword", oldPassword)
                            .params("phoneNumber",userSign)
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
                                        SharedPreferences mySharedPreferences = getSharedPreferences(
                                                "cun",MainActivity.MODE_WORLD_READABLE);
                                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                                        editor.putString("expirationDate","");
                                        editor.putString("phoneNumber", "");
                                        editor.putString("token","");
                                        editor.putString("masterDevice", "");
                                        editor.apply();
                                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(it);
                                        MainActivity.this.finish();
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

    private void togetDevicePicList() {
        Log.d("dddd"," into togetDevicePicList()"+" deviceId = "+deviceId);
        final String deviceImgApi = service_uri+"/image/deviceImg";
        OkGo.<String>get(deviceImgApi)
                .tag(this)
                .headers("Authorization", "Token" + token)
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .params("deviceId",deviceId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("dddd", response.body());
                        try {
                            dataBeans.clear();
                            JSONObject json = new JSONObject(response.body());
                            JSONArray data= json.getJSONArray("data");
                            Log.d("dddd"," data.length() = "+data.length());
                            for(int i = 0;i<data.length();i++){
                                List<imageInfo> imglist = new ArrayList<>();
                                JSONObject array1 = data.getJSONObject(i);
                                JSONArray deviceimglists = array1.getJSONArray("deviceImgList");
                                Log.d("dddd","deviceimglist.length() = "+deviceimglists.length());
                                for(int j = 0;j<deviceimglists.length();j++){
                                    JSONObject deviceimglist = deviceimglists.getJSONObject(j);
                                    String thumb = deviceimglist.getString("thumb");
                                    String id = deviceimglist.getString("id");
                                    imageInfo info = new imageInfo();
                                    info.setThumb(thumb);
                                    info.setId(id);
                                    imglist.add(info);
                                }
                                String Date = array1.getString("uploadDate");
                                   DataBean dataBean = new DataBean();
                                   dataBean.setDate(Date);
                                   dataBean.setPic_list(imglist);
                                   dataBeans.add(dataBean);
                            }
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
    }

    private void toBindDevice(final String deviceId){
        Log.d("dddd","into toBindDevice");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bindDeviceurl = LoginActivity.service_url + "/device/bindDevice";
                    OkGo.<String>post(bindDeviceurl)
                            .tag(this)
                            .headers("Authorization", "Token"+token)
                            .headers("Content-Type", "application/x-www-form-urlencoded\n")
                            .params("deviceId", deviceId)
                            .params("deviceName", deviceId)
                            .params("userSign",userSign)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    Log.e("dddd", response.body());
                                    try {
                                        JSONObject json = new JSONObject(response.body());
                                        String code = json.getString("code");
                                        Log.d("dddd"," code = "+code);
                                        if (code.equals("209")){
                                            /*toDisplayToast("该设备已被绑定");*/
                                            JSONObject data = new JSONObject(json.getString("data"));
                                            final String usersign = data.getString("userSign");
                                            new AlertDialog.Builder(MainActivity.this).setTitle("该设备已被绑定!")
                                                    .setMessage("是否要解绑原账号:"+usersign)
                                                    .setMessage("解绑帐号会清除帐号所有相关信息")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            tounboundDevice(deviceId,usersign,2);
                                                        }
                                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    /*toDisplayToast("取消解绑");*/
                                                }
                                            }).show();
                                            return;
                                        }
                                        Log.d("dddd"," after binddevice,curdeviceId = "+curdeviceId);
                                        if (curdeviceId.isEmpty()){
                                            Log.d("dddd","");
                                            curdeviceId = deviceId;
                                            curdeviceName = deviceId;
                                        }
                                        handler.sendEmptyMessage(9);
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

    private void toAddShare(final String deviceId,final String friend,final String userSign){
        Log.d("dddd","into toAddShare");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String addShareApi = LoginActivity.service_url + "/shared/addShared";
                OkGo.<String>post(addShareApi)
                        .tag(this)
                        .headers("Authorization","Token"+token)
                        .params("deviceId",deviceId)
                        .params("friend",friend)
                        .params("userSign",userSign)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.d("dddd","curdeviceId = "+curdeviceId);
                                if (curdeviceId.isEmpty()){
                                    Log.d("dddd","");
                                    curdeviceId = deviceId;
                                    curdeviceName = deviceId;
                                }
                                handler.sendEmptyMessage(12);
                            }
                        });
            }
        }).start();
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
                    toDisplayToast("当前密码，输入错误");
                    issend = false;
                    break;
                case 2:
                    toDisplayToast("验证码错误，请重新输入");
                    issend = false;
                    break;
                case 3:
                    toDisplayToast("该手机号码已被绑定，请先解绑");
                    issend = false;
                    break;
                case 4:
                    rl_deviceunbound.setVisibility(View.INVISIBLE);
                    rl_mydevice.setVisibility(View.VISIBLE);
                    initMyDeviceView();
                    issend = false;
                    break;
                case 5:
                    fl_leavemain.setVisibility(View.INVISIBLE);
                    rl_networkerr.setVisibility(View.INVISIBLE);
                    break;
                case 6:
                    rl_mydevice.setVisibility(View.VISIBLE);
                    rl_shareaccountunbound.setVisibility(View.INVISIBLE);
                    initMyDeviceView();
                    issend = false;
                    break;
                case 7:
                    rl_mydevice.setVisibility(View.VISIBLE);
                    rl_sharedeviceunbound.setVisibility(View.INVISIBLE);
                    initMyDeviceView();
                    issend = false;
                    break;
                case 9:
                    rl_nodevice.setVisibility(View.INVISIBLE);
                    rl_main.setVisibility(View.INVISIBLE);
                    fl_leavemain.setVisibility(View.VISIBLE);
                    rl_mydevice.setVisibility(View.VISIBLE);
                    toGetAllDevices();
                    toGetMainDevices();
                    toGetShareDevices();
                    initMyDeviceView();
                    curr_device.setText(getApplicationContext().getString(R.string.curr_device)+curdeviceName);
                    break;
                case 10:
                    iv_QRCode.setImageBitmap(decodedByte);
                    break;
                case 11:
                    fl_dialog.setVisibility(View.INVISIBLE);
                    rl_picmanager.setVisibility(View.INVISIBLE);
                    initSetDevices();
                    break;
                case 12:
                    rl_nodevice.setVisibility(View.INVISIBLE);
                    rl_main.setVisibility(View.INVISIBLE);
                    fl_leavemain.setVisibility(View.VISIBLE);
                    rl_mydevice.setVisibility(View.VISIBLE);
                    toGetAllDevices();
                    toGetMainDevices();
                    toGetShareDevices();
                    initMyDeviceView();
                    curr_device.setText(getApplicationContext().getString(R.string.curr_device)+curdeviceName);
                    break;
                case 66:
                    iv_upload.setVisibility(View.INVISIBLE);
                    fl.setVisibility(View.INVISIBLE);
                    issend = false;
                    break;
                /*case 88:
                    if (myDevices.size()<= 0){
                        havedevice.setVisibility(View.INVISIBLE);
                        nodevice.setVisibility(View.VISIBLE);
                    }else{
                        havedevice.setVisibility(View.VISIBLE);
                        nodevice.setVisibility(View.INVISIBLE);
                        deviceAdapter = new DeviceAdapter(MainActivity.this,myDevices,1);
                        list_devices.setAdapter(deviceAdapter);
                    }
                    break;*/
            }
        }
    };

    private void checkPermission(){
        Log.d("dddd","Build.VERSION.SDK_INT = "+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            Log.d("dddd","i = "+i);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 200);
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //点击拍照按钮
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fl_videoview.setVisibility(View.INVISIBLE);
                File outputImage = new File(getExternalCacheDir(), "output_image.png");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();

                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.fotoframe.fileprovider", outputImage);
                    } else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//打开相机
                    it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    Log.d("dddd", "imageUri = " + imageUri);
                    startActivityForResult(it, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    checkPermission();
                }

                /*final String[] items = {"拍照","拍视频","取消"};
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            fl_videoview.setVisibility(View.INVISIBLE);
                            File outputImage = new File(getExternalCacheDir(), "output_image.png");
                            try {
                                if (outputImage.exists()) {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();

                                if (Build.VERSION.SDK_INT >= 24) {
                                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.fotoframe.fileprovider", outputImage);
                                } else {
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//打开相机
                                it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                Log.d("dddd", "imageUri = " + imageUri);
                                startActivityForResult(it, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                checkPermission();
                            }

                        }else if (which == 1){
                            File outputVideo = new File(getExternalCacheDir(), "output_video.mp4");
                            try {

                                if (outputVideo.exists()) {
                                    outputVideo.delete();
                                }
                                outputVideo.createNewFile();

                                if (Build.VERSION.SDK_INT >= 24) {
                                    videoUrl = FileProvider.getUriForFile(MainActivity.this, "com.example.fotoframe.fileprovider", outputVideo);
                                } else {
                                    videoUrl = Uri.fromFile(outputVideo);
                                }
                                Intent it = new Intent("android.media.action.VIDEO_CAPTURE");//打开摄像机
                                it.putExtra(MediaStore.EXTRA_OUTPUT, videoUrl);
                                it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                it.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                Log.d("dddd", "videoUrl = " + videoUrl);
                                startActivityForResult(it, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                checkPermission();
                            }
                        }
                    }
                });
                alertBuilder.show();*/

            }
        });
        selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_PICK, null);
                it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
//                it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"video/*");
                startActivityForResult(it, 2);
            }
        });
        Log.d("dddd"," curdeviceId = "+curdeviceId);
        //点击上传按钮
        upphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ddddd", "picturePath = " + picturePath);
                Log.d("ddddd", "videoPath = " + videoPath);
                if (!issend) {

                } else {
                    if(!picturePath.isEmpty()){
                        Log.d("dddd", "path = " + picturePath);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getPhotoMes(getApplicationContext(), picturePath);
                                picturePath = "";
                            }
                        }).start();
                    }else{
                        Log.d("dddd", "path = " + videoPath);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getPhotoMes(getApplicationContext(), videoPath);
                                videoPath = "";
                            }
                        }).start();
                    }
                }
            }
        });

        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homelayout.setVisibility(View.VISIBLE);
                mylayout.setVisibility(View.INVISIBLE);
                bt_home.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.home_1));
                bt_my.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_2));
            }
        });
        bt_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homelayout.setVisibility(View.INVISIBLE);
                mylayout.setVisibility(View.VISIBLE);
                bt_home.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.home_2));
                bt_my.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_1));
                toGetAllDevices();
            }
        });

        btselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_select.setVisibility(View.VISIBLE);
                initSelectView();
            }
        });
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                toDisplayToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            } return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 上传照片
     * @param context
     * @param filepath  图片路径
     */
    private void getPhotoMes(Context context, String filepath) {
        try {
            Log.d("dddd","into getPhotoMes");
            final File file = new File(filepath);
            /*String uploadingurl = service_uri+"/image/uploadImg";
            OkGo.<String>post(uploadingurl)
                    .tag(this)
                    .isMultipart(true)
                    .headers("Authorization", "Token" + token)
                    .headers("Content-Type","multipart/form-data")
                    .params("file", file)
                    .params("userSign",userSign)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("dddd", response.body());
                            iv_upload.setVisibility(View.INVISIBLE);
                            ll_rpb.removeAllViews();
                            fl.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void uploadProgress(Progress progress) {
                            super.uploadProgress(progress);
                            Log.d("dddd0",""+progress.fraction);
                            int pg = (int) (100 * progress.fraction);
                            Log.d("dddd0","pg = "+pg);
                            publishProgress(pg);
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                        }
                    });*/
            String getApi = service_uri+"/image/getApi";
            OkGo.<String>get(getApi)
                    .tag(this)
                    .headers("Authorization", "Token" + token)
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("dddd", response.body());
                            try {
                                JSONObject json = new JSONObject(response.body());
                                String uploadServiceApi = json.getString("data");
                                Log.d("dddd","uploadServiceApi = "+uploadServiceApi);
                                requestUploadFile(uploadServiceApi ,file);
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

    private void requestUploadFile(String uploadServiceApi,File file ) {
        OkGo.<String>post(uploadServiceApi)
                .tag(this)
                .params("filename",file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("dddd", response.body());
                        try {
                            JSONObject json = new JSONObject(response.body());
                            String key = json.getString("key");
                            String TwotoThreeApi = "http://get3dfrom2d.com/check49ft/"+key;
//                            getPressg(TwotoThreeApi,key);
                            uploadKey(key);
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
    }

    public void uploadKey(String key){
        String uploadImgApi = service_uri + "/image/uploadImg";
        Log.d("dddd"," deviceId = "+curdeviceId+" key = "+key+" userSign = "+userSign);
        OkGo.<String>post(uploadImgApi)
                .tag(this)
                .headers("Authorization","Token"+token)
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .params("deviceId",curdeviceId)
                .params("key",key)
                .params("userSign",userSign)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("dddd", response.body());
                        iv_upload.setVisibility(View.VISIBLE);
                        iv_upload.setImageResource(R.drawable.iv_uploadok);
                        ll_rpb.removeAllViews();
                        fl.setVisibility(View.VISIBLE);
                        upphoto.setBackground(getApplicationContext().getDrawable(R.drawable.btconfirm_border));
                        handler.sendEmptyMessageDelayed(66,5000);
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        Log.d("dddd0",""+progress.fraction);
                        int pg = (int) (100 * progress.fraction);
                        Log.d("dddd0","pg = "+pg);
                        publishProgress(pg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        toDisplayUploadToast("上传失败，请重新上传");
                    }
                });
    }

    /**
     * 判断图片比例是否符合
     * @param filepath
     * @return
     */
    private boolean isCompliance(String filepath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath,options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        Log.d("dddd","imageWidth = "+imageWidth+" imageHeight"+imageHeight);
        if(imageWidth >= imageHeight){
            Toast.makeText(getApplicationContext(),"图片比例错误，请重新选择",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /**
     * 判断视频尺寸和时长是否符合
     * @param videoPath
     * @param uri
     * @return
     */
    private boolean isComlianceVideo(String videoPath,Uri uri){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if(uri!=null){
            mmr.setDataSource(videoPath);
        }
        Long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//获取视频时长
        Log.d("dddd","duration = "+(duration / 1000));
        Long width = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        Long height = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        Log.d("dddd","width = "+width+" height = "+height);
        mmr.release();
        if(duration / 1000 > 10){
            Toast.makeText(getApplicationContext(),"时长超过10s，请重新选择",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(width >= height){
            Toast.makeText(getApplicationContext(),"视频比例错误，请重新选择",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 圆型进度条
     * @param num 进度
     */
    private void publishProgress(final int num){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fl.setVisibility(View.VISIBLE);
                roundProgressBar = new RoundProgressBar(getApplicationContext());
                roundProgressBar.setCricleColor(getApplicationContext().getResources()
                        .getColor(R.color.CricleColor));
                roundProgressBar.setCricleProgressColor(getApplicationContext().getResources()
                        .getColor(R.color.CricleProgressColor));
                roundProgressBar.setTextColor(getApplicationContext().getResources()
                        .getColor(R.color.CricleProgressColor));
                roundProgressBar.setMax(100);
                roundProgressBar.setTextSize(70);
                roundProgressBar.setRoundWidth(22);
                ll_rpb.removeAllViews();
                ll_rpb.addView(roundProgressBar);
                roundProgressBar.setProgress(num);
            }
        });
    }
    /**
     * 保存拍摄的照片
     * @param bitmap
     */
    private void saveScreenShot(Bitmap bitmap,int code)  {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        //以保存时间为文件名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        String image_save_path = sdf.format(date);
        File Framephoto = new File(extStorageDirectory+"/Pictures/FramePhoto");
        if (!Framephoto.exists()){
            Framephoto.mkdir();
        }
        File file = new File(extStorageDirectory+"/Pictures/FramePhoto", image_save_path+".JPEG");//创建文件，第一个参数为路径，第二个参数为文件名
        if (code == 1){
            picturePath=file.getPath();
            Log.d("ddddd","pic = "+picturePath);
        }

        try {
            outStream = new FileOutputStream(file);//创建输入流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
//       这三行可以实现相册更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);intent.setData(uri);
            sendBroadcast(intent);

        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "exception:" + e,
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 通过BitmapShader实现圆形边框
     * @param bitmap
     * @param outWidth 输出的图片宽度
     * @param outHeight 输出的图片高度
     * @param radius 圆角大小
     * @param boarder 边框宽度
     */
    public static Bitmap getRoundBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder) {
        Log.d("dddd","into getRoundBitmapByShader");
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;

        Log.d("dddd"," height = "+height+" width = "+width+" widthScale = "+widthScale+" heightScale = "+heightScale);
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        //创建输出的bitmap
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //创建着色器
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //给着色器配置matrix
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        //创建矩形区域并且预留出border
        RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
        //把传入的bitmap绘制到圆角矩形区域内
        canvas.drawRoundRect(rect, radius, radius, paint);

        if (boarder > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.WHITE);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(boarder);
            canvas.drawRoundRect(rect, radius, radius, boarderPaint);
        }
        return desBitmap;
    }
    /**
     * 保存拍摄的视频
     * @param uri
     */
    private void saveVideo(Uri uri){
        try{
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(videoUrl,"r");
            FileInputStream fis = videoAsset.createInputStream();
            //以保存时间为文件名
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
            String video_save_path = sdf.format(date);

            File file = new File(extStorageDirectory+"/DCIM/Camera", video_save_path+".mp4");//创建文件，第一个参数为路径，第二个参数为文件名
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = fis.read(buf))>0){
                fos.write(buf,0,len);
            }
            fis.close();
            fos.close();
            videoPath=file.getPath();
            Log.d("dddd","videoPath = "+videoPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //显示图片startActivityForResult启动的intent会自动跳到这里
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        saveScreenShot(bitmap,1);
                        if(!isCompliance(picturePath)){
                            picturePath = "";
                            return;
                        }
                        Log.d("dddd", "bitmap = " + bitmap);
                        iv_upload.setVisibility(View.INVISIBLE);
                        fl_videoview.setVisibility(View.INVISIBLE);
                        showphoto.setVisibility(View.VISIBLE);
                        bitmap = getRoundBitmapByShader(bitmap,bitmap.getWidth(),bitmap.getHeight(),100,0);
                        showphoto.setImageBitmap(bitmap);
                        upphoto.setBackground(getApplicationContext().getDrawable(R.drawable.btlogin_border));
                        issend = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    try{
                        saveVideo(videoUrl);
                        if(!isComlianceVideo(videoPath,videoUrl)){
                            videoPath = "";
                            return;
                        }
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(videoPath);
                        Long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//获取视频时长
                        Log.d("dddd","duration = "+(duration / 1000));
                        Long width = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                        Long height = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                        Log.d("dddd","width = "+width+" height = "+height);
                        mmr.release();
                        playVideo();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if(data != null){
                    try {
                        Uri uri = data.getData();
                        Log.d("dddd", "uri = " + uri);
                        String path = getFilePath(getApplicationContext(), uri);
                        Log.d("dddd", "path = " + path);
                        if(path.endsWith(".mp4")){
                            videoPath = getFilePath(getApplicationContext(), uri);

                            if(!isComlianceVideo(videoPath,uri)){
                                videoPath = "";
                                return;
                            }
                            playVideo();

                        }else {
                            picturePath = getFilePath(getApplicationContext(), uri);
                            Log.d("dddd", "picturepath = " + picturePath);
                            if (!isCompliance(picturePath)) {
                                picturePath = "";
                                return;
                            }
                            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                            iv_upload.setVisibility(View.INVISIBLE);
                            fl_videoview.setVisibility(View.INVISIBLE);
                            showphoto.setVisibility(View.VISIBLE);
//                            showphoto.setImageURI(uri);
                            bitmap = getRoundBitmapByShader(bitmap,bitmap.getWidth(),bitmap.getHeight(),200,0);
                            showphoto.setImageBitmap(bitmap);
                            upphoto.setBackground(getApplicationContext().getDrawable(R.drawable.btlogin_border));
                            issend = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (data != null){
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    Log.d("dddd","content = "+content);
                    if (content.isEmpty()){
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(content);
                        String deviceId = json.getString("deviceId");
                        String friendId = "";
                        friendId = json.getString("userSign");
                        toAddShare(deviceId,userSign,friendId);
                       /* toBindDevice(deviceId);*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                        JSONObject json = null;
                        try {
                            json = new JSONObject(content);
                            String deviceId = json.getString("deviceId");
                            toBindDevice(deviceId);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获得文件绝对路径
     * @param context
     * @param uri
     * @return
     */
    public String getFilePath(Context context, Uri uri){
        String path = "";
        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (columnIndex > -1) {
                    path = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 播放视频
     */
    private void playVideo(){
        showphoto.setVisibility(View.INVISIBLE);
        iv_upload.setVisibility(View.INVISIBLE);
        fl_videoview.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoPath(videoPath);
                videoView.start();

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (!curdeviceId.isEmpty() && !curdeviceName.isEmpty()) {
            Log.d("dddd","有主设备存在");
            SharedPreferences mySharedPreferences = getSharedPreferences(
                    "cun", SplashActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("masterDevice", "{\"deviceid\":" + curdeviceId + ",\"usersign\":" + userSign + ",\"devicename\":" + curdeviceName + ",\"ismaster\":" + true + "}");
            editor.apply();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 200){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "请在设置中打开权限后继续", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 200);
            }
        }

    }
}