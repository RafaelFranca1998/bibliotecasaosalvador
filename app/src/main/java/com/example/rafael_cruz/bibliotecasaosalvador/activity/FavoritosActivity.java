package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerView;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewFavoritos;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FavoritosActivity extends AppCompatActivity{
    private Toolbar toolbar =  null;
    private static List<Livro> listLivros;
    private static RecyclerView listView;
    private LinearLayout linearLayoutFavorito;
    private AdapterRecyclerViewFavoritos adapterListView;
    private String ID_USER;
    private Livro livro;



    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        toolbar.setTitle("Favoritos");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        Preferencias preferencias =  new Preferencias(FavoritosActivity.this);
        ID_USER = preferencias.getId();

        listLivros = new ArrayList<>();
        listView =  findViewById(R.id.recycler_view_favoritos);


        toolbar = findViewById(R.id.toolbar_fav);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.Branco));
        toolbar.setNavigationOnClickListener(v -> finish());

        //-----------------------------------RECYCLERVIEW-------------------------------------------
        linearLayoutFavorito = findViewById(R.id.ll_favoritos);
        adapterListView =  new AdapterRecyclerViewFavoritos(FavoritosActivity.this,listLivros);
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listView.setLayoutManager(gridLayoutManager);
        //------------------------------------------------------------------------------------------
    }

    private void updateList(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("usuarios")
                .document(ID_USER)
                .collection("favoritos")
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
            listView.setVisibility(View.GONE);
        } else {
            linearLayoutFavorito.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

    }
}
