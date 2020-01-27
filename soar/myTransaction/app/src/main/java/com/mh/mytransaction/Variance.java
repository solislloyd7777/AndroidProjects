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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Variance extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ListView prod_list;
    ImageButton imp_variance,search_but,back;
    DatabaseHelper dh;
    List<Variance_list> list;
    List<Details_list> list1;
    LinearLayout for_import,for_back;
    TextView name;
    SearchView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variance);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        prod_list=(ListView)findViewById(R.id.listView);
        imp_variance=(ImageButton)findViewById(R.id.import_prod);
        search_but=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_import=(LinearLayout)findViewById(R.id.for_import);
        name=(TextView)findViewById(R.id.spin_temp);

try {
    search.setVisibility(View.GONE);
    for_back.setVisibility(View.GONE);
    name.setVisibility(View.VISIBLE);
    for_import.setVisibility(View.VISIBLE);
    search_but.setVisibility(View.VISIBLE);
}catch (Exception e){
    e.printStackTrace();
}


        dh=new DatabaseHelper(this);

        viewData();

        prod_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Variance_list mylist=list.get(i);
                if (mylist.getProduct().equals("No Result Found")){

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                    View mview = getLayoutInflater().inflate(R.layout.bom_details, null);
                    final TextView prod_name = (TextView) mview.findViewById(R.id.prod_name);
                    final ListView details=(ListView)mview.findViewById(R.id.details);

                    list1=dh.getDetails_list(mylist.getId());
                    Details_adapter adapter=new Details_adapter(Variance.this,R.layout.details,list1);
                    details.setAdapter(adapter);








                    prod_name.setText(mylist.getProduct());

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.setView(mview);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        });

        imp_variance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialog= new AlertDialog.Builder(Variance.this)
                        .setMessage("The current BOM will be automatically deleted. Do you want to proceed?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            new MaterialFilePicker()
                                    .withActivity(Variance.this)
                                    .withRequestCode(1000)
                                    .withHiddenFiles(true)
                                    .start();

                            dialog.cancel();



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });





            }
        });

        search_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.VISIBLE);
                for_back.setVisibility(View.VISIBLE);
                name.setVisibility(View.GONE);
                for_import.setVisibility(View.GONE);
                search_but.setVisibility(View.GONE);
            }
        });



        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                list=dh.getVariance_list1(newText);
                Variance_adapter adapter=new Variance_adapter(Variance.this,R.layout.prod_list,list);
                prod_list.setAdapter(adapter);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Variance.this,Variance.class);
                startActivity(in);
            }
        });


    }

    private void viewData(){
        dh=new DatabaseHelper(this);

        list=dh.getVariance_list();
        Variance_adapter adapter=new Variance_adapter(this,R.layout.prod_list,list);
        prod_list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if (requestCode == 1000 && resultCode == RESULT_OK) {

                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                import_variance(filePath);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void import_variance(String filepath){
        try{



            dh=new DatabaseHelper(this);

            dh.delete_bom();
            File file=new File(filepath);
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String created=format.format(Calendar.getInstance().getTime());
            String created_by=dh.getAdmin();
            char isactive='Y';



            try{

                BufferedReader br=new BufferedReader(new FileReader(file));
                String line;
                int count=0;
                int count1=0;
                String content = null;
                int count3=0;
                while((line=br.readLine())!=null){


                    if(count!=0){
                        String row[]=line.split(",");
                        do{
                            try{
                                content=row[count1];

                            }catch (Exception e){
                                break;
                            }
                            count1++;
                        }while(!content.equals(null));
                        count1--;
                        if(count1==2){
                            int prod_id=dh.get_prod_id(row[0]);
                            int bom_prod_id=dh.get_prod_id(row[1]);
                            int prod_uom=dh.getC_uom_id(prod_id);
                            int bom_uom=dh.getC_uom_id(bom_prod_id);
                            double price=Double.parseDouble(row[2]);
                            dh.insert_partial_bom(prod_id,prod_uom,bom_prod_id,bom_uom,price);
                            count3=1;
                        }

                    }
                    count++;
                    count1=0;

                }
                if(count3==1){
                    dh.insert_bom(created,created_by,isactive);
                    Toast.makeText(Variance.this,"Success",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Variance.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
                    dh.delete_partial_bom();

                }


                br.close();



                viewData();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Variance.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
        }


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
                Intent in = new Intent(this, Branch.class);
                startActivity(in);

            }
            else if(id==R.id.nav_change_pass){
                boolean check_level=dh.check_admin();
                if(check_level){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                    View mview1 = getLayoutInflater().inflate(R.layout.admin_pass_change, null);
                    Button admin1=(Button)mview1.findViewById(R.id.admin1);
                    Button admin2=(Button)mview1.findViewById(R.id.admin2);

                    dh = new DatabaseHelper(Variance.this);


                    admin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.change_admin_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText current_pass=(EditText)mview2.findViewById(R.id.current_pass);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Super Admin");

                            dh = new DatabaseHelper(Variance.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(current_pass.getText().toString())||TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Variance.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                boolean check_admin = dh.check_admin_via_pass(current_pass.getText().toString());
                                                if (check_admin) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 1);
                                                        Toast.makeText(Variance.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Variance.this,Variance.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Variance.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                    current_pass.setText("");
                                                    c_pass.setText("");
                                                    new_pass.setText("");
                                                }
                                            }else {
                                                Toast.makeText(Variance.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.admin_change_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Admin");

                            dh = new DatabaseHelper(Variance.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Variance.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                try {
                                                    dh.update_admin_pass(new_pass.getText().toString(), 2);
                                                    Toast.makeText(Variance.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                    Intent in=new Intent(Variance.this,Variance.class);
                                                    startActivity(in);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(Variance.this, "Wrong password", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Variance.this,"You're not allowed to change admin password",Toast.LENGTH_LONG).show();
                }

            }  else if (id == R.id.nav_employee) {
                Intent in = new Intent(this, Add_employee.class);
                startActivity(in);

            } else if (id == R.id.nav_transaction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Variance.this);
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
                                    Intent in = new Intent(Variance.this, Emp_template_list.class);
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


                AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Variance.this);
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

                                    Intent in=new Intent(Variance.this,Delivery_report.class);
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




            }else if (id == R.id.nav_created) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Variance.this);
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

                                    Intent in=new Intent(Variance.this,Created_transactions.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Variance.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Variance.this);
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
                                    Intent in=new Intent(Variance.this,compile_transaction.class);
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
                AlertDialog dialog= new AlertDialog.Builder(Variance.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Variance.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Variance.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_variance_layout);
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
