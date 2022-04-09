package com.example.fotoframe.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotoframe.DataBean;
import com.example.fotoframe.R;
import com.example.fotoframe.picAdapter;

import java.util.List;


public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder1> {

    private List<DataBean> dataBeans;
    private LayoutInflater inflater;
    Context context;
    private com.example.fotoframe.picAdapter picAdapter;
    OnClickRecyclerViewItemListener onClickRecyclerViewItemListener;

    public recyclerAdapter(Context context, List<DataBean> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public recyclerAdapter.ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder1 holder1 = new ViewHolder1(view);

        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, final int position) {
        if (!TextUtils.isEmpty(dataBeans.get(position).getDate())){
            holder.tv_date.setText(dataBeans.get(position).getDate());
        }
        picAdapter adapter = new picAdapter(context,dataBeans.get(position).getPic_list());
        holder.gv_pic.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView tv_date;
        MyGridView gv_pic;
        public ViewHolder1(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            gv_pic = (MyGridView) itemView.findViewById(R.id.gv_pic);

        }
    }

    public interface OnClickRecyclerViewItemListener{
        void recyclerViewItem(int position);

    }

    public void setRecyclerViewItemListener(OnClickRecyclerViewItemListener onClickRecyclerViewItemListener){
        this.onClickRecyclerViewItemListener = onClickRecyclerViewItemListener;
    }
}
