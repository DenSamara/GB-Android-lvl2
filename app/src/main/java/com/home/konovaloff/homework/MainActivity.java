package com.home.konovaloff.homework;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String COM_WHATSAPP = "com.whatsapp";
    private boolean doubleBackPress;
    private Handler handler;

    private DrawerLayout drawerLayout;
    private DrawerNavigation navigation;
    private ActionBarDrawerToggle drawerToggle;
    private ValueAnimator drawerToggleAnimator;
    private Toolbar toolbar;

    private final View.OnClickListener navigationClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            };

    private final FragmentManager.OnBackStackChangedListener fragmentBackStackListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    setDrawerToggleEnable(
                            getSupportFragmentManager().getBackStackEntryCount() == 0);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportFragmentManager().addOnBackStackChangedListener(fragmentBackStackListener);
        super.onCreate(savedInstanceState);

        bindContentView(R.layout.activity_main);

        this.setTitle(MyApp.getName());

        setSupportActionBar ( toolbar );
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.setToolbarNavigationClickListener(navigationClickListener);
        drawerLayout.addDrawerListener(drawerToggle);

        setDrawerToggleEnable(getSupportFragmentManager().getBackStackEntryCount() == 0);

        navigation.setNavigationItemSelectedListener(this);
        navigation.setHasOptionsMenu(true);

        handler = new Handler();
    }

    private void bindContentView(@LayoutRes int layoutResId) {
        setContentView(layoutResId);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        navigation = (DrawerNavigation) getSupportFragmentManager()
                .findFragmentById(R.id.drawer_navigation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearch(menu);
        return true;
    }

    private void setupSearch(Menu menu) {
        MenuItem search = menu.findItem (R.id.action_search ); // Поиск пункта меню поиска
        SearchView searchText = (SearchView)search.getActionView(); // Строка поиска
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackPress = false;
                }
            }, 500);
        } else {
            super.onBackPressed();
        }
    }

    private void setDrawerToggleEnable(boolean enable) {
        if (drawerToggle.isDrawerIndicatorEnabled() == enable) return;

        if (drawerToggleAnimator != null && drawerToggleAnimator.isRunning()) {
            drawerToggleAnimator.cancel();
        }

        if (enable) {
            drawerToggle.getDrawerArrowDrawable().setProgress(1f);
            drawerToggle.setDrawerIndicatorEnabled(true);

            drawerToggleAnimator = ValueAnimator.ofFloat(1f, 0f);
        } else {
            drawerToggleAnimator = ValueAnimator.ofFloat(0f, 1f);
            drawerToggleAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    drawerToggle.setDrawerIndicatorEnabled(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        drawerToggleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawerToggle.getDrawerArrowDrawable()
                        .setProgress((float) animation.getAnimatedValue());
            }
        });

        drawerToggleAnimator.setInterpolator(new DecelerateInterpolator());
        drawerToggleAnimator.setDuration(300);
        drawerToggleAnimator.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.app_navigation_action_sync:
                startSynchronization();
                return true;
            case R.id.app_navigation_whatsapp:
                startActionWhatsApp();
                return true;
            case R.id.app_navigation_about:
                startActionAbout();
                return true;
            case R.id.app_navigation_downloads_update:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            case R.id.app_navigation_sign_out:
                onSignOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startActionWhatsApp() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(COM_WHATSAPP, PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            Global.toast("WhatsApp не установлен");
            return;
        }

        //TODO Имя пользователя
        String fio = "";

        String description;
        if (fio != null && !fio.isEmpty()) {
            description = String.format("Здравствуйте! Меня зовут %s. У меня есть вопрос пр приложению %s:\n", fio, MyApp.getName());
        } else
            description = String.format("Здравствуйте! У меня есть вопрос пр приложению %s:\n", MyApp.getName());

        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        sendIntent.putExtra("jid", number + "@s.whatsapp.net"); // Для получения текущего номера нужны дополнительные привилегии
        sendIntent.putExtra(Intent.EXTRA_TEXT, description);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(COM_WHATSAPP);
        sendIntent.setType("text/plain");

        startActivity(sendIntent);
    }

    private void startActionAbout() {
        Global.toast(String.format("Автор: Коновалов Денис. Проект выполнен в качестве домашнего задания в рамках обучения программированию для платформы Android"));
    }

    /**
     * Запускаем процесс обновления данных
     */
    private void startSynchronization() {

    }

    /**
     * Меняем логин и лого на дефолт
     */
    private void onSignOut() {

    }
}
