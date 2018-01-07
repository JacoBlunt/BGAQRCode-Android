package com.jaco9.exwarehouse.bottomnavigationviewpager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruce on 2016/11/1.
 * HomeActivity 主界面
 */

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private Activity _this=this;
    private List<Fragment> fragmentList=null;
    private String barCodeContent=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_news:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_lib:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_find:
                                viewPager.setCurrentItem(2);
                                break;
//                            case R.id.item_more:
//                                viewPager.setCurrentItem(3);
//                                break;
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
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                if (position==1)
                {
                    IntentIntegrator integrator = new IntentIntegrator(_this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("请扫描条码");
                    integrator.setCameraId(0);  // Use a specific camera of the device
//                    integrator.setBeepEnabled(false);
//                    integrator.setBarcodeImageEnabled(true);
                    integrator.setOrientationLocked(true);
                    integrator.initiateScan();
//                    new IntentIntegrator(_this).initiateScan();
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentList=new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            Fragment fragment=BaseFragment.newInstance(i);
            adapter.addFragment(fragment);
            fragmentList.add(fragment);
        }
//        adapter.addFragment(BaseFragment.newInstance("更多"));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                this.setBarCodeContent(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getBarCodeContent() {
        return barCodeContent;
    }

    public void setBarCodeContent(String barCodeContent) {
        this.barCodeContent = barCodeContent;
    }
}