package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import ir.ninigraph.ninigraph.Adapter.RecyclerPaymentCaptionAdapter;
import ir.ninigraph.ninigraph.Model.Discount;
import ir.ninigraph.ninigraph.Model.PaymentCaption;
import ir.ninigraph.ninigraph.Model.Prices;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentEditActivity extends AppCompatActivity {

    //Values
    Context context = this;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again, btn_purchase, btn_back;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AlertDialog dialog;
    TextView txt_order_count, txt_drawing, txt_price_drawing, txt_edit, txt_price_edit, txt_print, txt_price_print,
            txt_post, txt_price_post, txt_discount, txt_price_discount, txt_all, txt_price_all, btn_enter_discount_code;
    EditText edt_txt_discount_code;
    RecyclerView recycler_payment_caption;
    List<Integer> selected_themes;
    long discount;
    boolean isPreferencesSet, isDiscountApplied, needToResetData, isConnected, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_edit);

        //Views
        txt_order_count = findViewById(R.id.txt_order_count);
        txt_price_drawing = findViewById(R.id.txt_price_drawing);
        txt_price_edit = findViewById(R.id.txt_price_edit);
        txt_price_print = findViewById(R.id.txt_price_print);
        txt_price_post = findViewById(R.id.txt_price_post);
        txt_discount = findViewById(R.id.txt_discount);
        txt_price_all = findViewById(R.id.txt_price_all);
        txt_drawing = findViewById(R.id.txt_drawing);
        txt_edit = findViewById(R.id.txt_edit);
        txt_print = findViewById(R.id.txt_print);
        txt_post = findViewById(R.id.txt_post);
        txt_price_discount = findViewById(R.id.txt_price_discount);
        txt_all = findViewById(R.id.txt_all);
        btn_enter_discount_code = findViewById(R.id.btn_enter_discount_code);
        edt_txt_discount_code = findViewById(R.id.edt_txt_discount_code);
        recycler_payment_caption = findViewById(R.id.recycler_payment_caption);
        btn_purchase = findViewById(R.id.btn_purchase);
        btn_back = findViewById(R.id.btn_back);
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        selected_themes = new ArrayList<>();
        isPreferencesSet = preferences.getBoolean("isPreferencesSet", false);
        isDiscountApplied = false;
        needToResetData = true;


        //Check Connection
        checkConnection();
        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });


        //Apply And Don't Apply Discount
        btn_enter_discount_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDialog();
                applyDiscount(edt_txt_discount_code.getText().toString());
            }
        });
        edt_txt_discount_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isDiscountApplied && s.length() == 0) {

                    txt_price_discount.setText("0");
                    txt_price_all.setText(preferences.getLong("price_all", 0) + preferences.getLong("discount", 0) + "");
                    editor.putLong("price_all", preferences.getLong("price_all", 0) + preferences.getLong("discount", 0)).apply();

                    isDiscountApplied = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("isPreferencesSet").apply();
                editor.remove("price_edit").apply();
                editor.remove("price_all").apply();
                editor.remove("order_count").apply();
                editor.remove("discount").apply();
                PaymentEditActivity.super.onBackPressed();
            }
        });
    }

    //Classes
    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        } else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);

            //Get Order Prices From Server
            if (!isPreferencesSet) {

                showLoadingDialog();
                getPrices();
            } else {

                if (txt_price_all.getText().equals("")) {

                    editor.remove("isPreferencesSet").apply();
                    editor.remove("price_edit").apply();
                    editor.remove("price_all").apply();
                    editor.remove("order_count").apply();
                    editor.remove("discount").apply();
                    PaymentEditActivity.super.onBackPressed();
                }
            }

            //Payment
            Uri data = getIntent().getData();
            ZarinPal.getPurchase(context).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
                @Override
                public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {

                    if (isPaymentSuccess) {

                        result = saveOrder();
                        editor.remove("isPreferencesSet").apply();
                        Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
                        intent.putExtra("result", result);
                        intent.putExtra("refId", refID);
                        intent.putExtra("price", preferences.getLong("price_all", 0));
                        intent.putExtra("des", "طراحی " + preferences.getInt("order_count", 0) + " عدد عکس");
                        startActivity(intent);
                        finish();
                    } else {

                        saveOrder();
                        editor.remove("isPreferencesSet").apply();
                        Intent intent = new Intent(context, PaymentUnsuccessfulActivity.class);
                        intent.putExtra("price", preferences.getLong("price_all", 0));
                        intent.putExtra("des", "طراحی " + preferences.getInt("order_count", 0) + " عدد عکس");
                        startActivity(intent);
                        finish();
                    }
                }
            });

            btn_purchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showLoadingDialog();
                    payment(preferences.getLong("price_all", 0));
                }
            });

            //Get Caption
            getCaption();
        }
    }

    private void getPrices() {

        editor.putBoolean("isPreferencesSet", true).apply();

        //Get Selected Theme Id
        for (int i = 0; i < getIntent().getExtras().getInt("count"); i++) {

            if (getIntent().getIntegerArrayListExtra("selected_themes" + i) != null) {

                List<Integer> x = getIntent().getIntegerArrayListExtra("selected_themes" + i);
                selected_themes.addAll(x);
            }
        }

        for (int y = 0; y < selected_themes.size(); y++)
            editor.putInt("theme" + y, selected_themes.get(y)).apply();

        //Get Prices
        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Prices>> call = apiService.getPrices();

        call.enqueue(new Callback<List<Prices>>() {
            @Override
            public void onResponse(Call<List<Prices>> call, Response<List<Prices>> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        //Preferences
                        editor.putLong("price_edit", response.body().get(0).getEdit()).apply();

                        txt_edit.setTextColor(Color.parseColor("#000000"));

                        txt_order_count.setText(selected_themes.size() + "");
                        txt_price_edit.setText(selected_themes.size() * preferences.getLong("price_edit", 0) + "");
                        editor.putLong("price_all", selected_themes.size() * preferences.getLong("price_edit", 0)).apply();
                        editor.putInt("order_count", selected_themes.size()).apply();
                        txt_price_all.setText(preferences.getLong("price_all", 0) + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Prices>> call, Throwable t) {

                Toast.makeText(PaymentEditActivity.this, "خطا", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCaption() {

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<PaymentCaption>> call = apiService.getCaption();

        call.enqueue(new Callback<List<PaymentCaption>>() {
            @Override
            public void onResponse(Call<List<PaymentCaption>> call, Response<List<PaymentCaption>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        recycler_payment_caption.setLayoutManager(new LinearLayoutManager(context));
                        recycler_payment_caption.setAdapter(new RecyclerPaymentCaptionAdapter(
                                context,
                                response.body()
                        ));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PaymentCaption>> call, Throwable t) {

            }
        });
    }

    private void applyDiscount(String code) {

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Discount>> call = apiService.applyDiscount(code);

        call.enqueue(new Callback<List<Discount>>() {
            @Override
            public void onResponse(Call<List<Discount>> call, Response<List<Discount>> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        discount = ((selected_themes.size() * preferences.getLong("price_edit", 0)) * response.body().get(0).getPercent() / 100);

                        if ((selected_themes.size() * preferences.getLong("price_edit", 0)) - discount < 100) {

                            if ((selected_themes.size() * preferences.getLong("price_edit", 0)) - discount == 0) {

                                txt_price_discount.setText(discount + "");
                                txt_price_all.setText("رایگان");
                                editor.putLong("discount", discount).apply();
                                editor.putLong("price_all", 0).apply();
                            } else {

                                Long x = (100 - discount) + discount;
                                txt_price_discount.setText((selected_themes.size() * preferences.getLong("price_edit", 0)) - x + "");
                                txt_price_all.setText("100");
                                editor.putLong("discount", (selected_themes.size() * preferences.getLong("price_edit", 0)) - x).apply();
                                editor.putLong("price_all", 100).apply();
                            }
                        } else {

                            txt_price_discount.setText(discount + "");
                            txt_price_all.setText(((selected_themes.size() * preferences.getLong("price_edit", 0)) - discount) + "");
                            editor.putLong("discount", discount).apply();
                            editor.putLong("price_all", (selected_themes.size() * preferences.getLong("price_edit", 0)) - discount).apply();
                        }

                        isDiscountApplied = true;
                    } else {

                        Toast.makeText(PaymentEditActivity.this, "کد تخفیف وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Discount>> call, Throwable t) {

            }
        });
    }

    private void payment(Long price) {

        if (price == 0) {

            dialog.dismiss();
            result = saveOrder();
            editor.remove("isPreferencesSet").apply();
            needToResetData = false;
            Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
            intent.putExtra("result", result);
            intent.putExtra("free", true);
            intent.putExtra("des", "طراحی " + preferences.getInt("order_count", 0) + " عدد عکس");
            startActivity(intent);
            finish();
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

                        needToResetData = false;
                        startActivity(intent);
                    } else
                        Toast.makeText(context, "خطا در ایجاد درخواست پرداخت", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean saveOrder() {

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

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<Save> call = apiService.saveOrderEdit(
                preferences.getInt("order_count", 0),
                preferences.getInt("id", 0),
                themes,
                preferences.getLong("price_all", 0)
        );

        call.enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        result = response.body().isResult();
                    }
                }
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {

                Toast.makeText(PaymentEditActivity.this, "خطا در ارسال درخواست، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });

        return result;
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

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (needToResetData) {

            editor.remove("order_type").apply();
            editor.remove("isPreferencesSet").apply();
            editor.remove("price_edit").apply();
            editor.remove("price_all").apply();
            editor.remove("order_count").apply();
            editor.remove("discount").apply();
        }
    }
}
