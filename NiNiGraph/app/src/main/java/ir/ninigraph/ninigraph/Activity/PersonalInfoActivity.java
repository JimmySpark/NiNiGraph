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
import android.widget.Toast;

import java.util.List;

import ir.ninigraph.ninigraph.Model.PersonalInfo;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PersonalInfoActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ConstraintLayout lay_parent, lay_no_con;
    EditText edt_txt_name, edt_txt_state, edt_txt_city, edt_txt_address, edt_txt_postal_code;
    Button btn_try_again, btn_save_info, btn_skip;
    AlertDialog dialog;
    boolean isConnected;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        id = preferences.getInt("id", 0);

        //Views
        edt_txt_name = findViewById(R.id.edt_txt_name);
        edt_txt_state = findViewById(R.id.edt_txt_state);
        edt_txt_city = findViewById(R.id.edt_txt_city);
        edt_txt_address = findViewById(R.id.edt_txt_address);
        edt_txt_postal_code = findViewById(R.id.edt_txt_postal_code);
        btn_save_info = findViewById(R.id.btn_save_info);
        btn_skip = findViewById(R.id.btn_skip);
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

        //Save Info
        btn_save_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edt_txt_name.getText().toString().equals("") &&
                        !edt_txt_state.getText().toString().equals("") &&
                        !edt_txt_city.getText().toString().equals("") &&
                        !edt_txt_address.getText().toString().equals("") &&
                        !edt_txt_postal_code.getText().toString().equals("")){

                    showLoadingDialog();
                    saveInfo(
                            edt_txt_name.getText().toString(),
                            edt_txt_state.getText().toString(),
                            edt_txt_city.getText().toString(),
                            edt_txt_address.getText().toString(),
                            edt_txt_postal_code.getText().toString()
                    );
                }
                else
                    Toast.makeText(context, "لطفا همه فیلد ها را کامل کنید", Toast.LENGTH_SHORT).show();
            }
        });

        //Skip Entering Info
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("isInfoEntered", false).apply();
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
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

            if (getIntent().getBooleanExtra("edit_mode", false)){

                showLoadingDialog();
                btn_skip.setVisibility(View.GONE);
                getSavedInfo();
            }
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
    private void saveInfo(String name, String state, String city, String address, String postal_code){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Save>> call = apiService.saveInfo(id, name, state, city, address, postal_code);

        call.enqueue(new Callback<List<Save>>() {
            @Override
            public void onResponse(Call<List<Save>> call, Response<List<Save>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        dialog.dismiss();
                        if (response.body().get(0).isResult()){

                            editor.putBoolean("isInfoEntered", true).apply();
                            startActivity(new Intent(context, MainMenuActivity.class));
                        }
                        else
                            Toast.makeText(context, "اطلاعات ثبت نشد، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Save>> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getSavedInfo(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<PersonalInfo>> call = apiService.getInfo(id);

        call.enqueue(new Callback<List<PersonalInfo>>() {
            @Override
            public void onResponse(Call<List<PersonalInfo>> call, Response<List<PersonalInfo>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        dialog.dismiss();

                        edt_txt_name.setText(response.body().get(0).getName());
                        edt_txt_state.setText(response.body().get(0).getState());
                        edt_txt_city.setText(response.body().get(0).getCity());
                        edt_txt_address.setText(response.body().get(0).getAddress());
                        edt_txt_postal_code.setText(response.body().get(0).getPostal_code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PersonalInfo>> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
