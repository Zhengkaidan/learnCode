package com.example.fotoframe.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fotoframe.Device;
import com.example.fotoframe.MainActivity;
import com.example.fotoframe.R;

import java.util.List;

public class AccountAdapter extends BaseAdapter {
    private List<Device> shareAccount;
    private Context context;
    private TextView device_name,device_id;
    private Button bt_delete;
    public int curposition = -1;

    public AccountAdapter(Context context, List<Device> shareAccount) {
        this.context = context;
        this.shareAccount = shareAccount;
    }

    @Override
    public int getCount() {
        return shareAccount  != null ? shareAccount.size():0;
    }

    @Override
    public Object getItem(int i) {
        return shareAccount== null ? null : shareAccount.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Log.d("dddd","into getView");
        Device myDevice = shareAccount.get(i);
        View view1 = LayoutInflater.from(context).inflate(R.layout.device_item,viewGroup,false);
        device_name = (TextView)view1.findViewById(R.id.device_name);
        device_id = (TextView)view1.findViewById(R.id.device_id);
        bt_delete = (Button)view1.findViewById(R.id.bt_set);

        device_name.setText(myDevice.getName());
        device_id.setText(myDevice.getId());
        bt_delete.setVisibility(View.VISIBLE);
        bt_delete.setBackground(context.getResources().getDrawable(R.drawable.delete));

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dddd","position = "+i);
                setSelectFocus(i);
                String shareId = getAccountId();
                String shareName = getAccountName();
                ((MainActivity)context).toClickDelete(shareName,shareId);
            }
        });
        return view1;
    }

    public void setSelectFocus(int position){
        Log.d("dddd","into setSelectFocus");
        curposition = position;
        Log.d("dddd","curposition = "+curposition);
    }
    public String getAccountName(){
        String id = shareAccount.get(curposition).getName();
        return id;
    }
    public String getAccountId(){
        String id = shareAccount.get(curposition).getId();
        return id;
    }
}
