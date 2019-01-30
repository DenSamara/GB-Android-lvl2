package com.home.konovaloff.homework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean res;
        switch (item.getItemId()){
            case R.id.menu_add:

                res = true;
                break;
            case R.id.menu_clear:

                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }
        return res;
    }
}
