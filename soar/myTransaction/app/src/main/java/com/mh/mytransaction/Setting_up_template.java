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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.Calendar;
import java.util.List;

public class Setting_up_template extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    String created="",created_by="",updated="",updated_by="",isactive="Y";
    private DatePickerDialog.OnDateSetListener mydate, mydate1;
    TextView type_of_template;
    ListView my_product;
    DatabaseHelper dh;
    List<Branch_list> list1,list2;
    private static final String TAG="Compile";
    String sku,dec_places,dec_places2;
    ImageButton search_btn,back,back1;
    SearchView search;
    LinearLayout for_done,for_back,for_back1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_template);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        type_of_template=(TextView)findViewById(R.id.type_of_template);
        my_product=(ListView)findViewById(R.id.my_products);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        back1=(ImageButton)findViewById(R.id.back1);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_done=(LinearLayout)findViewById(R.id.for_done);
        for_back1=(LinearLayout)findViewById(R.id.for_back1);

        dh=new DatabaseHelper(this);
        String sku = dh.getTemp_sku(type_of_template.getText().toString());
        int temp_id=Integer.parseInt(getIntent().getStringExtra("temp_id"));





    }

    private void viewData(String sku,int temp_id){
        dh=new DatabaseHelper(this);

        try {
            list1 = dh.getBranchlist();
            My_branch_adapter adapter = new My_branch_adapter(Setting_up_template.this, R.layout.branch_listview, list1);
            my_product.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1000 && resultCode == RESULT_OK) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                // Do anything with file
                //textView.setText(filePath);
                //import_branch(filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog= new AlertDialog.Builder(Setting_up_template.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in=new Intent(Setting_up_template.this,Edit_template.class);
                    startActivity(in);



                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
                Intent in = new Intent(this, List_product.class);
                startActivity(in);
            } else if (id == R.id.nav_branch) {


            }else if(id==R.id.nav_change_pass){
                boolean check_level=dh.check_admin();
                if(check_level){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                    View mview1 = getLayoutInflater().inflate(R.layout.admin_pass_change, null);
                    Button admin1=(Button)mview1.findViewById(R.id.admin1);
                    Button admin2=(Button)mview1.findViewById(R.id.admin2);

                    dh = new DatabaseHelper(Setting_up_template.this);


                    admin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.change_admin_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText current_pass=(EditText)mview2.findViewById(R.id.current_pass);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Super Admin");

                            dh = new DatabaseHelper(Setting_up_template.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(current_pass.getText().toString())||TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Setting_up_template.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                boolean check_admin = dh.check_admin_via_pass(current_pass.getText().toString());
                                                if (check_admin) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 1);
                                                        Toast.makeText(Setting_up_template.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Setting_up_template.this,Branch.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Setting_up_template.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                    current_pass.setText("");
                                                    c_pass.setText("");
                                                    new_pass.setText("");
                                                }
                                            }else {
                                                Toast.makeText(Setting_up_template.this, "Password didn't match", Toast.LENGTH_LONG).show();
                                                c_pass.setText("");
                                                new_pass.setText("");
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            });
                            builder.setView(mview2);
                            AlertDialog dialog2 = builder.create();
                            dialog2.show();
                        }
                    });
                    admin2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.admin_change_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Admin");

                            dh = new DatabaseHelper(Setting_up_template.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Setting_up_template.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                try {
                                                    dh.update_admin_pass(new_pass.getText().toString(), 2);
                                                    Toast.makeText(Setting_up_template.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                    Intent in=new Intent(Setting_up_template.this,Setting_up_template.class);
                                                    startActivity(in);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(Setting_up_template.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                c_pass.setText("");
                                                new_pass.setText("");
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            });
                            builder.setView(mview2);
                            AlertDialog dialog2 = builder.create();
                            dialog2.show();
                        }
                    });
                    builder.setView(mview1);
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                }else{
                    Toast.makeText(Setting_up_template.this,"You're not allowed to change admin password",Toast.LENGTH_LONG).show();
                }

            }else if (id == R.id.nav_purge) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview1 = getLayoutInflater().inflate(R.layout.variance_report_date_dialog, null);
                ImageButton next=(ImageButton)mview1.findViewById(R.id.next);
                final Button fdate=(Button)mview1.findViewById(R.id.f_date);
                final Button tdate=(Button)mview1.findViewById(R.id.t_date);

                final String[] from_date1 = {""};
                final String[] to_date1 = {""};


                fdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog2 = new DatePickerDialog(Setting_up_template.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate, year, month, day);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.show();
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
                            fdate.setText(from_date);
                            from_date1[0] = year+"-0" + month1 + "-0" + dayOfMonth+" 00:00:00";
                        } else if (month1 < 10 && dayOfMonth >= 10) {
                            String from_date = "0" + month1 + "/" + dayOfMonth+"/"+year;
                            fdate.setText(from_date);
                            from_date1[0] = year+"-0" + month1 + "-" + dayOfMonth+" 00:00:00";
                        } else if (month1 >= 10 && dayOfMonth < 10) {
                            String from_date = month1 + "/0" + dayOfMonth+"/"+year;
                            fdate.setText(from_date);
                            from_date1[0] =  year+"-"+month1 + "-0" + dayOfMonth+" 00:00:00";
                        } else if (month1 >= 10 && dayOfMonth >= 10) {
                            String from_date = month1 + "/" + dayOfMonth+"/"+year;
                            fdate.setText(from_date);
                            from_date1[0] = year+"-"+month1 + "-" + dayOfMonth+" 00:00:00";
                        }
                    }
                };
                tdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog3 = new DatePickerDialog(Setting_up_template.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate1, year, month, day);
                        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog3.show();
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
                            tdate.setText(to_date);
                            to_date1[0] = year+"-0" + month1 + "-0" + dayOfMonth+" 23:59:59";
                        } else if (month1 < 10 && dayOfMonth >= 10) {
                            String to_date = "0" + month1 + "/" + dayOfMonth+"/"+year;
                            tdate.setText(to_date);
                            to_date1[0] = year+"-0" + month1 + "-" + dayOfMonth+" 23:59:59";
                        } else if (month1 >= 10 && dayOfMonth < 10) {
                            String to_date = month1 + "/0" + dayOfMonth+"/"+year;
                            tdate.setText(to_date);
                            to_date1[0] = year+"-"+month1 + "-0" + dayOfMonth+" 23:59:59";
                        } else if (month1 >= 10 && dayOfMonth >= 10) {
                            String to_date = month1 + "/" + dayOfMonth+"/"+year;
                            tdate.setText(to_date);
                            to_date1[0] = year+"-"+month1 + "-" + dayOfMonth+" 23:59:59";
                        }

                    }

                };
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (!fdate.getText().toString().equals("FROM DATE") && !tdate.getText().toString().equals("TO DATE")) {
                                if (fdate.getText().toString().compareTo(tdate.getText().toString()) <= 0) {

                                    AlertDialog dialog= new AlertDialog.Builder(Setting_up_template.this)
                                            .setMessage("Are you sure you want to Proceed?, this will delete some data from Transactions")
                                            .setPositiveButton("Yes",null)
                                            .setNegativeButton("Cancel",null)
                                            .show();
                                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {



                                            dh = new DatabaseHelper(Setting_up_template.this);
                                            int tl = dh.getTrans_line_count(from_date1[0], to_date1[0]);
                                            int t = dh.getTrans_count(from_date1[0], to_date1[0]);
                                            int l = dh.getLog_count(from_date1[0], to_date1[0]);

                                            dh.deleteTransaction_line(from_date1[0], to_date1[0]);
                                            dh.deleteTransaction(from_date1[0], to_date1[0]);
                                            dh.deleteLog(from_date1[0], to_date1[0]);

                                            Toast.makeText(Setting_up_template.this, "Successfully deleted, Transaction " + t +" line, Transaction line " + tl + " line , Log " + l + " line", Toast.LENGTH_LONG).show();

                                            Intent in = new Intent(Setting_up_template.this, Setting_up_template.class);
                                            startActivity(in);


                                        }
                                    });
                                } else {
                                    Toast.makeText(Setting_up_template.this, "Wrong date input", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Setting_up_template.this, "Wrong date input", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });

                builder.setView(mview1);
                AlertDialog dialog1 = builder.create();
                dialog1.show();

            }  else if (id == R.id.nav_employee) {
                Intent in = new Intent(this, Add_employee.class);
                startActivity(in);

            } else if (id == R.id.nav_transaction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Setting_up_template.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(code.getText().toString())) {
                                error();
                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                error();
                            } else {
                                boolean check_emp_log = dh.check_emp_log(Integer.parseInt(code.getText().toString()),password.getText().toString());
                                if (check_emp_log) {
                                    String name=dh.getEmp_name(code.getText().toString());
                                    dh.updateadmin();
                                    Intent in = new Intent(Setting_up_template.this, Emp_template_list.class);
                                    in.putExtra("emp_name",name);
                                    startActivity(in);
                                } else {
                                    error();
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

            } else if (id == R.id.nav_template) {
                Intent in = new Intent(this, Template_list.class);
                startActivity(in);

            }
            else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
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




            }else if (id == R.id.nav_deliver) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Setting_up_template.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(code.getText().toString())) {
                                error();
                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                error();
                            } else {
                                boolean check_emp_log = dh.check_emp_log(Integer.parseInt(code.getText().toString()),password.getText().toString());
                                if (check_emp_log) {
                                    String name=dh.getEmp_name(code.getText().toString());

                                    Intent in=new Intent(Setting_up_template.this,Delivery_report.class);
                                    in.putExtra("emp_name",name);
                                    startActivity(in);
                                } else {
                                    error();
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




            }/*else if (id == R.id.nav_bom) {
                Intent in = new Intent(this, Variance.class);
                startActivity(in);

            }*/else if (id == R.id.nav_request) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Setting_up_template.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(code.getText().toString())) {
                                error();
                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                error();
                            } else {
                                boolean check_emp_log = dh.check_emp_log(Integer.parseInt(code.getText().toString()),password.getText().toString());
                                if (check_emp_log) {
                                    boolean check_branches=dh.check_branches1();
                                    if(check_branches) {
                                        dh.updateadmin();
                                        String name = dh.getEmp_name(code.getText().toString());
                                        Intent in = new Intent(Setting_up_template.this, Request.class);
                                        in.putExtra("emp_name", name);
                                        startActivity(in);
                                    }else{
                                        Toast.makeText(Setting_up_template.this,"Set branch first",Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    error();
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




            }else if (id == R.id.nav_created) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Setting_up_template.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(code.getText().toString())) {
                                error();
                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                error();
                            } else {
                                boolean check_emp_log = dh.check_emp_log(Integer.parseInt(code.getText().toString()),password.getText().toString());
                                if (check_emp_log) {
                                    String name=dh.getEmp_name(code.getText().toString());

                                    Intent in=new Intent(Setting_up_template.this,Created_transactions.class);
                                    in.putExtra("emp_name",name);
                                    startActivity(in);
                                } else {
                                    error();
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




            } else if (id == R.id.nav_send) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_up_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Setting_up_template.this);
                builder.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(code.getText().toString())) {
                                error();
                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                error();
                            } else {
                                boolean check_emp_log = dh.check_emp_log(Integer.parseInt(code.getText().toString()),password.getText().toString());
                                if (check_emp_log) {
                                    dh.updateadmin();
                                    String name=dh.getEmp_name(code.getText().toString());
                                    Intent in=new Intent(Setting_up_template.this,compile_transaction.class);
                                    in.putExtra("fromdate","FROM DATE");
                                    in.putExtra("todate","TO DATE");
                                    in.putExtra("fromdate1","FROM DATE");
                                    in.putExtra("todate1","TO DATE");
                                    in.putExtra("position",String.valueOf(0));
                                    in.putExtra("position",String.valueOf(0));
                                    in.putExtra("emp_name",name);
                                    in.putExtra("option","Delivery Report");
                                    startActivity(in);
                                } else {
                                    error();
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

            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(Setting_up_template.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Setting_up_template.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Setting_up_template.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_branch_layout);
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
