package com.mh.mytransaction;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dh=new DatabaseHelper(this);
        dh.StartWork();
        Intent i = new Intent(this, Home.class);
        startActivity(i);

        /*Thread t1 =new Thread(new Runnable(){

            @Override
            public void run() {
                try{

                    Thread.sleep(3*1000);
                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t1.start();*/
    }
}
