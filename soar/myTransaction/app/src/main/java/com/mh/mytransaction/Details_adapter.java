package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Details_adapter extends ArrayAdapter<Details_list> {

    private static final String TAG="PersonListAdapter";
    private Context mContext;
    int mResource;

    public Details_adapter(Context context, int resource, List<Details_list> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String product=getItem(position).getBom_name();
            String uom_name=getItem(position).getUom();
            double qty=getItem(position).getQty();

            Details_list details_list=new Details_list(product,uom_name,qty);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView product1=(TextView) convertView.findViewById(R.id.product_txt);
            TextView uom_name1=(TextView)convertView.findViewById(R.id.uom_txt);
            TextView qty1=(TextView)convertView.findViewById(R.id.qty);

            product1.setText(product);
            uom_name1.setText(uom_name);
            qty1.setText(String.valueOf(qty));
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
