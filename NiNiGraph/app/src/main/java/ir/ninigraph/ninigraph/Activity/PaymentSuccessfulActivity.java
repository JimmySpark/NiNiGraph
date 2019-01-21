package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.DateConvertor;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView txt_date_time, txt_refId, txt_price, txt_description;
    Button btn_send_pics, btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        //Views
        txt_date_time = findViewById(R.id.txt_date_time);
        txt_refId = findViewById(R.id.txt_refId);
        txt_price = findViewById(R.id.txt_price);
        txt_description = findViewById(R.id.txt_description);
        btn_send_pics = findViewById(R.id.btn_send_pics);
        btn_back = findViewById(R.id.btn_back);


        //Set Data
        //..Data And Time
        Calendar calendar = Calendar.getInstance();
        DateConvertor dateConvertor = new DateConvertor();
        dateConvertor.GregorianToPersian(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+ 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        String year = dateConvertor.getYear() + "/";
        String month = dateConvertor.getMonth() +  "/";
        String day = dateConvertor.getDay() + " - ";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + ":";
        String minute = calendar.get(Calendar.MINUTE) +"";
        txt_date_time.setText(year + month + day + hour + minute);

        //..Info
        txt_refId.setText(getIntent().getExtras().getString("refId"));
        txt_price.setText(getIntent().getExtras().getLong("price") + "");
        txt_description.setText(getIntent().getExtras().getString("des"));

        //Send Pics
        btn_send_pics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("order_type").apply();
                editor.remove("isPreferencesSet").apply();
                editor.remove("drawing").apply();
                editor.remove("edit").apply();
                editor.remove("print").apply();
                editor.remove("post").apply();
                editor.remove("all").apply();
                editor.remove("order_count").apply();
                startActivity(new Intent(context, FollowOrderActivity.class));
            }
        });
        //Back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("order_type").apply();
                editor.remove("isPreferencesSet").apply();
                editor.remove("drawing").apply();
                editor.remove("edit").apply();
                editor.remove("print").apply();
                editor.remove("post").apply();
                editor.remove("all").apply();
                editor.remove("order_count").apply();
                startActivity(new Intent(context, MainMenuActivity.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {}
}