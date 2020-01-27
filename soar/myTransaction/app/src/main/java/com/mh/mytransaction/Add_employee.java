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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Add_employee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ListView emp_list;
    List<Emp_list> list;
    ArrayAdapter<Emp_list> arrayAdapter;
    DatabaseHelper dh;
    String updated="",updated_by="",created="",created_by="",isactive="Y";
    ImageButton search_btn,back,add_emp;
    SearchView search;
    LinearLayout for_search,for_back;
    TextView name;
    private DatePickerDialog.OnDateSetListener mydate, mydate1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        emp_list=findViewById(R.id.emp_list);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        add_emp=(ImageButton)findViewById(R.id.add_emp);
        search=(SearchView)findViewById(R.id.search);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_search=(LinearLayout)findViewById(R.id.for_search);
        name=(TextView)findViewById(R.id.spin_temp);


        search.setVisibility(View.GONE);
        for_back.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        for_search.setVisibility(View.VISIBLE);
        dh=new DatabaseHelper(this);
        viewData();
        /*emp_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Emp_list emp= list.get(position);
                Intent in=new Intent(Add_employee.this,Edit_template.class);
                in.putExtra("Employee", emp.getName());
                startActivity(in);
            }
        });*/

        emp_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Emp_list emp= list.get(i);
                boolean status=false;
                if(emp.getName().equals("No Result Found") || emp.getName().equals("No registered employee yet")){
                    status=false;
                }else {
                    final AlertDialog dialog= new AlertDialog.Builder(Add_employee.this)
                            .setPositiveButton("EDIT",null)
                            .setNegativeButton("Cancel",null)
                            .setNeutralButton("DELETE",null)
                            .show();
                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                            View mview = getLayoutInflater().inflate(R.layout.update_emp_dialog, null);
                            final EditText emp_name=(EditText) mview.findViewById(R.id.emp_name);
                            final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                            final EditText email=(EditText)mview.findViewById(R.id.email);
                            final EditText password=(EditText)mview.findViewById(R.id.password);
                            final EditText c_password=(EditText)mview.findViewById(R.id.c_password);
                            try {

                                emp_name.setText(emp.getName());
                                code.setText(String.valueOf(emp.getCode()));
                                email.setText(emp.getEmail());
                                password.setText(emp.getPass());
                                c_password.setText(emp.getPass());
                                updated_by=dh.getAdmin();

                                dh = new DatabaseHelper(Add_employee.this);
                                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            if (TextUtils.isEmpty(emp_name.getText().toString())) {
                                                error1();
                                            } else if (TextUtils.isEmpty(code.getText().toString())) {
                                                error1();
                                            } else if (TextUtils.isEmpty(email.getText().toString())) {
                                                error1();
                                            } else if (TextUtils.isEmpty(password.getText().toString())) {
                                                error1();
                                            } else if (TextUtils.isEmpty(c_password.getText().toString())) {
                                                error1();
                                            } else {
                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                updated = format.format(Calendar.getInstance().getTime());
                                                if (password.getText().toString().equals(c_password.getText().toString())) {
                                                    dh.updateEmployee(emp_name.getText().toString(), Integer.parseInt(code.getText().toString()), email.getText().toString(), password.getText().toString(), updated, updated_by, isactive, emp.getEmp_id());
                                                    success1();
                                                    /*Intent in = new Intent(Add_employee.this, Add_employee.class);
                                                    startActivity(in);*/
                                                    viewData();
                                                } else {
                                                    error2();
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
                                        dialog.cancel();
                                    }
                                });

                                builder.setView(mview);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    });
                    Button neutralButton=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{

                                dh.deleteEmp(emp.getEmp_id());
                                Toast.makeText(Add_employee.this,"Successfully deleted",Toast.LENGTH_LONG).show();
                                /*Intent in=new Intent(Add_employee.this,Add_employee.class);
                                startActivity(in);*/
                                viewData();
                                dialog.cancel();

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    status=true;
                }
                return status;


            }
        });

        add_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.add_emp_dialog, null);
                final EditText emp_name=(EditText) mview.findViewById(R.id.emp_name);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText email=(EditText)mview.findViewById(R.id.email);
                final EditText password=(EditText)mview.findViewById(R.id.password);
                final EditText c_password=(EditText)mview.findViewById(R.id.c_password);

                dh = new DatabaseHelper(Add_employee.this);
                builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (TextUtils.isEmpty(emp_name.getText().toString())) {
                                error1();
                            } else if (TextUtils.isEmpty(code.getText().toString())) {
                                error1();
                            }
                            else if (TextUtils.isEmpty(email.getText().toString())) {
                                error1();
                            }
                            else if (TextUtils.isEmpty(password.getText().toString())) {
                                error1();
                            }
                            else if (TextUtils.isEmpty(c_password.getText().toString())) {
                                error1();
                            }else {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                created = format.format(Calendar.getInstance().getTime());
                                created_by=dh.getAdmin();
                                if(password.getText().toString().equals(c_password.getText().toString())){
                                    boolean check_code=dh.check_code(Integer.parseInt(code.getText().toString()));
                                    if(check_code){
                                        dh.insertEmployee(emp_name.getText().toString(),Integer.parseInt(code.getText().toString()),email.getText().toString(),password.getText().toString(),created,created_by,isactive);
                                        success();
                                        /*Intent in=new Intent(Add_employee.this,Add_employee.class);
                                        startActivity(in);*/
                                        viewData();
                                        dialog.cancel();
                                    }else{
                                        error3();
                                    }
                                }else{
                                    error2();
                                }

                            }
                        }catch (Exception e){
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
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.VISIBLE);
                for_back.setVisibility(View.VISIBLE);
                name.setVisibility(View.GONE);
                for_search.setVisibility(View.GONE);
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                list = dh.getEmplist1(newText);
                arrayAdapter = new ArrayAdapter<>(Add_employee.this, android.R.layout.simple_list_item_1, list);
                emp_list.setAdapter(arrayAdapter);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Add_employee.this,Add_employee.class);
                startActivity(in);
            }
        });


    }

    private void viewData(){
        try {
            dh = new DatabaseHelper(this);
            list = dh.getEmplist();
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            emp_list.setAdapter(arrayAdapter);
            Emp_list emp = list.get(0);
            if (emp.getName().equals("No registered employee yet")) {
                emp_list.setEnabled(false);
            } else {
                emp_list.setEnabled(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog= new AlertDialog.Builder(Add_employee.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in=new Intent(Add_employee.this,Home.class);
                    startActivity(in);



                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }
        });
    }


    private void error1(){
        Toast.makeText(this,"Your registration was not saved, please fillout all the information",Toast.LENGTH_LONG).show();
    }

    private void error2(){
        Toast.makeText(this,"Password don't match,please try again",Toast.LENGTH_LONG).show();
    }

    private void error3(){
        Toast.makeText(this,"Employee Number Already Exist, please try again",Toast.LENGTH_LONG).show();
    }
    private void success(){
        Toast.makeText(this,"Registered successfully",Toast.LENGTH_LONG).show();
    }

    private void success1(){
        Toast.makeText(this,"Updated successfully",Toast.LENGTH_LONG).show();
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

            dh=new DatabaseHelper(this);
            if (id == R.id.nav_product) {

                    Intent in = new Intent(this, List_product.class);
                    startActivity(in);

            } else if (id == R.id.nav_branch) {
                    Intent in = new Intent(this, Branch.class);
                    startActivity(in);

            }/*else if (id == R.id.nav_bom) {
                Intent in = new Intent(this, Variance.class);
                startActivity(in);

            }*/else if(id==R.id.nav_change_pass){
                boolean check_level=dh.check_admin();
                if(check_level){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                    View mview1 = getLayoutInflater().inflate(R.layout.admin_pass_change, null);
                    Button admin1=(Button)mview1.findViewById(R.id.admin1);
                    Button admin2=(Button)mview1.findViewById(R.id.admin2);

                    dh = new DatabaseHelper(Add_employee.this);


                    admin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.change_admin_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText current_pass=(EditText)mview2.findViewById(R.id.current_pass);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Super Admin");

                            dh = new DatabaseHelper(Add_employee.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(current_pass.getText().toString())||TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Add_employee.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                boolean check_admin = dh.check_admin_via_pass(current_pass.getText().toString());
                                                if (check_admin) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 1);
                                                        Toast.makeText(Add_employee.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Add_employee.this,Add_employee.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Add_employee.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                    current_pass.setText("");
                                                    c_pass.setText("");
                                                    new_pass.setText("");
                                                }
                                            }else {
                                                Toast.makeText(Add_employee.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.admin_change_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Admin");

                            dh = new DatabaseHelper(Add_employee.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Add_employee.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 2);
                                                        Toast.makeText(Add_employee.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Add_employee.this,Add_employee.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                            } else {
                                                Toast.makeText(Add_employee.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Add_employee.this,"You're not allowed to change admin password",Toast.LENGTH_LONG).show();
                }

            }  else if (id == R.id.nav_employee) {


            }else if (id == R.id.nav_purge) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
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

                                            DatePickerDialog dialog2 = new DatePickerDialog(Add_employee.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate, year, month, day);
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

                                            DatePickerDialog dialog3 = new DatePickerDialog(Add_employee.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate1, year, month, day);
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

                                                        AlertDialog dialog= new AlertDialog.Builder(Add_employee.this)
                                                                .setMessage("Are you sure you want to Proceed?, this will delete some data from Transactions")
                                                                .setPositiveButton("Yes",null)
                                                                .setNegativeButton("Cancel",null)
                                                                .show();
                                                        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                                        positiveButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {



                                                                            dh = new DatabaseHelper(Add_employee.this);
                                                                            int tl = dh.getTrans_line_count(from_date1[0], to_date1[0]);
                                                                            int t = dh.getTrans_count(from_date1[0], to_date1[0]);
                                                                            int l = dh.getLog_count(from_date1[0], to_date1[0]);

                                                                            dh.deleteTransaction_line(from_date1[0], to_date1[0]);
                                                                            dh.deleteTransaction(from_date1[0], to_date1[0]);
                                                                            dh.deleteLog(from_date1[0], to_date1[0]);

                                                                            Toast.makeText(Add_employee.this, "Successfully deleted, Transaction " + t +" line, Transaction line " + tl + " line , Log " + l + " line", Toast.LENGTH_LONG).show();

                                                                            Intent in = new Intent(Add_employee.this, Add_employee.class);
                                                                            startActivity(in);


                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(Add_employee.this, "Wrong date input", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toast.makeText(Add_employee.this, "Wrong date input", Toast.LENGTH_LONG).show();
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                        }
                                    });

                                    builder.setView(mview1);
                                    AlertDialog dialog1 = builder.create();
                                    dialog1.show();

            }
            else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
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




            } else if (id == R.id.nav_transaction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Add_employee.this);
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
                                    Intent in = new Intent(Add_employee.this, Emp_template_list.class);
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

            }else if (id == R.id.nav_deliver) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Add_employee.this);
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

                                    Intent in=new Intent(Add_employee.this,Delivery_report.class);
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




            } else if (id == R.id.nav_request) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Add_employee.this);
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
                                        Intent in = new Intent(Add_employee.this, Request.class);
                                        in.putExtra("emp_name", name);
                                        startActivity(in);
                                    }else{
                                        Toast.makeText(Add_employee.this,"Set branch first",Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Add_employee.this);
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

                                    Intent in=new Intent(Add_employee.this,Created_transactions.class);
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




            }else if (id == R.id.nav_send) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_employee.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Add_employee.this);
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
                                    Intent in=new Intent(Add_employee.this,compile_transaction.class);
                                    in.putExtra("fromdate","FROM DATE");
                                    in.putExtra("todate","TO DATE");
                                    in.putExtra("fromdate1","FROM DATE");
                                    in.putExtra("todate1","TO DATE");
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
                AlertDialog dialog= new AlertDialog.Builder(Add_employee.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Add_employee.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Add_employee.this)
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
            }else if(id == R.id.nav_change_pass){

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_employee_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void error(){
        Toast.makeText(this,"Incorrect employee number or password",Toast.LENGTH_LONG).show();
    }
}
