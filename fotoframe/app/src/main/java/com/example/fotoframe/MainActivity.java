package com.example.fotoframe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import android.util.Log;
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

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.fotoframe.LoginActivity.PHONE_PATTERN;

public class MainActivity extends AppCompatActivity {
    private boolean issend = false;
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
    private RelativeLayout rl_main,rl_select,rl_bindphone,rl_changephone,rl_weixin,rl_password;
    private TextView curr_device;
    //个人中心
    private RelativeLayout rl_tohome;
    private TextView tv_user;
    private Button bt_scan,bt_device,to_phone,to_weixin,to_password,btsignout;

    //选择设备
    private Button select_back,select_confirm;
    private ListView list_select;
    private List<MyDevice> myDevices;
    private DeviceAdapter deviceAdapter;

    //绑定手机号
    private Button phone_back,phone_chang;
    private TextView tv_bindphone;
    private Button changephone_back,changephone_confirm;
    private ImageButton delete1;
    private EditText etphone, et_vcode;
    private TextView getvcode;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String picturePath="",videoPath="";
    private String token="";
    private String userSign = "";
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

        Log.d("dddd","!!!!!!!!!!!!!");


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

        rl_main = (RelativeLayout)findViewById(R.id.rl_main);
        fl_leavemain = (FrameLayout)findViewById(R.id.fl_leavemain);
        rl_select = (RelativeLayout)findViewById(R.id.rl_select);
        rl_bindphone = (RelativeLayout)findViewById(R.id.rl_bindphone);
        rl_changephone = (RelativeLayout)findViewById(R.id.rl_changephone);
        rl_password = (RelativeLayout)findViewById(R.id.rl_password);

        initmyLayoutView();

    }
    private void initmyLayoutView(){
        rl_tohome = (RelativeLayout)findViewById(R.id.rl_tohome);
        tv_user = (TextView)findViewById(R.id.tv_user);
        bt_scan = (Button)findViewById(R.id.bt_scan);
        bt_device = (Button)findViewById(R.id.bt_device);
        to_phone = (Button)findViewById(R.id.to_phone);
        to_weixin = (Button)findViewById(R.id.to_weixin);
        to_password = (Button)findViewById(R.id.to_password);
        btsignout = (Button)findViewById(R.id.btsignout);

        tv_user.setText(userSign);
        rl_tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homelayout.setVisibility(View.VISIBLE);
                mylayout.setVisibility(View.INVISIBLE);
                bt_home.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.home_1));
                bt_my.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_2));
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

        to_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_main.setVisibility(View.INVISIBLE);
                fl_leavemain.setVisibility(View.VISIBLE);
                rl_bindphone.setVisibility(View.VISIBLE);
                initPhoneView();
            }
        });
    }
    private void initSelectView(){
        select_back = (Button)findViewById(R.id.select_back);
        select_confirm = (Button)findViewById(R.id.select_confirm);
        list_select = (ListView)findViewById(R.id.list_select);

        select_confirm.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btconfirm_border));

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
                    curr_device.setText("当前设备："+deviceAdapter.getDevicename());
                }
            }
        });
        myDevices = new ArrayList<>();
        MyDevice myDevice = new MyDevice();
        myDevice.setName("相框1");
        myDevice.setId("标识码: 1245678990865");
        myDevices.add(myDevice);
        MyDevice myDevice2 = new MyDevice();
        myDevice2.setName("相框2");
        myDevice2.setId("标识码: 1245678990865");
        myDevices.add(myDevice2);
        deviceAdapter = new DeviceAdapter(getApplicationContext(),myDevices,0);
        list_select.setAdapter(deviceAdapter);

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

        tv_bindphone.setText("绑定的手机号："+userSign);
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
                Log.d("dddd","issend = "+issend);
                /*if (!issend){
                    getVcode();
                }*/
                getVcode();
            }
        });
        changephone_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!issend) {
                    toChange();
                }
            }
        });

    }
    private void getVcode(){
        inputText1 = etphone.getText().toString();
        Log.d("dddd", "inputText1 = " + inputText1);
        if(inputText1.isEmpty()){
            Toast.makeText(getApplicationContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
        }else{
            Log.d("dddd","是否符合 = "+LoginActivity.isMatchered(PHONE_PATTERN,inputText1));
            if (!LoginActivity.isMatchered(PHONE_PATTERN,inputText1)){
                Toast.makeText(getApplicationContext(),"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            }else{
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

    private void toChange(){
        inputText1 = etphone.getText().toString();
        inputText2 = et_vcode.getText().toString();
        if (inputText1.isEmpty() || inputText2.isEmpty() || !LoginActivity.isMatchered(PHONE_PATTERN, inputText1)) {
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
                                            handler.sendEmptyMessage(3);
                                            SharedPreferences mySharedPreferences = getSharedPreferences(
                                                    "cun",LoginActivity.MODE_WORLD_READABLE);
                                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                                            editor.putString("expirationDate","");
                                            editor.putString("phoneNumber", "");
                                            editor.putString("token","");
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
                    issend = true;
                    Toast.makeText(getApplicationContext(),"换绑成功，请重新登录",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),"更换失败,请检查您的手机号和验证码",Toast.LENGTH_SHORT).show();
                    break;
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
                /*fl_videoview.setVisibility(View.INVISIBLE);
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
                }*/


                final String[] items = {"拍照","拍视频","取消"};
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
                alertBuilder.show();

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
        //点击上传按钮
        upphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ddddd", "picturePath = " + picturePath);
                Log.d("ddddd", "videoPath = " + videoPath);
                if (picturePath.equals("") || videoPath.equals("")) {
                    Toast.makeText(MainActivity.this, "请选择图片或视频", Toast.LENGTH_LONG).show();
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
     * 上传照片
     * @param context
     * @param filepath  图片路径
     */
    private void getPhotoMes(Context context, String filepath) {
        try {
            Log.d("dddd","into getPhotoMes");
            File file = new File(filepath);
            if(!file.exists()){
                Toast.makeText(getApplicationContext(),"没有该文件",Toast.LENGTH_SHORT).show();
                return;
            }

            String uploadingurl = service_uri+"/image/uploadImg";
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
                            Log.d("dddd", response.body());
                            Log.d("dddd", "上传错误");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    private void saveScreenShot(Bitmap bitmap)  {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        //以保存时间为文件名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        String image_save_path = sdf.format(date);

        File file = new File(extStorageDirectory+"/DCIM/Camera", image_save_path+".JPEG");//创建文件，第一个参数为路径，第二个参数为文件名
        picturePath=file.getPath();
        Log.d("ddddd","pic = "+picturePath);

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
                        bitmap = getRoundBitmapByShader(bitmap,bitmap.getWidth(),bitmap.getHeight(),200,0);
                        saveScreenShot(bitmap);
                        if(!isCompliance(picturePath)){
                            picturePath = "";
                            return;
                        }
                        Log.d("dddd", "bitmap = " + bitmap);
                        iv_upload.setVisibility(View.INVISIBLE);
                        fl_videoview.setVisibility(View.INVISIBLE);
                        showphoto.setVisibility(View.VISIBLE);
                        showphoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    try{
                        saveVideo(videoUrl);
                        /*
                        if(!isComlianceVideo(videoPath,videoUrl)){
                            videoPath = "";
                            return;
                        }*/
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
                            iv_upload.setVisibility(View.INVISIBLE);
                            fl_videoview.setVisibility(View.INVISIBLE);
                            showphoto.setVisibility(View.VISIBLE);
                            showphoto.setImageURI(uri);
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
