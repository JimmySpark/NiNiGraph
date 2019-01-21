package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerPaymentCaptionAdapter;
import ir.ninigraph.ninigraph.Model.Discount;
import ir.ninigraph.ninigraph.Model.PaymentCaption;
import ir.ninigraph.ninigraph.Model.Prices;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView txt_payment_title, txt_drawing, txt_cost_drawing, txt_edit, txt_cost_edit, txt_print, txt_cost_print,
            txt_post, txt_cost_post, txt_discount, txt_cost_discount, txt_all, txt_cost_all, btn_enter_discount_code;
    EditText edt_txt_discount_code;
    RecyclerView recycler_payment_caption;
    Button btn_purchase, btn_back;
    List<Integer> selected_themes;
    String order_type;
    long price_drawing, price_edit, price_print, price_post, price_all;
    int order_count;
    boolean isPreferencesSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //Views
        txt_payment_title = findViewById(R.id.txt_payment_title);
        txt_cost_drawing = findViewById(R.id.txt_cost_design);
        txt_cost_edit = findViewById(R.id.txt_cost_edit);
        txt_cost_print = findViewById(R.id.txt_cost_print);
        txt_cost_post = findViewById(R.id.txt_cost_post);
        txt_discount = findViewById(R.id.txt_discount);
        txt_cost_all = findViewById(R.id.txt_cost_all);
        txt_drawing = findViewById(R.id.txt_design);
        txt_edit = findViewById(R.id.txt_edit);
        txt_print = findViewById(R.id.txt_print);
        txt_post = findViewById(R.id.txt_post);
        txt_cost_discount = findViewById(R.id.txt_cost_discount);
        txt_all = findViewById(R.id.txt_all);
        btn_enter_discount_code = findViewById(R.id.btn_enter_discount_code);
        edt_txt_discount_code = findViewById(R.id.edt_txt_discount_code);
        recycler_payment_caption = findViewById(R.id.recycler_payment_caption);
        btn_purchase = findViewById(R.id.btn_purchase);
        btn_back = findViewById(R.id.btn_back);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        selected_themes = new ArrayList<>();
        order_type = preferences.getString("order_type", null);
        isPreferencesSet = preferences.getBoolean("isPreferencesSet", false);


        //Get Order Prices From Server
        if (!isPreferencesSet)
            getPrices();
        else {

            price_drawing = preferences.getLong("drawing", 0);
            price_edit = preferences.getLong("edit", 0);
            price_print = preferences.getLong("print", 0);
            price_post = preferences.getLong("post", 0);

            if (order_type.equals("edit")){

                price_all = preferences.getLong("all", 0);
                order_count = preferences.getInt("order_count", 0);
            }
        }

        //Order Type Action
        if (order_type.equals("edit")){

            //Payment
            Uri data = getIntent().getData();
            ZarinPal.getPurchase(context).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
                @Override
                public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {

                    if (isPaymentSuccess){

                        editor.remove("isPreferencesSet").apply();
                        Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
                        intent.putExtra("refId", refID);
                        intent.putExtra("price", price_all);
                        intent.putExtra("des", "طراحی "+ order_count + " عدد عکس");
                        startActivity(intent);
                    }
                    else{
                        editor.remove("isPreferencesSet").apply();
                        Intent intent = new Intent(context, PaymentUnsuccessfulActivity.class);
                        intent.putExtra("price", price_all);
                        intent.putExtra("des", "طراحی "+ order_count + " عدد عکس");
                        startActivity(intent);
                    }
                }
            });

            btn_purchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    payment(price_all);
                }
            });
        }

        //Apply Discount
        btn_enter_discount_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                applyDiscount(edt_txt_discount_code.getText().toString());
            }
        });

        //Get Caption
        getCaption();

        //Back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("order_type").apply();
                editor.remove("isPreferencesSet").apply();
                editor.remove("drawing").apply();
                editor.remove("edit").apply();
                editor.remove("print").apply();
                editor.remove("post").apply();
                editor.remove("all").apply();
                editor.remove("order_count").apply();
                PaymentActivity.super.onBackPressed();
            }
        });
    }

    //Classes
    private void getPrices(){

        editor.putBoolean("isPreferencesSet", true).apply();

        //Get Selected Theme Id
        for (int i = 0; i < getIntent().getExtras().getInt("count"); i++){

            if (getIntent().getIntegerArrayListExtra("selected_themes" + i) != null){

                List<Integer> x = getIntent().getIntegerArrayListExtra("selected_themes" + i);
                selected_themes.addAll(x);
            }
        }

        //Get Prices
        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Prices>> call = apiService.getPrices();

        call.enqueue(new Callback<List<Prices>>() {
            @Override
            public void onResponse(Call<List<Prices>> call, Response<List<Prices>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        //Preferences
                        editor.putLong("drawing", response.body().get(0).getDrawing()).apply();
                        editor.putLong("edit", response.body().get(0).getEdit()).apply();
                        editor.putLong("print", response.body().get(0).getPrint()).apply();
                        editor.putLong("post", response.body().get(0).getPost()).apply();

                        price_drawing = preferences.getLong("drawing", 0);
                        price_edit = preferences.getLong("edit", 0);
                        price_print = preferences.getLong("print", 0);
                        price_post = preferences.getLong("post", 0);

                        if (order_type.equals("edit")){

                            txt_drawing.setTextColor(Color.parseColor("#999999"));
                            txt_print.setTextColor(Color.parseColor("#999999"));
                            txt_post.setTextColor(Color.parseColor("#999999"));

                            txt_payment_title.setText("هزینه ویرایش " + selected_themes.size() + " عدد عکس");
                            txt_cost_drawing.setText("0");
                            txt_cost_print.setText("0");
                            txt_cost_post.setText("0");
                            txt_cost_discount.setText("0");
                            txt_cost_edit.setText(selected_themes.size() * price_edit + "");
                            txt_cost_all.setText(selected_themes.size() * price_edit + "");
                            price_all = selected_themes.size() * price_edit;
                            editor.putLong("all", selected_themes.size() * price_edit).apply();
                            editor.putInt("order_count", selected_themes.size()).apply();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Prices>> call, Throwable t) {

            }
        });
    }
    private void getCaption(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<PaymentCaption>> call = apiService.getCaption();

        call.enqueue(new Callback<List<PaymentCaption>>() {
            @Override
            public void onResponse(Call<List<PaymentCaption>> call, Response<List<PaymentCaption>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

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
    private void applyDiscount(String code){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Discount>> call = apiService.applyDiscount(code);

        call.enqueue(new Callback<List<Discount>>() {
            @Override
            public void onResponse(Call<List<Discount>> call, Response<List<Discount>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        long percent = ((selected_themes.size() * price_edit) * response.body().get(0).getPercent() / 100);
                        txt_cost_discount.setText(percent + "");
                        txt_cost_all.setText(((selected_themes.size() * price_edit) - percent) + "");
                        editor.putLong("all", (selected_themes.size() * price_edit) - percent).apply();
                        price_all = preferences.getLong("all", 0);
                    }
                    else {

                        Toast.makeText(PaymentActivity.this, "کد تخفیف وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Discount>> call, Throwable t) {

            }
        });
    }
    private void payment(Long price){

        ZarinPal purchase = ZarinPal.getPurchase(context);
        PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();

        paymentRequest.setMerchantID("67118fa0-4537-11e8-9ad7-005056a205be");
        paymentRequest.setAmount(price);
        paymentRequest.setDescription("پرداخت برای سفارش طراحی عکس کودک");
        paymentRequest.setCallbackURL("return://payment");

        purchase.startPayment(paymentRequest, new OnCallbackRequestPaymentListener() {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {

                if (status == 100)
                    startActivity(intent);
                else
                    Toast.makeText(context, "خطا در ایجاد درخواست پرداخت", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {}
}
