package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.rafael_cruz.bibliotecasaosalvador.R;


public class FavoritosActivity extends AppCompatActivity{
    private Toolbar toolbar =  null;

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("Favoritos");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        toolbar = findViewById(R.id.toolbar_2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

    }
}
