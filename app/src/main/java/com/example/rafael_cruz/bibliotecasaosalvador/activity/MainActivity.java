package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.config.MyCustomUtil;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerView;
import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewCategory;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerViewPesquisar;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.RecyclerItemClickListener;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int itemPosition;
    private String KEY;
    private String ID_USER;

    private Livro livro;
    private Livro livroRecente;


    private static List<Livro> listLivros;
    private static List<Categoria> listCategoria;
    private static List<Livro> listLivrosRecentes;

    @SuppressLint("StaticFieldLeak")
    private static RecyclerView recyclerViewRecomendados;
    private RecyclerView recyclerViewRecentes;
    private RecyclerView recyclerViewCategoria;

    private AdapterRecyclerView adapterListView;
    private AdapterRecyclerViewCategory adapterListViewCategoria;
    private AdapterRecyclerViewPesquisar adapterListViewRecentes;

    private LinearLayout ll_recentes;
    private View llLoading;
    private View llScrollView;
    private ScrollView scrollViewMain = null;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;

    //----------------------------------------------------------------------------------------------
    private File bookFile;
    private ProgressDialog progressDialogHorizontal;
    private double progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listLivros =  new ArrayList<>();
        listCategoria =  new ArrayList<>();
        listLivrosRecentes =  new ArrayList<>();
        //------------------------------------------------------------------------------------------
        KEY = getString(R.string.tag_id);
        Preferencias preferencias =  new Preferencias(MainActivity.this);
        ID_USER = preferencias.getId();
        //-----------------------------------FIND VIEWS---------------------------------------------
        recyclerViewRecomendados = findViewById(R.id.recycler_view_livros);
        recyclerViewCategoria = findViewById(R.id.recycler_view_category);
        recyclerViewRecentes = findViewById(R.id.recycler_view_ultimos);
        toolbar = findViewById(R.id.toolbar_fav);

        scrollViewMain = findViewById(R.id.scroll_view_main);
        ll_recentes = findViewById(R.id.ll_visto_ultimo);
        llLoading =  findViewById(R.id.loading_layout);
        llScrollView =  findViewById(R.id.ll_scrollview_main);

        setSupportActionBar(toolbar);
        //----------------------------------NAV-----------------------------------------------------
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //-----------------------------------RECYCLERVIEW-------------------------------------------
        adapterListView =
                new AdapterRecyclerView(MainActivity.this,listLivros);
        adapterListViewCategoria =
                new AdapterRecyclerViewCategory(MainActivity.this,listCategoria);
        adapterListViewRecentes =
                new AdapterRecyclerViewPesquisar(MainActivity.this,listLivrosRecentes);
        //------------------------------------------------------------------------------------------
        recyclerViewCategoria.setAdapter(adapterListViewCategoria);
        recyclerViewRecomendados.setAdapter(adapterListView);
        recyclerViewRecentes.setAdapter(adapterListViewRecentes);
        //------------------------------------------------------------------------------------------
        StaggeredGridLayoutManager gridLayoutManagerRecentes =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager gridLayoutManagerCategoria =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        //------------------------------------------------------------------------------------------
        recyclerViewRecomendados.setLayoutManager(gridLayoutManager);
        recyclerViewRecentes.setLayoutManager(gridLayoutManagerRecentes);
        recyclerViewCategoria.setLayoutManager(gridLayoutManagerCategoria);
        //------------------------------------------------------------------------------------------
        recyclerViewRecentes.addOnItemTouchListener(new RecyclerItemClickListener
                (this, recyclerViewRecomendados, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
        recyclerViewRecomendados.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewRecomendados,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
        recyclerViewCategoria.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewCategoria,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,CategoriasActivity.class);
                        intent.putExtra("categoria",listCategoria.get(position).getCategoryName());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
        //------------------------------------------------------------------------------------------
        updateAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAll();
    }

    private void updateAll(){
        llLoading.setVisibility(View.VISIBLE);
        llScrollView.setVisibility(View.GONE);
        try {
            updateRecomendados();
            updateCategorias();
            if (isConnected()) {
                if (ID_USER != null) {
                    updateVistoUltimo();
                } else {
                    ll_recentes.setVisibility(View.GONE);
                    llLoading.setVisibility(View.GONE);
                    llScrollView.setVisibility(View.VISIBLE);
                }
                ll_recentes.setVisibility(View.VISIBLE);
                llLoading.setVisibility(View.GONE);
                llScrollView.setVisibility(View.VISIBLE);
            } else {
                ll_recentes.setVisibility(View.GONE);
                llLoading.setVisibility(View.GONE);
                llScrollView.setVisibility(View.VISIBLE);
            }
            toolbar.setTitle("Principal");
            navigationView.setCheckedItem(R.id.nav_inicio);
        }catch (Exception e ){
            llLoading.setVisibility(View.GONE);
            llScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                try{
                    Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                    intent.putExtra(KEY,listLivros.get(itemPosition).getIdLivro());
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Intent intent = new Intent(MainActivity.this, OpenBookActivity.class);
                    intent.putExtra(KEY, listLivros.get(itemPosition).getIdLivro());
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
                    downloadFile(listLivros.get(itemPosition).getLinkDownload(),listLivros.get(itemPosition).getNome());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 3:
//                Delete delete =  new Delete(MainActivity.this,listLivros.get(itemPosition));
//                delete.deleteBook();
//                delete.addOnSuccessListener(new Delete.OnSuccessDeleteListener() {
//                    @Override
//                    public void onCompleteInsert(@NonNull Void aVoid) {
//                        adapterListView.notifyDataSetChanged();
//                    }
//                });
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void updateRecomendados(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection(getString(R.string.child_book))
                .orderBy("dataAdicionado",Query.Direction.DESCENDING)
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

    private void updateVistoUltimo(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        scrollViewMain.setVisibility(View.GONE);
        firebaseFirestore
                .collection("usuarios")
                .document(ID_USER)
                .collection("recentes")
                .orderBy("dataVisitado",Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            listLivrosRecentes.clear();
            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                livroRecente = ToHashMap.hashMapToLivro(documentSnapshot.getData());
                listLivrosRecentes.add(livroRecente);
                if (listLivrosRecentes.size()>= 5){
                    break;
                }
            }
            scrollViewMain.setVisibility(View.VISIBLE);
            adapterListViewRecentes.notifyDataSetChanged();
            if (listLivrosRecentes.size()== 0){
                ll_recentes.setVisibility(View.GONE);
            }
        }).addOnCompleteListener(task -> scrollViewMain.setVisibility(View.VISIBLE))
                .addOnCanceledListener(() -> scrollViewMain.setVisibility(View.VISIBLE))
                .addOnFailureListener(e -> scrollViewMain.setVisibility(View.VISIBLE));
    }

    private void updateCategorias(){
        FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("categorias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listCategoria.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            listCategoria.add(document.toObject(Categoria.class)) ;
                        }
                        adapterListViewCategoria.notifyDataSetChanged();
                    } else {
                        Log.w("Error: ", "Error getting documents.", task.getException());
                    }
                });
    }

    private void updateAleatorio(){
        FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("livros")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listCategoria.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            listCategoria.add(document.toObject(Categoria.class)) ;
                        }
                        adapterListViewCategoria.notifyDataSetChanged();
                    } else {
                        Log.w("Error: ", "Error getting documents.", task.getException());
                    }
                });
    }

    private  void downloadFile(String url, final String nomeLivro) {
        String mNome = MyCustomUtil.removeSpaces(nomeLivro);
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/" + mNome);
        bookFile = new File(getFilesDir(), mNome);
        if (!bookFile.exists()) {
            progressDialogHorizontal = new ProgressDialog(MainActivity.this);
            islandRef.getFile(bookFile).addOnProgressListener(taskSnapshot -> {
                if (!progressDialogHorizontal.isShowing()) {
                    progressDialogHorizontal.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialogHorizontal.setTitle("Baixando " + livro.getNome());
                    progressDialogHorizontal.setMax((int) taskSnapshot.getTotalByteCount());
                    progressDialogHorizontal.setCancelable(false);
                    progressDialogHorizontal.show();
                }
                progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            }).addOnSuccessListener(taskSnapshot -> {
                Log.e("firebase ", ";local tem file created  created " + bookFile.getAbsolutePath());
                progressDialogHorizontal.dismiss();
            }).addOnFailureListener(exception -> {
                exception.getCause();
                exception.printStackTrace();
                progressDialogHorizontal.dismiss();
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            });
        } else {
            Toast.makeText(MainActivity.this,"Livro já disponivel Offline!",Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            if (!item.isChecked()){
                Intent intent =  new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);}
        } else if (id == R.id.nav_pesquisar) {
            if (!item.isChecked()){
                Intent intent =  new Intent(MainActivity.this,PesquisarActivity.class);
                startActivity(intent);}
        } else if (id == R.id.nav_favoritos) {
            if (isConnected()){
            if (!item.isChecked()) {
                Intent intent = new Intent(MainActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }else {
                Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                startActivity( intent );
            }}
        } else if (id == R.id.nav_disponivel_offline) {
            if (!item.isChecked()){
                if (isConnected()){
                    Intent intent =  new Intent(MainActivity.this,DisponivelOfflineActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                    startActivity( intent );
                }
                }
        } else if (id == R.id.nav_conta) {
            if (isConnected()){
                Intent intent =  new Intent(MainActivity.this,ContaActivity.class);
                startActivity( intent );
            } else {
                Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                startActivity( intent );
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class MyOnClickListenerRecomendados implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            itemPosition = recyclerViewRecomendados.indexOfChild(v);
        }
    }

    private boolean isConnected(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    /** todo aqui código favorito
     *  firebaseFirestore
     *                                     .collection("livros")
     *                                     .document(ID_USER)
     *                                     .collection("favoritos")
     *                                     .document(livro.getIdLivro())
     *                                     .addSnapshotListener((documentSnapshot, e) -> {
     *                                         if (Objects.requireNonNull(documentSnapshot).exists()){
     *                                             livro.setFavorite(true);
     *                                         }else{
     *                                             livro.setFavorite(false);
     *                                         }
     *                                     });
     */

}
