package com.mh.mytransaction;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class My_temp_adapter extends ArrayAdapter<Temp_content> {

    private Context mContext;
    int mResource;

    public My_temp_adapter(Context context, int resource, List<Temp_content> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String product=getItem(position).getProd();
            String uom_name=getItem(position).getUom();
            String isnega=getItem(position).getIsnega();
            String isact=getItem(position).getIsactive();
            int id=getItem(position).getId();
            double price=getItem(position).getPrice();
            String iscomputed=getItem(position).getIscomputed();
            double qty=getItem(position).getQty();
            String isgrab=getItem(position).getIsgrab();


            Temp_content temp=new Temp_content(product,uom_name,isnega,isact,id,price,iscomputed,qty,getItem(position).getFav(),isgrab);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView prod=(TextView) convertView.findViewById(R.id.prod);
            TextView uom=(TextView)convertView.findViewById(R.id.uom);
            TextView priceent=(TextView)convertView.findViewById(R.id.price);
            TextView isnegative=(TextView)convertView.findViewById(R.id.isnega);
            TextView quantity=(TextView)convertView.findViewById(R.id.qty);


            if(qty==0.0){
                if(iscomputed.equals("Y")){
                    quantity.setText(String.valueOf(0));
                }else{
                    quantity.setText(String.valueOf(1));
                }

            }else{
                quantity.setText(String.valueOf(qty));
            }



            prod.setText(product);
            uom.setText(uom_name);
            priceent.setText(String.valueOf(price));
            isnegative.setText(isnega);

            if(product=="No Result Found"){
                priceent.setText("");
                quantity.setText("");
                uom.setText("");
                isnegative.setText("");
            }

            if(isgrab.equals("Y")){
                prod.setBackgroundColor(Color.parseColor("#FF9CC1E7"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
