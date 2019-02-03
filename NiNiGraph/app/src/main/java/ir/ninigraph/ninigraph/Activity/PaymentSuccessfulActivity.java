package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import co.ronash.pushe.Pushe;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.DateConvertor;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    //Values
    Context context = this;
    ConstraintLayout lay_parent, lay_no_con;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView txt_date_time, txt_refId, txt_price, txt_description;
    Button btn_send_pics, btn_back, btn_try_again;
    public static AlertDialog dialog;
    boolean isConnected;

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
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);


        //Check Connection
        checkConnection();

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

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
        if (getIntent().getBooleanExtra("free", false)){

            txt_refId.setText(" - ");
            txt_price.setText("رایگان");
            txt_description.setText(getIntent().getExtras().getString("des"));
        }
        else {

            txt_refId.setText(getIntent().getExtras().getString("refId"));
            txt_price.setText(getIntent().getExtras().getLong("price") + "");
            txt_description.setText(getIntent().getExtras().getString("des"));
        }

        if (getIntent().getBooleanExtra("result", true))
            showNotSavedDialog();

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

                Intent intent = new Intent(context, UploadEditPicActivity.class);
                intent.putExtra("order_id", 0);
                startActivity(intent);
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
    private void showNotSavedDialog(){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_order_not_saved, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        Display display = (getWindowManager().getDefaultDisplay());
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int)((width) * ((float)4 / 5));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {}
}
