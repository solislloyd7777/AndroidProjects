package com.mh.mytransaction;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class View_transactions extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Spinner file_name;
    ImageButton menu;
    ListView view_list;
    DatabaseHelper dh;
    List<Created_transaction_module> list;
    String created,created_by,updated,updated_by,isactive,doc_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        file_name=(Spinner)findViewById(R.id.name);
        menu=(ImageButton)findViewById(R.id.menu);
        view_list=(ListView)findViewById(R.id.view_list);

        dh=new DatabaseHelper(this);

        //file_name.setSe(getIntent().getStringExtra("file_name"));
        final ArrayList<String> list1=dh.getCreated_list();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,list1);
        file_name.setAdapter(adapter);
        file_name.setSelection(getIndex(file_name,getIntent().getStringExtra("file_name")));
        final int pos=Integer.parseInt(getIntent().getStringExtra("position"));



        file_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //int mytemp_id=dh.getTemp_id(temp_name.getSelectedItem().toString());
                try{
                    doc_num =dh.getdocument_number(file_name.getSelectedItem().toString());
                    list=dh.getCreatedList(doc_num);
                    final View_transaction_adapter adapter1 = new View_transaction_adapter(View_transactions.this, R.layout.view_list_layout, list);
                    view_list.setAdapter(adapter1);
                    view_list.setSelection(pos);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    created=format1.format(Calendar.getInstance().getTime());
                    SimpleDateFormat format2 = new SimpleDateFormat("MM-dd-yyyy");
                    final String filedate=format2.format(Calendar.getInstance().getTime());

                    final AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
                    final View mview = getLayoutInflater().inflate(R.layout.view_dialog, null);
                    final TextView doc_num1 = (TextView) mview.findViewById(R.id.doc_num);
                    TextView branch_name1 = (TextView) mview.findViewById(R.id.branch_name);
                    TextView gross = (TextView) mview.findViewById(R.id.gross);
                    final TextView reference=(TextView)mview.findViewById(R.id.reference);
                    final TextView remarks=(TextView) mview.findViewById(R.id.remarks);
                    final TextView cfd = (TextView) mview.findViewById(R.id.cfd);
                    final TextView coh = (TextView) mview.findViewById(R.id.coh);
                    final TextView exp = (TextView) mview.findViewById(R.id.exp);
                    final TextView scd = (TextView) mview.findViewById(R.id.scd);
                    final TextView vat = (TextView) mview.findViewById(R.id.vat);
                    final TextView pwd = (TextView) mview.findViewById(R.id.pwd);
                    final TextView tc = (TextView) mview.findViewById(R.id.tc);
                    final TextView tcr = (TextView) mview.findViewById(R.id.tcr);
                    final TextView refund = (TextView) mview.findViewById(R.id.refund);
                    TextView creator = (TextView) mview.findViewById(R.id.creator);
                    final TextView mydate1=(TextView) mview.findViewById(R.id.date_req);
                    final TextView created1=(TextView) mview.findViewById(R.id.created);


                    dh=new DatabaseHelper(View_transactions.this);

                    String[] array=dh.getView(doc_num);
                    doc_num1.setText(doc_num);
                    branch_name1.setText(array[0]);
                    mydate1.setText(array[1]);
                    creator.setText(array[2]);
                    reference.setText(array[3]);
                    remarks.setText(array[4]);
                    gross.setText(array[5]);
                    scd.setText(array[6]);
                    vat.setText(array[7]);
                    pwd.setText(array[8]);
                    coh.setText(array[9]);
                    exp.setText(array[10]);
                    cfd.setText(array[11]);
                    tc.setText(array[12]);
                    tcr.setText(array[13]);
                    refund.setText(array[14]);
                    created1.setText(array[15]);



                    builder.setCancelable(true);


                    builder.setView(mview);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

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

                    Intent in=new Intent(View_transactions.this,Created_transactions.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    in.putExtra("position",getIntent().getStringExtra("position"));
                    startActivity(in);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(View_transactions.this);
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
                                        Intent in = new Intent(View_transactions.this, List_product.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(View_transactions.this);
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
                                        Intent in = new Intent(View_transactions.this, Branch.class);
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

            }else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
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




            }else if (id == R.id.nav_request) {

                boolean check_branches=dh.check_branches1();
                if(check_branches) {
                    dh=new DatabaseHelper(this);
                    Intent in=new Intent(View_transactions.this,Request.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    startActivity(in);
                }else{
                    Toast.makeText(View_transactions.this,"Set branch first",Toast.LENGTH_LONG).show();
                }




            } else if (id == R.id.nav_employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(View_transactions.this);
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
                                    Intent in = new Intent(View_transactions.this, Add_employee.class);
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
                Intent in=new Intent(View_transactions.this,Emp_template_list.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);
            } else if (id == R.id.nav_template) {
                AlertDialog.Builder builder = new AlertDialog.Builder(View_transactions.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(View_transactions.this);
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
                                    Intent in = new Intent(View_transactions.this, Template_list.class);
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


                Intent in=new Intent(View_transactions.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);




            }else if (id == R.id.nav_created) {
                Intent in=new Intent(View_transactions.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                in.putExtra("position",getIntent().getStringExtra("position"));
                startActivity(in);

            }
            else if (id == R.id.nav_send) {

                Intent in=new Intent(this,compile_transaction.class);
                in.putExtra("fromdate","FROM DATE");
                in.putExtra("todate","TO DATE");
                in.putExtra("fromdate1","FROM DATE");
                in.putExtra("todate1","TO DATE");
                in.putExtra("position",String.valueOf(0));
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                in.putExtra("option","Delivery Report");
                startActivity(in);


            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(View_transactions.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(View_transactions.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(View_transactions.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_view_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void error(){
        Toast.makeText(this,"Incorrect employee number or password",Toast.LENGTH_LONG).show();
    }
    private void error1(){
        Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show();
    }
}
