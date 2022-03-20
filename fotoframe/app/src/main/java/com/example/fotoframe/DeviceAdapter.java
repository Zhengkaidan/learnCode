package com.example.fotoframe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private List<MyDevice> myDevices;
    private Context context;
    private TextView device_name,device_id;
    private Button bt_set;
    private RelativeLayout bg_device;
    private int code = 0;
    public int curposition = -1;
    public DeviceAdapter(Context context, List<MyDevice> myDevices,int code) {
        this.context = context;
        this.myDevices = myDevices;
        this.code = code;
    }

    @Override
    public int getCount() {
        return myDevices  != null ? myDevices.size():0;
    }

    @Override
    public Object getItem(int position) {
        return myDevices== null ? null : myDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("dddd","into getView");
        MyDevice myDevice = myDevices.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.device_item,parent,false);
        bg_device = (RelativeLayout)view.findViewById(R.id.bg_device);
        device_name = (TextView)view.findViewById(R.id.device_name);
        device_id = (TextView)view.findViewById(R.id.device_id);
        bt_set = (Button)view.findViewById(R.id.bt_set);

        bg_device.setBackground(context.getResources().getDrawable(R.drawable.bg_device));
        device_name.setText(myDevice.getName());
        device_id.setText(myDevice.getId());

        if(code == 0){
            bt_set.setVisibility(View.INVISIBLE);
        }else if(code == 1){
            bt_set.setVisibility(View.VISIBLE);
            bt_set.setBackground(context.getResources().getDrawable(R.drawable.btset));
        }else{
            bt_set.setVisibility(View.VISIBLE);
            bt_set.setBackground(context.getResources().getDrawable(R.drawable.delete));
        }
        Log.d("dddd","curposition = "+curposition +" position = "+position);
        if(curposition == position){
            bg_device.setBackground(context.getResources().getDrawable(R.drawable.bg_device_focus));
        }else{
            bg_device.setBackground(context.getResources().getDrawable(R.drawable.bg_device));
        }
        return view;
    }

    public void setSelectFocus(int position){
        Log.d("dddd","into setSelectFocus");
        curposition = position;
        Log.d("dddd","curposition = "+curposition);
    }
    public String getDevicename(){
        String name = myDevices.get(curposition).getName();
        return name;
    }
}
