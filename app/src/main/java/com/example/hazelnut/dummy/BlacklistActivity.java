package com.example.hazelnut.dummy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BlacklistActivity extends Activity {

    Button outBtn = findViewById(R.id.outBtn);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);


        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(pindah);
            }
        });

    }


}
