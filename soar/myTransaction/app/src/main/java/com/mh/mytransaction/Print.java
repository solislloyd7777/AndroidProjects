package com.mh.mytransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class Print extends Activity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    DatabaseHelper dh;

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.activity_print);
        mScan = (Button) findViewById(R.id.scan);
        mPrint = (Button) findViewById(R.id.print);
        mDisc = (Button) findViewById(R.id.disable);

        final int o=Integer.parseInt(getIntent().getStringExtra("to_print"));
        final int i=Integer.parseInt(getIntent().getStringExtra("ident"));
        try {
            mScan.setOnClickListener(new View.OnClickListener() {
                public void onClick(View mView) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(Print.this, "Message1", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(Print.this,
                                    DeviceListActivity.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);
                        }
                    }
                }
            });



        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = "";

                            dh=new DatabaseHelper(Print.this);
                            if(o==0) {

                                String[] ar = dh.getHeader_delivery(i);

                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Template: ", ar[0]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Doc no.: ", ar[1]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Date req.: ", ar[2]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Gross Sales: ", ar[3]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "SCD: ", ar[4]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "SVAT: ", ar[5]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "PWD: ", ar[6]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "COH/CID: ", ar[7]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Total Exp: ", ar[8]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Cash Deposit: ", ar[9]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "TC: ", ar[10]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "TCR: ", ar[11]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Refund: ", ar[12]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Created by: ", ar[13]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Ref no.: ", ar[14]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Remarks: ", ar[15]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Locator: ", ar[16]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Created: ", ar[17]);
                                BILL = BILL
                                        + "\n--------------------------------\n";


                                BILL = BILL + String.format("%1$-2s %2$2s %3$2s %4$2s", "PRODUCT", "UOM", "PRICE", "QTY");
                                BILL = BILL + "\n";
                                BILL = BILL
                                        + "--------------------------------";

                                SQLiteDatabase db = dh.getReadableDatabase();
                                String tablename1, tablename2;
                                String qty;
                                String prc;
                                if (i == 0) {
                                    tablename1 = "mh_delivery_report_line";
                                    prc = "priceentered_temporary";
                                    qty = "quantity_def0_temporary";
                                } else {
                                    tablename1 = "mh_transaction_line";
                                    prc = "priceentered";
                                    qty = "quantity_def0";
                                }
                                try {
                                    Cursor curs = db.rawQuery("select mh_product_id,c_uom_id," + prc + "," + qty + " from " + tablename1 + " where document_num='" + ar[1] + "' and changed='Y'", null);
                                    curs.moveToFirst();
                                    do {
                                        String prod = dh.getProduct_name(curs.getInt(0));
                                        String uom = dh.getUom_name1(curs.getInt(1));
                                        String price = curs.getString(2);
                                        String quantity = curs.getString(3);
                                        BILL = BILL + "\n " + String.format("%1$-2s %2$2s %3$2s %4$2s", prod, uom, price, quantity);
                                    } while (curs.moveToNext());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }else{

                                String doc_num=getIntent().getStringExtra("doc_num");
                                String[] ar = dh.getdelivery(doc_num);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Template: ", ar[0]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Doc no.: ", doc_num);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Date req.: ", ar[1]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Gross Sales: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "SCD: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "SVAT: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "PWD: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "COH/CID: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Total Exp: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Cash Deposit: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "TC: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "TCR: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Refund: ", String.valueOf(0.00));
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Created by: ", ar[2]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Ref no.: ", "");
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Remarks: ", "");
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Locator: ", ar[3]);
                                BILL = BILL + "\n" + String.format("%1$-2s %2$2s", "Created: ", "");
                                BILL = BILL
                                        + "\n--------------------------------\n";


                                BILL = BILL + String.format("%1$-2s %2$2s %3$2s %4$2s", "PRODUCT", "UOM", "PRICE", "QTY");
                                BILL = BILL + "\n";
                                BILL = BILL
                                        + "--------------------------------";

                                SQLiteDatabase db = dh.getReadableDatabase();
                                try {
                                    Cursor curs = db.rawQuery("select mh_product_id,c_uom_id,priceentered,quantity_def0 from mh_delivery_report_line where document_num='" + doc_num + "'", null);
                                    curs.moveToFirst();
                                    do {
                                        String prod = dh.getProduct_name(curs.getInt(0));
                                        String uom = dh.getUom_name1(curs.getInt(1));
                                        String price = curs.getString(2);
                                        String quantity = curs.getString(3);
                                        BILL = BILL + "\n " + String.format("%1$-2s %2$2s %3$2s %4$2s", prod, uom, price, quantity);
                                    } while (curs.moveToNext());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            BILL = BILL
                                    + "\n-----------------------------------------------";
                            BILL = BILL + "\n\n ";

                            BILL = BILL+"\n"+"Received by:";
                            BILL = BILL+"\n"+"Date Received:";
                            BILL = BILL+"\n"+"Acknowledge by:";
                            BILL = BILL+"\n"+"Date:";

                            BILL = BILL
                                    + "-----------------------------------------------\n";
                            BILL = BILL + "\n\n ";
                            os.write(BILL.getBytes());
                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));


                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });


        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

        }catch (Exception e){
            e.printStackTrace();
        }

    }// onCreate

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null){
                mBluetoothSocket.close();
            }

        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();

        int o=Integer.parseInt(getIntent().getStringExtra("to_print"));
        if(o==0){
            Intent in=new Intent(Print.this,compile_transaction.class);
            in.putExtra("fromdate","FROM DATE");
            in.putExtra("todate","TO DATE");
            in.putExtra("fromdate1","FROM DATE");
            in.putExtra("todate1","TO DATE");
            in.putExtra("position",String.valueOf(0));
            in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
            in.putExtra("option","Delivery Report");
            startActivity(in);
        }else{
            Intent in=new Intent(Print.this,Delivery_report.class);
            in.putExtra("emp_name",getIntent().getStringExtra("emp_name"));
            startActivity(in);
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(Print.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(Print.this, "Cancel Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(Print.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

}
