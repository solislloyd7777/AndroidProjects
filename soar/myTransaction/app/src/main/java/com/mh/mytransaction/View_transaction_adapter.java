package com.mh.mytransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class View_transaction_adapter extends ArrayAdapter<Created_transaction_module> {

    private Context mContext;
    int mResource;

    public View_transaction_adapter(Context context, int resource, List<Created_transaction_module> objects) {
        super(context, resource,objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            String product=getItem(position).getProduct();
            String uom=getItem(position).getUom();
            double price=getItem(position).getPrice();
            double qty=getItem(position).getQty();
            double subtotal=getItem(position).getSubtotal();

            //Created_transaction_module created_transaction_module=new Created_transaction_module(temp_id,product,uom_name,price,quantity,isnegative);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView prod1=(TextView) convertView.findViewById(R.id.prod1);
            TextView uom1=(TextView)convertView.findViewById(R.id.uom1);
            TextView priceent1=(TextView)convertView.findViewById(R.id.price1);
            TextView qty1=(TextView)convertView.findViewById(R.id.qty1);
            TextView sub=(TextView)convertView.findViewById(R.id.subtotal);

            prod1.setText(product);
            uom1.setText(uom);

            priceent1.setText(String.valueOf(price));
            qty1.setText(String.valueOf(qty));
            sub.setText(String.valueOf(subtotal));


        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }


}
