package com.example.fotoframe;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.example.fotoframe.Adapter.utitls.GlideUtils;
import com.example.fotoframe.MainActivity;
import com.example.fotoframe.R;
import com.example.fotoframe.imageInfo;

import java.util.ArrayList;
import java.util.List;


public class picAdapter extends BaseAdapter {

    private List<imageInfo> checkboxs;
    private Context context;
    private CheckBox checkBox;
    public boolean ischeck = false;
    public static int x = 0;
    public static List<String> toDeleteList;

    public picAdapter(Context context,List<imageInfo> checkboxs) {
        this.checkboxs = checkboxs;
        this.context = context;
        toDeleteList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return checkboxs != null ? checkboxs.size():0;
    }

    @Override
    public Object getItem(int i) {
        return checkboxs== null ? null : checkboxs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.pic_layout,viewGroup,false);
        checkBox = (CheckBox) view1.findViewById(R.id.checkbox);
        ImageView iv_img = view1.findViewById(R.id.iv_img);

        GlideUtils.loadImage(context,checkboxs.get(i).getThumb(),iv_img);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    x++;
                    Log.d("dddd"," position "+i+" is checked"+" pic.id = "+checkboxs.get(i).getId());
                    toDeleteList.add(checkboxs.get(i).getId());
                }else{
                    x--;
                    Log.d("dddd"," position "+i+" is unchecked"+" pic.id = "+checkboxs.get(i).getId());
                    toDeleteList.remove(checkboxs.get(i).getId());
                }
                Log.d("dddd"," x = "+x);
                ((MainActivity)context).toClickcheckbox();
            }
        });
        return view1;
    }
}
