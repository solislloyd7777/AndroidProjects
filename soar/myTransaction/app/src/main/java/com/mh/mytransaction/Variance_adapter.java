package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Variance_adapter extends ArrayAdapter<Variance_list> {

    private static final String TAG="PersonListAdapter";
    private Context mContext;
    int mResource;

    public Variance_adapter(Context context, int resource, List<Variance_list> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String product=getItem(position).getProduct();
            String uom_name=getItem(position).getProd_uom();
            int id=getItem(position).getId();

            Variance_list variance_list=new Variance_list(product,uom_name,id);
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
