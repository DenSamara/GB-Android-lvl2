package com.home.konovaloff.homework;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackPress;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle(MyApp.getName());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar ( toolbar );

        mHandler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearch(menu);
        return true;
    }

    private void setupSearch(Menu menu) {
        MenuItem search = menu.findItem (R.id.action_search ); // Поиск пункта меню поиска
        SearchView searchText = (SearchView)search.getActionView (); // Строка поиска
        searchText.setOnQueryTextListener ( new SearchView.OnQueryTextListener () {
            // Реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit ( String query ) {
                Global.toast(query);
                return true;
            }
            // Реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange ( String newText ) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean res;
        switch (item.getItemId()){
            case R.id.menu_add:
                addItem();
                res = true;
                break;
            case R.id.menu_clear:
                clear();
                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }
        return res;
    }

    private void clear() {
        Global.toast("Очищаем элементы");
    }

    private void addItem() {
        Global.toast("Добавляем элемент");
    }

    @Override
    public void onBackPressed() {
        //Возможно, потом пригодится
        if (getSupportFragmentManager().getBackStackEntryCount() != 0){
            super.onBackPressed();
            return;
        }

        if (!doubleBackPress) {
            doubleBackPress = true;
            Toast.makeText(this, "Для выхода из приложения дважды нажмите 'Назад'", Toast.LENGTH_LONG).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackPress = false;
                }
            }, 500);
        } else {
            super.onBackPressed();
        }
    }
}
