package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.codemonkeylabs.fpslibrary.FrameDataCallback;
import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //加载布局文件
        Toolbar toolbar = findViewById(R.id.toolbar); //获取id toolbar
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                /*
                Context context = getApplicationContext();
                TinyDancer.create().show(context);
                TinyDancer.create()
                        .redFlagPercentage(.1f) // set red indicator for 10%....different from default
                        .startingXPosition(200)
                        .startingYPosition(600)
                        .show(context);
                 */
            }
        });
        PerformanceMonitor p = new PerformanceMonitor();
        p.ScrollStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }
}
