package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork1Adapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerDrawingWork2Adapter;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.ImageUtil;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewPrintOrderActivity extends AppCompatActivity {

    Context context = this;
    private ConstraintLayout layParent;
    private ImageView imgBack;
    private TextView choosePic;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    boolean isConnected;
    Uri imageUri;
    private final int READ_PERMISSION_REQUEST = 30;
    private final int REQUEST_CHOOSE_IMAGE_GALLERY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_print_order);
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

                NewPrintOrderActivity.super.onBackPressed();
            }
        });

        //Go To Choose Picture
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*ActivityCompat.requestPermissions(NewPrintOrderActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_PERMISSION_REQUEST);*/

                startActivity(new Intent(context, ChoosePictureActivity.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_PERMISSION_REQUEST)
            ImageUtil.showImagePicker(context, REQUEST_CHOOSE_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CHOOSE_IMAGE_GALLERY){

            imageUri = data.getData();

            Intent intent = new Intent(context, EditPictureActivity.class);
            intent.setData(imageUri);
            startActivity(intent);
        }
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        imgBack = findViewById(R.id.img_back);
        choosePic = findViewById(R.id.choose_pic);
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
        }
    }
}
