package com.example.fotoframe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.fotoframe.R;

/**
 * Created by SmartSuperV_114 on 2022/3/10.
 */
public class gvAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private int[] images;
    private Context context;
    public gvAdapter(Context context,int[] images) {
        this.context = context;
        this.images = images;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.gv_icon,null);
        Button button = (Button)view.findViewById(R.id.button);
        button.setBackgroundResource(images[position]);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"该功能暂未开放，敬请期待....",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
