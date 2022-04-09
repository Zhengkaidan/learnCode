package com.example.fotoframe.Adapter.utitls;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;
    private Context context;

    public CountDownTimerUtils(Context context,TextView mTextView,long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = mTextView;
        this.context = context;
    }

    @Override
    public void onTick(long l) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(""+(l / 1000));  //设置倒计时时间
//        mTextView.setBackgroundResource(R.color.tv_nomarl); //设置按钮为灰色，这时是不能点击的
        SpannableString spannableString = new SpannableString(mTextView.getText().toString());  //获取按钮上的文字
        mTextView.setText(spannableString);
    }

    @Override
    public void onFinish() {
        mTextView.setText("获取验证码");
        mTextView.setClickable(true);//重新获得点击
    }
}
