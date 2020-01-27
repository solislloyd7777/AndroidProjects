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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Branch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String created="",created_by="",updated="",updated_by="",isactive="Y";
    private DatePickerDialog.OnDateSetListener mydate, mydate1;
    TextView name,setted_branch;
    ListView myListView;
    DatabaseHelper dh;
    List<Branch_list> list1,list2;
    private static final String TAG="Compile";
    String dec_places,dec_places2;
    ImageButton import_branch,add_branch,search_btn,back;
    SearchView search;
    LinearLayout for_import,for_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        myListView=(ListView)findViewById(R.id.my_listView);
        import_branch=(ImageButton)findViewById(R.id.import_branch);
        add_branch=(ImageButton)findViewById(R.id.add_branch);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        name=(TextView)findViewById(R.id.spin_temp);
        setted_branch=(TextView)findViewById(R.id.setted_branch);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_import=(LinearLayout)findViewById(R.id.for_import);


        search.setVisibility(View.GONE);
        for_back.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        for_import.setVisibility(View.VISIBLE);

        dh=new DatabaseHelper(this);
        setted_branch.setText(dh.getBranch_name());
        if(setted_branch.getText().toString().equals(null)){
            setted_branch.setText("Please Set Branch");
        }
        viewData();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Branch_list branch_list= list1.get(position);
                if(branch_list.getBranch_name().equals("No Result Found") || branch_list.getBranch_name().equals("No Branch yet")){

                }else{
                    boolean check1=dh.check_admin();
                    if(check1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                        View mview = getLayoutInflater().inflate(R.layout.edit_branch, null);
                        final EditText branch = (EditText) mview.findViewById(R.id.branch);
                        final Spinner spin_branch = (Spinner) mview.findViewById(R.id.branch_spinner);
                        branch.setText(branch_list.getBranch_name());
                        ArrayList<String> list1 = dh.getBranch_type();
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Branch.this, R.layout.spinner_layout, R.id.txt, list1);
                        spin_branch.setAdapter(adapter1);
                        spin_branch.setSelection(getIndex(spin_branch, branch_list.getType()));
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        updated = format1.format(Calendar.getInstance().getTime());
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    if (TextUtils.isEmpty(branch.getText().toString())) {
                                        branch.setError("Please provide branch name");
                                    } else {
                                        updated_by = dh.getAdmin();
                                        dh.update_branch1(branch_list.getBranch_id(), branch.getText().toString(), spin_branch.getSelectedItem().toString(), updated, updated_by);
                                        Toast.makeText(Branch.this, "Success", Toast.LENGTH_SHORT).show();
                                        /*Intent in=new Intent(Branch.this,Branch.class);
                                        startActivity(in);*/
                                        viewData();
                                        dialog.cancel();
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
                }

            }
        });

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Branch_list branch_list= list1.get(i);
                boolean status=false;
                if(branch_list.getBranch_name().equals("No Result Found") || branch_list.getBranch_name().equals("No Branch yet")){

                    status=true;
                }else {
                    final AlertDialog dialog= new AlertDialog.Builder(Branch.this)
                            .setPositiveButton("Set Branch",null)
                            .setNegativeButton("Delete",null)
                            .setNeutralButton("Delete All",null)
                            .show();
                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                dh.updateBranch(branch_list.getBranch_id());
                                /*Intent in=new Intent(Branch.this,Branch.class);
                                startActivity(in);*/
                                viewData();
                                setted_branch.setText(dh.getBranch_name());
                                dialog.cancel();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    Button negativeButton=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                boolean check2=dh.check_admin();

                                if(check2) {
                                    final AlertDialog dialog1= new AlertDialog.Builder(Branch.this)
                                            .setMessage("Are you sure you want to delete this BRANCH?")
                                            .setPositiveButton("Yes",null)
                                            .setNegativeButton("Cancel",null)
                                            .show();
                                    Button positiveButton=dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try{

                                                dh.deleteBranch(branch_list.getBranch_id());
                                                Toast.makeText(Branch.this, "Success", Toast.LENGTH_SHORT).show();
                                                viewData();
                                                dialog1.cancel();
                                                dialog.cancel();




                                            }catch (Exception ex){
                                                ex.printStackTrace();

                                            }

                                        }
                                    });


                                }else{
                                    Toast.makeText(Branch.this, "You're not allowed to delete this branch", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    Button neutralButton=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                boolean check2=dh.check_admin();

                                if(check2) {
                                    final AlertDialog dialog1= new AlertDialog.Builder(Branch.this)
                                            .setMessage("Are you sure you want to delete ALL BRANCHES?")
                                            .setPositiveButton("Yes",null)
                                            .setNegativeButton("Cancel",null)
                                            .show();
                                    Button positiveButton=dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try{

                                                dh.deleteBranch1();
                                                Toast.makeText(Branch.this, "Success", Toast.LENGTH_SHORT).show();
                                                viewData();
                                                dialog1.cancel();
                                                dialog.cancel();




                                            }catch (Exception ex){
                                                ex.printStackTrace();

                                            }

                                        }
                                    });



                                }else{
                                    Toast.makeText(Branch.this, "You're not allowed to delete branches", Toast.LENGTH_LONG).show();
                                }


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

        import_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check4=dh.check_admin();

                if(check4) {
                    new MaterialFilePicker()
                            .withActivity(Branch.this)
                            .withRequestCode(1000)
                            .withHiddenFiles(true)
                            .start();
                }else{
                    Toast.makeText(Branch.this, "You're not allowed to import branch", Toast.LENGTH_LONG).show();
                }

            }
        });

        add_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check3=dh.check_admin();
                if(check3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                    View mview = getLayoutInflater().inflate(R.layout.add_branch, null);
                    final EditText branch = (EditText) mview.findViewById(R.id.branch);
                    final Spinner spin_branch = (Spinner) mview.findViewById(R.id.branch_spinner);
                    ArrayList<String> list1 = dh.getBranch_type();
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Branch.this, R.layout.spinner_layout, R.id.txt, list1);
                    spin_branch.setAdapter(adapter1);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    created = format1.format(Calendar.getInstance().getTime());
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                if (TextUtils.isEmpty(branch.getText().toString())) {
                                    branch.setError("Please provide branch name");
                                } else {
                                    boolean checkbranch = dh.check_branch(branch.getText().toString());
                                    if (!checkbranch) {
                                        created_by = dh.getAdmin();
                                        dh.insertBranch1(branch.getText().toString(), spin_branch.getSelectedItem().toString(), created, created_by);

                                        Toast.makeText(Branch.this, "Success", Toast.LENGTH_SHORT).show();
                                    /*Intent in=new Intent(Branch.this,Branch.class);
                                    startActivity(in);*/
                                        viewData();
                                        dialog.cancel();
                                    } else {
                                        Toast.makeText(Branch.this, "Branch already exist", Toast.LENGTH_SHORT).show();
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
                }else{
                    Toast.makeText(Branch.this, "You're not allowed to add this branch", Toast.LENGTH_LONG).show();
                }
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
                list1=dh.getBranchlist1(newText);
                My_branch_adapter adapter=new My_branch_adapter(Branch.this,R.layout.branch_listview,list1);
                myListView.setAdapter(adapter);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Branch.this,Branch.class);
                startActivity(in);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1000 && resultCode == RESULT_OK) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                // Do anything with file
                //textView.setText(filePath);
                import_branch(filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void viewData(){
        dh=new DatabaseHelper(this);

        try {
            list1 = dh.getBranchlist();
            My_branch_adapter adapter = new My_branch_adapter(Branch.this, R.layout.branch_listview, list1);
            myListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void import_branch(String filepath){

        try{

            dh=new DatabaseHelper(Branch.this);
            //dh.deletePartial();
            File file=new File(filepath);




            try{
                BufferedReader br=new BufferedReader(new FileReader(file));
                String line;
                int count=0;
                int count1=0;
                int count2=0;
                String content;
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
                        }while (!content.equals(null));
                        count1--;
                        if(count1==1){
                            boolean checkbranch=dh.check_branch(row[0]);
                            if(!checkbranch){
                                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                created=format.format(Calendar.getInstance().getTime());
                                created_by=dh.getAdmin();
                                dh.insertBranch(row[0],row[1],created,created_by);
                                count2=1;
                            }else{
                                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                updated=format.format(Calendar.getInstance().getTime());
                                updated_by=dh.getAdmin();
                                dh.update_branch(row[0],row[1],updated,updated_by);
                               // count2=1;
                            }
                        }



                    }
                    count++;
                    count1=0;

                }

                if(count2==1){
                    dh.copyBranch();
                    dh.delete_Branch();

                }else {
                    Toast.makeText(Branch.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();

                }

                br.close();




                Toast.makeText(Branch.this,"Success",Toast.LENGTH_SHORT).show();
                Intent in=new Intent(Branch.this,Branch.class);
                startActivity(in);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Branch.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
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
        AlertDialog dialog= new AlertDialog.Builder(Branch.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .show();
        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent in=new Intent(Branch.this,Home.class);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                    View mview1 = getLayoutInflater().inflate(R.layout.admin_pass_change, null);
                    Button admin1=(Button)mview1.findViewById(R.id.admin1);
                    Button admin2=(Button)mview1.findViewById(R.id.admin2);

                    dh = new DatabaseHelper(Branch.this);


                    admin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.change_admin_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText current_pass=(EditText)mview2.findViewById(R.id.current_pass);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Super Admin");

                            dh = new DatabaseHelper(Branch.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(current_pass.getText().toString())||TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Branch.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                boolean check_admin = dh.check_admin_via_pass(current_pass.getText().toString());
                                                if (check_admin) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 1);
                                                        Toast.makeText(Branch.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Branch.this,Branch.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Branch.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                    current_pass.setText("");
                                                    c_pass.setText("");
                                                    new_pass.setText("");
                                                }
                                            }else {
                                                Toast.makeText(Branch.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.admin_change_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Admin");

                            dh = new DatabaseHelper(Branch.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Branch.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 2);
                                                        Toast.makeText(Branch.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Branch.this,Branch.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Branch.this, "Wrong password", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Branch.this,"You're not allowed to change admin password",Toast.LENGTH_LONG).show();
                }

            }else if (id == R.id.nav_purge) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
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

                        DatePickerDialog dialog2 = new DatePickerDialog(Branch.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate, year, month, day);
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

                        DatePickerDialog dialog3 = new DatePickerDialog(Branch.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate1, year, month, day);
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

                                    AlertDialog dialog= new AlertDialog.Builder(Branch.this)
                                            .setMessage("Are you sure you want to Proceed?, this will delete some data from Transactions")
                                            .setPositiveButton("Yes",null)
                                            .setNegativeButton("Cancel",null)
                                            .show();
                                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {



                                            dh = new DatabaseHelper(Branch.this);
                                            int tl = dh.getTrans_line_count(from_date1[0], to_date1[0]);
                                            int t = dh.getTrans_count(from_date1[0], to_date1[0]);
                                            int l = dh.getLog_count(from_date1[0], to_date1[0]);

                                            dh.deleteTransaction_line(from_date1[0], to_date1[0]);
                                            dh.deleteTransaction(from_date1[0], to_date1[0]);
                                            dh.deleteLog(from_date1[0], to_date1[0]);

                                            Toast.makeText(Branch.this, "Successfully deleted, Transaction " + t +" line, Transaction line " + tl + " line , Log " + l + " line", Toast.LENGTH_LONG).show();

                                            Intent in = new Intent(Branch.this, Branch.class);
                                            startActivity(in);


                                        }
                                    });
                                } else {
                                    Toast.makeText(Branch.this, "Wrong date input", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Branch.this, "Wrong date input", Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Branch.this);
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
                                    Intent in = new Intent(Branch.this, Emp_template_list.class);
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


                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Branch.this);
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

                                    Intent in=new Intent(Branch.this,Delivery_report.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Branch.this);
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
                                        Intent in = new Intent(Branch.this, Request.class);
                                        in.putExtra("emp_name", name);
                                        startActivity(in);
                                    }else{
                                        Toast.makeText(Branch.this,"Set branch first",Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Branch.this);
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

                                    Intent in=new Intent(Branch.this,Created_transactions.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Branch.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Branch.this);
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
                                    Intent in=new Intent(Branch.this,compile_transaction.class);
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
                AlertDialog dialog= new AlertDialog.Builder(Branch.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Branch.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Branch.this)
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
