package me.akshanshjain.manage;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.akshanshjain.manage.Adapters.SectionPagerAdapter;

public class LandingActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;
    private FloatingActionButton fab;
    private MenuItem bottomNavMenu;

    private ColorStateList overviewColors, profileColors, tobeChangedColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_window_focused}
        };

        int[] changing = new int[]{
                ContextCompat.getColor(getApplicationContext(), R.color.fieryRose),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.fieryRose)
        };

        int[] overview = new int[]{
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)
        };

        int[] profile = new int[]{
                ContextCompat.getColor(getApplicationContext(), R.color.vividCerulean),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.materialBlack),
                ContextCompat.getColor(getApplicationContext(), R.color.vividCerulean)
        };

        overviewColors = new ColorStateList(states, overview);
        profileColors = new ColorStateList(states, profile);
        tobeChangedColors = new ColorStateList(states, changing);

        bottomNavigationView = findViewById(R.id.bottom_nav_landing);
        bottomNavigationView.setItemIconTintList(overviewColors);
        viewPager = findViewById(R.id.view_pager_container);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        fab = findViewById(R.id.add_expense_landing);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.to_be_changed:
                        viewPager.setCurrentItem(0);
                        bottomNavigationView.setItemIconTintList(tobeChangedColors);
                        break;
                    case R.id.overview:
                        viewPager.setCurrentItem(1);
                        bottomNavigationView.setItemIconTintList(overviewColors);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(2);
                        bottomNavigationView.setItemIconTintList(profileColors);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setItemIconTintList(tobeChangedColors);
                        break;
                    case 1:
                        bottomNavigationView.setItemIconTintList(overviewColors);
                        break;
                    case 2:
                        bottomNavigationView.setItemIconTintList(profileColors);
                        break;
                }

                if (bottomNavMenu != null) {
                    bottomNavMenu.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                bottomNavMenu = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
