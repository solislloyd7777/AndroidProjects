package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class Delivery_template_adapter extends ArrayAdapter<Delivery_module> {

    private Context mContext;
    int mResource;
    DatabaseHelper dh;

public Delivery_template_adapter(Context context, int resource, List<Delivery_module> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        try{

            String name=getItem(position).getName();
            String branch_name=getItem(position).getBranch_name();
            String my_date=getItem(position).getDate_req();

            String setName=name.substring(0,3)+"_"+branch_name.replace(" ","_")+"_"+my_date.replace("-","");



            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);
            //TextView temp_name=(TextView)convertView.findViewById(R.id.text1);






        }catch (Exception e){
        e.printStackTrace();
        }
        return convertView;
        }
        }
