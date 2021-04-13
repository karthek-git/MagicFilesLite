package com.karthek.android.s.files;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

public class DisplayMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        String message = intent.getStringExtra("e1");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        File f1=new File("/sdcard");
        /*try {
            Process process = Runtime.getRuntime().exec("su -c ls -l / >");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] flist=f1.list();
        if(flist!=null) {
            for (int i = 0; i < flist.length; i++) {
                System.out.println(flist[i]);
            }
        }else System.out.println("ls -l /");*/

    }
}