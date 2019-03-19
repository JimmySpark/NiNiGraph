package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class ChooseAndUploadThemePicture extends AppCompatActivity {

    Context context = this;
    SharedPreferences preferences;
    int theme_id;
    private final int READ_PERMISSION_REQUEST = 30;
    private final int REQUEST_CHOOSE_IMAGE_GALLERY = 10;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_upload_theme_picture);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        theme_id = getIntent().getIntExtra("theme_id", 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_PERMISSION_REQUEST)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ImageUtil.showImagePicker(context, REQUEST_CHOOSE_IMAGE_GALLERY);
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("اجازه دسترسی به کارت حافظه داده نشده است، برای صدور مجوز به تنظیمات گوشی خود مراجعه کنید");
                builder.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, FollowOrderActivity.class);
                        context.startActivity(intent);
                        finish();
                    }
                });
                builder.create().show();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CHOOSE_IMAGE_GALLERY && resultCode == RESULT_OK) {

            showUploadingDialog();
            uploadPicture(data.getData());
        } else
            super.onBackPressed();
    }

    private void uploadPicture(Uri image) {

        JSONObject data = new JSONObject();

        try {
            data.put("theme_id", theme_id);
            data.put("image", ImageUtil.getStringImage(ImageUtil.getBitmap(context, image)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.uploadThemePicture(data.toString()).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        dialog.dismiss();
                        if (response.body().isResult()) {

                            Toast.makeText(context, "با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, FollowOrderActivity.class);
                            context.startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(context, "اطلاعات ذخیره نشد، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "خطا، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
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
}