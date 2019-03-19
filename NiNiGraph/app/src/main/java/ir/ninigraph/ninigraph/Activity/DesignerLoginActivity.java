package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.ninigraph.ninigraph.Model.DesignerLogin;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class DesignerLoginActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private RelativeLayout error;
    private EditText edtTxtUsername;
    private EditText edtTxtPassword;
    private TextView enterInfo;
    private TextView mr;
    private RelativeLayout btnLogin;
    private TextView enter;
    private RelativeLayout btnBack;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_login);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Vazir-Bold-FD-WOL.ttf");
        initView();


        //Change Font
        mr.setTypeface(font);
        enterInfo.setTypeface(font);
        enter.setTypeface(font);

        //Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDialog();
                login();
            }
        });

        //Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DesignerLoginActivity.super.onBackPressed();
            }
        });
    }

    //Classes
    private void initView() {
        error = findViewById(R.id.error);
        edtTxtUsername = findViewById(R.id.edt_txt_username);
        edtTxtPassword = findViewById(R.id.edt_txt_password);
        enterInfo = findViewById(R.id.enter_info);
        mr = findViewById(R.id.mr);
        btnLogin = findViewById(R.id.btn_login);
        enter = findViewById(R.id.enter);
        btnBack = findViewById(R.id.btn_back);
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

    private void login(){

        apiService.loginAsDesigner(edtTxtUsername.getText().toString(), edtTxtPassword.getText().toString()).enqueue(new Callback<DesignerLogin>() {
            @Override
            public void onResponse(Call<DesignerLogin> call, Response<DesignerLogin> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        dialog.dismiss();
                        if (response.body().isResult()){

                            error.setVisibility(View.INVISIBLE);
                            editor.putInt("designer_id", response.body().getId()).apply();
                            editor.putBoolean("dLogin", true).apply();
                            startActivity(new Intent(context, DesignerPanelActivity.class));
                            finish();
                        }
                        else
                            error.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<DesignerLogin> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
