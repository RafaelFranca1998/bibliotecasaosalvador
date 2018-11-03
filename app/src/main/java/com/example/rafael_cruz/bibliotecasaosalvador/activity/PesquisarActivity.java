package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewFavoritos;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PesquisarActivity extends AppCompatActivity {
    private Toolbar toolbar =  null;
    private FirebaseFirestore firebaseFirestore;
    private Livro livro;
    private ArrayList<Livro> result;
    private AdapterRecyclerViewFavoritos adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

        toolbar = findViewById(R.id.toolbar_2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter =  new AdapterRecyclerViewFavoritos(this,result);
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
            searchView.setSearchableInfo(searchManager.getSearchableInfo(PesquisarActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processQuery(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void processQuery(String query) {
        firebaseFirestore =  FirebaseFirestore.getInstance();
        // in real app you'd have it instantiated just once
        result = new ArrayList<>();

        firebaseFirestore.collection("livros")
                .whereArrayContains("nome",query)
                .whereArrayContains("categoria",query)
                .whereArrayContains("autor",query)
                .whereArrayContains("ano",query).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                        result.add( ToHashMap.hashMapToLivro(snapshot.getData()));
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
