package com.mh.mytransaction;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.github.mikephil.charting.data.BarEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String dbName = "Mydatabase";
    public static int dbVersion = 1;
    public static String dbPath = "";
    Context myContext;
    SQLiteDatabase db;
    Cursor cursor=null,curs=null;

    public DatabaseHelper(Context context) {
        super(context, dbName, null, dbVersion);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    private boolean ExistDatabase() {
        File myFile = new File(dbPath + dbName);
        return myFile.exists();
    }

    private void CopyDatabase() {
        try {
            InputStream myInput = myContext.getAssets().open(dbName);
            OutputStream myOutput = new FileOutputStream(dbPath + dbName);
            byte[] myBuffer = new byte[2048];
            int length;
            while ((length = myInput.read(myBuffer)) > 0) {
                myOutput.write(myBuffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public void StartWork(){
        dbPath=myContext.getFilesDir().getParent()+"/databases/";
        if(!ExistDatabase()){
            this.getWritableDatabase();
            CopyDatabase();
        }else{
            this.getWritableDatabase();
        }
    }

    public void partial_uom(String product,String uom,String issku){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_product_uom_import(product,uom,isSKU) values('"+product+"','"+uom+"','"+issku+"')");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete_partial_uom(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_product_uom_import");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert_uom(String created,String created_by,String isactive){
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select uom from mh_product_uom_import group by uom",null);
            cursor.moveToFirst();
            do{
                boolean check_uom=check_uom(cursor.getString(0));
                if(check_uom){
                    db.execSQL("update c_uom set created='"+created+"',created_by='"+created_by+"'");
                }else{
                    db.execSQL("insert into c_uom(value,name,created,created_by,isactive) values('"+cursor.getString(0)+"','"+cursor.getString(0)+"','"+created+"','"+created_by+"','"+isactive+"')");

                }
            }while(cursor.moveToNext());

            /*if (cursor!= null){
                cursor.close();
            }*/

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }


    public void insert_product(String created,String created_by,String isactive){
        db=this.getWritableDatabase();
        String name,sku;
        int uom_id,leaddays;


        try{
            Cursor curs=db.rawQuery("select product,uom,isSKU from mh_product_uom_import group by product",null);
            curs.moveToFirst();
            do{
                name=curs.getString(0);
                boolean check_prod=checkName(name);
                uom_id=getUom_id(curs.getString(1));
                sku=curs.getString(2);

                if(check_prod){
                    db.execSQL("insert into m_product(c_uom_id,value,name,isSKU,created,created_by,isactive) values('"+uom_id+"','"+name+"','"+name+"','"+sku+"','"+created+"','"+created_by+"','"+isactive+"')");

                }else{
                    db.execSQL("update m_product set c_uom_id='"+uom_id+"',isSKU='"+sku+"',created='"+created+"',created_by='"+created_by+"' where name='"+name+"'");
                }
            }while(curs.moveToNext());
            curs.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    public int getUom_id(String uom) {
        db = getReadableDatabase();
        int id=0;
        try {
            Cursor cursor3 = db.rawQuery("select c_uom_id from c_uom where name='" + uom + "'", null);
            cursor3.moveToFirst();
            id = cursor3.getInt(0);
            if (cursor3 != null) {
                cursor3.close();
            }

        /*Cursor cursor = null;
        try {
            cursor = db.query(...
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public ArrayList<String>  getProduct(String sku,int temp_id){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();



        try{
            if(sku.equals("S")){
                String selectQuery="select name,mh_product_id from m_product where isSKU='Y' or isSKU='X' order by name";
                cursor=db.rawQuery(selectQuery,null);
                if(cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        boolean status=check_prod_temp(temp_id,cursor.getInt(1));
                        if(status){

                            String prod=cursor.getString(0);
                            list.add(prod);
                        }

                    }
                }
                db.setTransactionSuccessful();
            }else{
                String selectQuery="select name,mh_product_id from m_product where isSKU='N' or isSKU='X' order by name";
                cursor=db.rawQuery(selectQuery,null);
                if(cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        boolean status=check_prod_temp1(temp_id,cursor.getInt(1));
                        if(!status){
                            String prod=cursor.getString(0);
                            list.add(prod);
                        }
                    }
                }
                db.setTransactionSuccessful();
            }

            cursor.close();


        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    public boolean check_prod_temp(int temp_id,int prod_id){

        db=getReadableDatabase();
        boolean status=false;
        try{

            curs=db.rawQuery("select count(mh_transaction_template_id) from mh_template_line where mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'",null);
            if(curs.moveToFirst()){
                if(curs.getInt(0)<=1){
                    status=true;

                }else {
                    status=false;
                }
            }else {
                status=false;
            }
            curs.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return status;

    }

    public boolean check_prod_temp1(int temp_id,int prod_id){

        db=getReadableDatabase();
        boolean status=false;
        try{

            curs=db.rawQuery("select count(mh_transaction_template_id) from mh_template_line where mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'",null);
            if(curs.moveToFirst()){
                if(curs.getInt(0)>0){
                    status=true;

                }else {
                    status=false;
                }
            }else {
                status=false;
            }
            curs.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return status;

    }

    public ArrayList<String> getUom(){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select name from c_uom order by name";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String branch=cursor.getString(cursor.getColumnIndex("name"));
                    list.add(branch);
                }
            }
            db.setTransactionSuccessful();
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    /*public ArrayList<Integer> getLeadDays(){
        ArrayList<Integer>list=new ArrayList<Integer>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select lead_days from m_product group by lead_days order by lead_days";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    int lead=cursor.getInt(0);
                    list.add(lead);
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }*/


    public void deleteProduct(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from m_product");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void deleteUom(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from c_uom");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*public void deletePartial(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from partial_uom");
        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public String[] get_productUom(String uom){
        db=getReadableDatabase();
        String[] array=new String[3];
        try {
            cursor = db.rawQuery("select c_uom_id,isactive from m_product where name='" + uom + "'", null);
            cursor.moveToFirst();
            array[0] = cursor.getString(0);
            array[1] = cursor.getString(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return array;
    }

    public String[] getUom_name(int uom_id){
        db=getReadableDatabase();
        String[] array=new String[2];
        cursor=db.rawQuery("select name,isactive from c_uom where c_uom_id='"+uom_id+"'",null);
        cursor.moveToFirst();
        array[0]=cursor.getString(0);
        array[1]=cursor.getString(1);
        if(cursor!=null){
            cursor.close();
        }
        return array;
    }

    public List<myList> getProduct_list() {
        List<myList> my_list=new ArrayList<>();
        db= getReadableDatabase();

        try{
            curs= db.rawQuery("SELECT name,c_uom_id,isSKU from m_product order by name", null);
            if(curs.moveToFirst()){

                do{

                    String product=curs.getString(0);
                    int c_uom_id=curs.getInt(1);
                    String sku=curs.getString(2);
                    String[] ar=this.getUom_name(c_uom_id);
                     myList mylist= new myList(product,ar[0],sku);
                    my_list.add(mylist);


                }while(curs.moveToNext());
            }

        }catch (Exception ex){

            ex.printStackTrace();


        }
        curs.close();

        return my_list;

    }

    public List<myList> getProduct_list1(String text) {
        List<myList> my_list=new ArrayList<>();
        db= getReadableDatabase();

        try{
            curs= db.rawQuery("SELECT name,c_uom_id,isSKU from m_product where name like '%"+text+"%'", null);
            if(curs.moveToFirst()){

                do{

                    String product=curs.getString(0);
                    int c_uom_id=curs.getInt(1);
                    String sku=curs.getString(2);
                    String[] ar=this.getUom_name(c_uom_id);
                    myList mylist= new myList(product,ar[0],sku);
                    my_list.add(mylist);


                }while(curs.moveToNext());
            }else{
                myList mylist= new myList("No Result Found","","");
                my_list.add(mylist);
            }

        }catch (Exception ex){

            ex.printStackTrace();


        }
        curs.close();

        return my_list;

    }

    public Boolean checkName(String prod_name) {
        db=getReadableDatabase();
        boolean status=false;
        try {
            cursor = db.rawQuery("select name from m_product where name='" + prod_name + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                status=false;
            } else {
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;

    }

    public void add_product(String name,int id,String created,String created_by,String isactive,String sku){
        try{

            db=getWritableDatabase();
            db.execSQL("insert into m_product(c_uom_id,value,name,created,created_by,isactive,isSKU) values('"+id+"','"+name+"','"+name+"','"+created+"','"+created_by+"','"+isactive+"','"+sku+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update_product(int id,String name,String updated,String updated_by,String active,int uom_id,String sku){
        db=getWritableDatabase();
        try{
            db.execSQL("update m_product set value='"+name+"', name='"+name+"',updated='"+updated+"',updated_by='"+updated_by+"',isactive='"+active+"',c_uom_id='"+uom_id+"',isSKU='"+sku+"' where mh_product_id='"+id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int get_prod_id(String name){
        db=getReadableDatabase();
        int id = 0;
        try{
            cursor=db.rawQuery("select mh_product_id from m_product where name='"+name+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public void delete_prod(int id){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from m_product where mh_product_id='"+id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert_transaction_temp(){
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,isnegtative,created,created_by,isactive,is_computed from mh_template_line_temp",null);
            if(cursor.moveToFirst()){
                do{
                    int temp_id=cursor.getInt(0);
                    int prod_id=cursor.getInt(1);
                    int uom_id=cursor.getInt(2);
                    double quantity=cursor.getDouble(3);
                    double price=cursor.getDouble(4);
                    String isnegative=cursor.getString(5);
                    String created=cursor.getString(6);
                    String created_by=cursor.getString(7);
                    String active=cursor.getString(8);
                    String is_computed=cursor.getString(9);


                    db.execSQL("insert into mh_template_line(mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,isnegative,created,created_by,isactive,is_computed) " +
                            "values ('"+temp_id+"','"+prod_id+"','"+uom_id+"','"+quantity+"','"+price+"','"+isnegative+"','"+created+"','"+created_by+"','"+active+"','"+is_computed+"')");
                }while (cursor.moveToNext());
            }

            cursor.close();



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void insert_transaction_temp1(int temp_id,int prod_id,int uom_id,double price,String isnegative,String is_computed,String created,String created_by,String active,String isGrab){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_template_line_temp(mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,isnegtative,is_computed,created,created_by,isactive,isgrab) " +
                    "values ('"+temp_id+"','"+prod_id+"','"+uom_id+"',0,'"+price+"','"+isnegative+"','"+is_computed+"','"+created+"','"+created_by+"','"+active+"','"+isGrab+"')");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete_template_line_temp(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_template_line_temp");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void partial_transaction_temp(String prod,String uom,double price,String isnegative,String temp,int lead){
        try{
            db=getWritableDatabase();
            db.execSQL("insert into partl_trans_temp (product,uom,price,isnegative,template_name,lead_days) values('"+prod+"','"+uom+"','"+price+"','"+isnegative+"','"+temp+"','"+lead+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getTempName() {
        db=getReadableDatabase();
        String[] ar=new String[2];
        try{
            cursor=db.rawQuery("select * from mh_transaction_template_temporary group by template_name",null);
            cursor.moveToFirst();
            if(cursor.getCount()<1){
                ar[0]="";
                ar[1]="";
            }else{
                ar[0]=cursor.getString(4);
                ar[1]=String.valueOf(cursor.getInt(5));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        cursor.close();
        return ar;
    }

    public Boolean checkTempName(String name) {
        db=getReadableDatabase();
        boolean status=false;
        try {
            cursor = db.rawQuery("select name from mh_template where name='" + name + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {

                status= false;
            } else status= true;
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;



    }

   public void insertTemp(String name, int f_lead,int t_lead,String sendto,String created,String created_by,String isactive,String sku) {
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_template(value,name,f_leaddays,t_leaddays,send_to,created,created_by,isactive,isSKU) values('"+name+"','"+name+"','"+f_lead+"','"+t_lead+"','"+sendto+"','"+created
        +"','"+created_by+"','"+isactive+"','"+sku+"') ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Temp_list> getTempList() {

        List<Temp_list> list_temp=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {

            cursor = db.rawQuery("SELECT mh_template_id,name,f_leaddays,t_leaddays,isactive,send_to,isSKU from mh_template order by name", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    int id = cursor.getInt(0);
                    String temp_name = cursor.getString(1);
                    int f_lead = cursor.getInt(2);
                    int t_lead = cursor.getInt(3);
                    String isact = cursor.getString(4);
                    String sendto=cursor.getString(5);
                    String sku=cursor.getString(6);

                    Temp_list temp_list = new Temp_list(id, f_lead,t_lead, temp_name, isact,sendto,sku);

                    list_temp.add(temp_list);


                } while (cursor.moveToNext());
            } else {
                Temp_list temp_list = new Temp_list(0, 0,0, "No Template Yet", "","","");

                list_temp.add(temp_list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return list_temp;
    }

    public List<Temp_list> getTempList1(String text) {

        List<Temp_list> list_temp=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {

            cursor = db.rawQuery("SELECT mh_template_id,name,f_leaddays,t_leaddays,isactive,send_to,isSKU from mh_template where name like '%"+text+"%'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    int id = cursor.getInt(0);
                    String temp_name = cursor.getString(1);
                    int f_lead = cursor.getInt(2);
                    int t_lead = cursor.getInt(3);
                    String isact = cursor.getString(4);
                    String sendto=cursor.getString(5);
                    String sku=cursor.getString(6);

                    Temp_list temp_list = new Temp_list(id, f_lead,t_lead,temp_name, isact,sendto,sku);

                    list_temp.add(temp_list);


                } while (cursor.moveToNext());
            } else {
                Temp_list temp_list = new Temp_list(0, 0, 0,"No Result Found", "","","");

                list_temp.add(temp_list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return list_temp;
    }


    public List<Created_transaction_module> getCreatedList(String doc_num1) {

        List<Created_transaction_module> created_list=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {

            cursor = db.rawQuery("SELECT name,doc_num,branch_name,file_name,ref_num,remarks,locator,product,uom,qty,price,subtotal,isnegative,is_computed,date_req,date_ref,created_by from mh_log where doc_num='"+doc_num1+"' order by date_ref desc", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    String name=cursor.getString(0);
                    String doc_num=cursor.getString(1);
                    String branch_name=cursor.getString(2);
                    String file_name=cursor.getString(3);
                    String ref_num=cursor.getString(4);
                    String remarks=cursor.getString(5);
                    String locator=cursor.getString(6);
                    String product=cursor.getString(7);
                    String uom=cursor.getString(8);
                    double qty=cursor.getDouble(9);
                    double price=cursor.getDouble(10);
                    double subtotal=cursor.getDouble(11);
                    String isnegative=cursor.getString(12);
                    String iscomputed=cursor.getString(13);
                    String date_req=cursor.getString(14);
                    String date_ref=cursor.getString(15);
                    String created_by=cursor.getString(16);
                    Created_transaction_module created_transaction_module = new Created_transaction_module(name,doc_num,branch_name,file_name,ref_num,remarks,locator,product,uom,qty,price,subtotal,isnegative,iscomputed,date_req,date_ref,created_by);

                    created_list.add(created_transaction_module);


                } while (cursor.moveToNext());
            } else {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return created_list;
    }

    public List<Created_transaction_module1> getCreatedList1() {

        List<Created_transaction_module1> created_list=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {

            cursor = db.rawQuery("SELECT  distinct doc_num, file_name from mh_log order by date_ref desc", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    String doc_num=cursor.getString(0);
                    String file_name=cursor.getString(1);

                    Created_transaction_module1 created_transaction_module1 = new Created_transaction_module1(doc_num,file_name);

                    created_list.add(created_transaction_module1);


                } while (cursor.moveToNext());
            } else {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return created_list;
    }

    public List<Delivery_module> getDelivery_list() {

        List<Delivery_module> content=new ArrayList<>();
        db= getReadableDatabase();
        try {

            curs = db.rawQuery("SELECT mh_delivery_report_template1_id,mh_branch_id,name,document_num,date_req from mh_delivery_report_template order by name", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int branch_id=curs.getInt(1);
                    String branch_name=this.getbranch_name(branch_id);
                    Delivery_module delivery_module = new Delivery_module(curs.getInt(0),branch_id,branch_name,curs.getString(2),curs.getString(3),curs.getString(4));

                    content.add(delivery_module);


                } while (curs.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return content;
    }

    public List<Delivery_module> getDelivery_list1(String text) {

        List<Delivery_module> content=new ArrayList<>();
        db= getReadableDatabase();
        try {

            curs = db.rawQuery("SELECT mh_delivery_report_template1_id,mh_branch_id,name,document_num,date_req from mh_delivery_report_template where name like '%"+text+"%'", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int branch_id=curs.getInt(1);
                    String branch_name=this.getbranch_name(branch_id);
                    Delivery_module delivery_module = new Delivery_module(curs.getInt(0),branch_id,branch_name,curs.getString(2),curs.getString(3),curs.getString(4));

                    content.add(delivery_module);


                } while (curs.moveToNext());
            }else{
                Delivery_module delivery_module = new Delivery_module(0,0,"","No Result Found","","");

                content.add(delivery_module);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return content;
    }

    /*public int getLeadDays(int prod_id){
        db=getReadableDatabase();
        int lead = 0;
        try {
            Cursor cursor1 = db.rawQuery("select lead_days from m_product where mh_product_id='" + prod_id + "'", null);
            cursor1.moveToFirst();
            lead = cursor1.getInt(0);
            if (cursor1!= null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lead;
    }*/

    public int getTemp_id(String temp_name) {
        db=getReadableDatabase();
        int temp = 0;
        try {
            Cursor cursor2 = db.rawQuery("select mh_template_id from mh_template where name='" + temp_name + "'", null);
            cursor2.moveToFirst();
            temp = cursor2.getInt(0);
            if (cursor2!= null){
                cursor2.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return temp;
    }
    public int getTemp_id1(String temp_name) {
        db=getReadableDatabase();
        int temp = 0;
        try {
            Cursor cursor2 = db.rawQuery("select mh_delivery_report_template1_id from mh_delivery_report_template where name='" + temp_name + "'", null);
            cursor2.moveToFirst();
            temp = cursor2.getInt(0);
            if (cursor2!= null){
                cursor2.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return temp;
    }



    public ArrayList<String> getTemp_list(){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select name from mh_template order by name";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String branch=cursor.getString(0);
                    list.add(branch);
                }
            }
            db.setTransactionSuccessful();
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }

        return list;
    }



    public ArrayList<String> getCreated_list(){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select distinct file_name from mh_log order by date_ref";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String branch=cursor.getString(0);
                    list.add(branch);
                }
            }
            db.setTransactionSuccessful();
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }


    public ArrayList<String> getTemp_list1(){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select name from mh_delivery_report_template order by name";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String branch=cursor.getString(0);
                    list.add(branch);
                }
            }
            db.setTransactionSuccessful();
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }
    public String getProduct_name(int id){
        db=getReadableDatabase();
        String name = null;
        try{
            Cursor cursor1=db.rawQuery("select name from m_product where mh_product_id='"+id+"'",null);
            cursor1.moveToFirst();
            name=cursor1.getString(0);
            if(cursor1!=null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            name=null;
        }
        return name;
    }

    public String getUom_name1(int uom_id){
        db=getReadableDatabase();
        String name="";
        try {
            Cursor cursor2 = db.rawQuery("select name from c_uom where c_uom_id='" + uom_id + "'", null);
            cursor2.moveToFirst();
            name = cursor2.getString(0);
            if (cursor2 != null) {
                cursor2.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }


    public List<Temp_content> getTemp_content(int id1) {
        List<Temp_content> content1=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        String prod_name=null;
        try {


            curs = db.rawQuery("SELECT mh_template_id,mh_product_id,c_uom_id,priceentered,isnegative,is_computed,isactive,quantity_def0,favorite,isgrab from mh_template_line where mh_template_id='"+id1+"' order by favorite desc ", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int id = curs.getInt(0);
                    prod_name =this.getProduct_name(curs.getInt(1));
                    String uom=this.getUom_name1(curs.getInt(2));
                    double price=curs.getDouble(3);
                    String isnega=curs.getString(4);
                    String iscomputed=curs.getString(5);
                    String isact = curs.getString(6);
                    double qty=curs.getDouble(7);
                    int fav=curs.getInt(8);
                    String isgrab=curs.getString(9);


                    Temp_content temp_cont = new Temp_content(prod_name,uom,isnega,isact,id,price,iscomputed,qty,fav,isgrab);

                    content1.add(temp_cont);


                } while (curs.moveToNext());

            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content1;
    }


    public List<Temp_content> getTemp_content1(String text,int id1) {
        List<Temp_content> content1=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        String prod_name="";
        String uom="";
        double price=0.0;
        String isnega="";
        String iscomputed="";
        String isact="";
        int id=0;
        double qty=0;
        try {
            //int count=this.count_prod(text);
            //int[] ids=new int[count];
            cursor=db.rawQuery("select mh_product_id from m_product where name like '%"+text+"%'",null);
            if(cursor.moveToFirst()) {

                do {
                    int id2 = cursor.getInt(0);
                    curs = db.rawQuery("SELECT mh_template_id,mh_product_id,c_uom_id,priceentered,isnegative,is_computed,isactive,quantity_def0,favorite,isgrab from mh_template_line where mh_template_id='" + id1 + "' and mh_product_id='" + id2 + "'", null);
                    if(curs.moveToFirst()) {
                        if (curs.getCount() > 0) {
                            do {
                                id = curs.getInt(0);
                                prod_name = this.getProduct_name(curs.getInt(1));
                                uom = this.getUom_name1(curs.getInt(2));
                                price = curs.getDouble(3);
                                isnega = curs.getString(4);
                                iscomputed = curs.getString(5);
                                isact = curs.getString(6);
                                qty = curs.getDouble(7);
                                int favorite=curs.getInt(8);
                                String isgrab=curs.getString(9);

                                Temp_content temp_cont = new Temp_content(prod_name, uom, isnega, isact, id, price, iscomputed, qty,favorite,isgrab);

                                content1.add(temp_cont);


                            } while (curs.moveToNext());

                        }
                    }else {

                    }
                    if (curs!= null){
                        curs.close();
                    }
                } while (cursor.moveToNext());
            }else {
                Temp_content temp_cont = new Temp_content("No Result Found", "", "", "", 0, 0, "",0,0,"");

                content1.add(temp_cont);
            }




            if (cursor!= null){
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content1;
    }

    public int count_prod(String name){
        db=getReadableDatabase();
        int count=0;
        try{
            Cursor cursor1=db.rawQuery("select count(*) from m_product where name like '%"+name+"%'",null);
            cursor1.moveToFirst();
            count=cursor1.getInt(0);
            cursor1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public Boolean check_Name(int prod_id,int id) {
        db=getReadableDatabase();
        boolean status=false;
        try {
            cursor = db.rawQuery("select count(mh_transaction_template_id) from mh_template_line where mh_product_id='" + prod_id + "' and mh_template_id='" + id + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                status=false;

            } else {
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;


    }


    public void deleteTemp(String template_name,int id) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_template where  name='"+template_name+"'");
            db.execSQL("delete from mh_template_line where mh_template_id='"+id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean check_Name1(int id,int prod_id) {
        db=getReadableDatabase();
        boolean status=false;
        try {
            cursor = db.rawQuery("select mh_product_id,mh_template_id from mh_template_line where mh_product_id='" + prod_id + "' and mh_template_id='" + id + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                status=true;

            } else {
               status=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public void deleteProd_temp(int temp_id, int prod_id) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_template_line where mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean checkTemp_Name(String name, int id) {

        db=getReadableDatabase();
        boolean state = false;
        try {
            cursor = db.rawQuery("select name from mh_template where name='" + name + "' and not mh_template_id='" + id + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                state=true;
            } else{
                state= false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return state;
    }
    public void update_temp(int id,String name, int f_lead,int t_lead,String recipient,String updated,String updated_by,String sku) {
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_template set value='"+name+"',name='"+name+"', f_leaddays='"+f_lead+"',t_leaddays='"+t_lead+"',send_to='"+recipient+"',updated='"+updated+"',updated_by='"+updated_by+"',isSKU='"+sku+"' where mh_template_id='"+id+"'");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void update_trans_temp(int trans_temp_id,int temp_id,int prod_id, int uom_id, double price,double qty, String isnega, String iscomputed,String updated, String updated_by,int fav,String isgrab) {
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_template_line set c_uom_id='"+uom_id+"',priceentered='"+price+"',quantity_def0='"+qty+"',isnegative='"+isnega+"',is_computed='"+iscomputed+"',updated='"+
                    updated+"',updated_by='"+updated_by+"',favorite='"+fav+"', isgrab='"+isgrab+"' where mh_transaction_template_id='"+trans_temp_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteTrans_item(int temp_id,int prod_id,String isgrab) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_template_line where  mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"' and isgrab='"+isgrab+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean check_uom(String uom) {
        db=getReadableDatabase();
        boolean state=false;
        try {


            curs = db.rawQuery("select name from c_uom where name='" + uom + "'", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                if (curs != null) {
                    curs.close();
                }
                state = true;
            } else state = false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }


    public List<Trans_line> getTrans_cont(int temp_id) {
        List<Trans_line> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {


            curs = db.rawQuery("SELECT mh_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,isnegative,is_computed,subtotal,priceentered_temporary,quantity_def0_temporary,changed,favorite,isgrab from mh_transaction_line_temporary where mh_template_id='"+temp_id+"'", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int id = curs.getInt(0);
                    String prod_name =this.getProduct_name(curs.getInt(1));
                    String uom=this.getUom_name1(curs.getInt(2));
                    double price=curs.getDouble(3);
                    double qty=curs.getDouble(4);
                    String isnegative=curs.getString(5);
                    String iscomputed=curs.getString(6);
                    double subtotal=curs.getDouble(7);
                    double price_temp=curs.getDouble(8);
                    double qty_temp=curs.getDouble(9);
                    String change=curs.getString(10);
                    int fav=curs.getInt(11);
                    String isgrab=curs.getString(12);

                    Trans_line trans_cont = new Trans_line(id,prod_name,uom,price,qty,isnegative,iscomputed,qty_temp,price_temp,change,fav,isgrab);

                    content.add(trans_cont);


                } while (curs.moveToNext());

            }
            if (curs!= null){
                curs.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


    public List<Trans_line> getTrans_cont0(int temp_id,String text) {
        List<Trans_line> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        String prod_name="No Result Found";
        String uom="";
        double price=0.0;
        String isnegative="";
        String iscomputed="";
        int id=0;
        double qty=0;
        double qty_temp=0;
        double price_temp=0.0;
        String change="";
        double subtotal=0.0;
        String isgrab="";
        try {

            cursor=db.rawQuery("select mh_product_id from m_product where name like '%"+text+"%'",null);
            if(cursor.moveToFirst()) {
                do {
                    int ids = cursor.getInt(0);

                    curs = db.rawQuery("SELECT mh_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,isnegative,is_computed,subtotal,priceentered_temporary,quantity_def0_temporary,changed,favorite,isgrab from mh_transaction_line_temporary where mh_template_id='" + temp_id + "' and mh_product_id='"+ids+"'", null);
                    curs.moveToFirst();
                    if (curs.getCount() > 0) {
                        do {
                            id = curs.getInt(0);
                            prod_name = getProduct_name(curs.getInt(1));
                            uom = getUom_name1(curs.getInt(2));
                            price = curs.getDouble(3);
                            qty = curs.getDouble(4);
                            isnegative = curs.getString(5);
                            iscomputed = curs.getString(6);
                            subtotal = curs.getDouble(7);
                            price_temp = curs.getDouble(8);
                            qty_temp = curs.getDouble(9);
                            change = curs.getString(10);
                            int fav=curs.getInt(11);
                            isgrab=curs.getString(12);

                            Trans_line trans_cont = new Trans_line(id, prod_name, uom, price, qty, isnegative, iscomputed, qty_temp, price_temp, change,fav,isgrab);

                            content.add(trans_cont);


                        } while (curs.moveToNext());


                    }else {

                    }
                    if (curs!= null){
                        curs.close();
                    }
                } while (cursor.moveToNext());
            }else{
                Trans_line trans_cont = new Trans_line(id, prod_name, uom, price, qty, isnegative, iscomputed, qty_temp, price_temp, change,0,isgrab);

                content.add(trans_cont);
            }
            if (cursor!= null){
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }
    public List<Trans_line> getTrans_cont1(int id1) {
        List<Trans_line> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        String prod_name="No Result Found";
        String uom="";
        double price=0.0;
        String isnegative="";
        String iscomputed="";
        int id=0;
        double qty=0.0;
        double qty_temp=0.0;
        double price_temp=0.0;
        String change="";
        String isgrab="";
        try {
                    curs = db.rawQuery("SELECT mh_delivery_report_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,isnegative,iscomputed,quantity_def0_temporary,priceentered_temporary,changed,isgrab from mh_delivery_report_line where mh_delivery_report_template_id='"+id1+"'", null);
                    curs.moveToFirst();
                    if (curs.getCount() > 0) {
                        do {
                            id = curs.getInt(0);
                            prod_name =this.getProduct_name(curs.getInt(1));
                            uom=this.getUom_name1(curs.getInt(2));
                            price=curs.getDouble(3);
                            qty=curs.getDouble(4);
                            isnegative=curs.getString(5);
                            iscomputed=curs.getString(6);
                            qty_temp=curs.getDouble(7);
                            price_temp=curs.getDouble(8);

                            change=curs.getString(9);
                            isgrab=curs.getString(10);

                            Trans_line trans_cont = new Trans_line(id,prod_name,uom,price,qty,isnegative,iscomputed,qty_temp,price_temp,change,0,isgrab);

                            content.add(trans_cont);


                        } while (curs.moveToNext());

                    }else{

                    }


            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public List<Trans_line> getTrans_cont2(int id1,String text) {
        List<Trans_line> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        String prod_name="No Result Found";
        String uom="";
        double price=0.0;
        String isnegative="";
        String iscomputed="";
        int id=0;
        double qty=0.0;
        double qty_temp=0.0;
        double price_temp=0.0;
        String change="";
        String isgrab="";
        try {

            cursor=db.rawQuery("select mh_product_id from m_product where name like '%"+text+"%'",null);
            if(cursor.moveToFirst()) {
                do{
                    int ids=cursor.getInt(0);
                    curs = db.rawQuery("SELECT mh_delivery_report_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,isnegative,iscomputed,quantity_def0_temporary,priceentered_temporary,changed,isgrab from mh_delivery_report_line where mh_delivery_report_template_id='"+id1+"' and mh_product_id='"+ids+"'", null);
                    curs.moveToFirst();
                    if (curs.getCount() > 0) {
                        do {
                            id = curs.getInt(0);
                            prod_name =this.getProduct_name(curs.getInt(1));
                            uom=this.getUom_name1(curs.getInt(2));
                            price=curs.getDouble(3);
                            qty=curs.getDouble(4);
                            isnegative=curs.getString(5);
                            iscomputed=curs.getString(6);
                            qty_temp=curs.getDouble(7);
                            price_temp=curs.getDouble(8);
                            change=curs.getString(9);
                            isgrab=curs.getString(10);

                            Trans_line trans_cont = new Trans_line(id,prod_name,uom,price,qty,isnegative,iscomputed,qty_temp,price_temp,change,0,isgrab);

                            content.add(trans_cont);


                        } while (curs.moveToNext());

                    }else{

                    }

                    if (curs!= null){
                        curs.close();
                    }

                }while (cursor.moveToNext());
            }else{
                Trans_line trans_cont = new Trans_line(id,prod_name,uom,price,qty,isnegative,iscomputed,qty_temp,price_temp,change,0,isgrab);

                content.add(trans_cont);
            }

            if (cursor!= null){
                cursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public void insertPartial_trans_line(int transac_id,int product_id,int uom_id,double price,int qty,double subtotal,String isnegative,String is_computed,String created,String created_by,String isactive){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_transaction_line_temporary(mh_transaction_id,mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,isnegative,is_computed,created,created_by,isactive) " +
                    " values('"+transac_id+"','"+product_id+"','"+uom_id+"','"+price+"','"+qty+"','"+subtotal+"','"+isnegative+"','"+is_computed+"','"+created+"','"+created_by+"','"+isactive+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert_transaction_line(int transact_id,String document_num,int temp_id) {
        try{
            db=getWritableDatabase();
            curs=db.rawQuery("select mh_product_id,c_uom_id,priceentered_temporary,quantity_def0_temporary,subtotal,isnegative,created,created_by,isactive,changed from mh_transaction_line_temporary where mh_template_id='"+temp_id+"' and changed='Y' order by mh_transaction_id asc",null);
            curs.moveToFirst();
            if(curs.getCount()>0){
                do {

                    int product_id = curs.getInt(0);
                    int uom_id = curs.getInt(1);
                    double price = curs.getDouble(2);
                    double qty = curs.getDouble(3);
                    double subtotal = curs.getDouble(4);
                    String isnegative=curs.getString(5);
                    String created = curs.getString(6);
                    String created_by = curs.getString(7);
                    String isactive = curs.getString(8);
                    String changed = curs.getString(9);
                    db.execSQL("insert into mh_transaction_line(mh_transaction_id,document_num,mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,isnegative,created,created_by,isactive,changed) " +
                            "values('"+transact_id+"','" +document_num+"','"+product_id + "','" + uom_id + "','" + price + "','" + qty + "','" + subtotal + "','"+isnegative+"','"  + created + "','" + created_by + "','" + isactive + "','"+changed+"')");
                }while(curs.moveToNext());
            }else{

            }
            if (curs!= null){
                curs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*public void update_trans_temp(){
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_template_line set quantity_def0=0");
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    /*public boolean checktemp(int id){
        boolean status=false;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select use_leaddays from mh_template where mh_template_id='"+id+"'",null);
            cursor.moveToFirst();
            if(cursor.getString(0).equals("N")){
                status=false;
            }else{
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }*/
    public void update_trans_temp1(int transact_id,int temp_id,double quantity,int prod,double price,String subtotal,String created,String created_by,String changed,String isgrab){
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_transaction_line_temporary set quantity_def0_temporary='"+quantity+"',priceentered_temporary='"+price+"',subtotal='"+subtotal+"', created='"+created+"',created_by='"+created_by+"',changed='"+changed+"' where mh_template_id='"+temp_id+"' and mh_product_id='"+prod+"' and isgrab='"+isgrab+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public double getGross(int temp){
        double gross=0.0;
        try{
            db=getReadableDatabase();
            curs=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='Y' and isnegative='N'",null);
            curs.moveToFirst();
            gross=curs.getDouble(0);
            if (curs!= null){
                curs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return gross;
    }
    public double getCoh(int temp){
        double coh=0.0;
        double sub;
        try{
            double gross=getGross(temp);
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M'  and (isnegative='SC' OR isnegative='SCD' OR isnegative='PWD')",null);
            cursor.moveToFirst();
            sub=cursor.getDouble(0);
            coh=gross-sub;
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return coh;
    }
    public double getCfd(int temp){
        double cfd=0.0;
        double sub;
        try{
            double gross=getGross(temp);
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M'  and (isnegative='SC' OR isnegative='SCD' OR isnegative='PWD' or isnegative='Z')",null);
            cursor.moveToFirst();
            sub=cursor.getDouble(0);
            cfd=gross-sub;
            cfd=getcfdcfd(cfd,temp);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cfd;
    }

    public double getcfdcfd(double net1,int temp){
        double cfd=0.0;
        double sub;
        try{
            db=getReadableDatabase();
            Cursor cursor1=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M'  and isnegative='X'",null);
            cursor1.moveToFirst();
            sub=cursor1.getDouble(0);
            cfd=net1+sub;
            if (cursor1!= null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cfd;
    }



    public double getExp(int temp){
        double exp=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M' and isnegative='Z'",null);
            cursor.moveToFirst();
            exp=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return exp;
    }

    public double getTc(int temp){
        double tc=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='N' and isnegative='TC'",null);
            cursor.moveToFirst();
            tc=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tc;
    }

    public double getTcr(int temp){
        double tcr=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='N' and isnegative='TCR'",null);
            cursor.moveToFirst();
            tcr=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tcr;
    }
    public double getR(int temp){
        double r=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='N' and isnegative='R'",null);
            cursor.moveToFirst();
            r=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return r;
    }
    public double getSc(int temp){
        double sc=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M' and isnegative='SC'",null);
            cursor.moveToFirst();
            sc=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sc;
    }

    public double getVat(int temp){
        double vat=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M' and isnegative='SCD'",null);
            cursor.moveToFirst();
            vat=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return vat;
    }
    public double getPwd(int temp){
        double pwd=0.0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp+"' and is_computed='M' and isnegative='PWD'",null);
            cursor.moveToFirst();
            pwd=cursor.getDouble(0);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return pwd;
    }

    public int getTransac_id(){
        int transac_id=0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select mh_transaction_id from mh_transaction",null);
            if(cursor.moveToLast()) {
                transac_id=cursor.getInt(0);
            }else{
                transac_id=0;
            }
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return transac_id;
    }


    public int getCount_branch(int branch_id) {
        int count = 0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select count(mh_branch_id) from mh_transaction where mh_branch_id='"+branch_id+"'",null);
            if(cursor.moveToFirst()) {
                count=cursor.getInt(0);
            }else{
                count=0;
            }
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public void insertTransaction(int temp_id, int branch_id, String doc_num, String mydate, double gross1, double scd,double vat,double pwd,double coh,double exp,double cfd,double tc,double tcr,double refund,String reference,String remarks,String filename,String choose, String created, String created_by, String isactive,String date_ref) {
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_transaction(mh_template_id,mh_branch_id,document_num,datetransaction,gross_sale,scd,svat,pwd,coh,total_exp,cash_dep,tc,tcr,refund,reference_num,remarks,filename,choosed,created,created_by,isactive,date_ref) " +
                    "values('"+temp_id+"','"+branch_id+"','"+doc_num+"','"+mydate+"','"+gross1+"','"+scd+"','"+vat+"','"+pwd+"','"+coh+"','"+exp+"','"+cfd+"','"+tc+"','"+tcr+"','"+refund+"','"+reference+"','"+remarks+"','"+filename+"','"+choose+"','"+created+"','"+created_by+"','"+isactive+"','"+date_ref+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clear_partial_trans_line(){
        try {
            db = getWritableDatabase();
            db.execSQL("delete from mh_transaction_line_temporary");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Emp_list> getEmplist() {
        List<Emp_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {



            curs = db.rawQuery("SELECT mh_employee_id,employee_code,name,email_address,password,isactive from mh_employee order by name", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int emp_id = curs.getInt(0);
                    int code=curs.getInt(1);
                    String name=curs.getString(2);
                    String email=curs.getString(3);
                    String pass=curs.getString(4);
                    String isactive=curs.getString(5);

                    Emp_list emp_list = new Emp_list(emp_id,code,name,email,pass,isactive);

                    content.add(emp_list);


                } while (curs.moveToNext());

            }else {
                Emp_list emp_list = new Emp_list(0, 0, "No registered employee yet", "","","");

                content.add(emp_list);
            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


    public List<Emp_list> getEmplist1(String text) {
        List<Emp_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {



            curs = db.rawQuery("SELECT mh_employee_id,employee_code,name,email_address,password,isactive from mh_employee where name like '%"+text+"%'", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int emp_id = curs.getInt(0);
                    int code=curs.getInt(1);
                    String name=curs.getString(2);
                    String email=curs.getString(3);
                    String pass=curs.getString(4);
                    String isactive=curs.getString(5);

                    Emp_list emp_list = new Emp_list(emp_id,code,name,email,pass,isactive);

                    content.add(emp_list);


                } while (curs.moveToNext());

            }else {
                Emp_list emp_list = new Emp_list(0, 0, "No Result Found", "","","");

                content.add(emp_list);
            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public boolean check_code(int code){
        db=getReadableDatabase();
        boolean status=false;
        try{
            cursor=db.rawQuery("select employee_code from mh_employee where employee_code='"+code+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                status=false;
            }else status= true;

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();

        return status;

    }

    public void insertEmployee(String name,int code,String email,String password,String created,String created_by,String isactive){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_employee(employee_code,value,name,email_address,password,created,created_by,isactive) " +
                    "values('"+code+"','"+name+"','"+name+"','"+email+"','"+password+"','"+created+"','"+created_by+"','"+isactive+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void deleteEmp(int emp_id) {

        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_employee where  mh_employee_id='"+emp_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateEmployee(String name,int code,String email,String password,String updated,String updated_by,String isactive,int emp_id) {
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_employee set employee_code='"+code+"', value='"+name+"',name='"+name+"',email_address='"+email+"',password='"+password+"',updated='"+updated+"',updated_by='"+updated_by+"',isactive='"+isactive+"' where  mh_employee_id='"+emp_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<Branch_list> getBranchlist() {
        List<Branch_list> content = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {


            curs = db.rawQuery("SELECT mh_branch_id,branch_name,branch_type,set_branch,isactive from mh_branch where not set_branch=1 order by branch_name asc ", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int branch_id = curs.getInt(0);
                    String name = curs.getString(1);
                    String branch_type = curs.getString(2);
                    int set_branch = curs.getInt(3);
                    String isactive = curs.getString(4);

                    Branch_list branch_list = new Branch_list(branch_id, name,branch_type,set_branch,isactive);

                    content.add(branch_list);


                } while (curs.moveToNext());

            } else {
                Branch_list branch_list = new Branch_list(0, "No Branch yet", "",0,"");

                content.add(branch_list);
            }
            if (curs != null) {
                curs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }

    public List<Branch_list> getBranchlist1(String text) {
        List<Branch_list> content = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {


            curs = db.rawQuery("SELECT mh_branch_id,branch_name,branch_type,set_branch,isactive from mh_branch where branch_name like '%"+text+"%' and not set_branch=1", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int branch_id = curs.getInt(0);
                    String name = curs.getString(1);
                    String branch_type = curs.getString(2);
                    int set_branch = curs.getInt(3);
                    String isactive = curs.getString(4);

                    Branch_list branch_list = new Branch_list(branch_id, name,branch_type,set_branch,isactive);

                    content.add(branch_list);


                } while (curs.moveToNext());

            } else {
                Branch_list branch_list = new Branch_list(0, "No Result Found", "",0,"");

                content.add(branch_list);
            }
            if (curs != null) {
                curs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }

    /*public void insertBranch(String branch,String type,String created,String created_by,String isactive){
        try{
            db=getWritableDatabase();
            db.execSQL("insert into mh_branch(value,branch_name,branch_type,set_branch,created,created_by,isactive) values('"+branch+"','"+branch+"','"+type+"',0,'"+created+"','"+created_by+"','"+isactive+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    public void insertBranch(String branch,String type,String created,String created_by){
        try{
            db=getWritableDatabase();
            db.execSQL("insert into mh_branch_import(value,branch_name,branch_type,created,created_by) values('"+branch+"','"+branch+"','"+type+"','"+created+"','"+created_by+"')");
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public void insertBranch1(String branch,String type,String created,String created_by){
        try{
            db=getWritableDatabase();
            db.execSQL("insert into mh_branch(value,branch_name,branch_type,created,created_by) values('"+branch+"','"+branch+"','"+type+"','"+created+"','"+created_by+"')");
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public void delete_Branch(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_branch_import");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void copyBranch(){
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select branch_name,branch_type,created,created_by from mh_branch_import",null);
            if(cursor.moveToFirst()){
                do{
                    db.execSQL("insert into mh_branch(value,branch_name,branch_type,created,created_by) values('"+cursor.getString(0)+"','"+cursor.getString(0)+"','"+cursor.getString(1)+"','"+cursor.getString(2)+"','"+cursor.getString(3)+"')");
                }while (cursor.moveToNext());

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }




    public void updateBranch(int branch_id) {
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_branch set set_branch=0");
            db.execSQL("update mh_branch set set_branch=1 where mh_branch_id='"+branch_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteBranch(int branch_id) {
        try{
            db=getWritableDatabase();
            db.execSQL("delete from mh_branch where mh_branch_id='"+branch_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteBranch1() {
        try{
            db=getWritableDatabase();
            db.execSQL("delete from mh_branch ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getBranch_type(){
        ArrayList<String>list=new ArrayList<String>();
        db=this.getReadableDatabase();
        db.beginTransaction();

        try{
            String selectQuery="select branch_type from mh_branch group by branch_type";
            cursor=db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String branch=cursor.getString(0);
                    list.add(branch);
                }
            }
            db.setTransactionSuccessful();
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    public boolean check_emp_log(int code, String pass) {
        boolean status=false;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select employee_code from mh_employee where employee_code='"+code+"' and password='"+pass+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                status=true;
            }else status=false;
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;

    }

    public String getEmp_name(String code) {

        db=getReadableDatabase();
        String name = null;
        try{
            cursor=db.rawQuery("select name from mh_employee where employee_code='"+code+"'",null);
            cursor.moveToFirst();
            name=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }

        cursor.close();
        return name;
    }

    public int getBranch_id() {

        db=getReadableDatabase();
        int id = 0;
        try{
            cursor=db.rawQuery("select mh_branch_id from mh_branch where set_branch=1",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }

        cursor.close();
        return id;
    }

    public String getBranch_name() {

        db=getReadableDatabase();
        String name = null;
        try{
            cursor=db.rawQuery("select branch_name from mh_branch where set_branch=1",null);
            cursor.moveToFirst();
            name=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();

        return name;
    }

    public String getbranch_name(int id) {

        db=getReadableDatabase();
        String name = null;
        try{
            cursor=db.rawQuery("select branch_name from mh_branch where mh_branch_id='"+id+"'",null);
            cursor.moveToFirst();
            name=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }

        cursor.close();

        return name;
    }







    public boolean check_branch(String branch_name) {
        boolean status=false;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select branch_name from mh_branch where branch_name='"+branch_name+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                status=true;
            }else status=false;

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;

    }

    public void update_branch(String branch, String type, String updated, String updated_by) {
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_branch set branch_type='"+type+"',updated='"+updated+"',updated_by='"+updated_by+"' where branch_name='"+branch+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update_branch1(int id,String branch, String type, String updated, String updated_by) {
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_branch set branch_name='"+branch+"',branch_type='"+type+"',updated='"+updated+"',updated_by='"+updated_by+"' where mh_branch_id='"+id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<File_list> getFilelist(String fdate,String tdate) {
        List<File_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {



            curs = db.rawQuery("SELECT doc_num,file_name,created_by,choosed from mh_log where date_ref between '"+fdate+"' and '"+tdate+"' group by doc_num order by date_req,created", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    String doc_num = curs.getString(0);
                    String filename=curs.getString(1);
                    String creator=curs.getString(2);
                    String choosed=curs.getString(3);

                    File_list file_list = new File_list(doc_num,filename,creator,choosed);

                    content.add(file_list);


                } while (curs.moveToNext());

            }else {
                File_list file_list = new File_list("", "List File", "", "");

                content.add(file_list);
            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


    public List<File_list> getTransaction_List(String fdate,String tdate) {
        List<File_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {



            curs = db.rawQuery("SELECT document_num,filename,created_by,choosed from mh_transaction where date_ref between '"+fdate+"' and '"+tdate+"' group by document_num order by datetransaction,created", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    String doc_num = curs.getString(0);
                    String filename=curs.getString(1);
                    String creator=curs.getString(2);
                    String choosed=curs.getString(3);

                    File_list file_list = new File_list(doc_num,filename,creator,choosed);

                    content.add(file_list);


                } while (curs.moveToNext());

            }else {
                File_list file_list = new File_list("", "List File", "", "");

                content.add(file_list);
            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


    public List<File_list> getDelivery_List(String fdate,String tdate) {
        List<File_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {



            curs = db.rawQuery("SELECT document_num,filename,created_by,choosed from mh_delivery_report where date_ref between '"+fdate+"' and '"+tdate+"' group by document_num order by datetransaction,created", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    String document_num = curs.getString(0);
                    String filename=curs.getString(1);
                    String creator=curs.getString(2);
                    String choosed=curs.getString(3);

                    File_list file_list = new File_list(document_num,filename,creator,choosed);

                    content.add(file_list);


                } while (curs.moveToNext());

            }else {
                File_list file_list = new File_list("", "List File", "", "");

                content.add(file_list);
            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    private String getTemp_Name(int id) {
        db=getReadableDatabase();
        String name="";
        try{
            curs=db.rawQuery("select name from mh_template where mh_template_id='"+id+"'",null);
            curs.moveToFirst();
            name=curs.getString(0);
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }

    public void updateTransaction(int transact_id,String choose){
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_transaction set choosed='"+choose+"' where mh_transaction_id='"+transact_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTemplate_files(String doc_num,String choose,String tablename){
        try{
            db=getWritableDatabase();
            db.execSQL("update '"+tablename+"' set choosed='"+choose+"' where document_num='"+doc_num+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int countSend(String table_name){
        int count=0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select count(distinct document_num) from '"+table_name+"' where choosed='Y'",null);
            cursor.moveToFirst();
            count=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (cursor!= null){
            cursor.close();
        }
        return count;
    }

    public String[] getFile_name(int count,String table_name){
        String[] filename=new String[count];
        int count1=0;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select filename from '"+table_name+"' where choosed='Y' group by filename order by filename",null);
            cursor.moveToFirst();
            do{
                filename[count1]=cursor.getString(0);
                count1++;

            }while(cursor.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }

        if (cursor!= null){
            cursor.close();
        }
        return filename;
    }

    public String getDoc_num1(String filename,String table_name){
        String doc_num="";
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select distinct document_num from '"+table_name+"' where filename='"+filename+"' and choosed='Y'",null);
            cursor.moveToFirst();
            do{
                doc_num=cursor.getString(0);
            }while(cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }

        if (cursor!= null){
            cursor.close();
        }
        return doc_num;
    }

    public String[] getContentToSend(String doc_num,String filename,String table_name){
        String[] info=new String[18];
        try{
            db=getReadableDatabase();
            Cursor cursor=db.rawQuery("select document_num,datetransaction,gross_sale,scd,svat,pwd,coh,total_exp,cash_dep,tc,tcr,refund,created_by,reference_num,remarks,locator,mh_branch_id,date_ref from '"+table_name+"' where document_num='"+doc_num+"' and filename='"+filename+"' and choosed='Y'",null);
            cursor.moveToFirst();

            info[0]=cursor.getString(0);
            info[1]=cursor.getString(1);
            info[2]=cursor.getString(2);
            info[3]=cursor.getString(3);
            info[4]=cursor.getString(4);
            info[5]=cursor.getString(5);
            info[6]=cursor.getString(6);
            info[7]=cursor.getString(7);
            info[8]=cursor.getString(8);
            info[9]=cursor.getString(9);
            info[10]=cursor.getString(10);
            info[11]=cursor.getString(11);
            info[12]=cursor.getString(12);
            info[13]=cursor.getString(13);
            info[14]=cursor.getString(14);
            info[15]=cursor.getString(15);
            info[16]=cursor.getString(16);
            info[17]=cursor.getString(17);
            if (cursor!= null){
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return info;
    }

    public String[] getContentToSend2(int transac_id){
        String[] info=new String[7];
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select mh_product_id,c_uom_id,priceentered,quantity_def0 from mh_transaction_line where mh_transaction_id='"+transac_id+"'",null);
            cursor.moveToFirst();

            info[0]=getProduct_name(cursor.getInt(0));
            info[1]=getUom_name1(cursor.getInt(1));
            info[2]=String.valueOf(cursor.getDouble(2));
            info[3]=String.valueOf(cursor.getInt(3));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (cursor!= null){
            cursor.close();
        }
        return info;
    }

    public String getBranch_name1(int id) {

        db=getReadableDatabase();
        String name = null;
        try{
            cursor=db.rawQuery("select branch_name from mh_branch where mh_branch_id='"+id+"'",null);
            if(cursor.moveToFirst()){
                name=cursor.getString(0);
            }else{
                name=null;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();

        return name;
    }

    public void updateTransaction1(String tablename){
        try{
            db=getWritableDatabase();
            db.execSQL("update '"+tablename+"' set choosed='N'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTransaction2(){
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_transaction set choosed='N'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTransaction3(){
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_delivery_report set choosed='N'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String uom_name(int id){
        String uom_name="";
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select c_uom_id from m_product where mh_product_id='"+id+"'",null);
            cursor.moveToFirst();
            uom_name=getUom_name1(cursor.getInt(0));

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return uom_name;
    }

    public boolean checkleaddays(int computed,int template_id){
        boolean status=false;
        db=getReadableDatabase();
        try{

                cursor=db.rawQuery("select f_leaddays,t_leaddays from mh_template where mh_template_id='"+template_id+"'",null);
                cursor.moveToFirst();
                int f_leaddays=cursor.getInt(0);
                int t_leaddays=cursor.getInt(1);

                if (computed>=f_leaddays && computed<=t_leaddays) {
                    status=true;
                } else if(computed<=f_leaddays && computed>=t_leaddays){
                    status=true;
                }else {
                    status=false;
                }


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public boolean checkadmin(String pass){
        boolean status=false;
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select mh_admin_id from mh_admin where admin_password='"+pass+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                status=true;
            }else status=false;
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public String getAdmin(){
        String admin="";
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select admin_level from mh_admin where identifier=1",null);
            cursor.moveToFirst();
            admin=cursor.getString(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return admin;
    }

    public void update_admin(String pass){
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_admin set identifier=1 where admin_password='"+pass+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateadmin(){
        try{
            db=getWritableDatabase();
            db.execSQL("update mh_admin set identifier=0");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String checkComp_Fra(){
        String branch_type="";
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select branch_type from mh_branch where set_branch=1",null);
            cursor.moveToFirst();
            if(cursor.getString(0).equals("Company-Owned")){
                branch_type="COM";
            }else if(cursor.getString(0).equals("Franchisee")){
                branch_type="FRA";
            }else if(cursor.getString(0).equals("Institutional")){
                branch_type="INS";
            }else if(cursor.getString(0).equals("HO Department")){
                branch_type="HOD";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return branch_type;
    }

    public String getBranchname(){
        String branch="";
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select branch_name from mh_branch where set_branch=1",null);
            cursor.moveToFirst();
            branch=cursor.getString(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return branch;
    }

    public int getactive_Admin(){
        int id=0;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select mh_admin_id from mh_admin where identifier=1",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    /*public List<Trans_line> getTrans_cont(int id1) {
        List<Trans_line> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {


            curs = db.rawQuery("SELECT mh_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,isnegative from mh_template_line where mh_template_id='"+id1+"' order by mh_product_id", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int id = curs.getInt(0);
                    String prod_name =this.getProduct_name(curs.getInt(1));
                    String uom=this.getUom_name1(curs.getInt(2));
                    double price=curs.getDouble(3);
                    int qty=curs.getInt(4);
                    String isnegative=curs.getString(5);

                    Trans_line trans_cont = new Trans_line(id,prod_name,uom,price,qty,isnegative);

                    content.add(trans_cont);


                } while (curs.moveToNext());

            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }
    */

    public List<Report_line> getReport_line(){
        List<Report_line> content=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        try {


            curs = db.rawQuery("select mh_template_id,doc_ref,datetransaction,gross_sale,net_sale,created_by,reference_num,remarks from mh_transaction ",null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int id = curs.getInt(0);
                    String prod_name =this.getProduct_name(curs.getInt(1));
                    String uom=this.getUom_name1(curs.getInt(2));
                    double price=curs.getDouble(3);
                    int qty=curs.getInt(4);
                    String isnegative=curs.getString(5);

                    Report_line rep_line = new Report_line();

                    content.add(rep_line);


                } while (curs.moveToNext());

            }
            if (curs!= null){
                curs.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public void insert_import_template(String route,String branch,int doc_num,String date,String product,String uom,double price,int qty,double subtotal,String isnegative){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_template_import(route,branch,doc_num,date_req,product,uom,price,qty,subtotal,isnegative) values('"+route+"','"+branch+"','"+doc_num+"','"+date+"','"+product+"','"+uom+"','"+price+"','"+qty+"','"+subtotal+"','"+isnegative+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int get_num_imp_temp(){
        db=getReadableDatabase();
        int count=0;
        try{
            cursor=db.rawQuery("select count(distinct doc_num) from mh_template_import",null);
            cursor.moveToFirst();
            count=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return count;
    }

    public String[] get_doc_num(int count){
        db=getReadableDatabase();
        int count1=0;
        String[] array=new String[count];
        try{
            cursor=db.rawQuery("select distinct doc_num from mh_template_import",null);
            cursor.moveToFirst();
            do{
                array[count1]=cursor.getString(0);
                count1++;
            }while (cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }


    public String[] get_info(String i) {
        db=getReadableDatabase();
        String[] array=new String[3];
        try{
            cursor=db.rawQuery("select route,branch,date_req from mh_template_import where doc_num='"+i+"'",null);
            cursor.moveToFirst();
            array[0]=cursor.getString(0);
            array[1]=cursor.getString(1);
            array[2]=cursor.getString(2);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return array;
    }


    public int getbranch_id(String bname) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_branch_id from mh_branch where branch_name='"+bname+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    public String checkComp_Fra1(String branchname){
        String type="";
        try{
            db=getReadableDatabase();
            cursor=db.rawQuery("select branch_type from mh_branch where branch_name='"+branchname+"'",null);
            cursor.moveToFirst();
            if(cursor.getString(0).equals("COMPANY")){
                type="COM";
            }else{
                type="FRA";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return type;
    }

    public void delete_for_import_temp(){
        try{
            db=getWritableDatabase();
            db.execSQL("delete from mh_template_import");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*public void insert_Trans_line(int id,String doc,String created,String created_by,String isactive){
        db=getReadableDatabase();
        int prod_id;
        int uom_id;
        try{
            curs=db.rawQuery("select product,uom,price,qty,subtotal,isnegative, from mh_template_import where doc_num='"+doc+"'",null);
            curs.moveToFirst();
            do{

                prod_id = get_prod_id(curs.getString(0));
                uom_id = getUom_id(curs.getString(1));
                insert_transaction_temp1(id,prod_id,uom_id,curs.getDouble(2),curs.getInt(3),curs.getString(5),created,created_by,isactive);


            }while (curs.moveToNext());


        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
    }*/

    public void insert_transaction_temp11(int temp_id,int prod_id,int uom_id,double price,String isnegative,String created,String created_by,String active,double qty,int fav,String iscomputed,String isgrab){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_template_line(mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,isnegative,created,created_by,isactive,favorite,is_computed,isgrab) " +
                    "values ('"+temp_id+"','"+prod_id+"','"+uom_id+"','"+qty+"','"+price+"','"+isnegative+"','"+created+"','"+created_by+"','"+active+"','"+fav+"','"+iscomputed+"','"+isgrab+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public int getCount_imp_temp(String num) {
        db=getReadableDatabase();
        int count=0;
        try{
            cursor=db.rawQuery("select count(doc_num) from mh_template_import where doc_num='"+num+"'",null);
            cursor.moveToFirst();
            count=cursor.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();

        return count;
    }

    public void insert_into_delivery_template(int branch_id,String name,String locator,String doc_num,String date_req,int prod_id,int uom_id,double price,double qty,double subtotal,String iscomputed,String isnegative,String created,String created_by,String isactive) {
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_delivery_report_import(mh_branch_id,value,name,document_num,date_req,locator,mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,isnegative,iscomputed,created,created_by,isactive) values('"+branch_id+"','"+name+"','"+name+"','"+doc_num+"','"+date_req+"','"+locator+"','"+prod_id+"','"+uom_id+"','"+price+"','"+qty+"','"+subtotal+"','"+isnegative+"','"+iscomputed+"','"+created+"','"+created_by+"','"+isactive+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int get_mh_delivery_template_id(String doc_num) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_delivery_report_template_id from mh_delivery_report_import where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    public void inset_into_delivery_line(int delivery_id, int prod_id, int uom_id, double price, int qty, double subtotal, String isnegative, String iscomputed, String created, String emp_name) {
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_delivery_report_line(mh_delivery_report_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,iscomputed,isnegative,created,created_by)" +
                    " values('"+delivery_id+"','"+prod_id+"','"+uom_id+"','"+price+"','"+qty+"','"+subtotal+"','"+iscomputed+"','"+isnegative+"','"+created+"','"+emp_name+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getDeliveryCount() {
        db=getReadableDatabase();
        int count=0;
        try{
            cursor=db.rawQuery("select distinct(document_num) from mh_delivery_report_import",null);
            cursor.moveToFirst();
            count=cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return count;
    }

    public String[] getDoc_num(int count){
        db=getReadableDatabase();
        String[] array=new String[count];
        int count1=0;
        try{
            curs=db.rawQuery("select distinct(document_num) from mh_delivery_report_import group by document_num",null);
            curs.moveToFirst();
            do{
                array[count1]=curs.getString(0);
                count1++;
            }while (curs.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return array;
    }

    public void insert_into_delivery_template1(int branch_id,String name,String doc_num,String date_req,String locator,String created,String created_by,String isactive) {

        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_delivery_report_template(mh_branch_id,value,name,document_num,date_req,locator,created,created_by,isactive) values('"+branch_id+"','"+name+"','"+name+"','"+doc_num+"','"+date_req+"','"+locator+"','"+created+"','"+created_by+"','"+isactive+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getDelivery_temp(String doc_num){
        db=getReadableDatabase();
        String[] array=new String[8];
        try{
            cursor=db.rawQuery("select mh_branch_id,name,document_num,date_req,locator,created,created_by,isactive from mh_delivery_report_import where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            array[0]=cursor.getString(0);
            array[1]=cursor.getString(1);
            array[2]=cursor.getString(2);
            array[3]=cursor.getString(3);
            array[4]=cursor.getString(4);
            array[5]=cursor.getString(5);
            array[6]=cursor.getString(6);
            array[7]=cursor.getString(7);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return array;
    }

    public int getDelivery_id(String doc_num) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_delivery_report_template1_id from mh_delivery_report_template where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();;
        return id;
    }

    public void insert_delivery_line(String doc_num, int delivery_id) {
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,isnegative,iscomputed,created,created_by,isactive,document_num from mh_delivery_report_import where document_num='"+doc_num+"'",null);

            cursor.moveToFirst();

            do{
                    boolean status=check_product_exist(doc_num,cursor.getInt(0));
                    if(!status){
                        try{
                            db.execSQL("update mh_delivery_report_line set mh_delivery_report_template_id='"+delivery_id+"', mh_product_id='"+cursor.getInt(0)+"',c_uom_id='"+cursor.getInt(1)+"',priceentered='"+cursor.getDouble(2)+"', quantity_def0='"+cursor.getDouble(3)+"',subtotal='"+cursor.getDouble(4)+"',isnegative='"+cursor.getString(5)+"',iscomputed='"+cursor.getString(6)+"', created='"+cursor.getString(7)+"',created_by='"+cursor.getString(8)+"',isactive='"+cursor.getString(9)+"',document_num='"+cursor.getString(10)+"',priceentered_temporary='"+cursor.getDouble(2)+"',quantity_def0_temporary='"+cursor.getDouble(3)+"' WHERE mh_product_id='"+cursor.getInt(0)+"' and document_num='"+doc_num+"'");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{
                        try{
                            db.execSQL("insert into mh_delivery_report_line(mh_delivery_report_template_id,mh_product_id,c_uom_id,priceentered,quantity_def0,subtotal,isnegative,iscomputed,created,created_by,isactive,document_num,priceentered_temporary,quantity_def0_temporary) values('"+delivery_id+"','"+cursor.getInt(0)+"','"+cursor.getInt(1)+"','"+cursor.getDouble(2)+"','"+cursor.getDouble(3)+"','"+cursor.getDouble(4)+"','"+cursor.getString(5)+"','"+cursor.getString(6)+"','"+cursor.getString(7)+"','"+cursor.getString(8)+"','"+cursor.getString(9)+"','"+cursor.getString(10)+"','"+cursor.getDouble(2)+"','"+cursor.getDouble(3)+"')");
                        }catch (Exception e){
                             e.printStackTrace();
                        }
                    }

            }while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }

        cursor.close();
    }

    public boolean check_product_exist(String doc_num,int id){
        db=getReadableDatabase();
        boolean status=false;
        try{
            curs=db.rawQuery("select mh_product_id from mh_delivery_report_line where mh_product_id='"+id+"' and document_num='"+doc_num+"'",null);
            if(curs.moveToFirst()){
                status=false;
            }else{
                status=true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return status;
    }

    public void delete_delivery(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete  from mh_delivery_report_import");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update_delivery_report_line(int temp_id, double quantity, int prod_id, double price, double subtotal,String updated,String updated_by,String changed) {
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_delivery_report_line set priceentered_temporary='"+price+"', quantity_def0_temporary='"+quantity+"',subtotal='"+subtotal+"', updated='"+updated+"',updated_by='"+updated_by+"', changed='"+changed+"' where mh_delivery_report_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double getgross(int id) {

        db=getReadableDatabase();
        double gross=0.0;
        try{
            curs=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='Y' and isnegative='N'",null);
            curs.moveToFirst();
            gross=curs.getDouble(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return gross;

    }

    public double getcoh(int id) {

        db=getReadableDatabase();
        double net=0.0;
        double sub;
        try{
            double gross=getgross(id);
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='Y' and (isnegative='SC' OR isnegative='SCD' or isnegative='PWD')",null);
            cursor.moveToFirst();
            sub=cursor.getDouble(0);
            net=gross-sub;


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public double getcfd(int id) {

        db=getReadableDatabase();
        double cfd=0.0;
        double sub;
        try{
            double gross=getgross(id);
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M'  and (isnegative='SC' OR isnegative='SCD' OR isnegative='PWD' or isnegative='Z')",null);
            cursor.moveToFirst();
            sub=cursor.getDouble(0);
            cfd=gross-sub;
            cfd=getcfdcfd1(cfd,id);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return cfd;
    }

    public double getcfdcfd1(double net1,int id){
        double cfd=0.0;
        double sub;
        try{
            db=getReadableDatabase();
            Cursor cursor1=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M' and isnegative='X'",null);
            cursor1.moveToFirst();
            sub=cursor1.getDouble(0);
            cfd=net1+sub;
            if (cursor1!= null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cfd;
    }

    public double getexp(int id) {

        db=getReadableDatabase();
        double exp=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M' and isnegative='Z'",null);
            cursor.moveToFirst();
            exp=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return exp;
    }

    public double getscd(int id) {

        db=getReadableDatabase();
        double scd=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M' and isnegative='SC'",null);
            cursor.moveToFirst();
            scd=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return scd;
    }

    public double getvat(int id) {

        db=getReadableDatabase();
        double scd=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M' and isnegative='SCD'",null);
            cursor.moveToFirst();
            scd=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return scd;
    }
    public double getpwd(int id) {

        db=getReadableDatabase();
        double pwd=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='M' and isnegative='PWD'",null);
            cursor.moveToFirst();
            pwd=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return pwd;
    }

    public double gettc(int id) {

        db=getReadableDatabase();
        double tc=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='N' and isnegative='TC'",null);
            cursor.moveToFirst();
            tc=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return tc;
    }

    public double gettcr(int id) {

        db=getReadableDatabase();
        double tcr=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='N' and isnegative='TCR'",null);
            cursor.moveToFirst();
            tcr=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return tcr;
    }

    public double getrefund(int id) {

        db=getReadableDatabase();
        double r=0.0;
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and iscomputed='N' and isnegative='R'",null);
            cursor.moveToFirst();
            r=cursor.getDouble(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return r;
    }

    public void insertTransaction_for_delivery(int temp_id, int branch_id, String doc_num, String mydate, double gross1, double scd,double vat,double pwd,double coh,double exp,double cash,double tc,double tcr,double refund,String reference,String remarks,String filename,String choose, String created, String created_by, String isactive,String date_ref,String locator) {
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_delivery_report(mh_delivery_report_template1_id,mh_branch_id,document_num,datetransaction,gross_sale,scd,svat,pwd,coh,total_exp,cash_dep,tc,tcr,refund,reference_num,remarks,filename,choosed,created,created_by,isactive,date_ref,locator) " +
                    "values('"+temp_id+"','"+branch_id+"','"+doc_num+"','"+mydate+"','"+gross1+"','"+scd+"','"+vat+"','"+pwd+"','"+coh+"','"+exp+"','"+cash+"','"+tc+"','"+tcr+"','"+refund+"','"+reference+"','"+remarks+"','"+filename+"','"+choose+"','"+created+"','"+created_by+"','"+isactive+"','"+date_ref+"','"+locator+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete_delivery_report(int delivery_template_id) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_delivery_report_template where mh_delivery_report_template1_id='"+delivery_template_id+"'");
            db.execSQL("delete from mh_delivery_report_line where mh_delivery_report_template_id='"+delivery_template_id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getLocator(String doc_num) {
        db=getReadableDatabase();
        String locator=null;
        try{
            cursor=db.rawQuery("select locator from mh_delivery_report_template where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            locator=cursor.getString(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return locator;
    }

    public void insert_into_template_files(int id,String temp_name, String doc_num, String branch, String filename, String reference1, String remarks, String locator, String date_req,String date_ref,String created, String created_by,double gsale,double scd,double svat,double pwd,double coh,double exp,double cfd, double tc,double tcref,double refund){
        db=getWritableDatabase();
        try{
            curs=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0_temporary,priceentered_temporary,subtotal,iscomputed,isnegative from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and changed='Y'",null);
            curs.moveToFirst();

            do{
                String prod_name=getProduct_name(curs.getInt(0));
                String uom_name=getUom_name1(curs.getInt(1));
                db.execSQL("insert into mh_log (name,doc_num,branch_name,file_name,ref_num,remarks,locator,product,uom,qty,price,subtotal,isnegative,is_computed,date_req,date_ref,created,created_by,gsale,scd,svat,pwd,coh,exp,cfd,tc,tcref,refund)" +
                        "values('"+temp_name+"','"+doc_num+"','"+branch+"','"+filename+"','"+reference1+"','"+remarks+"','"+locator+"','"+prod_name+"','"+uom_name+"','"+
                        curs.getDouble(2)+"','"+curs.getDouble(3)+"','"+curs.getDouble(4)+"','"+curs.getString(6)+"','"+
                        curs.getString(5)+"','"+date_req+"','"+date_ref+"','"+created+"','"+created_by+"','"+gsale+"','"+scd+"','"+svat+"','"+pwd+"','"+coh+"','"+exp+"','"+cfd+"','"+tc+"','"+tcref+"','"+refund+"')");

            }while (curs.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
    }

    public void update_delivery(int id,String temp_name, String doc_num, String branch, String filename, String reference1, String remarks, String locator, String date_req,String date_ref,String created, String created_by,double gsale,double scd,double svat,double pwd,double coh,double exp,double cfd, double tc,double tcref,double refund){
        db=getWritableDatabase();
        try{
            curs=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0,priceentered,subtotal,iscomputed,isnegative from mh_delivery_report_line where mh_delivery_report_template_id='"+id+"' and changed='Y'",null);
            curs.moveToFirst();

            do{
                String prod_name=getProduct_name(curs.getInt(0));
                String uom_name=getUom_name1(curs.getInt(1));
                db.execSQL("update mh_log set name='"+temp_name+"',doc_num='"+doc_num+"',branch_name='"+branch+"',file_name='"+filename+"',ref_num='"+reference1+"',remarks='"+remarks+"',locator='"+locator+"',product='"+prod_name+"',uom='"+uom_name+"'," +
                        "qty='"+curs.getDouble(2)+"',price='"+curs.getDouble(3)+"',subtotal='"+curs.getDouble(4)+"',isnegative='"+curs.getString(6)+"',is_computed='"+curs.getString(5)+"',date_req='"+date_req+"',date_ref='"+date_ref+"',updated='"+created+"',updated_by='"+created_by+"',gsale='"+gsale+"',scd='"+scd+"',svat='"+svat+"',pwd='"+pwd+"',coh='"+coh+"',exp='"+exp+"',cfd='"+cfd+"',tc='"+tc+"',tcref='"+tcref+"',refund='"+refund+"' where doc_num='"+doc_num+"' and product='"+prod_name+"'");

            }while (curs.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
    }


    public void insert_into_template_files1(int temp, String temp_name,String doc_num, String bname, String filename, String reference1, String remarks1, String s, String date_req, String filedate, String sendto,String created, String emp_name,double gsale,double scd,double svat,double pwd,double coh,double exp,double cfd, double tc,double tcref,double refund) {
        db=getWritableDatabase();
        try{
            curs=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0,priceentered,subtotal,isnegative,iscomputed from mh_transaction_line where mh_transaction_id='"+temp+"'  and changed='Y'",null);
            curs.moveToFirst();

            do{
                String prod_name=getProduct_name(curs.getInt(0));
                String uom_name=getUom_name1(curs.getInt(1));
                db.execSQL("insert into mh_log (name,doc_num,branch_name,file_name,ref_num,remarks,locator,product,uom,qty,price,subtotal,isnegative,is_computed,date_req,date_ref,sendto,created,created_by,gsale,scd,svat,pwd,coh,exp,cfd,tc,tcref,refund)" +
                        "values('"+temp_name+"','"+doc_num+"','"+bname+"','"+filename+"','"+reference1+"','"+remarks1+"','"+s+"','"+prod_name+"','"+uom_name+"','"+
                        curs.getDouble(2)+"','"+curs.getDouble(3)+"','"+curs.getDouble(4)+"','"+curs.getString(5)+"','"+curs.getString(6)+"','"+date_req+"','"+filedate+"','"+sendto+"','"+created+"','"+emp_name+"','"+gsale+"','"+scd+"','"+svat+"','"+pwd+"','"+coh+"','"+exp+"','"+cfd+"','"+tc+"','"+tcref+"','"+refund+"')");

            }while (curs.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
    }


    public void update_transaction(int temp, String temp_name,String doc_num, String bname, String filename, String reference1, String remarks1, String s, String date_req, String filedate, String sendto,String created, String emp_name,double gsale,double scd,double svat,double pwd,double coh,double exp,double cfd, double tc,double tcref,double refund){
        db=getWritableDatabase();
        try{
            curs=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0,priceentered,subtotal,isnegative,iscomputed from mh_transaction_line where mh_transaction_id='"+temp+"'  and changed='Y'",null);
            curs.moveToFirst();

            do{
                String prod_name=getProduct_name(curs.getInt(0));
                String uom_name=getUom_name1(curs.getInt(1));
                db.execSQL("update mh_log set name='"+temp_name+"',doc_num='"+doc_num+"',branch_name='"+bname+"',file_name='"+filename+"',ref_num='"+reference1+"',remarks='"+remarks1+"',locator='"+s+"',product='"+prod_name+"',uom='"+uom_name+"'," +
                        "qty='"+curs.getDouble(2)+"',price='"+curs.getDouble(3)+"',subtotal='"+curs.getDouble(4)+"',isnegative='"+curs.getString(6)+"',is_computed='"+curs.getString(5)+"',date_req='"+date_req+"',date_ref='"+filedate+"',sendto='"+sendto+"',updated='"+created+"',updated_by='"+emp_name+"',gsale='"+gsale+"',scd='"+scd+"',svat='"+svat+"',pwd='"+pwd+"',coh='"+coh+"',exp='"+exp+"',cfd='"+cfd+"',tc='"+tc+"',tcref='"+tcref+"',refund='"+refund+"' where doc_num='"+doc_num+"' and product='"+prod_name+"'");

            }while (curs.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
    }

    public int getTransation_id(int temp_id) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_transaction_id from mh_transaction where mh_template_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    public double getmy_gross(String doc_num,String tablename) {

        db=getReadableDatabase();
        double gross=0.0;
        try{

            curs=db.rawQuery("select sum(subtotal) from "+tablename+" where  document_num='"+doc_num+"' and iscomputed='Y' and isnegative='N'",null);

            curs.moveToFirst();
            gross=curs.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        curs.close();
        return gross;
    }

    public double getmy_coh(String doc_num,String tablename) {

        db=getReadableDatabase();
        double net=0.0;
        try{
            double gross=getmy_gross(doc_num,tablename);

            cursor=db.rawQuery("select sum(subtotal) from "+tablename+" where document_num='"+doc_num+"' and iscomputed='Y' and isnegative='Y'",null);

            cursor.moveToFirst();
            net=gross-cursor.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }




    public double getmy_net(String doc_num,String tablename) {

        db=getReadableDatabase();
        double net=0.0;
        try{
            double gross=getmy_gross(doc_num,tablename);

            cursor=db.rawQuery("select sum(subtotal) from "+tablename+" where document_num='"+doc_num+"' and iscomputed='Y' and (isnegative='Y' or isnegative='X' or isnegative='Z')",null);

            cursor.moveToFirst();
            net=gross-cursor.getDouble(0);
            net=getmy_netnet(net,doc_num,tablename);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public double getmy_netnet(double net1,String doc_num,String tablename){
        double net=0.0;
        double sub;
        try{
            db=getReadableDatabase();
            Cursor cursor1=db.rawQuery("select sum(subtotal) from "+tablename+" where document_num='"+doc_num+"' and iscomputed='Y' and isnegative='X'",null);
            cursor1.moveToFirst();
            sub=cursor1.getDouble(0);
            net=net1+sub;
            if (cursor1!= null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return net;
    }

    public double getmy_exp(String doc_num,String tablename) {

        db=getReadableDatabase();
        double net=0.0;
        try{

            cursor=db.rawQuery("select sum(subtotal) from "+tablename+" where document_num='"+doc_num+"' and iscomputed='Y' and isnegative='Z'",null);

            cursor.moveToFirst();
            net=cursor.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public double getmy_gross1(String doc_num) {

        db=getReadableDatabase();
        double gross=0.0;
        try{

            curs=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where  document_num='"+doc_num+"' and iscomputed='Y' and isnegative='N' ",null);

            curs.moveToFirst();
            gross=curs.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        curs.close();
        return gross;
    }

    public double getmy_net1(String doc_num) {

        db=getReadableDatabase();
        double net=0.0;
        try{
            double gross=getmy_gross1(doc_num);

            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where document_num='"+doc_num+"' and is_computed='Y' and isnegative='Y' or isnegative='X'",null);

            cursor.moveToFirst();
            net=gross-cursor.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }


    public double getmy_gross2(String doc_num) {

        db=getReadableDatabase();
        double gross=0.0;
        try{

            curs=db.rawQuery("select sum(subtotal) from mh_log where  doc_num='"+doc_num+"' and is_computed='Y' and isnegative='N' ",null);

            curs.moveToFirst();
            gross=curs.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        curs.close();
        return gross;
    }

    public double getmy_coh2(String doc_num) {

        db=getReadableDatabase();
        double net=0.0;
        try{
            double gross=getmy_gross2(doc_num);

            cursor=db.rawQuery("select sum(subtotal) from mh_log where doc_num='"+doc_num+"' and is_computed='Y' and isnegative='Y'",null);

            cursor.moveToFirst();
            net=gross-cursor.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public double getmy_net2(String doc_num) {

        db=getReadableDatabase();
        double net=0.0;
        try{
            double gross=getmy_gross2(doc_num);

            cursor=db.rawQuery("select sum(subtotal) from mh_log where doc_num='"+doc_num+"' and is_computed='Y' and (isnegative='Y' or isnegative='X' or isnegative='Z')",null);

            cursor.moveToFirst();
            net=gross-cursor.getDouble(0);
            net=getnetnet(net,doc_num);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public double getnetnet(double net1,String doc_num){
        double net=0.0;
        double sub;
        try{
            db=getReadableDatabase();
            Cursor cursor1=db.rawQuery("select sum(subtotal) from mh_log where doc_num='"+doc_num+"' and is_computed='Y' and isnegative='X'",null);
            cursor1.moveToFirst();
            sub=cursor1.getDouble(0);
            net=net1+sub;
            if (cursor1!= null){
                cursor1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return net;
    }



    public double getmy_exp2(String doc_num) {

        db=getReadableDatabase();
        double net=0.0;
        try{

            cursor=db.rawQuery("select sum(subtotal) from mh_log where doc_num='"+doc_num+"' and is_computed='Y' and isnegative='Z'",null);

            cursor.moveToFirst();
            net=cursor.getDouble(0);
        }catch ( Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return net;
    }

    public String getSendto(int temp_id) {
        db=getReadableDatabase();
        String sendto="";
        try{
            cursor=db.rawQuery("select send_to from mh_template where mh_template_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            sendto=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return sendto;
    }

    public String getSendto1(String filePaths) {
        db=getReadableDatabase();
        String sendto="";
        try{
            cursor=db.rawQuery("select sendto from mh_log where file_name='"+filePaths+"'",null);
            cursor.moveToFirst();
            sendto=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return sendto;
    }

    public String[] getView(String doc_num){
        db=getReadableDatabase();
        String[] array=new String[16];
        try{
            cursor = db.rawQuery("SELECT branch_name,date_req,created_by,ref_num,remarks,gsale,scd,svat,pwd,coh,exp,cfd,tc,tcref,refund,date_ref from mh_log where doc_num='"+doc_num+"'", null);
            cursor.moveToFirst();
            do{
                array[0]=cursor.getString(0);
                array[1]=cursor.getString(1);
                array[2]=cursor.getString(2);
                array[3]=cursor.getString(3);
                array[4]=cursor.getString(4);
                array[5]=cursor.getString(5);
                array[6]=cursor.getString(6);
                array[7]=cursor.getString(7);
                array[8]=cursor.getString(8);
                array[9]=cursor.getString(9);
                array[10]=cursor.getString(10);
                array[11]=cursor.getString(11);
                array[12]=cursor.getString(12);
                array[13]=cursor.getString(13);
                array[14]=cursor.getString(14);
                array[15]=cursor.getString(15);
            }while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return array;
    }

    /*public void copyprice(){
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_transaction_template_id,priceentered from mh_template_line ",null);
            cursor.moveToFirst();
            do{
                int id=cursor.getInt(0);
                double price=cursor.getDouble(1);
                double subtotal=0.0;
                db.execSQL("update mh_template_line set temporary_priceentered='"+price+"'  where mh_transaction_template_id='"+id+"'");

            }while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    /*public void copyTemplatetoTransactionline(int temp_id,String created,String created_by) {

        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0,priceentered,isnegative,is_computed from mh_template_line where mh_template_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            do{
                int prod_id=cursor.getInt(0);
                int uom_id=cursor.getInt(1);
                int qty=cursor.getInt(2);
                double price=cursor.getDouble(3);
                String isnegative=cursor.getString(4);
                String iscomputed=cursor.getString(5);
                db.execSQL("insert into mh_transaction_line_temporary(mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,priceentered_temporary,quantity_def0_temporary,isnegative,is_computed,created,created_by) values('"+temp_id+"','"+prod_id+"','"+uom_id+"','"+qty+"','"+price+"','"+price+"','"+qty+"','"+isnegative+"','"+iscomputed+"','"+created+"','"+created_by+"')");
            }while (cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }*/


    public void copyTemplatetoTransactionline() {

        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_product_id,c_uom_id,quantity_def0,priceentered,isnegative,is_computed,mh_template_id,favorite,isgrab from mh_template_line order by favorite desc",null);
            cursor.moveToFirst();
            do{
                int prod_id=cursor.getInt(0);
                int uom_id=cursor.getInt(1);
                double qty=cursor.getDouble(2);
                double price=cursor.getDouble(3);
                String isnegative=cursor.getString(4);
                String iscomputed=cursor.getString(5);
                int temp_id=cursor.getInt(6);
                int fav=cursor.getInt(7);
                String isgrab=cursor.getString(8);
                db.execSQL("insert into mh_transaction_line_temporary(mh_template_id,mh_product_id,c_uom_id,quantity_def0,priceentered,priceentered_temporary,quantity_def0_temporary,isnegative,is_computed,favorite,isgrab) values('"+temp_id+"','"+prod_id+"','"+uom_id+"','"+qty+"','"+price+"','"+price+"','"+qty+"','"+isnegative+"','"+iscomputed+"','"+fav+"','"+isgrab+"')");
            }while (cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }

    public boolean check_transactionline_temporary(){
        boolean status=false;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select * from mh_transaction_line_temporary",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                status=false;
            }else status=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public int getTrans_id(String doc_num) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_transaction_id from mh_transaction where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    public String getTemp_name(String document_num, String tablename2) {
        String tempname="";
        db=getReadableDatabase();
        try{
            if(tablename2.equals("mh_transaction")){
                cursor=db.rawQuery("select mh_template_id from "+tablename2+" where document_num='"+document_num+"'",null);
                cursor.moveToFirst();
                tempname=getTemp_Name(cursor.getInt(0));
            }else{
                cursor=db.rawQuery("select name from "+tablename2+" where document_num='"+document_num+"'",null);
                cursor.moveToFirst();
                tempname=cursor.getString(0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return tempname;
    }

    public boolean check_recipient(String tablename) {

        String id_name;
        String table;
        String fname;
        if(tablename.equals("mh_delivery_report")){
            id_name="mh_delivery_report_template1_id";
            table="mh_delivery_report_template";
            fname="filename";
        }else {
            id_name="mh_template_id";
            table="mh_template";
            fname="mh_template_id";
        }
        db=getReadableDatabase();
        boolean status=true;
        boolean status1;
        try{
            cursor=db.rawQuery("select "+id_name+" from "+tablename+" where choosed='Y' group by "+fname+" order by "+fname+"",null);
            cursor.moveToFirst();
            if(cursor.getCount()>1){
                int temp_id=cursor.getInt(0);
                while(cursor.moveToNext() && status==true){
                    status1=check_SendTo(temp_id,cursor.getInt(0),table,id_name);
                    if(status1){
                        status=true;
                    }else{
                        status=false;
                    }

                }


            }else if(cursor.getCount()==1){
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public boolean check_SendTo(int id1,int id2,String table,String id_name){
        boolean status=false;
        String[] send=new String[2];
        int count=0;
        db=getReadableDatabase();
        try{
            curs=db.rawQuery("select send_to from "+table+" where "+id_name+"='"+id1+"' or "+id_name+"='"+id2+"'",null);
            curs.moveToFirst();
            do{
                send[count]=curs.getString(0);
                count++;
            }while(curs.moveToNext());

            if(send[0].equals(send[1])){
                status=true;
            }else{
                status=false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return status;
    }

    public String getTemlpate_id(String tablename) {


        String id_name;
        String table;
        String sendto="";
        if(tablename.equals("mh_delivery_report")){
            id_name="mh_delivery_report_template1_id";
            table="mh_delivery_report_template";
        }else {
            id_name="mh_template_id";
            table="mh_template";
        }
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select "+id_name+" from "+tablename+" where choosed='Y' group by filename order by filename",null);
            cursor.moveToFirst();
            int temp_id=cursor.getInt(0);
            sendto=getSend(temp_id,table,id_name);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return sendto;
    }

    public String getSend(int id,String table,String id_name){
        String sendto="";
        db=getReadableDatabase();
        try{
            curs=db.rawQuery("select send_to from "+table+" where "+id_name+"='"+id+"'",null);
            curs.moveToFirst();
           sendto=curs.getString(0);


        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return sendto;
    }


    public boolean check_product() {
        db=getReadableDatabase();
        boolean status=false;
        try{
            cursor=db.rawQuery("select * from m_product",null);
            if(cursor.moveToFirst()){
                status=true;
            }else{
                status=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public boolean check_branches() {
        db = getReadableDatabase();
        boolean status = false;
        try {
            cursor = db.rawQuery("select * from mh_branch", null);
            if (cursor.moveToFirst()) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        cursor.close();
        return status;
    }
    public boolean check_branches1() {
        db = getReadableDatabase();
        boolean status = false;
        try {
            cursor = db.rawQuery("select * from mh_branch where set_branch=1", null);
            if (cursor.moveToFirst()) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        cursor.close();
        return status;
    }
    public void delete_delivery_reports() {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_delivery_report_import");
            db.execSQL("delete from mh_delivery_report_line");
            db.execSQL("delete from mh_delivery_report_template");
            db.execSQL("delete from mh_delivery_report");
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public boolean check_delivery(String doc_num) {
        boolean status=false;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select * from mh_log where doc_num='"+doc_num+"'",null);
            if(cursor.moveToFirst()){
                status=false;
            }else{
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return  status;
    }

    public int getDelivery_temp_id(String template) {
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select mh_delivery_report_template1_id from mh_delivery_report_template where name='"+template+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return id;
    }

    public String getDocument_num(int temp_id) {
        db=getReadableDatabase();
        String doc_num="";
        try{
            cursor=db.rawQuery("select document_num from mh_delivery_report_template where mh_delivery_report_template1_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            doc_num=cursor.getString(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return doc_num;
    }

    public String getbranch(String toString) {
        db=getReadableDatabase();
        String name=null;
        try{
            int id=branch_id(toString);
            name=getbranch_name(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }

    public int branch_id(String template_nane){
        db=getReadableDatabase();
        int id=0;
        try{
            curs=db.rawQuery("select branch_id from mh_delivery_report_template where name='"+template_nane+"'",null);
            curs.moveToFirst();
            id=curs.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return id;
    }

    public String getDate_req(int temp_id) {
        db=getReadableDatabase();
        String date_req="";
        try{
            cursor=db.rawQuery("select date_req from mh_delivery_report_template where mh_delivery_report_template1_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            date_req=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return date_req;
    }

    public void setDefault(int temp_id) {
        db=getWritableDatabase();
        try{

            cursor=db.rawQuery("select priceentered,quantity_def0,mh_product_id from mh_transaction_line_temporary where mh_template_id='"+temp_id+"'",null);
            cursor.moveToFirst();
            do{
                db.execSQL("update mh_transaction_line_temporary set priceentered_temporary='"+cursor.getDouble(0)+"',quantity_def0_temporary='"+cursor.getInt(1)+"',subtotal=0,changed='N' where mh_template_id='"+temp_id+"' and mh_product_id='"+cursor.getInt(2)+"'");
            }while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }

    public String getdocument_number(String file_name) {
        db=getReadableDatabase();
        String doc="";
        try{
            cursor=db.rawQuery("select distinct doc_num from mh_log where file_name='"+file_name+"'",null);
            cursor.moveToFirst();
            doc=cursor.getString(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return doc;
    }

    public boolean check_admin(){
        boolean status=false;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select mh_admin_id from mh_admin where identifier=1",null);
            cursor.moveToFirst();
            if(cursor.getInt(0)==1){
                status=true;
            }else{
                status=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;

    }

    public boolean check_admin_via_pass(String pass){
        boolean status=false;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select mh_admin_id from mh_admin where admin_password='"+pass+"'",null);
            cursor.moveToFirst();
            if(cursor.getInt(0)==1){
                status=true;
            }else{
                status=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }


    public void update_admin_pass(String toString,int id) {
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_admin set admin_password='"+toString+"' where mh_admin_id='"+id+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getHeader_delivery(int i) {

        String transaction,id;
        if(i==0){
            transaction="mh_delivery_report";
            id="mh_delivery_report_template1_id";
        }else{
            transaction="mh_transaction";
            id="mh_template_id";
        }
        db=getReadableDatabase();
        String[] ar=new String[18];
        try{
            cursor=db.rawQuery("select "+id+",document_num,datetransaction,gross_sale,scd,svat,pwd,coh,total_exp,cash_dep,tc,tcr,refund,created_by,reference_num,remarks,locator,date_ref from "+transaction+" where choosed='Y'",null);
            cursor.moveToFirst();
            ar[0]=getTemplate_name(cursor.getInt(0),i);
            ar[1]=cursor.getString(1);
            ar[2]=cursor.getString(2);
            ar[3]=cursor.getString(3);
            ar[4]=cursor.getString(4);
            ar[5]=cursor.getString(5);
            ar[6]=cursor.getString(6);
            ar[7]=cursor.getString(7);
            ar[8]=cursor.getString(8);
            ar[9]=cursor.getString(9);
            ar[10]=cursor.getString(10);
            ar[11]=cursor.getString(11);
            ar[12]=cursor.getString(12);
            ar[13]=cursor.getString(13);
            ar[14]=cursor.getString(14);
            ar[15]=cursor.getString(15);
            ar[16]=cursor.getString(16);
            ar[17]=cursor.getString(17);


        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return ar;
    }


    public String[] getdelivery(String doc_num) {
        db=getReadableDatabase();
        String[] ar=new String[4];
        try{
            cursor=db.rawQuery("select name,date_req,created_by,locator from mh_delivery_report_template where document_num='"+doc_num+"'",null);
            cursor.moveToFirst();
            ar[0]=cursor.getString(0);
            ar[1]=cursor.getString(1);
            ar[2]=cursor.getString(2);
            ar[3]=cursor.getString(3);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return ar;
    }

    private String getTemplate_name(int temp_id, int i) {
        String template,id,name="";
        if(i==0){
            template="mh_delivery_report_template";
            id="mh_delivery_report_template1_id";
        }else{
            template="mh_template";
            id="mh_template_id";
        }
        db=getReadableDatabase();
        try{
            curs=db.rawQuery("select name from "+template+" where "+id+"='"+temp_id+"'",null);
            curs.moveToFirst();
            name=curs.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        curs.close();
        return name;
    }

    public double getsubGross(int temp_id) {
        double gross=0.00;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_transaction_line_temporary where mh_template_id='"+temp_id+"'and is_computed='Y' and isnegative='N'",null);
            cursor.moveToFirst();
            gross=cursor.getDouble(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return gross;
    }

    public double getsubGross1(int temp_id) {
        double gross=0.00;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select sum(subtotal) from mh_delivery_report_line where mh_delivery_report_template_id='"+temp_id+"'and iscomputed='Y' and isnegative='N'",null);
            cursor.moveToFirst();
            gross=cursor.getDouble(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return gross;
    }

    public void deleteProd(String product) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from m_product where name='"+product+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteAllProd() {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from m_product");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getTemp_sku(String temp_name) {

        db=getReadableDatabase();
        String sku="";
        try{
            cursor=db.rawQuery("select isSKU from mh_template where name='"+temp_name+"'",null);
            cursor.moveToFirst();
            sku=cursor.getString(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return sku;
    }

    public void insert_partial_bom(int prod_id,int prod_uom_id,int bom_prod_id,int bom_uom_id,double price){
        db=getReadableDatabase();
        try{
            db.execSQL("insert into mh_bom (mh_prod_id,prod_uom_id,bom_prod_id,bom_uom_id,bom_price) values('"+prod_id+"','"+prod_uom_id+"','"+bom_prod_id+"','"+bom_uom_id+"','"+price+"')");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert_bom(String created,String created_by,char isactive){
        db=getWritableDatabase();
        try{
            cursor=db.rawQuery("select mh_prod_id,prod_uom,bom_prod_id,bom_uom_id,price from mh_bom_temporary",null);
            if(cursor.moveToFirst()){
                do{
                    db.execSQL("insert into mh_bom (mh_prod_id,prod_uom_id,bom_prod_id,bom_uom_id,bom_price,created,created_by,isactive) values('"+cursor.getInt(0)+"','"+cursor.getInt(1)+"','"+cursor.getInt(2)+"','"+cursor.getInt(3)+"','"+cursor.getDouble(4)+"','"+created+"','"+created_by+"','"+isactive+"')");
                }while (cursor.moveToNext());
            }
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete_partial_bom(){
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_bom_temporary");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Variance_list> getVariance_list() {
        List<Variance_list> my_list=new ArrayList<>();
        db= getReadableDatabase();

        try{
            curs= db.rawQuery("SELECT distinct mh_prod_id,prod_uom_id from mh_bom", null);
            if(curs.moveToFirst()){

                do{

                    String product=getProduct_name(curs.getInt(0));
                    String prod_uom=getUom_name1(curs.getInt(1));
                    Variance_list mylist= new Variance_list(product,prod_uom,curs.getInt(0));
                    my_list.add(mylist);


                }while(curs.moveToNext());
            }

        }catch (Exception ex){

            ex.printStackTrace();


        }
        curs.close();

        return my_list;

    }

    public List<Variance_list> getVariance_list1(String text) {

        List<Variance_list> my_list=new ArrayList<>();
        db= getReadableDatabase();

        try{
            curs= db.rawQuery("SELECT mh_product_id from m_product where name like '%"+text+"%'", null);
            if(curs.moveToFirst()){
                do{
                    cursor= db.rawQuery("SELECT distinct mh_prod_id,prod_uom_id from mh_bom where mh_prod_id='"+curs.getInt(0)+"'", null);
                    if(cursor.moveToFirst()){
                            String product=getProduct_name(cursor.getInt(0));
                            String prod_uom=getUom_name1(cursor.getInt(1));
                            Variance_list mylist= new Variance_list(product,prod_uom,cursor.getInt(0));
                            my_list.add(mylist);
                    }
                    cursor.close();


                }while(curs.moveToNext());
            }else {
                Variance_list mylist= new Variance_list("No Result Found","",0);
                my_list.add(mylist);
            }

        }catch (Exception ex){

            ex.printStackTrace();


        }
        curs.close();

        return my_list;

    }


    public List<Details_list> getDetails_list(int id) {

        List<Details_list> my_list=new ArrayList<>();
        db= getReadableDatabase();

        try{
            curs= db.rawQuery("SELECT bom_prod_id,bom_uom_id,bom_price from mh_bom where mh_prod_id='"+id+"'", null);
            if(curs.moveToFirst()){

                do{

                    String product=getProduct_name(curs.getInt(0));
                    String prod_uom=getUom_name1(curs.getInt(1));
                    double qty=curs.getDouble(2);
                    Details_list mylist= new Details_list(product,prod_uom,qty);
                    my_list.add(mylist);


                }while(curs.moveToNext());
            }

        }catch (Exception ex){

            ex.printStackTrace();


        }
        curs.close();

        return my_list;
    }

    public int getC_uom_id(int prod){
        db=getReadableDatabase();
        int id=0;
        try{
            cursor=db.rawQuery("select c_uom_id from m_product where mh_product_id='"+prod+"'",null);
            cursor.moveToFirst();
            id=cursor.getInt(0);
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }


    public void delete_bom() {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_bom");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getReq_count(int id) {
        db=getReadableDatabase();
        int count=0;
        try{
            cursor=db.rawQuery("select count(mh_request_id) from mh_request where mh_branch_id='"+id+"'",null);
            if(cursor.moveToFirst()){
                count=cursor.getInt(0);
            }else{
                count=0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return count;
    }

    public  void insert_request(int branch_id,String doc_num,String req_type,String summary,String created,String created_by,String isactive){
        db=getWritableDatabase();
        try{
            db.execSQL("insert into mh_request (mh_branch_id,doc_num,request_type,summary,created,created_by) values('"+branch_id+"','"+doc_num+"','"+req_type+"','"+summary+"','"+created+"','"+created_by+"')");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteTransaction_line(String fdate,String tdate) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_transaction_line where created between '"+fdate+"' and '"+tdate+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteTransaction(String fdate,String tdate) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_transaction where created between '"+fdate+"' and '"+tdate+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteLog(String fdate,String tdate) {
        db=getWritableDatabase();
        try{
            db.execSQL("delete from mh_log where created between '"+fdate+"' and '"+tdate+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getTrans_line_count(String fdate,String tdate) {
        int line=0;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select count(created) from mh_transaction_line where created between '"+fdate+"' and '"+tdate+"'",null);
            if(cursor.moveToFirst()){
                line=cursor.getInt(0);
            }else{
                line=0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return line;
    }

    public int getTrans_count(String fdate,String tdate) {
        int line=0;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select count(created) from mh_transaction where created between '"+fdate+"' and '"+tdate+"'",null);
            if(cursor.moveToFirst()){
                line=cursor.getInt(0);
            }else{
                line=0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return line;
    }

    public int getLog_count(String fdate,String tdate) {
        int line=0;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select count(created) from mh_log where created between '"+fdate+"' and '"+tdate+"'",null);
            if(cursor.moveToFirst()){
                line=cursor.getInt(0);
            }else{
                line=0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return line;
    }

    public int gettrans_temp_id(int prod_id, int id, String isgrab) {
        db=getReadableDatabase();
        int temp_id=0;
        try{
            cursor=db.rawQuery("select mh_transaction_template_id from mh_template_line where mh_template_id='"+id+"' and mh_product_id='"+prod_id+"' and isgrab='"+isgrab+"'",null);
            cursor.moveToFirst();
            temp_id=cursor.getInt(0);

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return temp_id;

    }

    public int getNum_count(int temp_id, int prod_id) {
        db=getReadableDatabase();
        int count=0;
        try{
            cursor=db.rawQuery("select count(mh_transaction_template_id) from mh_template_line where mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'",null);
            if(cursor.moveToFirst()){
                count=2;
            }else{
                count=1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public String getGrab(int temp_id, int prod_id) {
        String grab="N";
        db=getReadableDatabase();
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        return grab;
    }

    public boolean check_exist(int temp_id, int prod_id){
        boolean status=false;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("SELECT isgrab from mh_template_line where mh_template_id='"+temp_id+"' and mh_product_id='"+prod_id+"'",null);
            if(cursor.moveToFirst()){
                if(cursor.getString(0).equals("N")){
                    status=true;
                }else {
                    status=false;
                }
            }else{
                status=false;
            }

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    public int getTransaction_sales(){
        int number_sales=0;
        db=getReadableDatabase();
        try{
            cursor=db.rawQuery("select count(mh_transaction_id) from mh_transaction where mh_temlplate_id=1",null);

        }catch (Exception e){
            e.printStackTrace();
        }
        return number_sales;
    }

    public List<Performance_list> getTrans_sales() {
        List<Performance_list> content=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        try {


            curs = db.rawQuery("select created,gross_sale from mh_transaction where mh_template_id=1",null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {


                    Performance_list performance_list = new Performance_list(curs.getString(0),curs.getFloat(1));

                    content.add(performance_list);


                } while (curs.moveToNext());

            }
            if (curs!= null){
                curs.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


    /*public List<Prod_list> getProdlist() {
        List<Prod_list> content = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {


            curs = db.rawQuery("SELECT mh_branch_id,branch_name,branch_type,set_branch,isactive from mh_branch where not set_branch=1 order by branch_name asc ", null);
            curs.moveToFirst();
            if (curs.getCount() > 0) {
                do {
                    int branch_id = curs.getInt(0);
                    String name = curs.getString(1);
                    String branch_type = curs.getString(2);
                    int set_branch = curs.getInt(3);
                    String isactive = curs.getString(4);

                    Branch_list branch_list = new Branch_list(branch_id, name,branch_type,set_branch,isactive);

                    content.add(branch_list);


                } while (curs.moveToNext());

            } else {
                Branch_list branch_list = new Branch_list(0, "No Branch yet", "",0,"");

                content.add(branch_list);
            }
            if (curs != null) {
                curs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

     */

    public ArrayList<BarEntry>  getYvalue(){
        ArrayList<BarEntry>list=new ArrayList<BarEntry>();
        db=this.getReadableDatabase();
        db.beginTransaction();
        int count=0;
        try{

                String selectQuery="select gross_sale from mh_transaction where mh_template_id=1";
                cursor=db.rawQuery(selectQuery,null);
                if(cursor.getCount()>0){
                    while (cursor.moveToNext()){
                            double gross=cursor.getDouble(0);
                            list.add(new BarEntry(count, (float) gross));
                            count++;
                        }
                    }

                db.setTransactionSuccessful();

            cursor.close();


        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    public boolean check_recipient1(String tablename3) {
        db=getReadableDatabase();
        boolean status=false;
        try{
            cursor=db.rawQuery("select send_to from '"+tablename3+"'",null);
            if(cursor.moveToFirst()){
                if(!cursor.getString(0).equals("mhsoar.inventory@gmail.com")){
                    status=false;
                }else{
                    status=true;
                }

            }
            else{
                status=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        return status;
    }

    public void update_recipient(){
        db=getWritableDatabase();
        try{
            db.execSQL("update mh_delivery_report_template set send_to='mhsoar.inventory@gmailcom'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
