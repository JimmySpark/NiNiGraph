package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ir.ninigraph.ninigraph.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PersonalInfoActivity extends AppCompatActivity {

    //Values
    Button btn_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        //Views
        btn_skip = findViewById(R.id.btn_skip);


        //Skip entering info
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
