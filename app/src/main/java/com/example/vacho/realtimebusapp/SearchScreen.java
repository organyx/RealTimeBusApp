package com.example.vacho.realtimebusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import adapter.FragmentPagerAdapter;

public class SearchScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private FragmentPagerAdapter fragmentPagerAdapter;

    LinearLayout contentSearchScreenMainLayout;
    TextView tabTitle;
    TextInputEditText hintForSearchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        hintForSearchField = (TextInputEditText) findViewById(R.id.editable_text);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);

        contentSearchScreenMainLayout = ((LinearLayout) pagerSlidingTabStrip.getChildAt(0));
        tabTitle = (TextView) contentSearchScreenMainLayout.getChildAt(0);
        tabTitle.setTextColor(Color.parseColor("#FFCC80"));
        hintForSearchField.setHint("Where do you want to go?");

        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < contentSearchScreenMainLayout.getChildCount(); i++) {
                    tabTitle = (TextView) contentSearchScreenMainLayout.getChildAt(i);

                    if (i == position) {
                        tabTitle.setTextColor(Color.parseColor("#FFCC80"));
                        hintForSearchField.setHint("Type your line number: ");
                    }  else {
                        hintForSearchField.setHint("Where do you want to go?");
                        tabTitle.setTextColor(Color.GRAY);
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
