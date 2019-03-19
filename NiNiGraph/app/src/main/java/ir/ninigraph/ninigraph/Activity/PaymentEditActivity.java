package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Model.Discount;
import ir.ninigraph.ninigraph.Model.Prices;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class PaymentEditActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AlertDialog dialog;
    List<Integer> selected_themes;
    long discount;
    boolean isDiscountApplied, isConnected, result;
    private ConstraintLayout layParent;
    private TextView txtPaymentTitle;
    private TextView txtPriceEdit;
    private TextView txtPriceDiscount;
    private TextView txtPriceAll;
    private TextView btnEnterDiscountCode;
    private EditText edtTxtDiscountCode;
    private ProgressBar progressApplyingDiscount;
    private TextView btnBack;
    private TextView btnPurchase;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_edit);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.remove("prefs").apply();
        selected_themes = new ArrayList<>();
        isDiscountApplied = false;


        //Check Connection
        checkConnection();
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Discount
        btnEnterDiscountCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isDiscountApplied) {

                    progressApplyingDiscount.setVisibility(View.VISIBLE);
                    applyDiscount(edtTxtDiscountCode.getText().toString());
                }
            }
        });
        edtTxtDiscountCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isDiscountApplied && s.length() == 0) {

                    txtPriceDiscount.setText("0");
                    txtPriceAll.setText(String.valueOf(preferences.getLong("pAll", 0) + preferences.getLong("discount", 0)));
                    editor.putLong("pAll", preferences.getLong("pAll", 0) + preferences.getLong("discount", 0)).apply();

                    isDiscountApplied = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("prefs").apply();
                PaymentEditActivity.super.onBackPressed();
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
        PaymentEditActivity.super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (preferences.getBoolean("resetData", false))
            editor.remove("prefs").apply();
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        txtPaymentTitle = findViewById(R.id.txt_payment_title);
        txtPriceEdit = findViewById(R.id.txt_price_edit);
        txtPriceDiscount = findViewById(R.id.txt_price_discount);
        txtPriceAll = findViewById(R.id.txt_price_all);
        btnEnterDiscountCode = findViewById(R.id.btn_enter_discount_code);
        edtTxtDiscountCode = findViewById(R.id.edt_txt_discount_code);
        btnBack = findViewById(R.id.btn_back);
        btnPurchase = findViewById(R.id.btn_purchase);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
        progressApplyingDiscount = findViewById(R.id.progressApplyingDiscount);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);


            if (!preferences.getBoolean("prefs", false)) {

                //Get Order Prices From Server
                showLoadingDialog();
                getPrices();
            }

            //Payment
            Uri data = getIntent().getData();
            ZarinPal.getPurchase(context).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
                @Override
                public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {

                    if (isPaymentSuccess) {

                        showLoadingDialog();
                        editor.putString("refID", refID).apply();
                        saveOrder();
                    } else {

                        startActivity(new Intent(context, PaymentUnsuccessfulActivity.class));
                        finish();
                    }
                }
            });

            btnPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showLoadingDialog();
                    payment(preferences.getLong("pAll", 0));
                }
            });
        }
    }

    private void getPrices() {

        //Get Themes
        for (int i = 0; i < getIntent().getExtras().getInt("count"); i++) {

            if (getIntent().getIntegerArrayListExtra("selected_themes" + i) != null) {

                List<Integer> x = getIntent().getIntegerArrayListExtra("selected_themes" + i);
                selected_themes.addAll(x);
            }
        }
        for (int y = 0; y < selected_themes.size(); y++)
            editor.putInt("theme" + y, selected_themes.get(y)).apply();

        //Get Prices
        apiService.getPrices().enqueue(new Callback<List<Prices>>() {
            @Override
            public void onResponse(Call<List<Prices>> call, Response<List<Prices>> response) {

                if (response.isSuccessful()) {

                    dialog.dismiss();
                    if (response.body() != null) {

                        //Preferences
                        editor.putBoolean("prefs", true).apply();
                        editor.putInt("order_count", selected_themes.size()).apply();
                        editor.putLong("price_edit", response.body().get(0).getEdit()).apply();
                        editor.putLong("pAll", selected_themes.size() * preferences.getLong("price_edit", 0)).apply();
                        editor.putString("payFor", "طراحی " + preferences.getInt("order_count", 0) + " عدد عکس").apply();
                        editor.putInt("d", 8).apply();

                        txtPaymentTitle.setText("هزینه ویرایش " + preferences.getInt("order_count", 0) + " عدد عکس");
                        txtPriceEdit.setText(String.valueOf(preferences.getInt("order_count", 0) * preferences.getLong("price_edit", 0)));
                        txtPriceAll.setText(String.valueOf(preferences.getLong("pAll", 0)));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Prices>> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(PaymentEditActivity.this, "خطا", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyDiscount(String code) {

        apiService.applyDiscount(code).enqueue(new Callback<List<Discount>>() {
            @Override
            public void onResponse(Call<List<Discount>> call, Response<List<Discount>> response) {

                progressApplyingDiscount.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        discount = preferences.getLong("pAll", 0) * response.body().get(0).getPercent() / 100;

                        if (preferences.getLong("pAll", 0) - discount < 100) {

                            if (preferences.getLong("pAll", 0) - discount == 0) {

                                txtPriceDiscount.setText(String.valueOf(discount));
                                txtPriceAll.setText("رایگان");
                                editor.putLong("discount", discount).apply();
                                editor.putLong("pAll", 0).apply();
                            } else {

                                Long x = (100 - discount) + discount;
                                txtPriceDiscount.setText(String.valueOf(preferences.getLong("pAll", 0) - x));
                                txtPriceAll.setText("100");
                                editor.putLong("discount", preferences.getLong("pAll", 0) - x).apply();
                                editor.putLong("pAll", 100).apply();
                            }
                        } else {

                            txtPriceDiscount.setText(String.valueOf(discount));
                            txtPriceAll.setText(String.valueOf((preferences.getLong("pAll", 0) - discount)));
                            editor.putLong("discount", discount).apply();
                            editor.putLong("pAll", preferences.getLong("pAll", 0) - discount).apply();
                        }

                        isDiscountApplied = true;
                    } else {

                        Toast.makeText(context, "کد تخفیف وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Discount>> call, Throwable t) {

                progressApplyingDiscount.setVisibility(View.GONE);
                Toast.makeText(context, "خطایی رخ داد، لطفا مجددا امتحان کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payment(Long price) {

        if (price == 0) {

            saveOrder();
        } else {

            ZarinPal purchase = ZarinPal.getPurchase(context);
            PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();

            paymentRequest.setMerchantID("67118fa0-4537-11e8-9ad7-005056a205be");
            paymentRequest.setAmount(price);
            paymentRequest.setDescription("پرداخت برای سفارش طراحی عکس کودک");
            paymentRequest.setCallbackURL("return://payment");

            purchase.startPayment(paymentRequest, new OnCallbackRequestPaymentListener() {
                @Override
                public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {

                    dialog.dismiss();
                    if (status == 100) {

                        startActivity(intent);
                    } else
                        Toast.makeText(context, "خطا در ایجاد درخواست پرداخت", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveOrder() {

        JSONArray themes = new JSONArray();

        try {
            for (int i = 0; i < preferences.getInt("order_count", 0); i++) {

                JSONObject theme = new JSONObject();
                theme.put("theme", preferences.getInt("theme" + i, 0));
                themes.put(theme);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        apiService.saveOrderEdit(
                preferences.getInt("id", 0),
                preferences.getInt("order_count", 0),
                preferences.getLong("pAll", 0),
                themes
        ).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        dialog.dismiss();
                        result = response.body().isResult();
                        if (result) {

                            Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
                            intent.putExtra("result", result);
                            if (preferences.getLong("pAll", 0) == 0)
                                intent.putExtra("free", true);
                            startActivity(intent);
                            finish();
                        } else {

                            startActivity(new Intent(context, PaymentUnsuccessfulActivity.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(PaymentEditActivity.this, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
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
}
