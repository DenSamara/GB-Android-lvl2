package com.home.konovaloff.homework;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.home.konovaloff.homework.api.ServiceGenerator;
import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.model.CityItem;
import com.home.konovaloff.homework.model.Coord;
import com.home.konovaloff.homework.model.db.CityEntity;
import com.home.konovaloff.homework.model.db.DBHelper;
import com.home.konovaloff.homework.model.db.SearchHistoryEntity;
import com.home.konovaloff.homework.model.WeatherItem;
import com.home.konovaloff.homework.model.WeatherRequest;
import com.home.konovaloff.homework.settings.AppSettings;
import com.home.konovaloff.homework.settings.SettingsStorage;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        MyDialogFragment.IDlgResult,
        ActivityCompat.OnRequestPermissionsResultCallback {//, IListener
    public static final String COM_WHATSAPP = "com.whatsapp";
    public static final int IDD_SELECT_PHOTO = 1;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final String APP_DATA_STORAGE_NAME = "app.settings.storage";
    private static final String EXTRA_LATITUDE = "extra_latitude";
    private static final String EXTRA_LONGITUDE = "extra_longitude";

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

    //Lesson 9.
    private Coord latLong;

//    public interface OpenWeather {
//        @GET("data/2.5/weather")
//        Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
//    }

    public interface CoordService {
        void createLatLong(@Body Coord coord, Callback<Coord> cb);
    }

    public interface OpenWeather {
        @GET("data/2.5/weather")
        Call<WeatherRequest> loadWeather(@Query("lat") float lat, @Query("lon") float lon, @Query("appid") String keyApi);
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
//            requestRetrofitByCity(settings.city(), Global.APIKEY);

            requestPermission();
        }else {
            //Для поворота экрана
            latLong = new Coord();
            latLong.setLat(savedInstanceState.getFloat(EXTRA_LATITUDE));
            latLong.setLon(savedInstanceState.getFloat(EXTRA_LONGITUDE));
        }
    }

    private void requestPermission() {
        // Проверим на разрешения, и если их нет - запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // запросим координаты
            requestLocation();
        } else {
            // разрешений нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }
    }

    // Запрос разрешения для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Запросим разрешения у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
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

    private void requestRetrofit(Coord latLong, String keyApi) {
        if (latLong == null) return;

        openWeather.loadWeather(latLong.getLat(), latLong.getLon(), keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            //Сохраняем в БД
                            //TODO убрать из основного потока
                            WeatherItem item = parseResponse(response);
                            WeatherItem.insert(helper.getWritableDatabase(), item);

                            settings.city(item.city());
                            settingsStorage.saveSettings(settings);
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
                        Global.logE(TAG, call.toString());
                        Global.toast(call.toString());
                        showProgress(false);
                    }
                });
    }

    private WeatherItem parseResponse(Response<WeatherRequest> response) {
        CityItem city = CityItem.find(helper.getReadableDatabase(), response.body().getName(), response.body().getSys().getCountry());
        //Если такое сочетание города-страны не нашли - вставляем
        if (city == null) {
            city = new CityItem(-1, response.body().getName(), response.body().getSys().getCountry());
            long city_id = CityItem.insert(helper.getWritableDatabase(), city);
            city.id(city_id);
        }

        String details = String.format("%s\n%s %s\n%s %s\n%s %.0f degrees, %s ms",
                response.body().getWeather()[0].getDescription(),
                getString(R.string.humidity), response.body().getMain().getHumidity(),
                getString(R.string.pressure), response.body().getMain().getPressure(),
                getString(R.string.wind), response.body().getWind().getDeg(), response.body().getWind().getSpeed()
        );

        String imageUrl = String.format(getString(R.string.icon_url), response.body().getWeather()[0].getIcon());

        return new WeatherItem(-1,
                city, //city
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
        this.setTitle(MyApp.getName());//
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
//                settings.city(query);
//                settingsStorage.saveSettings(settings);
                //TODO Добавить выбор страны
//                requestRetrofit(query, Global.APIKEY);
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
//                requestRetrofitByCity(settings.city(), Global.APIKEY);
                requestRetrofit(latLong, Global.APIKEY);
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

        //Очищаем таблицы в БД
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            CityEntity.clear(db);
        } catch (Exception e) {
            Global.logE(TAG, e.toString());
        }

        try {
            SearchHistoryEntity.clear(db);
        } catch (Exception e) {
            Global.logE(TAG, e.toString());
        }

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
                        Global.logE(TAG, e.toString());
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
            String backStateName = fragment.getClass().getName();
            boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Это то разрешение, что мы запрашивали?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // разрешение дано
                requestLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location (in some rare situations this can be null)
                        if (location != null) {
                            latLong = new Coord();
                            latLong.setLat((float) location.getLatitude());    // Широта
                            latLong.setLon((float) location.getLongitude());  // Долгота

                            //TODO получить город по координатам
                            requestRetrofit(latLong, Global.APIKEY);
                        } else
                            Global.toast(getString(R.string.err_define_location));
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putFloat(EXTRA_LATITUDE, latLong.getLat());
        outState.putFloat(EXTRA_LONGITUDE, latLong.getLon());

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
