package com.tika.app2.Front;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tika.app2.Login.CustomerLoginActivity;
import com.tika.app2.Login.GovtLoginActivity;
import com.tika.app2.R;

public class MainActivity extends AppCompatActivity {

    private Button mGovt,mPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGovt = (Button)findViewById(R.id.Govt);
        mPublic = (Button)findViewById(R.id.Public);

        mGovt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GovtLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
