package com.mh.mytransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Delivery_report extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ListView delivery_list;
    private List<Delivery_module> list;
    private ArrayAdapter<Delivery_module> arrayAdapter;
    private DatabaseHelper dh;
    private String updated="",updated_by="",created="",created_by="",isactive="Y",iscomputed;
    private ImageButton import_delivery;
    ImageButton search_btn,back;
    SearchView search;
    LinearLayout for_import,for_back;
    TextView name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_report);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        delivery_list=findViewById(R.id.delivery_list);
        import_delivery=(ImageButton)findViewById(R.id.delivery_import);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        name=(TextView)findViewById(R.id.spin_temp);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_import=(LinearLayout)findViewById(R.id.for_import);
        try{
        search.setVisibility(View.GONE);
        for_back.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        for_import.setVisibility(View.VISIBLE);
        dh=new DatabaseHelper(this);
            viewData();
        import_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
                View mview = getLayoutInflater().inflate(R.layout.warning_to_delete_dialog, null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dh.delete_delivery_reports();
                        new MaterialFilePicker()
                                .withActivity(Delivery_report.this)
                                .withRequestCode(1000)
                                .withHiddenFiles(true)
                                .start();
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

        }catch (Exception e){
            e.printStackTrace();
        }

        delivery_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Delivery_module delivery_module = list.get(position);
                if(delivery_module.getName().equals("No Result Found")){

                }else{
                    String name=delivery_module.getName();
                    String branch_name=delivery_module.getBranch_name();
                    String date_req=delivery_module.getDate_req();
                    String name1=name.substring(0,3)+"_"+branch_name.replace(" ","_")+"_"+date_req.replace("-","")+"_"+delivery_module.getDoc_num();
                    Intent in=new Intent(Delivery_report.this,Transaction_line.class);
                    in.putExtra("ident",String.valueOf(0));
                    in.putExtra("doc_num",delivery_module.getDoc_num());
                    in.putExtra("position",String.valueOf(0));
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    in.putExtra("Template",name1);
                    in.putExtra("branch",delivery_module.getBranch_name());
                    in.putExtra("date_req",delivery_module.getDate_req());
                    startActivity(in);
                }

            }
        });


        delivery_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Delivery_module delivery_module= list.get(i);
                boolean status=false;
                if(delivery_module.getName().equals("No Result Found")){

                    status=false;
                }else{
                    final AlertDialog dialog= new AlertDialog.Builder(Delivery_report.this)
                            .setNeutralButton("Delete",null)
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("Print",null)
                            .show();
                    Button neutralButton=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                dh.delete_delivery_report(delivery_module.getDelivery_template_id());
                                Toast.makeText(Delivery_report.this,"Deleted successfully",Toast.LENGTH_LONG).show();
                                Intent in=new Intent(Delivery_report.this,Delivery_report.class);
                                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                                startActivity(in);
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    });
                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent in=new Intent(Delivery_report.this,Print.class);
                                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                                in.putExtra("to_print",String.valueOf(1));
                                in.putExtra("ident",String.valueOf(1));
                                in.putExtra("doc_num",delivery_module.getDoc_num());
                                startActivity(in);
                            }catch (Exception e){
                                e.printStackTrace();
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
                name.setVisibility(View.GONE);
                for_import.setVisibility(View.GONE);
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                list = dh.getDelivery_list1(newText) ;
                arrayAdapter = new ArrayAdapter<>(Delivery_report.this, android.R.layout.simple_list_item_1, list);
                delivery_list.setAdapter(arrayAdapter);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Delivery_report.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);
            }
        });


    }

    private void viewData(){
        dh=new DatabaseHelper(this);

        try {
            list = dh.getDelivery_list() ;
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            delivery_list.setAdapter(arrayAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void error1(){
        Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            int id = menuItem.getItemId();

            if (id == R.id.nav_product) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);
                String name = getIntent().getStringExtra("emp_name");
                dh = new DatabaseHelper(Delivery_report.this);
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
                                        Intent in = new Intent(Delivery_report.this, Branch.class);
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
            } else if (id == R.id.nav_request) {

                boolean check_branches=dh.check_branches1();
                if(check_branches) {
                    dh=new DatabaseHelper(this);
                    Intent in=new Intent(Delivery_report.this,Request.class);
                    in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                    startActivity(in);
                }else{
                    Toast.makeText(Delivery_report.this,"Set branch first",Toast.LENGTH_LONG).show();
                }




            }else if (id == R.id.nav_branch) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Delivery_report.this);
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
                                        Intent in = new Intent(Delivery_report.this, Branch.class);
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

            }else if (id == R.id.nav_employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Delivery_report.this);
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
                                    Intent in = new Intent(Delivery_report.this, Add_employee.class);
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
                Intent in=new Intent(Delivery_report.this,Emp_template_list.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            } else if (id == R.id.nav_created) {
                Intent in=new Intent(Delivery_report.this,Created_transactions.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);

            } else if (id == R.id.nav_template) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
                View mview = getLayoutInflater().inflate(R.layout.admin_log, null);
                final EditText password=(EditText)mview.findViewById(R.id.ad_password);

                dh = new DatabaseHelper(Delivery_report.this);
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
                                    Intent in = new Intent(Delivery_report.this, Template_list.class);
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

            } else if (id == R.id.nav_send) {
                Intent in=new Intent(Delivery_report.this,compile_transaction.class);
                in.putExtra("fromdate","FROM DATE");
                in.putExtra("todate","TO DATE");
                in.putExtra("fromdate1","FROM DATE");
                in.putExtra("todate1","TO DATE");
                in.putExtra("position",String.valueOf(0));
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                in.putExtra("option","Delivery Report");
                startActivity(in);
            }else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_report.this);
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
                Intent in=new Intent(Delivery_report.this,Delivery_report.class);
                startActivity(in);
            }else if(id == R.id.nav_logout){
                AlertDialog dialog= new AlertDialog.Builder(Delivery_report.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Delivery_report.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Delivery_report.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_delivery_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1000 && resultCode == RESULT_OK) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                // Do anything with file
                //textView.setText(filePath);




                import_file(filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void import_file(String filepath){
        try{

            dh=new DatabaseHelper(this);
            File file=new File(filepath);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            created = format.format(Calendar.getInstance().getTime());






            try{
                BufferedReader br=new BufferedReader(new FileReader(file));
                String line;
                int count=0;
                int count1=0;
                int count2=0;
                String content=null;
                while((line=br.readLine())!=null){


                    if(count!=0){
                        String[] row=line.split(",");
                        do{
                            try{
                                content=row[count1];
                            }catch (Exception e){
                                break;
                            }
                            count1++;
                        }while(!content.equals(null));
                        count1--;
                        if(count1==11){
                            int branch_id=dh.getbranch_id(row[1]);
                            int prod_id=dh.get_prod_id(row[5]);
                            int uom_id=dh.getUom_id(row[6]);
                            if(branch_id==0){
                                dh.delete_delivery();
                                Toast.makeText(Delivery_report.this,row[1]+" doesn't exist as Branch",Toast.LENGTH_SHORT).show();
                                count2=0;
                                break;
                            }else{

                                dh.insert_into_delivery_template(branch_id,row[0],row[2],row[3],row[4],prod_id,uom_id,Double.parseDouble(row[7]),Double.parseDouble(row[8]),Double.parseDouble(row[9]),row[10],row[11],created,getIntent().getStringExtra("emp_name"),isactive);
                                count2=1;
                            }

                        }else{

                            break;
                        }


                    }
                    count++;
                    count1=0;

                }
                if(count2==1){
                    int count3=dh.getDeliveryCount();
                    String[] array=dh.getDoc_num(count3);
                    for(int i=0;i<count3;i++){

                        String[] array2=dh.getDelivery_temp(array[i]);
                        int branch_ID=Integer.parseInt(array2[0]);
                        String branch_name=dh.getbranch_name(branch_ID);
                        String name=array2[1].substring(0,3)+"_"+branch_name.replace(" ","_")+"_"+array2[3].replace("-","")+"_"+array2[2];
                        dh.insert_into_delivery_template1(branch_ID,name,array2[2],array2[3],array2[4],array2[5],array2[6],array2[7]);
                        int delivery_id=dh.getDelivery_id(array2[2]);
                        dh.insert_delivery_line(array2[2],delivery_id);

                    }
                    dh.delete_delivery();
                    Toast.makeText(Delivery_report.this,"Success",Toast.LENGTH_SHORT).show();
                }else if(count2==0){

                }
                else {
                    Toast.makeText(Delivery_report.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
                }
                br.close();

                Intent in=new Intent(Delivery_report.this,Delivery_report.class);
                in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
                startActivity(in);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Delivery_report.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
        }
    }

    /*private void insert_temp(){

        int count=0;
        int count1=0;
        int count2=1;
        int branch_id=0;
        int temp_id=0;
        String filename="";
        String subname="";
        String mydate="";
        String btype="";
        String branch;
        String use_leaddays="N";
        int lead=0;
        Boolean check_temp_name=true;
        try {
            dh=new DatabaseHelper(this);
            count=dh.get_num_imp_temp();
            String[] array=dh.get_doc_num(count);



            for (count1 = 0; count1 < count; count1++) {

                String[] info = dh.get_info(array[count1]);

                do {
                    check_temp_name = dh.checkTempName(info[0]);
                    if (check_temp_name) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        created = format.format(Calendar.getInstance().getTime());
                        //dh.insertTemp(info[0], lead, use_leaddays, created, getIntent().getStringExtra("emp_name"), isactive);
                        Toast.makeText(Delivery_report.this, "New Template Created", Toast.LENGTH_LONG).show();
                        Intent in = new Intent(Delivery_report.this, Delivery_report.class);
                        startActivity(in);
                    } else {
                        info[0] = info[0] + "-" + count2;
                        count2++;
                    }
                    temp_id = dh.getTemp_id(info[0]);
                    branch_id = dh.getbranch_id(info[1]);
                    subname = info[0].substring(0, 3);
                    mydate = info[2].replace("-", "");
                    btype = dh.checkComp_Fra1(info[1]);
                    branch=info[1].replace(" ","_");
                    filename = subname + "_" + branch + "_" + btype + "_" + mydate;

                    dh.insertTransaction(temp_id, branch_id, array[count1], info[2], 0.0, 0.0, "", "", filename, "N", created, getIntent().getStringExtra("emp_name"), isactive);

                    dh.insert_Trans_line(temp_id,array[count1],created, getIntent().getStringExtra("emp_name"), isactive);

                } while (!check_temp_name);


            }




        }catch (Exception e){
            e.printStackTrace();
        }

        dh.delete_for_import_temp();
    }*/

    /*public void insert_to_transact_temp(String id,int prod,int uom,String price,String qty,String subtotal,String isnegative){
        int prod_id;
        int uom_id;
        int id1=Integer.parseInt(id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        created = format.format(Calendar.getInstance().getTime());
        dh=new DatabaseHelper(this);
        try {


            dh.insert_transaction_temp1(id1, prod, uom, Double.parseDouble(price), Integer.parseInt(qty), isnegative, created, getIntent().getStringExtra("emp_name"), isactive);

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

}
