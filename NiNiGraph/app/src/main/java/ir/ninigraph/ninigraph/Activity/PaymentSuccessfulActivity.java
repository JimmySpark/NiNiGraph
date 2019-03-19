package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.DateConvertor;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean isConnected;
    private ConstraintLayout layParent;
    private TextView txtDateTime;
    private TextView txtRefId;
    private TextView txtPrice;
    private TextView txtDescription;
    private TextView btnBack;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    private TextView btnFollowOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();


        //Check Connection
        checkConnection();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Display
        if (preferences.getInt("d", 0) == 8) {

            editor.putInt("d", 0).apply();
            btnFollowOrder.setVisibility(View.VISIBLE);
            btnFollowOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(context, FollowOrderActivity.class));
                    finish();
                }
            });
        }

        //Set Data
        //..Data And Time
        Calendar calendar = Calendar.getInstance();
        DateConvertor dateConvertor = new DateConvertor();
        dateConvertor.GregorianToPersian(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        String year = dateConvertor.getYear() + "/";
        String month = dateConvertor.getMonth() + "/";
        String day = dateConvertor.getDay() + " - ";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + ":";
        String minute = calendar.get(Calendar.MINUTE) + "";
        txtDateTime.setText(year + month + day + hour + minute);

        //..Info
        if (getIntent().getBooleanExtra("free", false)) {

            txtRefId.setText(" - ");
            txtPrice.setText("رایگان");
            txtDescription.setText(preferences.getString("payFor", null));
        } else {

            txtRefId.setText(preferences.getString("refId", null));
            txtPrice.setText(String.valueOf(preferences.getLong("pAll", 0)));
            txtDescription.setText(preferences.getString("payFor", null));
        }

        if (!getIntent().getBooleanExtra("result", true))
            showNotSavedDialog();

        //Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("prefs").apply();
                startActivity(new Intent(context, MainMenuActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        editor.remove("prefs").apply();
        startActivity(new Intent(context, MainMenuActivity.class));
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        txtDateTime = findViewById(R.id.txt_date_time);
        txtRefId = findViewById(R.id.txt_refId);
        txtPrice = findViewById(R.id.txt_price);
        txtDescription = findViewById(R.id.txt_description);
        btnBack = findViewById(R.id.btn_back);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
        btnFollowOrder = findViewById(R.id.btn_follow_order);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);
        }
    }

    private void showNotSavedDialog() {

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
        width = (int) ((width) * ((float) 4 / 5));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}