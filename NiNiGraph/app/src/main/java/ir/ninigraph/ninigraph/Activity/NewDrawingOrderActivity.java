package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork1Adapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork2Adapter;
import ir.ninigraph.ninigraph.Model.OrderDrawingData;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.ImageUtil;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class NewDrawingOrderActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    OrderDrawingData orderDrawingDataModel;
    Uri imageUri;
    int size = 0;
    long price;
    private ProgressBar progressBar;
    private TextView choosePic;
    private TextView chooseSize;
    private ImageView imgBack;
    private TextView btnOk;
    private RecyclerView recyclerWork1;
    private LinearLayout layParent;
    private TextView btnChoosePic;
    private TextView txtPrice;
    private TextView txtSize30;
    private TextView txtSize35;
    private TextView txtSize50;
    private RecyclerView recyclerWork2;
    private ConstraintLayout layFirstParent;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    private final int READ_PERMISSION_REQUEST = 30;
    private final int REQUEST_CHOOSE_IMAGE_GALLERY = 10;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drawing_order);
        initView();

        //Values
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Vazir-Bold-FD-WOL.ttf");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();


        //Change Fonts
        choosePic.setTypeface(font);
        chooseSize.setTypeface(font);

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

                NewDrawingOrderActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_PERMISSION_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ImageUtil.showImagePicker(context, REQUEST_CHOOSE_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CHOOSE_IMAGE_GALLERY && resultCode == RESULT_OK) {

            btnChoosePic.setBackgroundResource(R.drawable.bg_button_color_green);
            btnChoosePic.setText("انتخاب شد");
            imageUri = data.getData();
            nextStep();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //Classes
    private void initView() {
        btnOk = findViewById(R.id.btn_ok);
        layParent = findViewById(R.id.lay_parent);
        progressBar = findViewById(R.id.progress_bar);
        choosePic = findViewById(R.id.choose_pic);
        chooseSize = findViewById(R.id.choose_size);
        imgBack = findViewById(R.id.img_back);
        recyclerWork1 = findViewById(R.id.recycler_work1);
        btnChoosePic = findViewById(R.id.btn_choose_pic);
        txtPrice = findViewById(R.id.txt_price);
        txtSize30 = findViewById(R.id.txt_size30);
        txtSize35 = findViewById(R.id.txt_size35);
        txtSize50 = findViewById(R.id.txt_size50);
        recyclerWork2 = findViewById(R.id.recycler_work2);
        layFirstParent = findViewById(R.id.lay_first_parent);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layFirstParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layFirstParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);

            //Get Data
            getData(new Runnable() {
                @Override
                public void run() {

                    recyclerWork1.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    recyclerWork1.setAdapter(new RecyclerDrawingWork1Adapter(context, orderDrawingDataModel.work));

                    progressBar.setVisibility(View.GONE);
                    layParent.setVisibility(View.VISIBLE);

                    recyclerWork2.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    recyclerWork2.setAdapter(new RecyclerDrawingWork2Adapter(context, orderDrawingDataModel.work));

                    txtSize30.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            size = 30;
                            txtSize30.setTextColor(Color.WHITE);
                            txtSize30.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            txtSize35.setTextColor(Color.BLACK);
                            txtSize35.setBackgroundColor(Color.WHITE);
                            txtSize50.setTextColor(Color.BLACK);
                            txtSize50.setBackgroundColor(Color.WHITE);
                            txtPrice.setText(String.valueOf(orderDrawingDataModel.prices.get(0).drawing30));
                            price = orderDrawingDataModel.prices.get(0).drawing30;
                            nextStep();
                        }
                    });

                    txtSize35.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            size = 35;
                            txtSize35.setTextColor(Color.WHITE);
                            txtSize35.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            txtSize30.setTextColor(Color.BLACK);
                            txtSize30.setBackgroundColor(Color.WHITE);
                            txtSize50.setTextColor(Color.BLACK);
                            txtSize50.setBackgroundColor(Color.WHITE);
                            txtPrice.setText(String.valueOf(orderDrawingDataModel.prices.get(0).drawing35));
                            price = orderDrawingDataModel.prices.get(0).drawing35;
                            nextStep();
                        }
                    });

                    txtSize50.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            size = 50;
                            txtSize50.setTextColor(Color.WHITE);
                            txtSize50.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            txtSize30.setTextColor(Color.BLACK);
                            txtSize30.setBackgroundColor(Color.WHITE);
                            txtSize35.setTextColor(Color.BLACK);
                            txtSize35.setBackgroundColor(Color.WHITE);
                            txtPrice.setText(String.valueOf(orderDrawingDataModel.prices.get(0).drawing50));
                            price = orderDrawingDataModel.prices.get(0).drawing50;
                            nextStep();
                        }
                    });

                    btnChoosePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        READ_PERMISSION_REQUEST);
                            }
                            else
                                ImageUtil.showImagePicker(context, REQUEST_CHOOSE_IMAGE_GALLERY);
                        }
                    });

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            editor.remove("paymentStarted").apply();
                            Intent intent = new Intent(getApplicationContext(), CustomerInfoActivity.class);
                            intent.putExtra("orderType", "d");
                            intent.putExtra("size", size);
                            intent.putExtra("pDrawing", price);
                            intent.putExtra("pPrint", orderDrawingDataModel.prices.get(0).print);
                            intent.putExtra("pPost", orderDrawingDataModel.prices.get(0).post);
                            intent.setData(imageUri);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    private void getData(final Runnable runnable) {

        layParent.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        apiService.getDrawingData().enqueue(new Callback<OrderDrawingData>() {
            @Override
            public void onResponse(Call<OrderDrawingData> call, Response<OrderDrawingData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        orderDrawingDataModel = response.body();
                        runnable.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDrawingData> call, Throwable t) {

                Toast.makeText(context, "خطا در دریافت اطلاعات از سرور، لطفا مجددا امتحان کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nextStep() {

        if (size != 0 && imageUri != null)

            btnOk.setVisibility(View.VISIBLE);
        else
            btnOk.setVisibility(View.GONE);
    }
}