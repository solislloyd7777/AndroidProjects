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

public class Edit_template extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{



    String created="",created_by="",updated="",updated_by="",isactive="Y";
    TextView name;
    List<Temp_content> list1,list2;
    DatabaseHelper dh;
    ListView mListView1;
    ImageButton import_template,add_product,search_btn,back;
    LinearLayout for_import,for_back;
    SearchView search;
    int identifier=0;
    int fav=0,fav1=0;
    int positioning=0;
    double pricee=0.0,qtyy=0.0;
    private DatePickerDialog.OnDateSetListener mydate, mydate1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        name=(TextView)findViewById(R.id.spin_temp);
        mListView1=(ListView)findViewById(R.id.my_listView);
        import_template=(ImageButton)findViewById(R.id.import_template);
        add_product=(ImageButton)findViewById(R.id.add_prod);
        search_btn=(ImageButton)findViewById(R.id.search_btn);
        back=(ImageButton)findViewById(R.id.back);
        search=(SearchView)findViewById(R.id.search);
        for_back=(LinearLayout)findViewById(R.id.for_back);
        for_import=(LinearLayout)findViewById(R.id.for_import);




        search.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        for_import.setVisibility(View.VISIBLE);
        String template_name=getIntent().getStringExtra("Template");
        name.setText(template_name);

        dh=new DatabaseHelper(this);
        /*final ArrayList<String> list=dh.getTemp_list();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,list);
        name.setAdapter(adapter);
        name.setSelection(getIndex(name,template_name));*/
        //final int temp_id=dh.getTemp_id(name.getSelectedItem().toString());
        final int temp_id=dh.getTemp_id(template_name);
        viewData(temp_id,positioning);

        /*name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int mytemp_id=dh.getTemp_id(name.getSelectedItem().toString());
                try{
                    list1=dh.getTemp_content(mytemp_id);
                    My_temp_adapter adapter2=new My_temp_adapter(Edit_template.this,R.layout.my_list_dialog,list1);
                    mListView1.setAdapter(adapter2);



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });*/

        mListView1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    try {
                        final Temp_content temp_content = list1.get(position);
                        if(temp_content.getProd().equals("No Result Found")||temp_content.getProd().equals(null)){
                           Toast.makeText(Edit_template.this,"Product not found",Toast.LENGTH_LONG).show();
                        }else{
                            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            updated=format1.format(Calendar.getInstance().getTime());
                            updated_by=dh.getAdmin();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                            View mview = getLayoutInflater().inflate(R.layout.remove_prod, null);
                            final TextView prod_name = (TextView) mview.findViewById(R.id.prod_name);
                            final TextView uom = (TextView) mview.findViewById(R.id.spinner_uom);
                            final Spinner isnegs = (Spinner) mview.findViewById(R.id.spin_nega);
                            final Spinner iscomputed=(Spinner)mview.findViewById(R.id.spin_computed);
                            final Spinner isgrab=(Spinner)mview.findViewById(R.id.spin_grab);
                            final EditText price = (EditText) mview.findViewById(R.id.edit_price);
                            final EditText qty = (EditText) mview.findViewById(R.id.edit_qty);
                            final ImageButton favorite=(ImageButton)mview.findViewById(R.id.favorite);
                            final String[] grab = {"N"};


                            prod_name.setText(temp_content.getProd());
                            price.setText(String.valueOf(temp_content.getPrice()));
                            qty.setText(String.valueOf(temp_content.getQty()));

                            if(Double.parseDouble(price.getText().toString())==0.0){
                                price.setText(String.valueOf(0));
                            }
                            if(Double.parseDouble(qty.getText().toString())==0){
                                if(temp_content.getIscomputed().equals("Y")){
                                    qty.setText(String.valueOf(0));
                                }else{
                                    qty.setText(String.valueOf(1));
                                }

                            }

                            fav=temp_content.getFav();
                            if(fav==0){
                                favorite.setBackgroundResource(R.drawable.ic_star1);

                            }else if(fav==1){
                                favorite.setBackgroundResource(R.drawable.ic_star);
                            }


                            favorite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(fav==0){
                                        fav=1;
                                        favorite.setBackgroundResource(R.drawable.ic_star);

                                    }else if(fav==1){
                                        fav=0;
                                        favorite.setBackgroundResource(R.drawable.ic_star1);
                                    }
                                }
                            });

                            /*ArrayList<String> list1 = dh.getUom();
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list1);
                            uom.setAdapter(adapter1);*/

                            uom.setText(temp_content.getUom());

                            String[] list2 = isnega();
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list2);
                            isnegs.setAdapter(adapter2);

                            isnegs.setSelection(getIndex(isnegs, temp_content.getIsnega()));


                            String[] list3=iscomputed();
                            ArrayAdapter<String> adapter3=new ArrayAdapter<String>(Edit_template.this,R.layout.spinner_layout,R.id.txt,list3);
                            iscomputed.setAdapter(adapter3);

                            String[] list4=isgrab();
                            ArrayAdapter<String> adapter4=new ArrayAdapter<String>(Edit_template.this,R.layout.spinner_layout,R.id.txt,list4);
                            isgrab.setAdapter(adapter4);

                            iscomputed.setSelection(getIndex(iscomputed, temp_content.getIscomputed()));
                            isgrab.setSelection(getIndex(isgrab, temp_content.getIsgrab()));
                            positioning=1;
                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(price.getText().toString().isEmpty()){
                                        pricee=0;
                                    }else {
                                        pricee=Double.parseDouble(price.getText().toString());
                                    }

                                    if(qty.getText().toString().isEmpty()){
                                        qtyy=0;
                                    }else {
                                        qtyy=Double.parseDouble(qty.getText().toString());
                                    }

                                    try {

                                            int uom_id=dh.getUom_id(uom.getText().toString());

                                            int prod_id=dh.get_prod_id(prod_name.getText().toString());
                                            int trans_temp_id=dh.gettrans_temp_id(prod_id,temp_content.getId(),isgrab.getSelectedItem().toString());
                                            dh.update_trans_temp(trans_temp_id,temp_content.getId(),prod_id,uom_id,pricee,qtyy,isnegs.getSelectedItem().toString(),iscomputed.getSelectedItem().toString(),updated,updated_by,fav,isgrab.getSelectedItem().toString());
                                            Toast.makeText(Edit_template.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
                                            /*Intent in = new Intent(Edit_template.this, Edit_template.class);
                                            in.putExtra("Template",name.getSelectedItem().toString());
                                            startActivity(in);*/

                                            viewData(temp_id,positioning);
                                            dialog.cancel();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        int prod_id=dh.get_prod_id(prod_name.getText().toString());
                                        dh.deleteTrans_item(temp_content.getId(),prod_id,isgrab.getSelectedItem().toString());
                                        Toast.makeText(Edit_template.this,"Successfully Removed",Toast.LENGTH_SHORT).show();
                                        /*Intent in = new Intent(Edit_template.this, Edit_template.class);
                                        in.putExtra("Template",name.getSelectedItem().toString());
                                        startActivity(in);*/
                                        viewData(temp_id,position);
                                        dialog.cancel();
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                            });


                            builder.setView(mview);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }
        });
        import_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check_prod=dh.check_product();
                boolean check_branches=dh.check_branches();
                if(check_prod && check_branches){
                    new MaterialFilePicker()
                            .withActivity(Edit_template.this)
                            .withRequestCode(1000)
                            .withHiddenFiles(true)
                            .start();
                }else{
                    Toast.makeText(Edit_template.this, "It seems you haven't imported Products or Branches yet", Toast.LENGTH_LONG).show();
                }

            }
        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                    View mview = getLayoutInflater().inflate(R.layout.add_prod_temp, null);
                    final Spinner prod = (Spinner) mview.findViewById(R.id.prod1_spinner);
                    final TextView uom = (TextView) mview.findViewById(R.id.uom1_spinner);
                    final Spinner isnegs = (Spinner) mview.findViewById(R.id.isnega1_spinner);
                    final Spinner iscomputed = (Spinner) mview.findViewById(R.id.spin_computed);
                    final Spinner isgrab = (Spinner) mview.findViewById(R.id.spin_grab);
                    final EditText price = (EditText) mview.findViewById(R.id.price);
                    final EditText qty = (EditText) mview.findViewById(R.id.qty);
                    final ImageButton favorite1 = (ImageButton) mview.findViewById(R.id.favorite);
                    dh = new DatabaseHelper(Edit_template.this);

                    final String sku = dh.getTemp_sku(name.getText().toString());
                    ArrayList<String> list = dh.getProduct(sku, temp_id);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list);
                    prod.setAdapter(adapter);

                    /*ArrayList<String> list1=dh.getUom();
                    ArrayAdapter<String> adapter1=new ArrayAdapter<String>(Edit_template.this,R.layout.spinner_layout,R.id.txt,list1);
                    uom.setAdapter(adapter1);*/

                    String[] list2 = isnega();
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list2);
                    isnegs.setAdapter(adapter2);

                    String[] list3 = iscomputed();
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list3);
                    iscomputed.setAdapter(adapter3);

                    String[] list4 = isgrab();
                    ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(Edit_template.this, R.layout.spinner_layout, R.id.txt, list4);
                    isgrab.setAdapter(adapter4);


                    final int temp_id = dh.getTemp_id(name.getText().toString());




                    fav1 = 0;
                    if (fav1 == 0) {
                        favorite1.setBackgroundResource(R.drawable.ic_star1);
                    } else if (fav1 == 1) {
                        favorite1.setBackgroundResource(R.drawable.ic_star);
                    }

                    favorite1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fav1 == 0) {
                                fav1 = 1;
                                favorite1.setBackgroundResource(R.drawable.ic_star);

                            } else if (fav == 1) {
                                fav1 = 0;
                                favorite1.setBackgroundResource(R.drawable.ic_star1);
                            }
                        }
                    });

                    if(sku.equals("S")){
                        final int prod_id1 = dh.get_prod_id(prod.getSelectedItem().toString());
                        boolean check_isgrab=dh.check_exist(temp_id,prod_id1);
                        if(check_isgrab){
                            isgrab.setSelection(getIndex(isgrab, "Y"));
                        }else{
                            isgrab.setSelection(getIndex(isgrab, "N"));
                        }
                    }else{
                        isgrab.setSelection(getIndex(isgrab, "N"));
                    }
                    prod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String uom_name1 = dh.uom_name(dh.get_prod_id(prod.getSelectedItem().toString()));
                            try {
                                final int prod_id2 = dh.get_prod_id(prod.getSelectedItem().toString());
                                uom.setText(uom_name1);
                                if(sku.equals("S")){
                                    boolean check_isgrab=dh.check_exist(temp_id,prod_id2);
                                    if(check_isgrab){
                                        isgrab.setSelection(getIndex(isgrab, "Y"));
                                    }else{
                                        isgrab.setSelection(getIndex(isgrab, "N"));
                                    }
                                }else{
                                    isgrab.setSelection(getIndex(isgrab, "N"));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                My_temp_adapter ad=new My_temp_adapter(Edit_template.this,R.layout.my_list_dialog,list1);


                                final int prod_id = dh.get_prod_id(prod.getSelectedItem().toString());
                                if (price.getText().toString().isEmpty()) {
                                    pricee = 0;
                                } else {
                                    pricee = Double.parseDouble(price.getText().toString());
                                }

                                if (qty.getText().toString().isEmpty()) {
                                    qtyy = 0;
                                } else {
                                    qtyy = Double.parseDouble(qty.getText().toString());
                                }
                                //Boolean check_prod = dh.check_Name(prod_id, temp_id);
                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                created = format1.format(Calendar.getInstance().getTime());
                                created_by = dh.getAdmin();
                                //if (check_prod) {

                                    //int lead = dh.getLeadDays(prod_id);
                                    int uom_id = dh.getUom_id(uom.getText().toString());
                                    String getGrab=dh.getGrab(temp_id,prod_id);
                                    dh.insert_transaction_temp11(temp_id, prod_id, uom_id, pricee, isnegs.getSelectedItem().toString(), created, created_by, isactive, qtyy, fav1, iscomputed.getSelectedItem().toString(),isgrab.getSelectedItem().toString());
                                    dh.insert_transaction_temp();
                                    dh.delete_template_line_temp();
                                    Toast.makeText(Edit_template.this, "Successfully added", Toast.LENGTH_LONG).show();
                                    /*Intent in = new Intent(Edit_template.this, Edit_template.class);
                                    in.putExtra("Template",name.getSelectedItem().toString());
                                    startActivity(in);*/
                                    positioning=-1;
                                    viewData(temp_id,positioning);
                                    dialog.cancel();
                                //} else {
                                   // Toast.makeText(Edit_template.this, "This product can't be add in this template", Toast.LENGTH_LONG).show();
                                //}
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(identifier==0) {
                    search.setVisibility(View.VISIBLE);
                    name.setVisibility(View.GONE);
                    for_import.setVisibility(View.GONE);
                    identifier=1;
                }else if(identifier==1){
                    Intent in=new Intent(Edit_template.this,Template_list.class);
                    startActivity(in);
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

                int mytemp_id=dh.getTemp_id(name.getText().toString());
                list1=dh.getTemp_content1(newText,mytemp_id);
                My_temp_adapter adapter1=new My_temp_adapter(Edit_template.this,R.layout.my_list_dialog,list1);
                mListView1.setAdapter(adapter1);
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(identifier==0){
                    Intent in=new Intent(Edit_template.this,Template_list.class);
                    startActivity(in);
                }else if(identifier==1){
                    Intent in=new Intent(Edit_template.this,Edit_template.class);
                    in.putExtra("Template",name.getText().toString());
                    startActivity(in);
                }

            }
        });

    }

    private void viewData(int temp_id,int position){

        try {


            dh = new DatabaseHelper(this);
            list1 = dh.getTemp_content(temp_id);
            My_temp_adapter adapter1 = new My_temp_adapter(this, R.layout.my_list_dialog, list1);
            mListView1.setAdapter(adapter1);
            int pos=adapter1.getCount()-1;
            if(position==0){
                mListView1.setSelection(position);
            }else if(position<0){
                mListView1.setSelection(pos);
            }else if(position>0){
                mListView1.setSelection(position);
            }
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
                import_template(filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void import_template(String filePath){
        try{




            //File file1=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"IMPORT_TEMPLATE.csv");
            File file1=new File(filePath);
            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            created=format1.format(Calendar.getInstance().getTime());
            created_by=dh.getAdmin();
            dh=new DatabaseHelper(Edit_template.this);


            try{
                BufferedReader br1=new BufferedReader(new FileReader(file1));
                String line1;
                int count=0;
                int count1=0;
                int count2=0;
                String content=null;
                while((line1=br1.readLine())!=null){


                    if(count!=0){
                        String[] row=line1.split(",");
                        do{
                            try{
                                content=row[count1];
                            }catch (Exception e){
                                break;
                            }
                            count1++;
                        }while (!content.equals(null));
                        count1--;
                        if(count1==4){
                            int temp_id=dh.getTemp_id(name.getText().toString());
                            int prod_id=dh.get_prod_id(row[0]);
                            String isGrab="N";
                            if(prod_id==0){

                            }else{
                                Boolean check_prod=dh.check_Name1(temp_id,prod_id);
                                if(check_prod){
                                    dh.deleteProd_temp(temp_id,prod_id);
                                }
                                int uom_id=dh.getUom_id(row[1]);
                                dh.insert_transaction_temp1(temp_id,prod_id,uom_id,Double.parseDouble(row[2]),row[3],row[4],created,created_by,isactive,isGrab);
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
                    dh.insert_transaction_temp();
                    dh.delete_template_line_temp();
                    Toast.makeText(Edit_template.this,"Success",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Edit_template.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
                }
                //dh.copyprice();

                br1.close();



                Intent in=new Intent(Edit_template.this,Edit_template.class);
                in.putExtra("Template",name.getText().toString());
                startActivity(in);

            }catch (IOException ex){
                //ex.printStackTrace();
                Toast.makeText(Edit_template.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Edit_template.this,"Seems you've chosen the wrong file to import",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
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

    public String[] isnega(){
        String[] ar=new String[9];
        ar[0]="N";
        ar[1]="PWD";
        ar[2]="R";
        ar[3]="SC";
        ar[4]="SCD";
        ar[5]="TC";
        ar[6]="TCR";
        ar[7]="X";
        ar[8]="Z";
        return ar;
    }
    public String[] iscomputed(){
        String[] ar=new String[3];
        ar[0]="Y";
        ar[1]="N";
        ar[2]="M";
        return ar;
    }

    public String[] isgrab(){
        String[] ar=new String[2];
        ar[0]="N";
        ar[1]="Y";
        return ar;
    }

    @Override
    public void onBackPressed() {
        Intent in=new Intent(Edit_template.this,Template_list.class);
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
                    Intent in = new Intent(this, List_product.class);
                    startActivity(in);
            } else if (id == R.id.nav_branch) {

                    Intent in = new Intent(this, Branch.class);
                    startActivity(in);
            }/*else if (id == R.id.nav_bom) {
                Intent in = new Intent(this, Variance.class);
                startActivity(in);

            }*/else if (id == R.id.nav_request) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Edit_template.this);
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
                                        Intent in = new Intent(Edit_template.this, Request.class);
                                        in.putExtra("emp_name", name);
                                        startActivity(in);
                                    }else{
                                        Toast.makeText(Edit_template.this,"Set branch first",Toast.LENGTH_LONG).show();
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




            }else if (id == R.id.nav_purge) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
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

                        DatePickerDialog dialog2 = new DatePickerDialog(Edit_template.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate, year, month, day);
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

                        DatePickerDialog dialog3 = new DatePickerDialog(Edit_template.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mydate1, year, month, day);
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

                                    AlertDialog dialog= new AlertDialog.Builder(Edit_template.this)
                                            .setMessage("Are you sure you want to Proceed?, this will delete some data from Transactions")
                                            .setPositiveButton("Yes",null)
                                            .setNegativeButton("Cancel",null)
                                            .show();
                                    Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {



                                            dh = new DatabaseHelper(Edit_template.this);
                                            int tl = dh.getTrans_line_count(from_date1[0], to_date1[0]);
                                            int t = dh.getTrans_count(from_date1[0], to_date1[0]);
                                            int l = dh.getLog_count(from_date1[0], to_date1[0]);

                                            dh.deleteTransaction_line(from_date1[0], to_date1[0]);
                                            dh.deleteTransaction(from_date1[0], to_date1[0]);
                                            dh.deleteLog(from_date1[0], to_date1[0]);

                                            Toast.makeText(Edit_template.this, "Successfully deleted, Transaction " + t +" line, Transaction line " + tl + " line , Log " + l + " line", Toast.LENGTH_LONG).show();

                                            Intent in = new Intent(Edit_template.this, Edit_template.class);
                                            startActivity(in);


                                        }
                                    });
                                } else {
                                    Toast.makeText(Edit_template.this, "Wrong date input", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Edit_template.this, "Wrong date input", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });

                builder.setView(mview1);
                AlertDialog dialog1 = builder.create();
                dialog1.show();

            }else if(id==R.id.nav_change_pass){
                boolean check_level=dh.check_admin();
                if(check_level){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                    View mview1 = getLayoutInflater().inflate(R.layout.admin_pass_change, null);
                    Button admin1=(Button)mview1.findViewById(R.id.admin1);
                    Button admin2=(Button)mview1.findViewById(R.id.admin2);

                    dh = new DatabaseHelper(Edit_template.this);


                    admin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.change_admin_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText current_pass=(EditText)mview2.findViewById(R.id.current_pass);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Super Admin");

                            dh = new DatabaseHelper(Edit_template.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(current_pass.getText().toString())||TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Edit_template.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                boolean check_admin = dh.check_admin_via_pass(current_pass.getText().toString());
                                                if (check_admin) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 1);
                                                        Toast.makeText(Edit_template.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Edit_template.this,Edit_template.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    Toast.makeText(Edit_template.this, "Wrong password", Toast.LENGTH_LONG).show();
                                                    current_pass.setText("");
                                                    c_pass.setText("");
                                                    new_pass.setText("");
                                                }
                                            }else {
                                                Toast.makeText(Edit_template.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                            View mview2 = getLayoutInflater().inflate(R.layout.admin_change_pass, null);
                            ImageButton save=(ImageButton)mview2.findViewById(R.id.save);
                            final EditText new_pass=(EditText)mview2.findViewById(R.id.new_pass);
                            final EditText c_pass=(EditText)mview2.findViewById(R.id.confirm_pass);
                            TextView type=(TextView)mview2.findViewById(R.id.admin_type);
                            type.setText("Change Password for Admin");

                            dh = new DatabaseHelper(Edit_template.this);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(new_pass.getText().toString())||TextUtils.isEmpty(c_pass.getText().toString())) {
                                        Toast.makeText(Edit_template.this,"All fields are required",Toast.LENGTH_LONG).show();
                                    }else {
                                        try {
                                            if (new_pass.getText().toString().equals(c_pass.getText().toString())) {
                                                    try {
                                                        dh.update_admin_pass(new_pass.getText().toString(), 2);
                                                        Toast.makeText(Edit_template.this, "Successfully changed password", Toast.LENGTH_LONG).show();
                                                        Intent in=new Intent(Edit_template.this,Edit_template.class);
                                                        startActivity(in);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                            } else {
                                                Toast.makeText(Edit_template.this, "Password didn't match", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Edit_template.this,"You're not allowed to change admin password",Toast.LENGTH_LONG).show();
                }

            }  else if (id == R.id.nav_employee) {
                Intent in = new Intent(this, Add_employee.class);
                startActivity(in);

            } else if (id == R.id.nav_transaction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Edit_template.this);
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
                                    Intent in = new Intent(Edit_template.this, Emp_template_list.class);
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

            }else if (id == R.id.nav_about) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
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




            }else if (id == R.id.nav_created) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Edit_template.this);
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
                                    Intent in = new Intent(Edit_template.this, Created_transactions.class);
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

            } else if (id == R.id.nav_deliver) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Edit_template.this);
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

                                    Intent in=new Intent(Edit_template.this,Delivery_report.class);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_template.this);
                View mview = getLayoutInflater().inflate(R.layout.login_dialog, null);
                final EditText code=(EditText)mview.findViewById(R.id.emp_code);
                final EditText password=(EditText)mview.findViewById(R.id.password);

                dh = new DatabaseHelper(Edit_template.this);
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
                                    Intent in=new Intent(Edit_template.this,compile_transaction.class);
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
                AlertDialog dialog= new AlertDialog.Builder(Edit_template.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes",null)
                        .setNegativeButton("Cancel",null)
                        .show();
                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            Intent in=new Intent(Edit_template.this,Home.class);
                            startActivity(in);



                        }catch (Exception ex){
                            ex.printStackTrace();

                        }

                    }
                });
            }
            else if(id == R.id.nav_exit){
                AlertDialog dialog= new AlertDialog.Builder(Edit_template.this)
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

        DrawerLayout drawer = findViewById(R.id.drawer_edittemp_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void error(){
        Toast.makeText(this,"Incorrect employee number or password",Toast.LENGTH_LONG).show();
    }
}
