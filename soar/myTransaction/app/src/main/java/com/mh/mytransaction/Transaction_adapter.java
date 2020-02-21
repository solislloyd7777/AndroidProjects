package com.mh.mytransaction;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Transaction_adapter extends ArrayAdapter<Trans_line> {

    private Context mContext;
    int mResource;

    public Transaction_adapter(Context context, int resource, List<Trans_line> objects) {
        super(context, resource,objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            int temp_id=getItem(position).getTemplate_id();
            String product=getItem(position).getProduct();
            String uom_name=getItem(position).getUom();
            double quantity=getItem(position).getQty();
            double price=getItem(position).getPrice();
            double price_temp=getItem(position).getPrice_temp();
            double qty_temp=getItem(position).getQty_temp();
            String changed=getItem(position).getChanged();
            String isgrab=getItem(position).getIsgrab();
            double subtotal;
            String isnegative=getItem(position).getIsnegative();
            String iscomputed=getItem(position).getIs_computed();
            int fav=getItem(position).getFav();
            String total;

            Trans_line tline=new Trans_line(temp_id,product,uom_name,price,quantity,isnegative,iscomputed,qty_temp,price_temp,changed,fav,isgrab);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView prod=(TextView) convertView.findViewById(R.id.prod1);
            TextView uom=(TextView)convertView.findViewById(R.id.uom1);
            TextView priceent=(TextView)convertView.findViewById(R.id.price1);
            TextView qty=(TextView)convertView.findViewById(R.id.qty1);
            TextView sub=(TextView)convertView.findViewById(R.id.subtotal);

            prod.setText(product);
            uom.setText(uom_name);

            priceent.setText(String.valueOf(price_temp));
            if(qty_temp==0){
                if(iscomputed.equals("Y")){
                    qty.setText(String.valueOf(0));
                }else{
                    qty.setText(String.valueOf(1));
                }

            }else{
                qty.setText(String.valueOf(qty_temp));
            }

            subtotal=Double.parseDouble(qty.getText().toString())*Double.parseDouble(priceent.getText().toString());
            total=String.format("%.2f", subtotal);
            sub.setText(total);
                if(quantity==qty_temp){

                    qty.setBackgroundColor(Color.parseColor("#DD3B3B"));
                }else{
                    qty.setBackgroundColor(Color.parseColor("#27B7F3"));

                }
                if(price==price_temp){
                    priceent.setBackgroundColor(Color.parseColor("#DD3B3B"));
                }else{
                    priceent.setBackgroundColor(Color.parseColor("#4BC4F3"));
                }
                if(changed.equals("Y")){
                    prod.setBackgroundColor(Color.parseColor("#FFFFEB3B"));
                }

                if(product=="No Result Found"){
                    uom.setText("");
                    priceent.setText("");
                    qty.setText("");
                    sub.setText("");
                    prod.setBackgroundColor(Color.parseColor("#EEEDEF"));
                    qty.setBackgroundColor(Color.parseColor("#EEEDEF"));
                    priceent.setBackgroundColor(Color.parseColor("#EEEDEF"));
                    sub.setBackgroundColor(Color.parseColor("#EEEDEF"));
                    uom.setBackgroundColor(Color.parseColor("#EEEDEF"));
                }

                if(isgrab.equals("Y")){
                    prod.setTypeface(null, Typeface.BOLD);
                    qty.setTypeface(null, Typeface.BOLD);
                    priceent.setTypeface(null, Typeface.BOLD);
                    sub.setTypeface(null, Typeface.BOLD);
                    uom.setTypeface(null, Typeface.BOLD);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }


}
