package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewPesquisar;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class PesquisarActivity extends AppCompatActivity {
    private Toolbar toolbar =  null;
    private FirebaseFirestore firebaseFirestore;
    private Livro livro;
    private ArrayList<Livro> result;
    private RecyclerView recyclerViewPesquisar;
    AdapterRecyclerViewPesquisar adapter;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    Query nameOfTheWritterQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

        result =  new ArrayList<>();

        recyclerViewPesquisar =  findViewById(R.id.recycler_pesquisar);

        toolbar = findViewById(R.id.toolbar_2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        //------------------------------------------------------------------------------------------
        adapter =  new AdapterRecyclerViewPesquisar(this,result);
        recyclerViewPesquisar.setAdapter( adapter );
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerViewPesquisar.setLayoutManager(gridLayoutManager);
        //------------------------------------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("Pesquisar");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) PesquisarActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(PesquisarActivity.this.getComponentName()));
        }

        Objects.requireNonNull(searchView).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processQuery(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processQuery(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void processQuery(String query) {
        firebaseFirestore =  FirebaseFirestore.getInstance();
        result.clear();
        firebaseFirestore.collection("livros")
                .whereGreaterThan("nome",query)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                        result.add( ToHashMap.hashMapToLivro(snapshot.getData()));
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
