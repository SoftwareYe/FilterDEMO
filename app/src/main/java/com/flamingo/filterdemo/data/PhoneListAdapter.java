package com.flamingo.filterdemo.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.flamingo.filterdemo.R;

import java.util.List;

/**
 * Created by matrix on 16/7/3.
 */
public class PhoneListAdapter extends RecyclerView.Adapter<PhoneHolder> {

    private List<String> Datas;
    private MyItemListener myItemListener;

    public PhoneListAdapter(List<String> mDatas){
        if (mDatas==null){
            throw new IllegalArgumentException(
                    "mDatas must not be null");
        }
        Datas = mDatas;
    }

    @Override
    public PhoneHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //view view =
        return new PhoneHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list,viewGroup,false),myItemListener);
    }

    @Override
    public void onBindViewHolder(PhoneHolder phoneHolder, int position) {
        phoneHolder.setData(Datas.get(position));
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public void setDatas(List<String> mDatas){
        Datas = mDatas;
    }

    public void setMyItemListener(MyItemListener myItemListener){
        this.myItemListener = myItemListener;
    }

}
