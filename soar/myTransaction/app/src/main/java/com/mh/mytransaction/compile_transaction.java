package com.mh.mytransaction;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class compile_transaction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    String created = "", created_by = "", updated = "", updated_by = "", isactive = "Y", branch_name = "";
    private DatePickerDialog.OnDateSetListener mydate, mydate1;
    ListView myListView;
    DatabaseHelper dh;
    List<File_list> list1, list2;
    Button fromdate, todate;
    TextView fdate, tdate;
    ImageView compile, send,print;
    SQLiteDatabase db;
    Cursor cursor3;
    Spinner option1;
    String tablename,tablename1,tablename3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_transaction);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        myListView = (ListView) findViewById(R.id.file_list);
        fromdate = (Button) findViewById(R.id.fdate);
        todate = (Button) findViewById(R.id.tdate);
        fdate = (TextView) findViewById(R.id.fdate1);
        tdate = (TextView) findViewById(R.id.tdate1);
        compile = (ImageView) findViewById(R.id.compile);
        send = (ImageView) findViewById(R.id.send);
        print = (ImageView) findViewById(R.id.print);
        option1=(Spinner)findViewById(R.id.option);
        dh = new DatabaseHelper(this);

        dh.updateTransaction2();
        dh.updateTransaction3();

        fromdate.setText(getIntent().getStringExtra("fromdate"));
        todate.setText(getIntent().getStringExtra("todate"));
        fdate.setText(getIntent().getStringExtra("fromdate1"));
        tdate.setText(getIntent().getStringExtra("todate1"));
        int pos = Integer.parseInt(getIntent().getStringExtra("position"));

        String[] list=options();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(compile_transaction.this,R.layout.spinner_layout,R.id.txt,list);
        option1.setAdapter(adapter);

        option1.setSelection(getIndex(option1,getIntent().getStringExtra("option")));

        getData(option1.getSelectedItem().toString(),fdate.getText().toString(), tdate.getText().toString(),pos);


        /*if(option1.getSelectedItem().toString().equals("Delivery Report")){
            tablename="mh_delivery_report";
            list1 = dh.getDelivery_List(fdate.getText().toString(), tdate.getText().toString());
        }else{
            tablename="mh_transaction";
            list1 = dh.getTransaction_List(fdate.getText().toString(), tdate.getText().toString());
        }
        try {

            File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
            myListView.setAdapter(adapter1);
            myListView.setSelection(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }*/



        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(compile_transaction.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        mydate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month1=month+1;
                if(month1==13){
                    month1=1;
                }

                if (month1 < 10 && dayOfMonth < 10) {
                    String from_date = "0" + month1 + "/0" + dayOfMonth+"/"+year;
                    fromdate.setText(from_date);
                    String from_date1 = "0" + month1 + "-0" + dayOfMonth+"-"+year;
                    fdate.setText(from_date1);
                } else if (month1 < 10 && dayOfMonth >= 10) {
                    String from_date = "0" + month1 + "/" + dayOfMonth+"/"+year;
                    fromdate.setText(from_date);
                    String from_date1 = "0" + month1 + "-" + dayOfMonth+"-"+year;
                    fdate.setText(from_date1);
                } else if (month1 >= 10 && dayOfMonth < 10) {
                    String from_date = month1 + "/0" + dayOfMonth+"/"+year;
                    fromdate.setText(from_date);
                    String from_date1 =  month1 + "-0" + dayOfMonth+"-"+year;
                    fdate.setText(from_date1);
                } else if (month1 >= 10 && dayOfMonth >= 10) {
                    String from_date = month1 + "/" + dayOfMonth+"/"+year;
                    fromdate.setText(from_date);
                    String from_date1 = month1 + "-" + dayOfMonth+"-"+year;
                    fdate.setText(from_date1);

                }

            }
        };
        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(compile_transaction.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate1, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mydate1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month1=month+1;
                if(month1==13){
                    month1=1;
                }
                if (month1 < 10 && dayOfMonth < 10) {
                    String to_date = "0" + month1 + "/0" + dayOfMonth+"/"+year;
                    todate.setText(to_date);
                    String from_date1 = "0" + month1 + "-0" + dayOfMonth+"-"+year;
                    tdate.setText(from_date1);
                } else if (month1 < 10 && dayOfMonth >= 10) {
                    String to_date = "0" + month1 + "/" + dayOfMonth+"/"+year;
                    todate.setText(to_date);
                    String from_date1 = "0" + month1 + "-" + dayOfMonth+"-"+year;
                    tdate.setText(from_date1);
                } else if (month1 >= 10 && dayOfMonth < 10) {
                    String to_date = month1 + "/0" + dayOfMonth+"/"+year;
                    todate.setText(to_date);
                    String from_date1 = month1 + "-0" + dayOfMonth+"/"+year;
                    tdate.setText(from_date1);
                } else if (month1 >= 10 && dayOfMonth >= 10) {
                    String to_date = month1 + "/" + dayOfMonth+"/"+year;
                    todate.setText(to_date);
                    String from_date1 = month1 + "-" + dayOfMonth+"-"+year;
                    tdate.setText(from_date1);
                }
            }


        };
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File_list file_list = list1.get(position);
                String identifier;
                if (!file_list.getFilename().equals("List File")) {
                    try {
                        if (file_list.getChoosed().equals("Y")) {
                            identifier = "N";
                        } else {
                            identifier = "Y";
                        }


                        int pos = myListView.getFirstVisiblePosition();
                        dh.updateTemplate_files(file_list.getDoc_num(), identifier,tablename);
                        /*Intent in = new Intent(compile_transaction.this, compile_transaction.class);
                        in.putExtra("fromdate", fromdate.getText().toString());
                        in.putExtra("todate", todate.getText().toString());
                        in.putExtra("fromdate1", fdate.getText().toString());
                        in.putExtra("todate1", tdate.getText().toString());
                        in.putExtra("position", String.valueOf(pos));
                        in.putExtra("option",option1.getSelectedItem().toString());
                        in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                        startActivity(in);*/
                        getData(option1.getSelectedItem().toString(),fdate.getText().toString(), tdate.getText().toString(),pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        compile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if(option1.getSelectedItem().toString().equals("Delivery Report")){
                        tablename="mh_delivery_report";
                        tablename3="mh_delivery_report_template";
                        list1 = dh.getDelivery_List(fdate.getText().toString(), tdate.getText().toString());
                        File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
                        myListView.setAdapter(adapter1);
                        boolean check_recipient=dh.check_recipient1(tablename3);
                        if(!check_recipient){
                            dh.update_recipient();
                        }
                    }else{
                        tablename="mh_transaction";
                        tablename3="mh_template";
                        list1 = dh.getTransaction_List(fdate.getText().toString(), tdate.getText().toString());
                        File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
                        myListView.setAdapter(adapter1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compile(tablename);
                try {
                    dh = new DatabaseHelper(compile_transaction.this);
                    int count = dh.countSend(tablename);
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("application/octet-stream");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    String[] filePaths = dh.getFile_name(count,tablename);
                    boolean check_recipient=dh.check_recipient(tablename);
                    if(check_recipient){
                        String sendto=dh.getTemlpate_id(tablename);
                        /*if(tablename.equals("mh_delivery_report")){
                            sendto="mhsoar.inventory@gmail.com";
                        }else{
                            sendto=dh.getTemlpate_id(tablename);
                        }*/
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendto});
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (String file : filePaths) {


                            intent.putExtra(Intent.EXTRA_SUBJECT, file);
                            File root = Environment.getExternalStorageDirectory();
                            String file2 = "Download/" + file+".csv";
                            File filePath = new File(root, file2);
                            if (!filePath.exists() || !filePath.canRead()) {
                                Toast.makeText(compile_transaction.this, "Attachment Error", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Uri u;
                            if (Build.VERSION.SDK_INT < 24) {
                                u = Uri.fromFile(filePath);
                            } else {
                                u = FileProvider.getUriForFile(compile_transaction.this, BuildConfig.APPLICATION_ID + ".provider", filePath);
                            }
                            uris.add(u);
                        }
                        intent.putExtra(Intent.EXTRA_STREAM, uris);
                        startActivity(Intent.createChooser(intent, "Send email..."));
                        dh.updateTransaction1(tablename);
                        dh.updateTransaction2();
                        dh.updateTransaction3();

                        try {
                            if(option1.getSelectedItem().toString().equals("Delivery Report")){
                                tablename="mh_delivery_report";
                                tablename3="mh_delivery_report_template";
                                list1 = dh.getDelivery_List(fdate.getText().toString(), tdate.getText().toString());
                                File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
                                myListView.setAdapter(adapter1);
                            }else{
                                tablename="mh_transaction";
                                tablename3="mh_template";
                                list1 = dh.getTransaction_List(fdate.getText().toString(), tdate.getText().toString());
                                File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
                                myListView.setAdapter(adapter1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }






                    }else{
                        Toast.makeText(compile_transaction.this, "You're not able to sent multiple files with different recipient", Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i=0;
                if(option1.getSelectedItem().toString().equals("Delivery Report")){
                    i=0;
                }else{
                    i=1;
                }
                Intent in=new Intent(compile_transaction.this,Print.class);
                in.putExtra("ident",String.valueOf(i));
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                in.putExtra("to_print",String.valueOf(0));
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog= new AlertDialog.Builder(compile_transaction.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in = new Intent(compile_transaction.this, Home.class);
                    startActivity(in);



                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }
        });

    }

    private void getData(String position,String fdate,String tdate,int pos){
        if(position.equals("Delivery Report")){
            tablename="mh_delivery_report";
            list1 = dh.getDelivery_List(fdate, tdate);
        }else{
            tablename="mh_transaction";
            list1 = dh.getTransaction_List(fdate, tdate);
        }
        try {

            File_adapter adapter1 = new File_adapter(compile_transaction.this, R.layout.file_list_layout, list1);
            myListView.setAdapter(adapter1);
            myListView.setSelection(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean compile(String tablename) {


        dh = new DatabaseHelper(this);
        String tablename2;
        String qty;
        String prc;
        if(tablename.equals("mh_delivery_report")){
            tablename1="mh_delivery_report_line";
            tablename2="mh_delivery_report_template";
            prc="quantity_def0";
            qty="quantity_def0_temporary";
        }else{
            tablename1="mh_transaction_line";
            tablename2="mh_transaction";
            prc="priceentered";
            qty="quantity_def0";
        }


        int count = dh.countSend(tablename);
        try {

            String[] label = {"Template", "Document Number", "Date Requirement", "Gross Sales", "SCD","SVAT","PWD","COH/CID","Total Expenses","Cash Deposit","TC","TCR","Refund","Created by","Reference Number","Remarks","Locator","Created"};
            String[] filename = dh.getFile_name(count,tablename);
            String[] doc_num = new String[count];
            for (int i = 0; i < count; i++) {
                doc_num[i] = dh.getDoc_num1(filename[i],tablename);
                String[] info = dh.getContentToSend(doc_num[i], filename[i],tablename);
                String temp_name=dh.getTemp_name(doc_num[i],tablename2);
                String state = Environment.getExternalStorageState();
                if (!Environment.MEDIA_MOUNTED.equals(state)) {
                    return false;
                } else {
                    //We use the Download directory for saving our .csv file.
                    File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!exportDir.exists()) {
                        exportDir.mkdirs();
                    }

                    File file;
                    PrintWriter printWriter;
                    try {
                        file = new File(exportDir, filename[i] + ".csv");
                        if (!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (!file.exists()){
                            file.createNewFile();
                        }
                        printWriter = new PrintWriter(new FileWriter(file));
                        for (int o = 0; o < 18; o++) {
                            if(o==0){
                                printWriter.println(label[o] + "," + temp_name);
                            }else if(o==17){
                                printWriter.println(label[o] + "," + info[17]);
                            }else{
                                printWriter.println(label[o] + "," + info[o-1]);
                            }
                        }
                        printWriter.println();
                        if(!tablename.equals("mh_delivery_report")){
                            printWriter.println("BRANCH,LOCATOR,PRODUCT,UOM,PRICE,QUANTITY,SUBTOTAL,DATE REQUIREMENT,REMARKS,REFERENCE NUMBER");
                        }else{
                            printWriter.println("BRANCH,LOCATOR,PRODUCT,UOM,ORIG QUANTITY,ENTERED QUANTITY,SUBTOTAL,DATE REQUIREMENT,REMARKS,REFERENCE NUMBER");
                        }

                        try {
                            db = dh.getReadableDatabase();
                            String branch=dh.getbranch_name(Integer.parseInt(info[16]));
                            String locator=info[15];
                            cursor3 = db.rawQuery("select mh_product_id,c_uom_id,"+prc+","+qty+",subtotal from '"+tablename1+"' where document_num='" + doc_num[i] + "' and changed='Y'", null);
                            cursor3.moveToFirst();
                            do {
                                String product=dh.getProduct_name(cursor3.getInt(0));
                                String uom=dh.getUom_name1(cursor3.getInt(1));
                                double price=cursor3.getDouble(2);
                                double quantity=cursor3.getDouble(3);
                                double subtotal=cursor3.getDouble(4);
                                printWriter.println(branch+","+locator+","+product + "," + uom + "," + price + "," + quantity+","+subtotal+","+info[1]+","+info[14]+","+info[13]);
                            } while (cursor3.moveToNext());
                            if(!tablename.equals("mh_delivery_report")){
                                printWriter.println(branch+","+locator+","+"CASH DEPOSIT"+ "," + "PIECE"+ "," + "1" + "," + "1"+","+info[8]+","+info[1]+","+info[14]+","+info[13]);
                            }
                            cursor3.close();
                            db.close();
                            //Toast.makeText(compile_transaction.this, "compiled success", Toast.LENGTH_SHORT).show();
                        } catch (Exception exc) {
                            exc.printStackTrace();
                            return false;
                        } finally {
                            if (printWriter != null) printWriter.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(compile_transaction.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(compile_transaction.this);
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
                                    Intent in = new Intent(compile_transaction.this, List_product.class);
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
            }else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(compile_transaction.this);
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




            } else if (id == R.id.nav_request) {

                boolean check_branches=dh.check_branches1();
                if(check_branches) {
                    dh=new DatabaseHelper(this);
                    Intent in=new Intent(compile_transaction.this,Request.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    startActivity(in);
                }else{
                    Toast.makeText(compile_transaction.this,"Set branch first",Toast.LENGTH_LONG).show();
                }




            }else if (id == R.id.nav_branch) {
                AlertDialog.Builder builder = new AlertDialog.Builder(compile_transaction.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(compile_transaction.this);
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
                                    Intent in = new Intent(compile_transaction.this, Branch.class);
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

            } else if (id == R.id.nav_employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(compile_transaction.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(compile_transaction.this);
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
                                    Intent in = new Intent(compile_transaction.this, Add_employee.class);
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
                //String name=dh.getEmp_name(code.getText().toString());
                Intent in = new Intent(compile_transaction.this, Emp_template_list.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);
            } else if (id == R.id.nav_created) {
                //String name=dh.getEmp_name(code.getText().toString());
                Intent in = new Intent(compile_transaction.this, Created_transactions.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);
            } else if (id == R.id.nav_template) {
                AlertDialog.Builder builder = new AlertDialog.Builder(compile_transaction.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(compile_transaction.this);
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
                                    Intent in = new Intent(compile_transaction.this, Template_list.class);
                                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
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

            }else if (id == R.id.nav_deliver) {


                Intent in=new Intent(compile_transaction.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);




            } else if (id == R.id.nav_send) {



            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(compile_transaction.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(compile_transaction.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(compile_transaction.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_send_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void error(){
        Toast.makeText(this,"Incorrect employee number or password",Toast.LENGTH_LONG).show();
    }

    private void error1(){
        Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show();
    }

    public String[] options(){
        String[] ar=new String[2];
        ar[0]="Delivery Report";
        ar[1]="Other Transaction";
        return ar;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}

