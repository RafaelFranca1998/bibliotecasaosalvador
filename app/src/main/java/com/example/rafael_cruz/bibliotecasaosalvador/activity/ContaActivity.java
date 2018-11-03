package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        Button btSair =  findViewById(R.id.bt_logout);
        btSair.setOnClickListener(v -> {
            FirebaseAuth user = FirebaseAuth.getInstance();
            user.signOut();
        });
    }
}
