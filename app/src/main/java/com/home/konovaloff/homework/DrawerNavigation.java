package com.home.konovaloff.homework;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
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
        navigationView = view.findViewById(R.id.app_navigation);
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

    public String getUserName(){
        if (tvUserName == null) return null;

        return  tvUserName.getText().toString();
    }

    public void setUserImage(Bitmap image){
        if (imageView != null)
            imageView.setImageBitmap(image);
    }

    public void setUserImage(Drawable image){
        if (imageView != null)
            imageView.setImageDrawable(image);
    }
}
