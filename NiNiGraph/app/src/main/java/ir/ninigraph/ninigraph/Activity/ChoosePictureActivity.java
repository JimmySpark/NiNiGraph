package ir.ninigraph.ninigraph.Activity;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerGalleryAdapter;
import ir.ninigraph.ninigraph.R;

public class ChoosePictureActivity extends AppCompatActivity {

    Context context = this;
    private ImageView imgBack;
    private RecyclerView recyclerGallery;
    public static TextView choosePic;
    public static TextView picCount;
    private ProgressBar progressBar;
    private final int READ_PERMISSION_REQUEST = 30;
    RecyclerGalleryAdapter adapter;
    GridLayoutManager gridLayoutManager;
    List<String> picturesListLoaded, picturesList;
    int minCount, maxCount;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        initView();

        ActivityCompat.requestPermissions(ChoosePictureActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_PERMISSION_REQUEST);

        //Back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChoosePictureActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_PERMISSION_REQUEST && grantResults.length > 0)
            showGallery();
    }

    //Classes
    private void initView() {
        imgBack = findViewById(R.id.img_back);
        recyclerGallery = findViewById(R.id.recycler_gallery);
        choosePic = findViewById(R.id.choose_pic);
        picCount = findViewById(R.id.pic_count);
        progressBar = findViewById(R.id.progressBar);
    }

    private void showGallery() {

        picturesListLoaded = new ArrayList<>();
        picturesList = imageReader();
        minCount = 0;
        maxCount = 18;
        if (picturesList.size() < maxCount)
            maxCount = maxCount - (maxCount - picturesList.size());

        for (int i = minCount; i < maxCount; i++)
            picturesListLoaded.add(picturesList.get(i));

        gridLayoutManager = new GridLayoutManager(context, 3);
        adapter = new RecyclerGalleryAdapter(context, picturesListLoaded);

        recyclerGallery.setLayoutManager(gridLayoutManager);
        recyclerGallery.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        recyclerGallery.setVisibility(View.VISIBLE);

        if (picturesList.size() > maxCount) {

            recyclerGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (gridLayoutManager.findLastVisibleItemPosition() == maxCount - 1) {

                        minCount = maxCount;
                        maxCount += 12;
                        if (picturesList.size() < maxCount)
                            maxCount = maxCount - (maxCount - picturesList.size());

                        for (int i = minCount; i < maxCount; i++)
                            picturesListLoaded.add(picturesList.get(i));

                        showLoadingDialog();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                dialog.dismiss();
                                adapter.notifyItemInserted(maxCount - 1);
                            }
                        }, 3000);
                    }
                }
            });
        }
    }

    private List<String> imageReader() {

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        List<String> arrPath = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath.add(cursor.getString(dataColumnIndex));
        }

        return arrPath;
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
