package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class myListAdapter extends ArrayAdapter<myList> {

    private static final String TAG="PersonListAdapter";
    private Context mContext;
    int mResource;

    public myListAdapter(Context context, int resource, List<myList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String product=getItem(position).getProduct();
            String uom_name=getItem(position).getUom();
            String sku=getItem(position).getSku();

            myList mylist=new myList(product,uom_name,sku);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView product1=(TextView) convertView.findViewById(R.id.product_txt);
            TextView uom_name1=(TextView)convertView.findViewById(R.id.uom_txt);

            product1.setText(product);
            uom_name1.setText(uom_name);
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
