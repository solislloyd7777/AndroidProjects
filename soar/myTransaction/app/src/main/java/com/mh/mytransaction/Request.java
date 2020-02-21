package com.mh.mytransaction;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Request extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView doc_num,branch;
    Spinner request_type;
    EditText summary;
    DatabaseHelper dh;
    String created,created_by,isactive="Y";
    ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        doc_num=(TextView)findViewById(R.id.doc_num);
        branch=(TextView)findViewById(R.id.branch);
        request_type=(Spinner)findViewById(R.id.request_type);
        summary=(EditText)findViewById(R.id.summary);
        send=(ImageButton)findViewById(R.id.send);

        dh=new DatabaseHelper(this);
        try {

            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

            created = format.format(Calendar.getInstance().getTime());
            final int branch_id = dh.getBranch_id();
            int count = dh.getReq_count(branch_id);
            int id_count = 100000 + count;

            //String branch_name = dh.getBranch_name1(branch_id).replace(" ", "_");
            String branch_name = dh.getBranch_name1(branch_id);
            String branch_name1=branch_name.substring(0,3);

                final String document_num = id_count + "_" + branch_name1 + "_" + created.replace("-", "").replace(" ", "").replace(":", "") +"_REQ";

                doc_num.setText(document_num);
                branch.setText(branch_name);


                String[] list2 = request_type();
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Request.this, R.layout.spinner_layout, R.id.txt, list2);
                request_type.setAdapter(adapter2);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(summary.getText().toString())) {
                            summary.setError("This field is required");
                        } else {
                            try {
                                dh.insert_request(branch_id, doc_num.getText().toString(), request_type.getSelectedItem().toString(), summary.getText().toString().replace(",", "."), created, getIntent().getStringExtra("emp_name"), isactive);

                                compile(doc_num.getText().toString(), request_type.getSelectedItem().toString(), branch.getText().toString(), summary.getText().toString().replace(",", "."));
                                send(doc_num.getText().toString());
                                int count1 = dh.getReq_count(branch_id);
                                int id_count1 = 100000 + count1;

                                String branch_name2 = dh.getBranch_name1(branch_id).replace(" ", "_");
                                //String branch_name2 = dh.getBranch_name1(branch_id);
                                String branch_name3=branch_name2.substring(0,3);
                                final String document_num1 = id_count1 + "_" + branch_name3 + "_" + created.replace("-", "").replace(" ", "").replace(":", "")+"_REQ";

                                doc_num.setText(document_num1);
                                summary.setText("");
                                Toast.makeText(Request.this, "Success", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
        }catch (Exception e){
            e.printStackTrace();
        }




    }



    public String[] request_type(){
        String[] ar=new String[8];
        ar[0]="BRANCH OPENING / OFFSITE REQUESTS";
        ar[1]="CF AND PCF";
        ar[2]="EMERGENCY CASH FUND";
        ar[3]="GIFT CHECK";
        ar[4]="OTHERS";
        ar[5]="PAYMENT REQUEST";
        ar[6]="REPAIRS & MAINTENANCE";
        ar[7]="TRANSPORTATION AND OB ADVANCES";
        return ar;
    }

    private void error1(){
        Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show();
    }

    private boolean compile(String doc_num,String request_type,String branch,String summary){
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
                file = new File(exportDir, doc_num + ".csv");
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }

                if (!file.exists()){
                    file.createNewFile();
                }

                printWriter = new PrintWriter(new FileWriter(file));
                printWriter.println("DOCUMENT NUMBER,BRANCH,REQUEST TYPE,SUMMARY");
                try {
                        printWriter.println(doc_num+","+branch+","+request_type+ "," + summary);

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
        return true;
    }

    private void send(String doc_num){
        try {
                    File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+ "/"+doc_num+".csv");

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.setType("text/csv");

                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mhsoar.inventory@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, doc_num);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                    Uri uri ;
                    if (Build.VERSION.SDK_INT < 24) {
                        uri = Uri.fromFile(file);
                    } else {
                        uri = FileProvider.getUriForFile(Request.this, BuildConfig.APPLICATION_ID + ".provider",file);// My work-around for new SDKs, causes ActivityNotFoundException in API 10.
                    }
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void onBackPressed() {
        AlertDialog dialog= new AlertDialog.Builder(Request.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in=new Intent(Request.this,Home.class);
                    startActivity(in);



                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        try {
            int id = menuItem.getItemId();

            if (id == R.id.nav_product) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Request.this);
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
                                    Intent in = new Intent(Request.this, List_product.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);
                dh = new DatabaseHelper(Request.this);
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
                                    Intent in = new Intent(Request.this, Branch.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);
                dh = new DatabaseHelper(Request.this);
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
                                    Intent in = new Intent(Request.this, Add_employee.class);
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
                Intent in=new Intent(Request.this,Emp_template_list.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            }else if (id == R.id.nav_created) {
                dh=new DatabaseHelper(this);
                Intent in=new Intent(Request.this,Created_transactions.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            }else if (id == R.id.nav_template) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
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
                                    Intent in = new Intent(Request.this, Template_list.class);
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
                Intent in=new Intent(Request.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);




            }else if (id == R.id.nav_send) {
                dh=new DatabaseHelper(this);
                //dh.copyprice();
                Intent in=new Intent(Request.this,compile_transaction.class);
                in.putExtra("fromdate","FROM DATE");
                in.putExtra("todate","TO DATE");
                in.putExtra("fromdate1","FROM DATE");
                in.putExtra("todate1","TO DATE");
                in.putExtra("position",String.valueOf(0));
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                in.putExtra("option","Delivery Report");
                startActivity(in);
            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(Request.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{
                            dh=new DatabaseHelper(Request.this);
                            // dh.copyprice();

                            Intent in=new Intent(Request.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                dh=new DatabaseHelper(this);
                //dh.copyprice();
                AlertDialog dialog= new AlertDialog.Builder(Request.this)
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
        DrawerLayout drawer = findViewById(R.id.drawer_request_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}