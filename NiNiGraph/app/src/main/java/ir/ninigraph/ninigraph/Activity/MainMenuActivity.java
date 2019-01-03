package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.ninigraph.ninigraph.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainMenuActivity extends AppCompatActivity {

    //Values
    TextView txt_logo_title;
    LinearLayout lay_menu;
    RelativeLayout item_new_order, item_follow_order, item_edit_info, item_support;
    boolean isMenuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Views
        txt_logo_title = findViewById(R.id.txt_logo_title);
        lay_menu = findViewById(R.id.lay_menu);
        item_new_order = findViewById(R.id.item_new_order);
        item_follow_order = findViewById(R.id.item_follow_order);
        item_edit_info = findViewById(R.id.item_edit_info);
        item_support = findViewById(R.id.item_support);


        //Change Font
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Lalezar-Regular.ttf");
        txt_logo_title.setTypeface(font);

        //Show Menu Items
        lay_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMenuVisible) {

                    showMenuItmes();
                    item_new_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "ثبت سفارش", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_follow_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "پیگیری سفارش", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_edit_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "ویرایش اطلاعات", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_support.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "پشتیبانی و تماس با ما", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });
                }
                else {

                    hideMenuItems();
                }
            }
        });
    }

    //Classes
    private void showMenuItmes(){

        isMenuVisible = true;
        item_new_order.setVisibility(View.VISIBLE);
        item_follow_order.setVisibility(View.VISIBLE);
        item_edit_info.setVisibility(View.VISIBLE);
        item_support.setVisibility(View.VISIBLE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_new_order));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_follow_order));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_edit_info));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_support));
    }
    private void hideMenuItems(){

        isMenuVisible = false;
        item_new_order.setVisibility(View.GONE);
        item_follow_order.setVisibility(View.GONE);
        item_edit_info.setVisibility(View.GONE);
        item_support.setVisibility(View.GONE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        if (isMenuVisible)
            hideMenuItems();
        else
            super.onBackPressed();
    }
}
