package com.home.konovaloff.homework;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.global.Preferences;
import com.home.konovaloff.homework.interfaces.IListener;
import com.home.konovaloff.homework.tasks.DummyIntentService;
import com.home.konovaloff.homework.tasks.DummyTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.home.konovaloff.homework.tasks.DummyIntentService.EXTRA_MESSAGE;
import static com.home.konovaloff.homework.tasks.DummyIntentService.EXTRA_REPEAT_COUNT;
import static com.home.konovaloff.homework.tasks.DummyIntentService.EXTRA_RESULT;
import static com.home.konovaloff.homework.tasks.DummyIntentService.RESULT_ERROR;
import static com.home.konovaloff.homework.tasks.DummyIntentService.RESULT_SUCCESS;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IListener {
    public static final byte SERVICE_STOPPED = Byte.MAX_VALUE;
    public static final byte SERVICE_RUNNING = Byte.MIN_VALUE;

    public static final String COM_WHATSAPP = "com.whatsapp";
//    public static final String DEFAULT_USERNAME = "Guest";
    public static final int IDD_SELECT_PHOTO = 1;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_TASK = "main.activity.dummytask";
    private static final String KEY_SERVICE = "main.activity.dummyservice";

    private DrawerLayout drawerLayout;
    private DrawerNavigation navigation;
    private ActionBarDrawerToggle drawerToggle;
    private ValueAnimator drawerToggleAnimator;
    private Toolbar toolbar;

    private boolean doubleBackPress;
    private Handler handler;

    //Lesson 2
    private List<Sensor> sensors;

    //lesson 3
    private ProgressBar progressBar;
    private Button btStartTask;
    private Button btStartService;

    private DummyTask dummyTask;
    private BroadcastReceiver receiver;
    private byte service_is_in_progress;

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
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_more);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.setToolbarNavigationClickListener(navigationClickListener);
        drawerLayout.addDrawerListener(drawerToggle);

        setDrawerToggleEnable(getSupportFragmentManager().getBackStackEntryCount() == 0);

        navigation.setNavigationItemSelectedListener(this);
        navigation.setUserName(Preferences.loadStringPreference(this, getString(R.string.pref_username), getString(R.string.default_username)));
        String avatarPathString = Preferences.loadStringPreference(this, getString(R.string.pref_ava_path), getString(R.string.empty));
        if (!TextUtils.isEmpty(avatarPathString)){
            setAvatar(Uri.parse(avatarPathString));
        }

        navigation.setImageClickListener(this);
        navigation.setUserNameClickListener(this);


        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if (sensorManager != null)
            sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        handler = new Handler();

        setupButtons();

        setupReceiver();

        if (savedInstanceState == null){
            //Если мы тут в первый раз
        }else {
            //Проверяем, может есть запущенные задачи, для восстановления прогресса
            restoreInstanceState();
        }
    }

    private void setAvatar(Uri path) {
        try{
            InputStream inputStream = getContentResolver().openInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            navigation.setUserImage(bitmap);
        }catch (Exception e){
            Global.log_e(TAG, e.toString());
        }
    }

    /**
     * Настраиваем ресивер для службы
     */
    private void setupReceiver() {
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                service_is_in_progress = SERVICE_STOPPED;

                byte status = intent.getByteExtra(EXTRA_RESULT, (byte)0);
                String resText = getString(R.string.empty);
                switch (status) {
                    case RESULT_SUCCESS:
                        resText = getString(R.string.success);
                        break;
                    case RESULT_ERROR:
                        resText = intent.getStringExtra(EXTRA_MESSAGE);
                        break;
                    default:
                        break;
                }

                Global.toast(resText);
                showProgress(false);
                enableButton(btStartService, true);
            }
        };

        registerReceiver(receiver, new IntentFilter(DummyIntentService.INTENT_FILTER));
    }

    private void setupButtons() {
        btStartTask = findViewById(R.id.btStartTask);
        if (btStartTask != null){
            btStartTask.setOnClickListener(this);
        }

        btStartService = findViewById(R.id.btStartService);
        if (btStartService != null){
            btStartService.setOnClickListener(this);

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
        String[] array = new String[sensors.size()];
        for (int i = 0; i < sensors.size();i++){
            Sensor item = sensors.get(i);
            array[i] = String.format("%s %s %s", item.getName(), item.getVendor(), item.getVersion());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sensors_list)
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Global.toast(sensors.get(which).toString());
                    }
                });
        builder.create().show();
    }

    /**
     * Меняем логин и лого на дефолт
     */
    private void onSignOut() {
        navigation.setUserName(getString(R.string.default_username));
        navigation.setUserImage(getResources().getDrawable(android.R.drawable.btn_star_big_off));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.app_navigation_image:
                //Вызываем диалог выбора картинки
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, IDD_SELECT_PHOTO);
                break;
            case R.id.app_navigation_username:
                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.username, null);
                final EditText editText = v.findViewById(R.id.username);

                new AlertDialog.Builder(this)
                        .setTitle(R.string.caption_meet)
                        .setView(v)
                        .setPositiveButton(getString(R.string.change), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (editText != null) {
                                    Preferences.saveStringPreference(MainActivity.this, getString(R.string.pref_username), editText.getText().toString());
                                    navigation.setUserName(editText.getText().toString());
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;

            case R.id.btStartTask:
                startDummyTask();
                break;
            case R.id.btStartService:
                startDummyService(10);
                break;
        }
    }

    private void startDummyService(int repeatCount) {
        enableButton(btStartService, false);
        showProgress(true);

        Intent dummyIS = new Intent(this, DummyIntentService.class);
        dummyIS.putExtra(EXTRA_REPEAT_COUNT, repeatCount);

        startService(dummyIS);
        service_is_in_progress = SERVICE_RUNNING;
    }

    private void startDummyTask(){
        enableButton(btStartTask, false);

        showProgress(true);

        dummyTask = new DummyTask();
        dummyTask.setListener(this);
        dummyTask.execute();
    }

    private void enableButton(Button button, boolean value){
        if (button != null){
            button.setEnabled(value);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case IDD_SELECT_PHOTO:
                    try{
                        Uri path = data.getData();

                        Preferences.saveStringPreference(this, getString(R.string.pref_ava_path), path.toString());

                        setAvatar(path);
                    }catch (Exception e){
                        Global.log_e(TAG, e.toString());
                    }
                    break;
            }
        }
    }

    @Override
    public void onTaskComplete(DummyTask task) {
        boolean res;
        try{
            res = task.get();
        }catch (Exception e){
            res = false;
        }

        if (res){
            Global.toast(getString(R.string.success));
        }else {
            Global.toast(getString(R.string.err_runtime)+task.getLastError());
        }

        this.dummyTask.setListener(null);
        this.dummyTask = null;

        showProgress(false);
        enableButton(btStartTask, true);
        //Возвращаем заголовок в исходное состояние
        setTitle(MyApp.getName());
    }

    @Override
    public void onProgressUpdate(String txt) {
        //Статус прогресса будем показывать в заголовке
        this.setTitle(txt);
    }

    private void showProgress(boolean value){
        if (progressBar != null){
            progressBar.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Map<String, Object> customInstance = new HashMap<>();

        //Отвязываемся от активности
        if (dummyTask != null) {
            dummyTask.setListener(null);
            customInstance.put(KEY_TASK, dummyTask);
            dummyTask = null;
        }

        if (service_is_in_progress == SERVICE_RUNNING){
            customInstance.put(KEY_SERVICE, service_is_in_progress);
        }

        return customInstance.isEmpty() ?
                super.onRetainCustomNonConfigurationInstance() : customInstance;
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            Global.log_e(TAG, e.toString());
        }
        super.onDestroy();
    }

}
