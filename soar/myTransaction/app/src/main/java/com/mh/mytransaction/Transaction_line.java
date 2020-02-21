package com.mh.mytransaction;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Transaction_line extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    String created="",created_by="",updated="",updated_by="",isactive="Y",branch_name="",choose="N",filename;
    private DatePickerDialog.OnDateSetListener mydate9;
    Spinner temp_list;
    private ListView myListView2;
    DatabaseHelper dh;
    List<Trans_line> list1;
    String dec_places,dec_places2,dec_places3,dec_places4,date1;
    ImageButton done;
    ImageButton search_btn,back;
    LinearLayout for_import,for_back;
    SearchView search;
    int identifier=0;
    TextView gross_value;
    double pricee=0.0;
    double qty2=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_line);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        temp_list = (Spinner) findViewById(R.id.temp_name);
        myListView2 = (ListView) findViewById(R.id.contlistview);
        done=(ImageButton)findViewById(R.id.done);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_import=(LinearLayout)findViewById(R.id.for_import);

        gross_value=(TextView)findViewById(R.id.gross_id);
        search.setVisibility(View.GONE);
        temp_list.setVisibility(View.VISIBLE);
        for_import.setVisibility(View.VISIBLE);

        String doc_num = "";
        int temp_id;
        final String template= getIntent().getStringExtra("Template");
        final String name = getIntent().getStringExtra("emp_name");
        final int pos = Integer.parseInt(getIntent().getStringExtra("position"));
        dh = new DatabaseHelper(this);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        created = format1.format(Calendar.getInstance().getTime());

        final int ident=Integer.parseInt(getIntent().getStringExtra("ident"));
        if(ident==0){
            try {
                temp_id=dh.getDelivery_temp_id(template);
                final ArrayList<String> list=dh.getTemp_list1();
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,list);
                temp_list.setAdapter(adapter);
                //temp_list.setSele(template);
                temp_list.setSelection(getIndex(temp_list,template));
                doc_num=dh.getDocument_num(temp_id);
                int temp_id2=dh.getTemp_id1(template);
                temp_id = dh.getDelivery_id(doc_num);
                gross_value.setText(String.valueOf(dh.getsubGross1(temp_id2)));
                /*list1 = dh.getTrans_cont1(temp_id);
                Transaction_adapter adapter1 = new Transaction_adapter(this, R.layout.trans_line_dialog, list1);
                myListView2.setAdapter(adapter1);
                myListView2.setSelection(pos);*/
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try {
                final ArrayList<String> list=dh.getTemp_list();
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,list);
                temp_list.setAdapter(adapter);
                //temp_list.setSele(template);
                temp_list.setSelection(getIndex(temp_list,template));
                int temp_id1=dh.getTemp_id(template);
                gross_value.setText(String.valueOf(dh.getsubGross(temp_id1)));
                /*list1 = dh.getTrans_cont();
                Transaction_adapter adapter1 = new Transaction_adapter(this, R.layout.trans_line_dialog, list1);
                myListView2.setAdapter(adapter1);
                myListView2.setSelection(pos);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        temp_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                int temp_id1=0;
                if(ident==0){
                    temp_id1=dh.getTemp_id1(temp_list.getSelectedItem().toString());
                    gross_value.setText(String.valueOf(dh.getsubGross1(temp_id1)));
                }else{
                    temp_id1=dh.getTemp_id(temp_list.getSelectedItem().toString());
                    gross_value.setText(String.valueOf(dh.getsubGross(temp_id1)));
                }

                gross_value.setText(String.valueOf(dh.getsubGross(temp_id1)));
                viewData(ident,pos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        myListView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {



                return true;
            }
        });
            myListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    try {

                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        created = format1.format(Calendar.getInstance().getTime());
                        final Trans_line trans_line = list1.get(position);
                        if (trans_line.getProduct().equals("No Result Found")) {


                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                            View mview = getLayoutInflater().inflate(R.layout.edit_trans_line, null);
                            final TextView prod_name = (TextView) mview.findViewById(R.id.product_name);
                            TextView uom_name = (TextView) mview.findViewById(R.id.uom_name);
                            final EditText price = (EditText) mview.findViewById(R.id.price);
                            final EditText quantity = (EditText) mview.findViewById(R.id.quantity);
                            final TextView Isgrab=(TextView) mview.findViewById(R.id.isgrab);



                            dh = new DatabaseHelper(Transaction_line.this);

                            if(trans_line.getPrice_temp()==0.0){
                                price.setText("");
                            }else{
                                price.setText(String.valueOf(trans_line.getPrice_temp()));
                            }
                            if(trans_line.getQty_temp()==0.0){
                                if(trans_line.getIs_computed().equals("Y")){
                                    quantity.setText(String.valueOf(0));
                                }else{
                                    quantity.setText(String.valueOf(1));
                                }

                            }else{
                                quantity.setText(String.valueOf(trans_line.getQty_temp()));
                            }

                            prod_name.setText(trans_line.getProduct());
                            uom_name.setText(trans_line.getUom());
                            if(!trans_line.getIsgrab().equals(null)){
                                Isgrab.setText(trans_line.getIsgrab());
                            }else{
                                Isgrab.setText("N");
                            }



                            //quantity.setText(String.valueOf(trans_line.getQty()));
                            final String iscomputed=trans_line.getIs_computed();
                        /*if(Double.parseDouble(price.getText().toString())==0.0){
                            price.setEnabled(true);
                        }else{
                            price.setEnabled(false);
                        }*/

                            final int temp_id = trans_line.getTemplate_id();
                            final int prod_id = dh.get_prod_id(prod_name.getText().toString());
                            final int uom_id = dh.getUom_id(uom_name.getText().toString());
                            final String isnegative = trans_line.getIsnegative();
                            final String isgrab=trans_line.getIsgrab();
                            final String prev_price=price.getText().toString();

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {

                                    try {
                                        if(price.getText().toString().equals("")){
                                            pricee=0.0;
                                        }else if(!price.getText().toString().equals(prev_price)){

                                            pricee=Double.parseDouble(price.getText().toString());
                                            final AlertDialog dialog1= new AlertDialog.Builder(Transaction_line.this)
                                                    .setMessage("You're about to change the price, are you sure you want to continue?")
                                                    .setPositiveButton("Yes",null)
                                                    .setNegativeButton("Cancel",null)
                                                    .show();
                                            Button positiveButton=dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    try{

                                                        if(quantity.getText().toString().equals("")){
                                                            qty2=0.0;
                                                        }else{
                                                            qty2=Double.parseDouble(quantity.getText().toString());
                                                        }
                                                        double subtotal = (pricee * (qty2));
                                                        int pos = myListView2.getFirstVisiblePosition();
                                                        String change="Y";
                                        /*if(!price.getText().toString().equals(String.valueOf(trans_line.getPrice())) || !quantity.getText().toString().equals(String.valueOf(trans_line.getQty()))){
                                            change="Y";
                                        }else{
                                            change="N";
                                        }*/
                                                        if(ident==0){



                                                            dh.update_delivery_report_line(temp_id, qty2, prod_id,pricee,subtotal,created,getIntent().getStringExtra("emp_name"),change);
                                                            Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();
                                                            gross_value.setText(String.valueOf(dh.getsubGross1(temp_id)));
                                                            viewData(ident,position);

                                                        }else{
                                                            int prev_transac_id = dh.getTransac_id();
                                                            final int transac_id = prev_transac_id + 1;
                                                            double sub=pricee*qty2;

                                                            String subtotal1=String.format("%.2f", sub);
                                                            dh.update_trans_temp1(transac_id,temp_id, qty2, prod_id,pricee,subtotal1,created,name,change,isgrab);
                                                            Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();

                                                            int temp_id2=dh.getTemp_id(temp_list.getSelectedItem().toString());
                                                            gross_value.setText(String.valueOf(dh.getsubGross(temp_id2)));
                                                            viewData(ident,position);
                                                            dialog.cancel();
                                                            dialog1.cancel();
                                                        }


                                                    }catch (Exception ex){
                                                        ex.printStackTrace();

                                                    }
                                                }
                                            });
                                        }else if(price.getText().toString().equals(prev_price)){
                                            pricee=Double.parseDouble(price.getText().toString());
                                            if(quantity.getText().toString().equals("")){
                                                qty2=0.0;
                                            }else{
                                                qty2=Double.parseDouble(quantity.getText().toString());
                                            }
                                            double subtotal = (pricee * (qty2));
                                            int pos = myListView2.getFirstVisiblePosition();
                                            String change="Y";
                                        /*if(!price.getText().toString().equals(String.valueOf(trans_line.getPrice())) || !quantity.getText().toString().equals(String.valueOf(trans_line.getQty()))){
                                            change="Y";
                                        }else{
                                            change="N";
                                        }*/
                                            if(ident==0){



                                                dh.update_delivery_report_line(temp_id, qty2, prod_id,pricee,subtotal,created,getIntent().getStringExtra("emp_name"),change);
                                                Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();
                                                gross_value.setText(String.valueOf(dh.getsubGross1(temp_id)));
                                                viewData(ident,position);

                                            }else{
                                                int prev_transac_id = dh.getTransac_id();
                                                final int transac_id = prev_transac_id + 1;
                                                double sub=pricee*qty2;

                                                String subtotal1=String.format("%.2f", sub);
                                                dh.update_trans_temp1(transac_id,temp_id, qty2, prod_id,pricee,subtotal1,created,name,change,isgrab);
                                                Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();

                                                int temp_id2=dh.getTemp_id(temp_list.getSelectedItem().toString());
                                                gross_value.setText(String.valueOf(dh.getsubGross(temp_id2)));
                                                viewData(ident,position);
                                                dialog.cancel();
                                            }
                                        }






                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });


                            builder.setView(mview);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String name=getIntent().getStringExtra("emp_name");
                    SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    created=format1.format(Calendar.getInstance().getTime());
                    SimpleDateFormat format2 = new SimpleDateFormat("MM-dd-yyyy");
                    final String filedate=format2.format(Calendar.getInstance().getTime());

                    final AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                    final View mview = getLayoutInflater().inflate(R.layout.transaction_dialog, null);
                    TextView temp_name = (TextView) mview.findViewById(R.id.template_name);
                    final TextView doc_num = (TextView) mview.findViewById(R.id.doc_num);
                    TextView branch_name1 = (TextView) mview.findViewById(R.id.branch_name);
                    TextView gross = (TextView) mview.findViewById(R.id.gross);
                    final EditText reference=(EditText)mview.findViewById(R.id.reference);
                    final EditText remarks=(EditText)mview.findViewById(R.id.remarks);
                    final TextView coh = (TextView) mview.findViewById(R.id.coh);
                    final TextView exp = (TextView) mview.findViewById(R.id.exp);
                    final TextView sc = (TextView) mview.findViewById(R.id.sc);
                    final TextView vat = (TextView) mview.findViewById(R.id.vat);
                    final TextView cash = (TextView) mview.findViewById(R.id.cash);
                    final TextView tc = (TextView) mview.findViewById(R.id.tc);
                    final TextView tcref = (TextView) mview.findViewById(R.id.tcr);
                    final TextView pwd = (TextView) mview.findViewById(R.id.pwd);
                    final TextView refund = (TextView) mview.findViewById(R.id.refund);
                    TextView creator = (TextView) mview.findViewById(R.id.creator);
                    TextView created1 = (TextView) mview.findViewById(R.id.created);
                    final Button mydate1=(Button)mview.findViewById(R.id.date_req);
                    final ImageView save=(ImageView)mview.findViewById(R.id.save);


                    created1.setText(filedate);



                    dh=new DatabaseHelper(Transaction_line.this);
                    final int ident=Integer.parseInt(getIntent().getStringExtra("ident"));
                    if(ident==0){
                        try {

                            mydate1.setEnabled(false);
                            temp_name.setText(temp_list.getSelectedItem().toString());
                            doc_num.setText(getIntent().getStringExtra("doc_num"));
                            branch_name1.setText(getIntent().getStringExtra("branch"));
                            mydate1.setText(getIntent().getStringExtra("date_req"));
                            creator.setText(getIntent().getStringExtra("emp_name"));

                            final int temp_id=dh.getDelivery_id(doc_num.getText().toString());
                            final double gsale=dh.getgross(temp_id);
                            final double cfd_comp=dh.getcfd(temp_id);
                            final double coh_comp=dh.getcoh(temp_id);
                            final double total_exp=dh.getexp(temp_id);
                            final double scd=dh.getscd(temp_id);
                            final double vat1=dh.getvat(temp_id);
                            final double pwd1=dh.getpwd(temp_id);
                            final int branch_id=dh.getbranch_id(branch_name1.getText().toString());

                            gross.setText(String.valueOf(gsale));
                            coh.setText(String.valueOf(coh_comp));
                            exp.setText(String.valueOf(total_exp));
                            cash.setText(String.valueOf(cfd_comp));
                            sc.setText(String.valueOf(scd));
                            vat.setText(String.valueOf(vat1));
                            pwd.setText(String.valueOf(pwd1));
                            tc.setText(String.valueOf(dh.gettc(temp_id)));
                            tcref.setText(String.valueOf(dh.gettcr(temp_id)));
                            refund.setText(String.valueOf(dh.getrefund(temp_id)));
                            reference.setText(getIntent().getStringExtra("doc_num"));
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {


                                        String refer = reference.getText().toString();
                                        String remarks1 = remarks.getText().toString();

                                        String locator=dh.getLocator(getIntent().getStringExtra("doc_num"));
                                        dh.insertTransaction_for_delivery(temp_id, branch_id, doc_num.getText().toString(), mydate1.getText().toString(), gsale,scd,vat1,pwd1,coh_comp,total_exp,cfd_comp,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()), refer, remarks1, temp_list.getSelectedItem().toString(), choose, created, name, isactive,filedate,locator);
                                        int temp_id=dh.getDelivery_id(getIntent().getStringExtra("doc_num"));
                                        boolean check_delivery=dh.check_delivery(getIntent().getStringExtra("doc_num"));
                                        if(check_delivery){
                                            dh.insert_into_template_files(temp_id,temp_list.getSelectedItem().toString(),getIntent().getStringExtra("doc_num"),getIntent().getStringExtra("branch"),temp_list.getSelectedItem().toString(),refer,remarks1,locator,getIntent().getStringExtra("date_req"),filedate,created,name,gsale,scd,vat1,pwd1,coh_comp,total_exp,cfd_comp,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()));
                                        }else{
                                            dh.update_delivery(temp_id,temp_list.getSelectedItem().toString(),getIntent().getStringExtra("doc_num"),getIntent().getStringExtra("branch"),temp_list.getSelectedItem().toString(),refer,remarks1,locator,getIntent().getStringExtra("date_req"),filedate,created,name,gsale,scd,vat1,pwd1,coh_comp,total_exp,cfd_comp,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()));
                                        }


                                        Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();
                                        Intent in = new Intent(Transaction_line.this, Transaction_line.class);
                                        in.putExtra("Template", temp_list.getSelectedItem().toString());
                                        in.putExtra("emp_name", name);
                                        in.putExtra("position", String.valueOf(0));
                                        in.putExtra("ident", getIntent().getStringExtra("ident"));
                                        in.putExtra("branch", getIntent().getStringExtra("branch"));
                                        in.putExtra("date_req", getIntent().getStringExtra("date_req"));
                                        in.putExtra("doc_num", getIntent().getStringExtra("doc_num"));

                                        startActivity(in);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            });



                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        int temp_id2=dh.getTemp_id(temp_list.getSelectedItem().toString());
                        final String com_fra;
                        com_fra=dh.checkComp_Fra();
                        temp_name.setText(temp_list.getSelectedItem().toString());
                        final double cfd1=dh.getCfd(temp_id2);
                        final double gross1=dh.getGross(temp_id2);
                        final double coh1=dh.getCoh(temp_id2);
                        final double exp1=dh.getExp(temp_id2);
                        final int branch_id=dh.getBranch_id();
                        branch_name=dh.getBranch_name();
                        final int ref_num;
                        final String bname=branch_name;


                        dec_places=String.format("%.2f", cfd1);
                        dec_places2=String.format("%.2f", gross1);
                        dec_places3=String.format("%.2f", coh1);
                        dec_places4=String.format("%.2f", exp1);
                        cash.setText(String.valueOf(dec_places));
                        gross.setText(String.valueOf(dec_places2));
                        coh.setText(String.valueOf(dec_places3));
                        exp.setText(String.valueOf(dec_places4));
                        tc.setText(String.valueOf(dh.getTc(temp_id2)));
                        tcref.setText(String.valueOf(dh.getTcr(temp_id2)));
                        refund.setText(String.valueOf(dh.getR(temp_id2)));
                        sc.setText(String.valueOf(dh.getSc(temp_id2)));
                        vat.setText(String.valueOf(dh.getVat(temp_id2)));
                        pwd.setText(String.valueOf(dh.getPwd(temp_id2)));
                        branch_name1.setText(branch_name);
                        creator.setText(name);
                        int count_branch=dh.getCount_branch(branch_id);
                        ref_num=1000000+count_branch;
                        branch_name=branch_name.replace(" ","_");
                        doc_num.setText(ref_num+" "+branch_name);

                        mydate1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar cal= Calendar.getInstance();
                                int year=cal.get(Calendar.YEAR);
                                int month=cal.get(Calendar.MONTH);
                                int day=cal.get(Calendar.DAY_OF_MONTH);
                                DatePickerDialog dialog=new DatePickerDialog(Transaction_line.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,mydate9,year,month,day);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                            }
                        });

                        mydate9=new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                int month1=month+1;
                                if(month1==13){
                                    month1=1;
                                }
                                if(month1<10 && dayOfMonth<10){
                                    String from_date="0"+month1+"-0"+dayOfMonth+"-"+year;
                                    mydate1.setText(from_date);
                                    date1="0"+month1+"-0"+dayOfMonth+"-"+year;
                                }
                                else if(month1<10 && dayOfMonth>=10){
                                    String from_date="0"+month1+"-"+dayOfMonth+"-"+year;
                                    mydate1.setText(from_date);
                                    date1="0"+month1+"-"+dayOfMonth+"-"+year;
                                }
                                else if(month1>=10 && dayOfMonth<10){
                                    String from_date=""+month1+"-0"+dayOfMonth+"-"+year;
                                    mydate1.setText(from_date);
                                    date1=""+month1+"-0"+dayOfMonth+"-"+year;
                                }else if(month1>=10 && dayOfMonth>=10){
                                    String from_date=""+month1+"-"+dayOfMonth+"-"+year;
                                    mydate1.setText(from_date);
                                    date1=""+month1+"-"+dayOfMonth+"-"+year;
                                }
                            }

                        };



                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{

                                    String type=dh.getTemp_sku(temp_list.getSelectedItem().toString());
                                    String type_sku="";
                                    if(type.equals("S")){
                                        type_sku="SALES";
                                    }else if(type.equals("O")){
                                        type_sku="ORDERS";
                                    }else if(type.equals("P")){
                                        type_sku="PHYSICAL";
                                    }else if(type.equals("R")){
                                        type_sku="REQUISITION";
                                    }

                                    //filename=temp_list.getSelectedItem().toString()+"_"+doc_num.getText().toString()+"_"+com_fra+"_"+mydate1.getText().toString().replace("/","")+"_"+type_sku;
                                    filename=branch_name+"_"+com_fra+"_"+mydate1.getText().toString().replace("-","")+"_"+temp_list.getSelectedItem().toString()+"_"+ref_num+"_"+type_sku;
                                    if(!mydate1.getText().toString().equals("CHOOSE DATE")){
                                        long diff;
                                        String refer=reference.getText().toString();

                                        String remarks1=remarks.getText().toString();
                                        final int min,hour,day;
                                        SimpleDateFormat format=new SimpleDateFormat("MM-dd-yyyy");
                                        String today=format.format(Calendar.getInstance().getTime());
                                        Date date2=format.parse(date1);
                                        Date date3=format.parse(today);

                                        int template_id=dh.getTemp_id(temp_list.getSelectedItem().toString());

                                        diff= TimeUnit.MILLISECONDS.toSeconds(date2.getTime() - date3.getTime());
                                        min= (int) (diff/60);
                                        hour=min/60;
                                        day=hour/24;
                                        boolean checklead=dh.checkleaddays(day,template_id);
                                        if(checklead) {
                                            int temp_id = dh.getTemp_id(temp_list.getSelectedItem().toString());
                                            dh.insertTransaction(temp_id, branch_id, doc_num.getText().toString(), mydate1.getText().toString(), Double.parseDouble(dec_places2),Double.parseDouble(sc.getText().toString()),Double.parseDouble(vat.getText().toString()),Double.parseDouble(pwd.getText().toString()),Double.parseDouble(dec_places3),Double.parseDouble(dec_places4),cfd1,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()),refer,remarks1, filename, choose, created, name, isactive,filedate);
                                            int transact_id=dh.getTrans_id(doc_num.getText().toString());
                                            dh.insert_transaction_line(transact_id,doc_num.getText().toString(),temp_id);
                                            int trans=dh.getTransation_id(temp_id);
                                            String sendto=dh.getSendto(temp_id);
                                            boolean check_transaction=dh.check_delivery(doc_num.getText().toString());
                                            if(check_transaction){
                                                dh.insert_into_template_files1(transact_id,temp_list.getSelectedItem().toString(),doc_num.getText().toString(),bname,filename,refer,remarks1,"",mydate1.getText().toString(),filedate,sendto,created,getIntent().getStringExtra("emp_name"), Double.parseDouble(dec_places2),Double.parseDouble(sc.getText().toString()),Double.parseDouble(vat.getText().toString()),Double.parseDouble(pwd.getText().toString()),Double.parseDouble(dec_places3),Double.parseDouble(dec_places4),cfd1,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()));

                                            }else{
                                                dh.update_transaction(transact_id,temp_list.getSelectedItem().toString(),doc_num.getText().toString(),bname,filename,refer,remarks1,"",mydate1.getText().toString(),filedate,sendto,created,getIntent().getStringExtra("emp_name"), Double.parseDouble(dec_places2),Double.parseDouble(sc.getText().toString()),Double.parseDouble(vat.getText().toString()),Double.parseDouble(pwd.getText().toString()),Double.parseDouble(dec_places3),Double.parseDouble(dec_places4),cfd1,Double.parseDouble(tc.getText().toString()),Double.parseDouble(tcref.getText().toString()),Double.parseDouble(refund.getText().toString()));
                                            }
                                            dh.setDefault(temp_id);
                                            Toast.makeText(Transaction_line.this, "success", Toast.LENGTH_LONG).show();
                                            Intent in = new Intent(Transaction_line.this, Transaction_line.class);
                                            in.putExtra("Template", temp_list.getSelectedItem().toString());
                                            in.putExtra("emp_name", name);
                                            in.putExtra("position", String.valueOf(0));
                                            in.putExtra("ident",String.valueOf(1));
                                            startActivity(in);
                                        }else{
                                            Toast.makeText(Transaction_line.this,"date requirement is not covered by the given lead days",Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(Transaction_line.this,"Please choose date requirement",Toast.LENGTH_LONG).show();

                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }



                    builder.setCancelable(true);


                    builder.setView(mview);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(Transaction_line.this,"Set your branch first",Toast.LENGTH_LONG).show();

                }
            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (identifier == 0) {
                        search.setVisibility(View.VISIBLE);
                        temp_list.setVisibility(View.GONE);
                        for_import.setVisibility(View.GONE);
                        identifier = 1;
                    } else if (identifier == 1) {

                    }
                }
            });


            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }


                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        if (ident == 0) {
                            int temp_id1 = dh.getDelivery_temp_id(temp_list.getSelectedItem().toString());
                            list1 = dh.getTrans_cont2(temp_id1, newText);
                        } else {
                            int temp_id = dh.getTemp_id(temp_list.getSelectedItem().toString());
                            list1 = dh.getTrans_cont0(temp_id, newText);
                        }

                        Transaction_adapter adapter1 = new Transaction_adapter(Transaction_line.this, R.layout.trans_line_dialog, list1);
                        myListView2.setAdapter(adapter1);
                        myListView2.setSelection(pos);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (identifier == 1 && ident == 0) {
                        Intent in = new Intent(Transaction_line.this, Transaction_line.class);
                        in.putExtra("Template", temp_list.getSelectedItem().toString());
                        in.putExtra("emp_name", name);
                        in.putExtra("position", String.valueOf(pos));
                        in.putExtra("ident",getIntent().getStringExtra("ident"));
                        in.putExtra("branch",getIntent().getStringExtra("branch"));
                        in.putExtra("date_req",getIntent().getStringExtra("date_req"));
                        in.putExtra("doc_num",getIntent().getStringExtra("doc_num"));

                        startActivity(in);
                    } else if (identifier == 1 && ident == 1) {
                        Intent in = new Intent(Transaction_line.this, Transaction_line.class);
                        in.putExtra("Template", temp_list.getSelectedItem().toString());
                        in.putExtra("emp_name", name);
                        in.putExtra("position", String.valueOf(pos));
                        in.putExtra("ident",String.valueOf(1));

                        startActivity(in);
                    } else if (identifier == 0 && ident == 0) {

                        AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                                .setMessage("Are you sure you want exit?")
                                .setPositiveButton("Yes",null)
                                .setNegativeButton("Cancel",null)
                                .show();
                        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try{

                                    Intent in = new Intent(Transaction_line.this, Delivery_report.class);
                                    in.putExtra("emp_name", name);
                                    startActivity(in);


                                }catch (Exception ex){
                                    ex.printStackTrace();

                                }

                            }
                        });
                    } else if (identifier == 0 && ident == 1) {
                        AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                                .setMessage("Are you sure you want exit?")
                                .setPositiveButton("Yes",null)
                                .setNegativeButton("Cancel",null)
                                .show();
                        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try{

                                    Intent in = new Intent(Transaction_line.this, Emp_template_list.class);
                                    in.putExtra("emp_name", name);
                                    startActivity(in);


                                }catch (Exception ex){
                                    ex.printStackTrace();

                                }
                            }
                        });

                    }

                }
            });
    }

    private void viewData(int ident,int pos){
        try{
            if(ident==0){
                int temp_id1=dh.getDelivery_temp_id(temp_list.getSelectedItem().toString());
                list1 = dh.getTrans_cont1(temp_id1);
            }else{
                int temp_id = dh.getTemp_id(temp_list.getSelectedItem().toString());
                list1 = dh.getTrans_cont(temp_id);
            }
            Transaction_adapter adapter1 = new Transaction_adapter(Transaction_line.this, R.layout.trans_line_dialog, list1);
            myListView2.setAdapter(adapter1);
            myListView2.setSelection(pos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;


    }



    @Override
    public void onBackPressed() {
        dh=new DatabaseHelper(this);


        int ident=Integer.parseInt(getIntent().getStringExtra("ident"));
        if(ident==0){
            AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                    .setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes",null)
                    .setNegativeButton("Cancel",null)
                    .show();
            Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{

                        Intent in = new Intent(Transaction_line.this, Delivery_report.class);
                        in.putExtra("emp_name", getIntent().getStringExtra("emp_name"));
                        startActivity(in);


                    }catch (Exception ex){
                        ex.printStackTrace();

                    }

                }
            });
        }else{
            AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                    .setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes",null)
                    .setNegativeButton("Cancel",null)
                    .show();
            Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{

                        Intent in = new Intent(Transaction_line.this, Emp_template_list.class);
                        in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                        startActivity(in);


                    }catch (Exception ex){
                        ex.printStackTrace();

                    }
                }
            });
        }
    }

    private void error(){
        Toast.makeText(this,"Incorrect employee number or password",Toast.LENGTH_LONG).show();
    }
    private void error1(){
        Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        try {
            int id = menuItem.getItemId();

            if (id == R.id.nav_product) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Transaction_line.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(password.getText().toString())) {
                                error1();
                            } else {
                                boolean check_emp_log = dh.checkadmin(password.getText().toString());
                                if (check_emp_log) {
                                        dh.update_admin(password.getText().toString());
                                        Intent in = new Intent(Transaction_line.this, List_product.class);
                                        startActivity(in);
                                } else {
                                    error1();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (id == R.id.nav_branch) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);
                dh = new DatabaseHelper(Transaction_line.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(password.getText().toString())) {
                                error1();
                            } else {
                                boolean check_emp_log = dh.checkadmin(password.getText().toString());
                                if (check_emp_log) {
                                        dh.update_admin(password.getText().toString());
                                        Intent in = new Intent(Transaction_line.this, Branch.class);
                                        startActivity(in);
                                } else {
                                    error1();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else if(id==R.id.nav_change_pass){
            }

            else if (id == R.id.nav_about) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                View mview = getLayoutInflater().inflate(R.layout.about_dialog, null);
                TextView training=(TextView)mview.findViewById(R.id.training);
                TextView site=(TextView)mview.findViewById(R.id.site);
                try{
                    training.setMovementMethod(LinkMovementMethod.getInstance());
                    site.setMovementMethod(LinkMovementMethod.getInstance());
                }catch (Exception e){
                    e.printStackTrace();
                }
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (id == R.id.nav_employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);
                dh = new DatabaseHelper(Transaction_line.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(password.getText().toString())) {
                                error1();
                            } else {
                                boolean check_emp_log = dh.checkadmin(password.getText().toString());
                                if (check_emp_log) {
                                    dh.update_admin(password.getText().toString());
                                    Intent in = new Intent(Transaction_line.this, Add_employee.class);
                                    startActivity(in);
                                } else {
                                    error1();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (id == R.id.nav_transaction) {
                dh=new DatabaseHelper(this);
                Intent in=new Intent(Transaction_line.this,Emp_template_list.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            }else if (id == R.id.nav_created) {
                dh=new DatabaseHelper(this);
                Intent in=new Intent(Transaction_line.this,Created_transactions.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            }else if (id == R.id.nav_template) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_line.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh=new DatabaseHelper(this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(password.getText().toString())) {
                                error1();
                            } else {
                                boolean check_emp_log = dh.checkadmin(password.getText().toString());
                                if (check_emp_log) {
                                    dh.update_admin(password.getText().toString());
                                    Intent in = new Intent(Transaction_line.this, Template_list.class);
                                    startActivity(in);
                                } else {
                                    error1();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });


                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (id == R.id.nav_deliver) {

                dh=new DatabaseHelper(this);
                Intent in=new Intent(Transaction_line.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);




            }else if (id == R.id.nav_send) {
                                    dh=new DatabaseHelper(this);
                                    //dh.copyprice();
                                    Intent in=new Intent(Transaction_line.this,compile_transaction.class);
                                    in.putExtra("fromdate","FROM DATE");
                                    in.putExtra("todate","TO DATE");
                                    in.putExtra("fromdate1","FROM DATE");
                                    in.putExtra("todate1","TO DATE");
                                    in.putExtra("position",String.valueOf(0));
                                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                                    in.putExtra("option","Delivery Report");
                                    startActivity(in);
            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{
                            dh=new DatabaseHelper(Transaction_line.this);
                           // dh.copyprice();

                            Intent in=new Intent(Transaction_line.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }else if (id == R.id.nav_request) {

                boolean check_branches=dh.check_branches1();
                if(check_branches) {
                    dh=new DatabaseHelper(this);
                    Intent in=new Intent(Transaction_line.this,Request.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    startActivity(in);
                }else{
                    Toast.makeText(Transaction_line.this,"Set branch first",Toast.LENGTH_LONG).show();
                }




            }
            else if(id == R.id.nav_exit){
                dh=new DatabaseHelper(this);
                //dh.copyprice();
                AlertDialog dialog= new AlertDialog.Builder(Transaction_line.this)
                        .setMessage("Are you sure you want to close this app?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            finishAffinity();
                            System.exit(0);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }



                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_transactionline_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
