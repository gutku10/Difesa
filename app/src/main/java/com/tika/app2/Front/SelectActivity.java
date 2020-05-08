package com.tika.app2.Front;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.tika.app2.R;
import com.tika.app2.Selections.Child1Activity;
import com.tika.app2.Selections.PregActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        spinner = findViewById(R.id.spinner);

        List<String> categories = new ArrayList<>();
        categories.add(0,"Choose Category");
        categories.add("Infant");
        categories.add("Children");
        categories.add("Pregnant Woman");

        ArrayAdapter<String> dataAdapter;

        dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) {

                if (parent.getItemAtPosition(position).toString().equals("Choose Category")){
                    //do nothing
                }
                else{
                    String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(),"Selected " +item,Toast.LENGTH_SHORT).show();

                    if(parent.getItemAtPosition(position).equals("Infant")){

                        Intent intent = new Intent(SelectActivity.this, InfantFragment.class);
                        startActivity(intent);
                    }

                    if(parent.getItemAtPosition(position).equals("Children")){

                        Intent intent = new Intent(SelectActivity.this, Child1Activity.class);
                        startActivity(intent);
                    }


                    if(parent.getItemAtPosition(position).equals("Pregnant Woman")){

                        Intent intent = new Intent(SelectActivity.this, PregActivity.class);
                        startActivity(intent);
                    }








                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });

    }


}
