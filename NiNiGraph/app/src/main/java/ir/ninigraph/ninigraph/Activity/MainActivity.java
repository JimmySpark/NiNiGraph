package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import co.ronash.pushe.Pushe;
import ir.ninigraph.ninigraph.Model.SMS;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public static ApiService apiService;
    //Values
    Context context = this;
    SharedPreferences preferences;
    ConstraintLayout lay_parent, lay_no_con;
    ImageView img_logo;
    Button btn_try_again;
    EditText edt_text_phone_number;
    TextView btn_login;
    AlertDialog dialog;
    boolean login, dLogin, isConnected, doubleClick, tripleClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApp();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        login = preferences.getBoolean("login", false);
        dLogin = preferences.getBoolean("dLogin", false);

        if (dLogin)
            startActivity(new Intent(context, DesignerPanelActivity.class));
        else if (login)
            startActivity(new Intent(context, MainMenuActivity.class));
        else {

            //Views
            edt_text_phone_number = findViewById(R.id.edt_txt_phone_number);
            btn_login = findViewById(R.id.btn_login);
            lay_parent = findViewById(R.id.lay_parent);
            lay_no_con = findViewById(R.id.lay_no_con);
            btn_try_again = findViewById(R.id.btn_try_again);
            img_logo = findViewById(R.id.img_logo);
//            btn_login.setShadowLayer(0, 0, 8, Color.parseColor("#000000"));


            //Check Connection
            checkConnection();

            btn_try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checkConnection();
                }
            });

            //Login
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checkConnection2();
                }
            });

            //Designer Login
            img_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if (tripleClick) {

                        startActivity(new Intent(context, DesignerLoginActivity.class));
                    } else if (doubleClick) {

                        tripleClick = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tripleClick = false;
                            }
                        }, 500);
                    } else {

                        doubleClick = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleClick = false;
                            }
                        }, 1000);
                    }*/
                }
            });
        }
    }

    //Classes
    private void initApp() {
        String languageToLoad = "fa_IR";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        apiService = new Retrofit.Builder()
                .baseUrl("http://ninigraph.ir/app_server/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);

    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        } else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);

            //Push Notification
            Pushe.initialize(this, true);
        }
    }

    private void checkConnection2() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        } else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);

            showLoadingDialog();
            sendSms(edt_text_phone_number.getText().toString());
        }
    }

    private void showLoadingDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        //Change Layout
        Display display = (getWindowManager().getDefaultDisplay());
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (0.6 / 3));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void sendSms(final String phone) {

        //Check If Phone Start With 0
        String phone2 = phone;
        if (!phone.startsWith("0"))
            phone2 = "0" + phone;
        final String finalPhone = phone2;

        apiService.sendSMS(finalPhone).enqueue(new Callback<List<SMS>>() {
            @Override
            public void onResponse(Call<List<SMS>> call, Response<List<SMS>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        dialog.dismiss();
                        if (response.body().get(0).getStatus() == 200) {

                            Intent intent = new Intent(getApplicationContext(), CodeVerificationActivity.class);
                            intent.putExtra("phone", finalPhone);
                            startActivity(intent);
                        } else if (response.body().get(0).getStatus() == 411 ||
                                response.body().get(0).getStatus() == 406 ||
                                response.body().get(0).getStatus() == 8000)
                            Toast.makeText(MainActivity.this, "لطفا شماره تلفن خود را به درستی وارد کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SMS>> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
