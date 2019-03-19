package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import ir.ninigraph.ninigraph.Model.Orders;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class OrderDrawingActivity extends AppCompatActivity {

    Context context = this;
    AlertDialog dialog;
    private TextView txtOrderId;
    private TextView txtSize;
    private TextView txtOrderDuration;
    private TextView btnDownload;
    private TextView btnFinish;
    private TextView txtTime;
    private TextView btnDeny;
    private TextView btnAccept;
    private LinearLayout layTime;
    private LinearLayout layButtons;
    int id, size, dExpire, aExpire, status;
    String imageUrl;
    private final int WRITE_PERMISSION_REQUEST = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_drawing);
        initView();

        //Values
        id = getIntent().getIntExtra("id", 0);
        size = getIntent().getIntExtra("size", 0);
        dExpire = getIntent().getIntExtra("dExpire", 0);
        aExpire = getIntent().getIntExtra("aExpire", 0);
        imageUrl = getIntent().getStringExtra("imageUrl");
        status = getIntent().getIntExtra("status", 0);

        //Display
        txtOrderId.setText(String.valueOf(id));
        switch (size) {

            case 30:
                txtSize.setText("(30 × 40)");
                break;
            case 35:
                txtSize.setText("(35 × 50)");
                break;
            case 50:
                txtSize.setText("(50 × 70)");
                break;
        }
        txtOrderDuration.setText(dExpire + " ساعت");
        txtTime.setText(aExpire + " ساعت");
        if (status == 2) {
            layTime.setVisibility(View.GONE);
            layButtons.setVisibility(View.GONE);
        }

        //Download
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_PERMISSION_REQUEST);
                } else {

                    showLoadingDialog();
                    downloadImage(imageUrl);
                }
            }
        });

        //Finish
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDialog();
                finishOrder();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            showLoadingDialog();
            downloadImage(imageUrl);
        }
    }

    private void initView() {
        txtOrderId = findViewById(R.id.txt_order_id);
        txtSize = findViewById(R.id.txt_size);
        txtOrderDuration = findViewById(R.id.txt_order_duration);
        btnDownload = findViewById(R.id.btn_download);
        btnFinish = findViewById(R.id.btn_finish);
        txtTime = findViewById(R.id.txt_time);
        btnDeny = findViewById(R.id.btn_deny);
        btnAccept = findViewById(R.id.btn_accept);
        layTime = findViewById(R.id.lay_time);
        layButtons = findViewById(R.id.lay_buttons);
    }

    private void downloadImage(String url) {

        Calendar calendar = Calendar.getInstance();
        String imgName = calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.MONTH) + "-"
                + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ".jpg";

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/NiNi Graph");
        file.mkdirs();

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalPublicDir("/NiNi Graph/", imgName);

        //Download Starting
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadManager.enqueue(request));
        final Cursor cursor = downloadManager.query(query);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    //Toast.makeText(context, String .valueOf(status), Toast.LENGTH_SHORT).show();
                    switch (status) {

                        case 1:

                            dialog.dismiss();
                            Toast.makeText(context, "عکس با موفقیت دانلود شد", Toast.LENGTH_SHORT).show();
                            break;

                        default:

                            dialog.dismiss();
                            Toast.makeText(context, "خطا در بارگیری عکس، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }, 2000);
    }

    private void showLoadingDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        //Change Layout
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (0.6 / 3));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void finishOrder() {

        JSONObject data = new JSONObject();

        try {
            data.put("type", "finish");
            data.put("orderType", "drawing");
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.getOrders(data.toString()).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        dialog.dismiss();
                        if (response.body().isResult()) {

                            Toast.makeText(context, "سفارش شما با موفقیت کامل شد", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, DesignerPanelActivity.class));
                        } else
                            Toast.makeText(context, "خطا، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "خطا، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
