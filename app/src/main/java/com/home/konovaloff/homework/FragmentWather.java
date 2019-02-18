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

/**
 *Фрагмент для отображения погоды
 *
 */
public class FragmentWather extends Fragment {
    public static final String TAG = FragmentWather.class.getSimpleName();
    public static final String EXTRA_CITY = "FragmentWather.data";

    private String city;

    private TextView tvCity;
    private TextView tvLastUpdate;
    private TextView tvTemperature;
    private TextView tvDetails;

    private ImageView imageWeather;
//    DbHelper helper;

    public static FragmentWather newInstance(String city) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CITY, city);

        FragmentWather fragment = new FragmentWather();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getArguments().getString(EXTRA_CITY);
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
        tvCity.setText(city);
    }

    private void bind(View v){
        tvCity = v.findViewById(R.id.city_field);
        tvLastUpdate = v.findViewById(R.id.updated_field);
        imageWeather = v.findViewById(R.id.weather_icon);
        tvTemperature = v.findViewById(R.id.current_temperature_field);
        tvDetails = v.findViewById(R.id.details_field);
    }
}
