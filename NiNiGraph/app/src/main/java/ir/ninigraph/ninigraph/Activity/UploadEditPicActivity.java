package ir.ninigraph.ninigraph.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ir.ninigraph.ninigraph.R;

public class UploadEditPicActivity extends AppCompatActivity {

    //Values
    int order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_edit_pic);

        //Values
        order_id = getIntent().getIntExtra("order_id", 0);

        Toast.makeText(this, order_id + "", Toast.LENGTH_SHORT).show();
    }
}
