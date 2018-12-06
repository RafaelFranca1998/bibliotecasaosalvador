package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerView;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.RecyclerItemClickListener;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriasActivity extends AppCompatActivity {

    private String categoria_key,KEY;
    private Livro livro;
    private NavigationView navigationView;
    private static List<Livro> listLivros;
    @SuppressLint("StaticFieldLeak")
    private static RecyclerView listView;
    private AdapterRecyclerView adapterListView;
    static int itemPosition;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        listLivros =  new ArrayList<>();

        listView =  findViewById(R.id.recyclerview_categorias2);

        toolbar = findViewById(R.id.toolbar_2);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            categoria_key = extras.getString("categoria");
            }
        //-------------------------------------------------------
        adapterListView =  new AdapterRecyclerView(CategoriasActivity.this,listLivros);
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);

        updateList();

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CategoriasActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
    }

    private void updateList(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection(getString(R.string.child_book))
                .whereArrayContains("categoria",categoria_key)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listLivros.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            livro = document.toObject(Livro.class);
                            listLivros.add(livro) ;
                        }
                        adapterListView.notifyDataSetChanged();
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        toolbar.setTitle(categoria_key);
    }
}
