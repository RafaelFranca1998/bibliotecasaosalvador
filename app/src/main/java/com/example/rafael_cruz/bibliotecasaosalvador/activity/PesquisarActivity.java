package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;

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


    CollectionReference searchRef;

    private void processQuery(String query) {
        result.clear();
        if (firebaseFirestore == null){
            firebaseFirestore =  FirebaseFirestore.getInstance();
            searchRef = firebaseFirestore.collection("livros");
        }
        String output;
        try {
            output = query.substring(0, 1).toUpperCase() + query.substring(1);
        }catch (Exception e){
            e.printStackTrace();
            output = "";
        }

        firebaseFirestore.collection("livros")
                .whereGreaterThanOrEqualTo("nome",query)
                .whereGreaterThanOrEqualTo("nome",capitalize(output))
                .whereGreaterThanOrEqualTo("nome",query.toUpperCase())
                .whereGreaterThanOrEqualTo("nome",query.toLowerCase())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                        result.add( ToHashMap.hashMapToLivro(snapshot.getData()));
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    result.add( ToHashMap.hashMapToLivro(snapshot.getData()));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private String capitalize(String value){
        try {
            return value = new StringBuilder()
                    .append(value.substring(0, 1).toUpperCase())
                    .append(value.substring(1))
                    .toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
