package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ir.ninigraph.ninigraph.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewDrawingOrderActivity extends AppCompatActivity {

    //Values
    TextView choose_pic, choose_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drawing_order);

        //Values
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Vazir-Bold-FD-WOL.ttf");

        //Views
        choose_pic = findViewById(R.id.choose_pic);
        choose_size = findViewById(R.id.choose_size);


        //Change Fonts
        choose_pic.setTypeface(font);
        choose_size.setTypeface(font);
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}