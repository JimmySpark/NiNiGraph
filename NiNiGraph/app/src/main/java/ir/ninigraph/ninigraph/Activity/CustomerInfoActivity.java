package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerInfoActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean isConnected;
    int id, size, chassis, type;
    long pDrawing, pPrint, pPost;
    Uri imageUri;
    private ConstraintLayout layParent;
    private EditText edtTxtName;
    private EditText edtTxtState;
    private EditText edtTxtCity;
    private EditText edtTxtAddress;
    private EditText edtTxtPostalCode;
    private Button btnSaveInfo;
    private Button btnTryAgain;
    private ConstraintLayout layNoCon;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        id = preferences.getInt("id", 0);
        size = getIntent().getIntExtra("size", 0);
        chassis = getIntent().getIntExtra("chassis", 0);
        type = getIntent().getIntExtra("type", 0);
        pDrawing = getIntent().getLongExtra("pDrawing", 0);
        pPost = getIntent().getLongExtra("pPrint", 0);
        pPrint = getIntent().getLongExtra("pPost", 0);
        imageUri = getIntent().getData();

        //Views
        initView();


        //Check Connection
        checkConnection();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerInfoActivity.super.onBackPressed();
            }
        });

        //Save Info
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edtTxtName.getText().toString().equals("") &&
                        !edtTxtState.getText().toString().equals("") &&
                        !edtTxtCity.getText().toString().equals("") &&
                        !edtTxtAddress.getText().toString().equals("") &&
                        !edtTxtPostalCode.getText().toString().equals("")) {

                    if (getIntent().getExtras().getString("orderType").equals("d")) {

                        Intent intent = new Intent(context, PaymentDrawingActivity.class);
                        intent.putExtra("name", edtTxtName.getText().toString());
                        intent.putExtra("state", edtTxtState.getText().toString());
                        intent.putExtra("city", edtTxtCity.getText().toString());
                        intent.putExtra("address", edtTxtAddress.getText().toString());
                        intent.putExtra("postalCode", edtTxtPostalCode.getText().toString());
                        intent.putExtra("size", size);
                        intent.putExtra("pDrawing", pDrawing);
                        intent.putExtra("pPrint", pPrint);
                        intent.putExtra("pPost", pPost);
                        intent.setData(imageUri);
                        startActivity(intent);
                    }
                    else if (getIntent().getExtras().getString("orderType").equals("p")){

                        Intent intent = new Intent(context, PaymentPrintActivity.class);
                        intent.putExtra("name", edtTxtName.getText().toString());
                        intent.putExtra("state", edtTxtState.getText().toString());
                        intent.putExtra("city", edtTxtCity.getText().toString());
                        intent.putExtra("address", edtTxtAddress.getText().toString());
                        intent.putExtra("postalCode", edtTxtPostalCode.getText().toString());
                        intent.putExtra("chassis", chassis);
                        intent.putExtra("type", type);
                        intent.putExtra("size", size);
                        intent.setData(imageUri);
                        startActivity(intent);
                    }
                } else
                    Toast.makeText(context, "لطفا همه فیلد ها را کامل کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        edtTxtName = findViewById(R.id.edt_txt_name);
        edtTxtState = findViewById(R.id.edt_txt_state);
        edtTxtCity = findViewById(R.id.edt_txt_city);
        edtTxtAddress = findViewById(R.id.edt_txt_address);
        edtTxtPostalCode = findViewById(R.id.edt_txt_postal_code);
        btnSaveInfo = findViewById(R.id.btn_save_info);
        btnTryAgain = findViewById(R.id.btn_try_again);
        layNoCon = findViewById(R.id.lay_no_con);
        imgBack = findViewById(R.id.img_back);
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

}
