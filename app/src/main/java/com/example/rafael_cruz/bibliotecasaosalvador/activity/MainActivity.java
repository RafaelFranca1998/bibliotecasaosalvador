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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.config.Base64Custom;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.AdapterRecyclerView;
import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview.RecyclerItemClickListener;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private NavigationView navigationView=  null;
    private Toolbar toolbar =  null;
    private static List<Livro> listLivros;
    @SuppressLint("StaticFieldLeak")
    private static RecyclerView listView;
    private AdapterRecyclerView adapterListView;

    //----------------------------------------------------------------------------------------------
    private File bookFile;
    private ProgressDialog dialog;
    private ProgressDialog progressDialogHorizontal;
    private double progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listLivros =  new ArrayList<>();
        KEY = getString(R.string.tag_id);
        Preferencias preferencias =  new Preferencias(MainActivity.this);
        ID_USER = preferencias.getId();
        //-----------------------------------FIND VIEWS---------------------------------------------
        listView =  findViewById(R.id.recycler_view_livros);
        toolbar = findViewById(R.id.toolbar_fav);

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
        adapterListView =  new AdapterRecyclerView(MainActivity.this,listLivros);
        listView.setAdapter(adapterListView);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        listView.setLayoutManager(gridLayoutManager);
        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                        intent.putExtra(KEY,listLivros.get(position).getIdLivro());
                        startActivity(intent);                    }
                    @Override public void onLongItemClick(View view, int position) {
                        itemPosition = position;
                    }
                })
        );
        //------------------------------------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        toolbar.setTitle("Principal");
        navigationView.setCheckedItem(R.id.nav_inicio);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    private void updateList(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection(getString(R.string.child_book))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listLivros.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            livro = document.toObject(Livro.class);
                            firebaseFirestore
                                    .collection("usuarios")
                                    .document(ID_USER)
                                    .collection("favoritos")
                                    .document(livro.getIdLivro())
                                    .addSnapshotListener((documentSnapshot, e) -> {
                                        if (Objects.requireNonNull(documentSnapshot).exists()){
                                            livro.setFavorite(true);
                                        }else{
                                            livro.setFavorite(false);
                                        }
                                    });
                            listLivros.add(livro) ;
                        }
                        adapterListView.notifyDataSetChanged();
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                });
    }

    private  void downloadFile(String url, final String nomeLivro) {
        String mNome = Base64Custom.renoveSpaces(nomeLivro);
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
                dialog.setProgress((int) progress);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about){
//            AboutFragment fragment = new AboutFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container, fragment);
//            fragmentTransaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
            if (!item.isChecked()){
                Intent intent =  new Intent(MainActivity.this,FavoritosActivity.class);
                startActivity(intent);}
        }else if (id == R.id.nav_preferencias) {
            if (!item.isChecked()){
//            PreferenciasFragment fragment= new PreferenciasFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container, fragment);
//            fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.nav_conta) {
            if (isConected()){
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

    public static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            itemPosition = listView.indexOfChild(v);
        }
    }

    private boolean isConected(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }


}
