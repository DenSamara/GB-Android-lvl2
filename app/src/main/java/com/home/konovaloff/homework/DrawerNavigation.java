package com.home.konovaloff.homework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class DrawerNavigation extends Fragment{
    private final static String TAG = DrawerNavigation.class.getSimpleName();

    private NavigationView navigationView;
    private TextView headerEmployeeId;

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

        headerEmployeeId = (TextView) navigationHeader
                .findViewById(R.id.app_navigation_header_user);
    }

    public void setNavigationItemSelectedListener(
            NavigationView.OnNavigationItemSelectedListener listener) {
        this.listener = listener;
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(listener);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Global.log_e(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Global.log_e(TAG, "onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }
}
