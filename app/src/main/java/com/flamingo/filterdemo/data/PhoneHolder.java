package com.flamingo.filterdemo.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.flamingo.filterdemo.R;

/**
 * Created by matrix on 16/7/3.
 */
public class PhoneHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Button compoundButton;
    private MyItemListener myItemListener;


    public PhoneHolder(View itemView,MyItemListener myItemListener) {
        super(itemView);
        compoundButton = (Button) itemView.findViewById(R.id.phone_number);
        this.myItemListener = myItemListener;
        compoundButton.setOnClickListener(this);
    }

    public void setData(String phone){
        compoundButton.setText(phone);
    }

    @Override
    public void onClick(View view) {
        if(myItemListener != null){
            myItemListener.onItemClick(view,getPosition());
        }
    }
}
