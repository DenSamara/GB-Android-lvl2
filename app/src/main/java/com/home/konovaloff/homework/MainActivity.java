package com.home.konovaloff.homework;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.home.konovaloff.homework.api.ServiceGenerator;
import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.model.DB.DBHelper;
import com.home.konovaloff.homework.model.WeatherItem;
import com.home.konovaloff.homework.model.WeatherRequest;
import com.home.konovaloff.homework.settings.AppSettings;
import com.home.konovaloff.homework.settings.SettingsStorage;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        MyDialogFragment.IDlgResult {//, IListener

    public static final String COM_WHATSAPP = "com.whatsapp";
    private static final String APP_DATA_STORAGE_NAME = "app.settings.storage";
    public static final int IDD_SELECT_PHOTO = 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private DrawerNavigation navigation;
    private ActionBarDrawerToggle drawerToggle;
    private ValueAnimator drawerToggleAnimator;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    /**
     * Нужен для реализации двойного нажатия
     */
    private Handler handler;
    private boolean doubleBackPress;
    private AppSettings settings;
    private SettingsStorage settingsStorage;

    //Lesson 6. Retrofit+GSON
    private OpenWeather openWeather;

    //Lesson 7.
    private DBHelper helper;

    public interface OpenWeather {
        @GET("data/2.5/weather")
        Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
    }

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

        handler = new Handler();

        bindContentView(R.layout.activity_main);

        settingsStorage = new SettingsStorage(this, APP_DATA_STORAGE_NAME);
        settings = settingsStorage.getSettings();
        if (settings == null) {
            settings = AppSettings.getDefault(this);
        }

        setupActionBar();

        setupNavigation();

        openWeather = ServiceGenerator.createService(OpenWeather.class);
        helper = new DBHelper(MainActivity.this);

        if (savedInstanceState == null) {
            showProgress(true);
            requestRetrofit(settings.city(), Global.APIKEY);
        }
    }

    private void setupNavigation() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.setToolbarNavigationClickListener(navigationClickListener);
        drawerLayout.addDrawerListener(drawerToggle);

        setDrawerToggleEnable(getSupportFragmentManager().getBackStackEntryCount() == 0);

        navigation.setNavigationItemSelectedListener(this);
        navigation.setUserName(settings.userName());
        String avatarPathString = settings.logoPath();
        if (!TextUtils.isEmpty(avatarPathString)) {
            setAvatar(Uri.parse(avatarPathString));
        }

        navigation.setImageClickListener(this);
        navigation.setUserNameClickListener(this);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_more);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            Global.log_e(TAG, response.body().getBase());
                            //Сохраняем в БД
                            //TODO убрать из основного потока

                            WeatherItem item = parseResponse(response);
                            long id = WeatherItem.insert(helper.getWritableDatabase(), item);
                            Global.log_e(TAG, "Record inserted with id = "+Long.toString(id));
                        }

                        Global.toast(getString(R.string.success));
                        showProgress(false);

                        //Запускам фрагмент с информацией
                        //Пока фрагменты с городами будем добавлять
                        //Потом нужно проверить его существование и обновить только данные в нём
                        addFragment(FragmentWeather.newInstance(settings.city()));
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        Global.log_e(TAG, call.toString());
                        Global.toast(call.toString());
                        showProgress(false);
                    }
                });
    }

    private WeatherItem parseResponse(Response<WeatherRequest> response) {
        String details = String.format("%s\n%s %s\n%s %s\n%s %.0f degrees, %s ms",
                response.body().getWeather()[0].getDescription(),
                getString(R.string.humidity), response.body().getMain().getHumidity(),
                getString(R.string.pressure), response.body().getMain().getPressure(),
                getString(R.string.wind), response.body().getWind().getDeg(), response.body().getWind().getSpeed()
                );
        String imageUrl = String.format(getString(R.string.icon_url), response.body().getWeather()[0].getIcon());
        return new WeatherItem(-1,
                response.body().getName(),                          //city
                details,   //details
                response.body().getMain().getTemp(),                //temp
                imageUrl,          //url
                System.currentTimeMillis());
    }


    private void setAvatar(Uri path) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            navigation.setUserImage(bitmap);
        } catch (Exception e) {
            navigation.setUserImage(getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }
    }

    private void bindContentView(@LayoutRes int layoutResId) {
        setContentView(layoutResId);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.app_progress_bar);

        navigation = (DrawerNavigation) getSupportFragmentManager()
                .findFragmentById(R.id.drawer_navigation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setTitle(settings.city());//MyApp.getName()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        setupSearch(menu);

        return true;
    }

    private void setupSearch(Menu menu) {
        MenuItem search = menu.findItem(R.id.action_search); // Поиск пункта меню поиска
        SearchView searchText = (SearchView) search.getActionView(); // Строка поиска
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchText.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        searchText.setIconifiedByDefault(false);
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit(String query) {
                settings.city(query);
                settingsStorage.saveSettings(settings);
//                requestRetrofit(settings.city(), Global.APIKEY);
                return false;
            }

            // Реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean res;
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                requestRetrofit(settings.city(), Global.APIKEY);
                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }
        return res;
    }

    @Override
    public void onBackPressed() {
        //Возможно, потом пригодится
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
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
            pm.getPackageInfo(COM_WHATSAPP, PackageManager.GET_META_DATA);//PackageInfo info =
        } catch (PackageManager.NameNotFoundException e) {
            Global.toast(getString(R.string.err_whatsapp_is_not_installed));
            return;
        }

        String fio = navigation.getUserName();

        String description;
        if (fio != null && !fio.isEmpty() && !fio.equalsIgnoreCase(getString(R.string.default_username))) {
            description = String.format(getString(R.string.whatsapp_with_username), fio, MyApp.getName());
        } else
            description = String.format("%s %s:\n", getString(R.string.whatsapp_default), MyApp.getName());

        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        sendIntent.putExtra("jid", number + "@s.whatsapp.net"); // Для получения текущего номера нужны дополнительные привилегии
        sendIntent.putExtra(Intent.EXTRA_TEXT, description);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(COM_WHATSAPP);
        sendIntent.setType("text/plain");

        startActivity(sendIntent);
    }

    private void startActionAbout() {
        Global.toast(getString(R.string.about_item_text));
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
        //Удаляем настройки
        settingsStorage.saveSettings(null);

        //меняем на настройки по-умолчанию
        settings = AppSettings.getDefault(this);

        //Меняем в И/Ф
        navigation.setUserName(settings.userName());
        navigation.setUserImage(getResources().getDrawable(android.R.drawable.btn_star_big_off));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_navigation_image:
                //Вызываем диалог выбора картинки
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, IDD_SELECT_PHOTO);
                break;
            case R.id.app_navigation_username:
                MyDialogFragment dlg = MyDialogFragment.newInstance(getString(R.string.caption_meet), settings.userName(), getString(R.string.bt_change), null);
                dlg.setListener(this);
                dlg.show(getSupportFragmentManager(), "");

                break;
        }
    }

    private void enableButton(Button button, boolean value) {
        if (button != null) {
            button.setEnabled(value);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IDD_SELECT_PHOTO:
                    try {
                        Uri path = data.getData();
                        setAvatar(path);

                        settings.logoPath(path.toString());
                        settingsStorage.saveSettings(settings);
                    } catch (Exception e) {
                        Global.log_e(TAG, e.toString());
                    }
                    break;
            }
        }
    }

    private void showProgress(boolean value) {
        if (progressBar != null) {
            progressBar.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.container);

        FragmentTransaction ft = fm.beginTransaction();

        if (current == null) {
            ft.add(R.id.container, fragment);
        } else {
            String backStateName =  fragment.getClass().getName();
            boolean fragmentPopped = fm.popBackStackImmediate (backStateName, 0);

            if (!fragmentPopped) {
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
            }
        }

        ft.commit();
    }

    /**
     * TODO добавить id для многократного использования
     *
     * @param result
     */
    @Override
    public void onDialogResult(byte result, MyDialogFragment sender) {
        switch (result) {
            case MyDialogFragment.RESULT_YES:
                navigation.setUserName(sender.getInputText());

                settings.userName(sender.getInputText());
                settingsStorage.saveSettings(settings);
                break;
            case MyDialogFragment.RESULT_NO:
                break;
            case MyDialogFragment.RESULT_CANCEL:
                break;
        }
    }

}
