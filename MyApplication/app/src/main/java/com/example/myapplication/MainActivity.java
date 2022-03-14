package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String server_url = "http://get3dfrom2d.com";
    private String apiUrl_getLatestImageKey = server_url + "/check49fkeys";
    private String apiUrl_getImageList = server_url + "/foto49fList/fotoframe49/pwd";

    private String lastImageUri = "";
    //    private List<String> playlist;
    private List<File> list;

    private int updateTime = 10000;
    private ImageView pictureView;

    public List<File> localFiles = new ArrayList<>();
    private String lastestImageUri;
    private String lastestImageUri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pictureView = (ImageView) findViewById(R.id.pictureView);

        AllowPermission();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getImageList();
            }
        }).start();

        init();
    }
    private void AllowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;

            }
        }
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean play = true;
//                getImageList();
                while (true) {
                    try {
                        if (play) {
                            Log.d(TAG, "没有最新图片也没有本地图片，播放默认图片");
                            handler.sendEmptyMessage(1);
                            Thread.sleep(updateTime);
                        }
                        getLatestImageKey();

                        if (!lastestImageUri.equals("")) {
                            play = false;
                            Log.d(TAG, "有最新图片，播放最新图片");
                            String Playpath = getExternalFilesDir(null).getAbsolutePath() + "/" + lastestImageUri.substring(lastestImageUri.lastIndexOf("/") + 1);
                            lastestImageUri = "";
                            File f = new File(Playpath);
                            if (f.getName().endsWith("jpg") || f.getName().endsWith("jpeg") || f.getName().endsWith("png")) {
                                Log.d(TAG, "开始播放最新图片");
                                startPlaying(f);
                                Thread.sleep(updateTime);
                            }
                        }
                        Log.d("dddd", "list = " + list.toString());
                        if (list != null && list.size() > 0) {
                            play = false;
                            Log.d(TAG, "轮播本地图片");
                            for (int i = 0; i < list.size(); i++) {
                                File f = list.get(i);
                                if (f.getName().endsWith("jpg") || f.getName().endsWith("jpeg") || f.getName().endsWith("png")) {
                                    Log.d(TAG, "开始播放本地");
                                    startPlaying(f);
                                    Thread.sleep(updateTime);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void startPlaying(final File f) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
                pictureView.setImageBitmap(bitmap);
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pictureView.setImageResource(R.mipmap.moren);
                    break;
            }
        }
    };

    /**
     * 获取列表清单
     */
    private void getImageList() {
        list = new ArrayList<File>();
        Log.d(TAG, "into getImageList()");
        List<String>playlist = new ArrayList<String>();
        try {
            String response = getHttpResponse(apiUrl_getImageList);
            JSONObject json = new JSONObject(response);
            JSONArray list = json.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject oj = list.getJSONObject(i);
                Iterator iterator = oj.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = oj.getString(key);
                    playlist.add(value);
                }
            }
            Log.d("dddd", "playlist = " + playlist.toString());
            //下载列表清单的文件
            if (playlist.size() > 0) {
                for (int x = 0; x < playlist.size(); x++) {
                    File file = new File(getExternalFilesDir(null).getAbsolutePath());
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        File file1 = files[i];
                        String name = file.getName();
                        if (name.equals(playlist.get(x))) {
                            continue;
                        }
                        String Downloaduri = server_url + "/" + playlist.get(x);
                        Download(Downloaduri, playlist.get(x));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新图片的地址
     */
    private void getLatestImageKey() {
        Log.d("dddd", "into getLatestImageKey()");
        try {
            String response = getHttpResponse(apiUrl_getLatestImageKey);
            JSONObject json = new JSONObject(response);
            String output = json.getString("output");

            if (!TextUtils.isEmpty(output)&& !output.equals(lastestImageUri2)) {
                String imageURl = server_url + "/" + output;
                lastestImageUri = output;
                lastestImageUri2 = output;
                //进行最新图片下载
                Download(imageURl, output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载图片
     */
    private void Download(final String Downloaduri, String Downloadname) {
        String savename = Downloadname.substring(Downloadname.lastIndexOf("/") + 1);
        Log.d("dddd", "保存的文件名" + savename);
        final String filePath = getExternalFilesDir(null).getAbsolutePath() + "/" + savename;
        File file = new File(filePath);
        FileOutputStream fos = null;
        InputStream in = null;
        try {
            URL url = new URL(Downloaduri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Charset", "utf-8");
            //计算文件的大小
            Long filesize = Long.parseLong(conn.getHeaderField("Content-Length"));
            Log.d("dddd", "filesize = " + filesize);
            Log.d("dddd", "file.exists() = " + file.exists());
            byte[] bytes = new byte[1024];
            if (file.exists()) {
                long size = file.length();
                Log.d("dddd", "file.size = " + size);
                if(size < filesize){
                    file.delete();
                    file.createNewFile();
                }else{
                    list.add(file);
                    return;
                }
            }else{
                file.createNewFile();
            }
            in = conn.getInputStream();
            fos = new FileOutputStream(file);
            int nRead;
            while ((nRead = in.read(bytes, 0, 1024)) > 0) {
                fos.write(bytes, 0, nRead);
                fos.flush();
            }
            list.add(file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getHttpResponse(String strurl) throws Exception {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try {
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        System.out.println(resultBuffer.toString());
        return resultBuffer.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        }
    }
}
