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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ir.ninigraph.ninigraph.Model.Verification;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CodeVerificationActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText edt_txt_code;
    Button btn_verify;
    TextView txt_btn_edit_phone_number;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again;
    AlertDialog dialog;
    boolean isConnected;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        phone = getIntent().getStringExtra("phone");

        //Views
        btn_verify = findViewById(R.id.btn_verify);
        txt_btn_edit_phone_number = findViewById(R.id.txt_btn_edit_phone_number);
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);
        edt_txt_code = findViewById(R.id.edt_txt_code);


        //Check Connection
        checkConnection();

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Verify
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDialog();
                checkCode(phone, edt_txt_code.getText().toString());
            }
        });

        //Edit Phone Number
        txt_btn_edit_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
    private void showLoadingDialog(){

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
        width = (int)((width) * (0.6 / 3));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void checkCode(String phone, String code){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Verification>> call = apiService.checkCode(phone, code);

        call.enqueue(new Callback<List<Verification>>() {
            @Override
            public void onResponse(Call<List<Verification>> call, Response<List<Verification>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        dialog.dismiss();

                        if (response.body().get(0).isResult()){

                            editor.putInt("id", response.body().get(0).getId()).apply();
                            editor.putBoolean("login", true).apply();
                            if (preferences.getBoolean("isInfoEntered", false))

                                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                            else
                                startActivity(new Intent(getApplicationContext(), PersonalInfoActivity.class));
                        }
                        else
                            Toast.makeText(context, "کد وارد شده صحیح نمی باشد", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Verification>> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
