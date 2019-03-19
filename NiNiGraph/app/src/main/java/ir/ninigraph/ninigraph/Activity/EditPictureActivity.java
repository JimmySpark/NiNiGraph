package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork1Adapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork2Adapter;
import ir.ninigraph.ninigraph.Adapter.SpinnerAdapter;
import ir.ninigraph.ninigraph.Model.OrderPrintPrices;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.ImageUtil;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class EditPictureActivity extends AppCompatActivity {

    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private ConstraintLayout layParent;
    private ConstraintLayout layContents;
    private ProgressBar progressBar;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    private ImageView imgBack;
    private CropImageView cropImageView;
    private ImageView btnRotate;
    private TextView txtPrice;
    private Spinner spinnerChassis;
    private Spinner spinnerType;
    private Spinner spinnerSize;
    private TextView btnNext;
    Uri imageUri;
    List<String> chassisList, typeList, sizeList;
    List<OrderPrintPrices.PricesModel> prices;
    int chassis, type, size;
    boolean isConnected;
    long pChassis, pSize, pPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        initView();

        //Values
        imageUri = getIntent().getData();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        chassisList = new ArrayList<>();
        typeList = new ArrayList<>();
        sizeList = new ArrayList<>();


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

                EditPictureActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //Classes
    private void initView() {
        imgBack = findViewById(R.id.img_back);
        cropImageView = findViewById(R.id.crop_image_view);
        btnRotate = findViewById(R.id.btn_rotate);
        txtPrice = findViewById(R.id.txt_price);
        btnNext = findViewById(R.id.btn_next);
        spinnerChassis = findViewById(R.id.spinner_chassis);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerSize = findViewById(R.id.spinner_size);
        layParent = findViewById(R.id.lay_parent);
        layContents = findViewById(R.id.lay_contents);
        progressBar = findViewById(R.id.progressBar);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);

            //Get Prices
            progressBar.setVisibility(View.VISIBLE);
            layContents.setVisibility(View.GONE);
            getPrices(new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    layContents.setVisibility(View.VISIBLE);

                    //CropImageView
                    cropImageView.setImageBitmap(ImageUtil.getBitmap(context, imageUri));
                    cropImageView.setAspectRatio(4, 6);
                    cropImageView.setFixedAspectRatio(true);
                    btnRotate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            cropImageView.rotateImage(-90);
                        }
                    });

                    //Spinners
                    chassisList.add("شاسی");
                    chassisList.add("بله");
                    chassisList.add("خیر");
                    typeList.add("نوع");
                    typeList.add("مات");
                    typeList.add("براق");
                    typeList.add("ابریشمی");
                    sizeList.add("سایز");
                    sizeList.add("A4 (20×30)");
                    sizeList.add("A5 (15×20)");
                    sizeList.add("A6 (10×20)");

                    spinnerChassis.setAdapter(new SpinnerAdapter(context, chassisList));
                    spinnerType.setAdapter(new SpinnerAdapter(context, typeList));
                    spinnerSize.setAdapter(new SpinnerAdapter(context, sizeList));

                    spinnerChassis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            chassis = (int) id;

                            switch ((int) id) {

                                case 0:
                                    pChassis = 0;
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                                case 1:
                                    pChassis = prices.get(0).print_chassis;
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                                case 2:
                                    pChassis = 0;
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            type = (int) id;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            size = (int) id;

                            switch ((int) id) {

                                case 0:
                                    pSize = 0;
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                                case 1:
                                    pSize = prices.get(0).getPrint_A4();
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                                case 2:
                                    pSize = prices.get(0).getPrint_A5();
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                                case 3:
                                    pSize = prices.get(0).getPrint_A6();
                                    txtPrice.setText(pChassis + pSize + pPost + " تومان");
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    //Next
                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (chassis != 0 && type != 0 && size != 0) {

                                editor.putLong("pPrint", pChassis + pSize).apply();
                                editor.putLong("pPost", pPost).apply();
                                editor.putLong("pAll", pChassis + pSize + pPost).apply();
                                editor.putString("payFor", "چاپ و پست عکس").apply();
                                Intent intent = new Intent(context, CustomerInfoActivity.class);
                                intent.setData(ImageUtil.getImageUri(context, cropImageView.getCroppedImage()));
                                intent.putExtra("orderType", "p");
                                intent.putExtra("chassis", chassis);
                                intent.putExtra("type", type);
                                intent.putExtra("size", size);
                                startActivity(intent);
                            } else
                                Toast.makeText(context, "لطفا همه گزینه ها را مشخص کنید", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void getPrices(final Runnable runnable) {

        apiService.getPrintPrices().enqueue(new Callback<OrderPrintPrices>() {
            @Override
            public void onResponse(Call<OrderPrintPrices> call, Response<OrderPrintPrices> response) {

                prices = response.body().prices;
                pPost = response.body().prices.get(0).getPost();
                runnable.run();
            }

            @Override
            public void onFailure(Call<OrderPrintPrices> call, Throwable t) {


                progressBar.setVisibility(View.GONE);
                layContents.setVisibility(View.VISIBLE);
                Toast.makeText(context, "خطا در برقراری ارتباط با سرور، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
