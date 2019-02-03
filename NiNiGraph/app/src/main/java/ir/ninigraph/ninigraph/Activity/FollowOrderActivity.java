package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ir.ninigraph.ninigraph.Fragment.OrderDrawingFragment;
import ir.ninigraph.ninigraph.Fragment.OrderEditFragment;
import ir.ninigraph.ninigraph.Fragment.OrderPrintFragment;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import ir.ninigraph.ninigraph.Util.TabLayoutUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FollowOrderActivity extends AppCompatActivity {

    //Values
    Context context = this;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again;
    ImageView img_back;
    ViewPager view_pager;
    SharedPreferences preferences;
    TabLayout tab_layout;
    boolean isConnected;
    int id;
    OrderEditFragment orderEditFragment = new OrderEditFragment();
    OrderDrawingFragment orderDrawingFragment = new OrderDrawingFragment();
    OrderPrintFragment orderPrintFragment = new OrderPrintFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_order);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        id = preferences.getInt("id", 0);

        //Views
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);
        img_back = findViewById(R.id.img_back);
        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.bottom_nav);


        //Check Connection
        checkConnection();

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Tab Layout
        setupTabLayout(view_pager);
        tab_layout.setupWithViewPager(view_pager);
        setupTabIcons();

        //Back
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FollowOrderActivity.super.onBackPressed();
            }
        });
    }

    //Classes
    private void checkConnection(){

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected){

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        }
        else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);
        }
    }
    private void setupTabLayout(ViewPager viewPager){

        TabLayoutUtils.ViewPagerAdapter adapter = new TabLayoutUtils.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(orderPrintFragment);
        adapter.addFragment(orderDrawingFragment);
        adapter.addFragment(orderEditFragment);
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons(){

        int[] icons = {R.drawable.icon_print2, R.drawable.icon_drawing2, R.drawable.icon_edit_image2};
        int defaultTabColor = Color.parseColor("#55000000");
        int selectedTabColor = Color.parseColor("#ffffff");

        TabLayoutUtils.setupTabIcons(
                getApplicationContext(),
                tab_layout,
                icons,
                2,
                selectedTabColor,
                defaultTabColor);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
