package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.ninigraph.ninigraph.Model.Discount;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.ImageUtil;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class PaymentPrintActivity extends AppCompatActivity {

    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private ConstraintLayout layParent;
    private TextView txtPricePrint;
    private TextView txtPricePost;
    private TextView txtPriceDiscount;
    private TextView txtPriceAll;
    private TextView btnEnterDiscountCode;
    private EditText edtTxtDiscountCode;
    private TextView btnBack;
    private TextView btnPurchase;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    private ProgressBar progressApplyingDiscount;
    AlertDialog dialog;
    boolean result, isConnected, isDiscountApplied;
    int id;
    Uri imageUri;
    long discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_print);
        initView();


        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        id = preferences.getInt("id", 0);
        imageUri = getIntent().getData();
        editor.putString("name", getIntent().getStringExtra("name")).apply();
        editor.putString("state", getIntent().getStringExtra("state")).apply();
        editor.putString("city", getIntent().getStringExtra("city")).apply();
        editor.putString("address", getIntent().getStringExtra("address")).apply();
        editor.putString("postalCode", getIntent().getStringExtra("postalCode")).apply();
        editor.putInt("chassis", getIntent().getIntExtra("chassis", 0)).apply();
        editor.putInt("type", getIntent().getIntExtra("type", 0)).apply();
        editor.putInt("size", getIntent().getIntExtra("size", 0)).apply();

        //Check Connection
        checkConnection();
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Purchase
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showUploadingDialog();
                saveOrder();
            }
        });

        //Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentPrintActivity.super.onBackPressed();
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        txtPricePrint = findViewById(R.id.txt_price_print);
        txtPricePost = findViewById(R.id.txt_price_post);
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


            //Set Data
            txtPricePrint.setText(String.valueOf(preferences.getLong("pPrint", 0)));
            txtPricePost.setText(String.valueOf(preferences.getLong("pPost", 0)));
            txtPriceAll.setText(String.valueOf(preferences.getLong("pAll", 0)));

            //Payment
            if (preferences.getBoolean("paymentStarted", false)) {

                editor.putBoolean("paymentStarted", false).apply();
                Uri data = getIntent().getData();
                ZarinPal.getPurchase(context).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
                    @Override
                    public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {

                        if (isPaymentSuccess) {

                            editor.putString("refID", refID).apply();
                            result = saveOrder();

                            Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
                            intent.putExtra("result", result);
                            startActivity(intent);
                            finish();
                        } else {

                            startActivity(new Intent(context, PaymentUnsuccessfulActivity.class));
                            finish();
                        }
                    }
                });
            }
        }
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

                                txtPriceDiscount.setText(discount + "");
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

            dialog.dismiss();
            Intent intent = new Intent(context, PaymentSuccessfulActivity.class);
            intent.putExtra("result", result);
            intent.putExtra("free", true);
            startActivity(intent);
            finish();
        } else {

            ZarinPal purchase = ZarinPal.getPurchase(context);
            PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();

            paymentRequest.setMerchantID("67118fa0-4537-11e8-9ad7-005056a205be");
            paymentRequest.setAmount(price);
            paymentRequest.setDescription("پرداخت برای سفارش چاپ");
            paymentRequest.setCallbackURL("return://payment");

            purchase.startPayment(paymentRequest, new OnCallbackRequestPaymentListener() {
                @Override
                public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {

                    dialog.dismiss();
                    if (status == 100) {

                        editor.putBoolean("paymentStarted", true).apply();
                        startActivity(intent);
                    } else
                        Toast.makeText(context, "خطا در ایجاد درخواست پرداخت", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showUploadingDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_uploading_data, null);

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
        width = (int) ((width) * (((double) 4 / 5)));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private boolean saveOrder() {

        JSONObject data = new JSONObject();
        JSONObject idObj = new JSONObject();
        JSONObject nameObj = new JSONObject();
        JSONObject stateObj = new JSONObject();
        JSONObject cityObj = new JSONObject();
        JSONObject addressObj = new JSONObject();
        JSONObject postalCodeObj = new JSONObject();
        JSONObject chassisObj = new JSONObject();
        JSONObject typeObj = new JSONObject();
        JSONObject sizeObj = new JSONObject();
        JSONObject imageObj = new JSONObject();
        JSONObject priceObj = new JSONObject();

        try {
            idObj.put("client_id", id);
            nameObj.put("name", preferences.getString("name", null));
            stateObj.put("state", preferences.getString("state", null));
            cityObj.put("city", preferences.getString("city", null));
            addressObj.put("address", preferences.getString("address", null));
            postalCodeObj.put("postalCode", preferences.getString("postalCode", null));
            chassisObj.put("chassis", preferences.getInt("chassis", 0));
            typeObj.put("type", preferences.getInt("type", 0));
            sizeObj.put("size", preferences.getInt("size", 0));
            imageObj.put("image", ImageUtil.getStringImage(ImageUtil.getBitmap(context, imageUri)));
            priceObj.put("price", preferences.getLong("pAll", 0));
            data.put("client_id", idObj);
            data.put("name", nameObj);
            data.put("state", stateObj);
            data.put("city", cityObj);
            data.put("address", addressObj);
            data.put("postal_code", postalCodeObj);
            data.put("chassis", chassisObj);
            data.put("type", typeObj);
            data.put("size", sizeObj);
            data.put("image", imageObj);
            data.put("price", priceObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.saveOrderPrint(data.toString()).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        result = response.body().isResult();
                        if (result) {

                            payment(preferences.getLong("pAll", 0));
                        } else
                            Toast.makeText(context, "مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "اطلاعات ثبت نشد، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });

        return result;
    }
}
