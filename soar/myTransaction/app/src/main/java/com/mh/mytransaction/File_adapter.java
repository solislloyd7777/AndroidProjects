package com.mh.mytransaction;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class File_adapter  extends ArrayAdapter<File_list> {

    private Context mContext;
    int mResource;
    DatabaseHelper dh;

    public File_adapter(Context context, int resource, List<File_list> objects) {
        super(context, resource,objects);
        mContext = context;
        mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{

            String doc_num=getItem(position).getDoc_num();
            String filename=getItem(position).getFilename();
            String choose=getItem(position).getChoosed();
            String creator=getItem(position).getCreaor();

            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mResource,parent,false);

            TextView filename1=(TextView) convertView.findViewById(R.id.file_name);
            TextView creator1=(TextView)convertView.findViewById(R.id.creator);
            TextView choose1=(TextView)convertView.findViewById(R.id.choosed);

            if(filename1.getText().toString().equals("List File")){
                filename1.setEnabled(false);
                creator1.setEnabled(false);
                choose1.setBackgroundColor(Color.parseColor("#C6DFF1"));
                choose1.setEnabled(false);

            }

            filename1.setText(filename);
            creator1.setText(creator);
            if(choose.equals("Y")){
                choose1.setBackgroundColor(Color.parseColor("#5AD55F"));
            }else if(choose.equals("N")){
                choose1.setBackgroundColor(Color.parseColor("#DA3838"));
            }

            /*if(choose1.isChecked()){
                getItem(position).setChoosed("Y");
            }else{
                getItem(position).setChoosed("N");
            }*/
//#5AD55F//#DA3838





        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
