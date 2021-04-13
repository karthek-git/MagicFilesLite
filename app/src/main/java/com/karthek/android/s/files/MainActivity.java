package com.karthek.android.s.files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fic(View view) {
        Intent intent=new Intent(this,FActivity.class);
        intent.putExtra("p", "/storage/emulated/0/");
        startActivity(intent);
    }
}
