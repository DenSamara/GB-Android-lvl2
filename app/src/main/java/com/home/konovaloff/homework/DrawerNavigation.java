package com.home.konovaloff.homework;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public final class DrawerNavigation extends Fragment{
    private final static String TAG = DrawerNavigation.class.getSimpleName();

    private NavigationView navigationView;
    private ImageView imageView;
    private TextView tvUserName;

    private NavigationView.OnNavigationItemSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        navigationView = (NavigationView) view.findViewById(R.id.app_navigation);
        navigationView.setNavigationItemSelectedListener(listener);

        View navigationHeader = navigationView.inflateHeaderView(R.layout.app_navigation_header);

        imageView = navigationHeader.findViewById(R.id.app_navigation_image);
        tvUserName = navigationHeader
                .findViewById(R.id.app_navigation_username);
    }

    public void setNavigationItemSelectedListener(
            NavigationView.OnNavigationItemSelectedListener listener) {
        this.listener = listener;
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(listener);
        }
    }

    public void setImageClickListener(View.OnClickListener listener){
        if (imageView != null){
            imageView.setOnClickListener(listener);
        }
    }

    public void setUserNameClickListener(View.OnClickListener listener){
        if (tvUserName != null){
            tvUserName.setOnClickListener(listener);
        }
    }

    public void setUserName(String userName){
        if (tvUserName != null)
            tvUserName.setText(userName);
    }

    public void setUserImage(Bitmap image){
        if (imageView != null)
            imageView.setImageBitmap(image);
    }

}
