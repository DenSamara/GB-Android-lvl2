package com.home.konovaloff.homework;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.model.CityItem;
import com.home.konovaloff.homework.model.DB.DBHelper;
import com.home.konovaloff.homework.model.WeatherItem;

import java.util.ArrayList;

/**
 * Фрагмент для отображения погоды
 */
public class FragmentWeather extends Fragment {
    public static final String TAG = FragmentWeather.class.getSimpleName();
    public static final String EXTRA_CITY = "FragmentWeather.city";

    private CityItem city;

    private TextView tvCity;
    private TextView tvLastUpdate;
    private TextView tvTemperature;
    private TextView tvDetails;
    private WeatherItem weather;

    private ImageView imageWeather;
    DBHelper helper;

    public static FragmentWeather newInstance(CityItem city) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CITY, city);

        FragmentWeather fragment = new FragmentWeather();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getArguments().getParcelable(EXTRA_CITY);
        helper = new DBHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind(view);

        //Загружаем информацию из БД
        //TODO убрать загрузку из основного потока
        if (city != null) {
            ArrayList<WeatherItem> items = WeatherItem.load(helper.getReadableDatabase(), city.id());
            if (items != null && items.size() > 0) {
                WeatherItem last = items.get(0);
                showData(last);
            }
        }
    }

    private void bind(View v) {
        tvCity = v.findViewById(R.id.city_field);
        tvLastUpdate = v.findViewById(R.id.updated_field);
        imageWeather = v.findViewById(R.id.weather_icon);
        tvTemperature = v.findViewById(R.id.current_temperature_field);
        tvDetails = v.findViewById(R.id.details_field);
    }

    private void showData(WeatherItem item) {
        tvCity.setText(item.city().cityName());
        tvLastUpdate.setText(Formatter.formatDateTime(item.lastUpdate()));

        Glide.with(this)
                .load(item.imageUrl())
                .apply(Global.IMAGE_REQUEST_OPTIONS)
                .into(imageWeather);
        tvTemperature.setText(Formatter.formatTemperature(item.temperature()));
        tvDetails.setText(item.details());
    }
}
