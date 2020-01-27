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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Emp_template_list extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView temp_list;
    private List<Temp_list> list;
    private ArrayAdapter<Temp_list> arrayAdapter;
    private DatabaseHelper dh;
    private String updated="",updated_by="",created="",created_by="",isactive="Y";
    ImageButton search_btn,back;
    SearchView search;
    LinearLayout for_search,for_back;
    TextView name1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_template_list);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        temp_list=findViewById(R.id.temp_list);

        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_search=(LinearLayout)findViewById(R.id.for_search);
        name1=(TextView)findViewById(R.id.spin_temp);

        search.setVisibility(View.GONE);
        for_back.setVisibility(View.GONE);
        name1.setVisibility(View.VISIBLE);
        for_search.setVisibility(View.VISIBLE);

        String name = getIntent().getStringExtra("emp_name");
        dh=new DatabaseHelper(this);
        viewData();
        try {

            list = dh.getTempList();
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            temp_list.setAdapter(arrayAdapter);
            Temp_list temp = list.get(0);
            if (temp.getTemplate_name().equals("No Template Yet")) {
                temp_list.setEnabled(false);
            } else {
                temp_list.setEnabled(true);
            }
            temp_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Temp_list temp = list.get(position);
                    if(temp.getTemplate_name().equals("No Result Found") || temp.getTemplate_name().equals("No Template Yet")){

                    }else{
                        dh.clear_partial_trans_line();
                        dh.copyTemplatetoTransactionline();
                        //dh.update_trans_temp();
                        //dh.copyprice();
                        String name = getIntent().getStringExtra("emp_name");
                        Intent in = new Intent(Emp_template_list.this, Transaction_line.class);
                        in.putExtra("Template", temp.getTemplate_name());
                        in.putExtra("emp_name", name);
                        in.putExtra("position", String.valueOf(0));
                        in.putExtra("ident",String.valueOf(1));
                        startActivity(in);
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        temp_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Temp_list temp= list.get(i);
                boolean status=false;
                if(temp.getTemplate_name().equals("No Result Found") || temp.getTemplate_name().equals("No Template Yet")){

                    status=false;
                }else{
                    final AlertDialog dialog= new AlertDialog.Builder(Emp_template_list.this)
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("DELETE",null)
                            .show();

                    Button positive=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{

                                dh.deleteTemp(temp.getTemplate_name(),temp.getId());
                                Toast.makeText(Emp_template_list.this,"Successfully deleted",Toast.LENGTH_LONG).show();
                                Intent in=new Intent(Emp_template_list.this,Emp_template_list.class);
                                startActivity(in);
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

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.VISIBLE);
                for_back.setVisibility(View.VISIBLE);
                name1.setVisibility(View.GONE);
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
                list=dh.getTempList1(newText);
                arrayAdapter=new ArrayAdapter<>(Emp_template_list.this,android.R.layout.simple_list_item_1,list);
                temp_list.setAdapter(arrayAdapter);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Emp_template_list.this,Emp_template_list.class);
                startActivity(in);
            }
        });
    }

    private void viewData(){
        try {
            dh=new DatabaseHelper(this);
            list=dh.getTempList();

            arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
            temp_list.setAdapter(arrayAdapter);
            Temp_list temp= list.get(0);
            if(temp.getTemplate_name().equals("No Template Yet")) {
                temp_list.setEnabled(false);
            }else if(temp.getTemplate_name().equals("No Result Found")) {
                temp_list.setEnabled(false);
            }else{
                temp_list.setEnabled(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog= new AlertDialog.Builder(Emp_template_list.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in=new Intent(Emp_template_list.this,Home.class);
                    startActivity(in);



                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }
        });
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Emp_template_list.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Emp_template_list.this);
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
                                        Intent in = new Intent(Emp_template_list.this, List_product.class);
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
            }
            else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Emp_template_list.this);
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




            } else if (id == R.id.nav_branch) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Emp_template_list.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Emp_template_list.this);
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
                                        Intent in = new Intent(Emp_template_list.this, Branch.class);
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

            } else if(id==R.id.nav_change_pass){



            }else if (id == R.id.nav_request) {

                boolean check_branches=dh.check_branches1();
                if(check_branches) {
                    dh=new DatabaseHelper(this);
                    Intent in=new Intent(Emp_template_list.this,Request.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    startActivity(in);
                }else{
                    Toast.makeText(Emp_template_list.this,"Set branch first",Toast.LENGTH_LONG).show();
                }






            }else if (id == R.id.nav_employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Emp_template_list.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Emp_template_list.this);
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
                                    Intent in = new Intent(Emp_template_list.this, Add_employee.class);
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
            } else if (id == R.id.nav_template) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Emp_template_list.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Emp_template_list.this);
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
                                    Intent in = new Intent(Emp_template_list.this, Template_list.class);
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


                                    Intent in=new Intent(Emp_template_list.this,Delivery_report.class);
                                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                                    startActivity(in);




            }else if (id == R.id.nav_send) {

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
                AlertDialog dialog= new AlertDialog.Builder(Emp_template_list.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Emp_template_list.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Emp_template_list.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_emptemplate_layout);
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
