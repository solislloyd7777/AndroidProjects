package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class My_branch_adapter extends ArrayAdapter<Branch_list> {
    private Context mContext;
    int mResource;

    public My_branch_adapter(Context context, int resource, List<Branch_list> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String branch=getItem(position).getBranch_name();
            int set_branch=getItem(position).getSet_branch();

            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView branch_name=(TextView) convertView.findViewById(R.id.branch_field);
            branch_name.setText(branch);
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
