package com.example.fotoframe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Uri imageUri;
    private ImageView showphoto,iv_upload;
    private Button takephoto, selectphoto, upphoto;
    private Bitmap bitmap;
    private String service_uri = "http://43.154.235.89:8888";
    private long mTtotalSize;
    private RoundProgressBar roundProgressBar;
    private LinearLayout ll_rpb;
    private FrameLayout fl;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String picturePath="";
    private String token="";
    private String userSign = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        showphoto = (ImageView) findViewById(R.id.showphoto);
        takephoto = (Button) findViewById(R.id.takephoto);
        selectphoto = (Button) findViewById(R.id.selectphoto);
        ll_rpb = (LinearLayout)findViewById(R.id.ll_rpb);
        upphoto = (Button) findViewById(R.id.upphoto);
        iv_upload = (ImageView)findViewById(R.id.iv_upload);
        iv_upload.setVisibility(View.VISIBLE);
        iv_upload.setImageResource(R.drawable.iv_upload);
        fl = (FrameLayout)findViewById(R.id.fl);


        Intent it = getIntent();
        token= it.getStringExtra("token");
        Log.d("dddd", "token = " + token);
        userSign=it.getStringExtra("userSign");
        Log.d("dddd", "userSign = " + userSign);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //点击拍照按钮
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File outputImage = new File(getExternalCacheDir(), "output_image.png");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.fotoframe.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//打开相机
                it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                Log.d("dddd", "imageUri = " + imageUri);
                startActivityForResult(it, 1);
            }
        });
        selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_PICK, null);
                it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(it, 2);
            }
        });
        //点击上传按钮
        upphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ddddd", "picturePath = " + picturePath);
                if (picturePath.equals("")) {
                    Toast.makeText(MainActivity.this, "请选择图片", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("dddd", "path = " + picturePath);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getPhotoMes(getApplicationContext(), picturePath);
                            picturePath = "";
                        }
                    }).start();
                }
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
        if(imageWidth > imageHeight){
            Toast.makeText(getApplicationContext(),"图片比例错误，请重新选择",Toast.LENGTH_SHORT).show();
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
        /*long length = 0;
        int mBytesRead, mbytesAvailable, mBufferSize;
        byte[] buffer;
        int maxBufferSize = 256 * 1024;// 256KB
        DataOutputStream outputStream = null;*/
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
                            Toast.makeText(getApplicationContext(),"图片上传成功",Toast.LENGTH_SHORT).show();
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

        File file = new File(extStorageDirectory, image_save_path+".JPEG");//创建文件，第一个参数为路径，第二个参数为文件名
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //显示图片startActivityForResult启动的intent会自动跳到这里
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        saveScreenShot(bitmap);
                        if(!isCompliance(picturePath)){
                            picturePath = "";
                            return;
                        }
                        Log.d("dddd", "bitmap = " + bitmap);
                        iv_upload.setVisibility(View.INVISIBLE);
                        showphoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if(data != null){
                    try {
                        Uri uri = data.getData();
                        Log.d("dddd", "uri = " + uri);
                        picturePath = getFilePath(getApplicationContext(),uri);
                        Log.d("dddd", "picturepath = " + picturePath);
                        if(!isCompliance(picturePath)){
                            picturePath = "";
                            return;
                        }
                        iv_upload.setVisibility(View.INVISIBLE);
                        showphoto.setImageURI(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获得图片绝对路径
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
}
