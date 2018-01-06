package me.akshanshjain.manage;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.akshanshjain.manage.Adapters.SectionPagerAdapter;

public class LandingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;
    private FloatingActionButton fab;

    private ColorStateList overviewColors, profileColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Setting up the toolbar for the activity.
        toolbar = findViewById(R.id.toolbar_landing);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_landing);
        viewPager = findViewById(R.id.view_pager_container);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        fab = findViewById(R.id.add_expense_landing);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_window_focused}
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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.overview:
                        viewPager.setCurrentItem(0);
                        bottomNavigationView.setItemIconTintList(overviewColors);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(1);
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
                        bottomNavigationView.setItemIconTintList(overviewColors);
                        break;
                    case 1:
                        bottomNavigationView.setItemIconTintList(profileColors);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
