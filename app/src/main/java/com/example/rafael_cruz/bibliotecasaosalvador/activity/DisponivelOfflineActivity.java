package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewFavoritos;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.RecyclerItemClickListener;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DisponivelOfflineActivity extends AppCompatActivity {

    private Toolbar toolbar =  null;
    private static List<Livro> listLivros;
    private RecyclerView recyclerView;
    private LinearLayout linearLayoutFavorito;
    private AdapterRecyclerViewFavoritos adapterListView;
    private String ID_USER;
    private Livro livro;
    private String KEY;
    private int itemPosition;

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        toolbar.setTitle("Disponíveis Offline");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponivel_offline);
        //------------------------------------------------------------------------------------------
        Preferencias preferencias =  new Preferencias(DisponivelOfflineActivity.this);
        ID_USER = preferencias.getId();
        KEY = getString(R.string.tag_id);
        //------------------------------------------------------------------------------------------
        listLivros = new ArrayList<>();
        recyclerView =  findViewById(R.id.recycler_view_disponiveis_offline);
        //------------------------------------------------------------------------------------------
        toolbar = findViewById(R.id.toolbar_2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        //-----------------------------------RECYCLERVIEW-------------------------------------------
        linearLayoutFavorito = findViewById(R.id.ll_disponivel_offline);
        adapterListView =  new AdapterRecyclerViewFavoritos(this,listLivros);
        recyclerView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        //------------------------------------------------------------------------------------------
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DisponivelOfflineActivity.this,InfoActivity.class);
                intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                itemPosition =  position;
            }
        }));
    }

    private void updateList(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("usuarios")
                .document(ID_USER)
                .collection("offline")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listLivros.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            livro = document.toObject(Livro.class);
                            livro.setFavorite(true);
                            listLivros.add(livro) ;
                        }
                        checkList();
                        adapterListView.notifyDataSetChanged();
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                });
    }

    private void checkList(){
        if (listLivros.size() == 0){
            linearLayoutFavorito.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            linearLayoutFavorito.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
