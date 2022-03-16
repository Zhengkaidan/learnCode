package com.example.imagepickerdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.common.ImageLoader;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.config.DVListConfig;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.donkingliang.imageselector.utils.ImageSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pictureselect.PictureSelector;

public class MainActivity extends AppCompatActivity {

    private Button btselect,bttake;
    private ImageView iv;
    private String tvResult = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AllowPermission();
        iv = (ImageView)findViewById(R.id.iv);
        bttake = (Button)findViewById(R.id.bttake);
        btselect = (Button)findViewById(R.id.btselect);

        File sd = Environment.getExternalStorageDirectory();
        boolean can_write = sd.canWrite();
        Log.d("dddd","can_write = "+can_write);

        MediaSelectorManager.getInstance().initImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });


        bttake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DVCameraConfig config = MediaSelectorManager.getDefaultCameraConfigBuilder()
                        .isUseSystemCamera(false)
                        .needCrop(true)
                        .cropSize(3,4,300,400)
                        .mediaType(DVMediaType.ALL)
                        .maxDuration(10)
                        .build();

                MediaSelectorManager.openCameraWithConfig(MainActivity.this, config, new OnSelectMediaListener() {
                    @Override
                    public void onSelectMedia(List<String> li_path) {
                        for (String path:li_path){
                            tvResult = path;
                            Log.d("dddd","tvResult = "+tvResult);

                        }
                    }
                });
            }
        });
        btselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
                        .multiSelect(false)
//第一个菜单是否显示照相机
                        .needCamera(false)
//每行显示的数量
                        .listSpanCount(4)
// 确定按钮文字颜色
                        .sureBtnTextColor(Color.WHITE)
// 使用沉浸式状态栏
                        .statusBarColor(Color.parseColor("#3F51B5"))
//标题背景
                        .titleBgColor(Color.parseColor("#3F51B5"))
//是否需要裁剪
                        .needCrop(true)
//裁剪大小
                        .cropSize(1, 1, 200, 200)
                        .build();

                MediaSelectorManager.openSelectMediaWithConfig(MainActivity.this, config, new OnSelectMediaListener() {
                    @Override
                    public void onSelectMedia(List<String> li_path) {
                        for (String path : li_path) {
                            tvResult = path;
                            Log.d("dddd","tvResult = "+tvResult);
                        }
                    }
                });
            }
        });
    }
    private void AllowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }

            int readPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }

            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {

                ArrayList<String> arrayList = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                Log.d("dddd","arrayList = "+arrayList.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
