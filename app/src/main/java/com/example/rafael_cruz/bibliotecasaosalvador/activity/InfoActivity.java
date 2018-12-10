package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.MyCustomUtil;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Delete;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Insert;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class InfoActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private String idLivro;
    private String idUsuario;
    private String livroNome;
    private String linkLivro;
    private Livro livro;
    //----------------------------------------------------------------------------------------------
    private TextView txtNome;
    private TextView txtAutor;
    private TextView txtCategoria;
    private TextView txtAno;
    private TextView txtCurso;
    private TextView txtSituacao;
    //----------------------------------------------------------------------------------------------
    private Button buttonOpen;
    private Button buttonFavoritar;
    private Button buttonBaixar;
    private Button buttonDeleteLocal;
    private String KEY;
    private String url;
    //----------------------------------------------------------------------------------------------
    private ImageView imageViewThumbnail;
    private ProgressBar progressBar;
    private File bookFile;
    private ProgressDialog dialog;
    private ProgressDialog progressDialogHorizontal;
    private double progress;
    private Calendar myCalendar;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //------------------------------------------------------------------------------------------
        String TAG = getString(R.string.tag_debug);
        KEY = getString(R.string.tag_id);

        myCalendar =  new GregorianCalendar();

        progressBar =  findViewById(R.id.progress_bar_thumbnail);
        imageViewThumbnail =  findViewById(R.id.image_view_thumbnail);
        buttonOpen = findViewById(R.id.bt_open_book);
        buttonBaixar =findViewById(R.id.bt_baixar_livro);
        buttonFavoritar =findViewById(R.id.bt_favoritar);
        buttonDeleteLocal =findViewById(R.id.bt_apagar_livro);
        txtNome = findViewById(R.id.tv_nome);
        txtAutor = findViewById(R.id.tv_autor);
        txtCategoria = findViewById(R.id.tv_categoria);
        txtAno = findViewById(R.id.tv_ano);
        txtCurso = findViewById(R.id.tv_curso);
        txtSituacao = findViewById(R.id.tv_situation);

        toolbar =  findViewById(R.id.toolbar_2);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e(TAG,"Não está null");
            idLivro = extra.getString(KEY);
            Preferencias preferencias =  new Preferencias(InfoActivity.this);
            idUsuario = preferencias.getId();
        }

        buttonOpen.setOnClickListener(v -> {
            Intent intent =
                    new Intent(InfoActivity.this,OpenBookActivity.class);
            intent.putExtra(KEY,livro.getIdLivro());
            startActivity(intent);
        });

        buttonBaixar.setOnClickListener(v -> downloadFile(linkLivro,livroNome));

        buttonDeleteLocal.setOnClickListener(v -> {
            boolean confirm = deleteLocalFile(livroNome);
            if (confirm){
                Toast.makeText(InfoActivity.this,
                        "Livro apagado da memória com sucesso",Toast.LENGTH_LONG).show();
                txtSituacao.setTextColor(getResources().getColor(R.color.red));
                txtSituacao.setText(R.string.livro_n_baixado);
                buttonBaixar.setEnabled(true);
                buttonDeleteLocal.setEnabled(false);
                Delete delete = new Delete(this);
                delete.deleteOffline(idUsuario,livro);
            }
        });

        buttonFavoritar.setOnClickListener(v ->{
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("favoritos")
                    .document(idLivro).addSnapshotListener((documentSnapshot, e) -> {
                if (Objects.requireNonNull(documentSnapshot).exists()){
                    Delete delete =  new Delete(InfoActivity.this);
                    delete.deleteUserFav(idUsuario,livro);
                    delete.addOnSuccessListener(aVoid ->
                            buttonFavoritar.setBackgroundColor(getResources().getColor(R.color.red)));
                    firebaseFirestore = null;
                } else {
                    Insert insert =  new Insert(InfoActivity.this);
                    insert.saveUserFav(idUsuario,livro);
                    firebaseFirestore =  null;
                    insert.addOnSuccessListener(taskSnapshot ->
                            buttonFavoritar.setBackgroundColor(getResources().getColor(R.color.Branco))
                    );
                }
            });
        });
        getDatabase1();
    }

    private void getDatabase1(){
        createDialog();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("livros")
                .document(idLivro)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        assert doc != null;
                        livro = doc.toObject(Livro.class);
                        assert livro != null;
                        txtNome.setText(livro.getNome());
                        txtAutor.setText(livro.getAutor());
                        txtCategoria.setText(livro.getCategoria());
                        txtAno.setText(livro.getAno());
                        txtCurso.setText(livro.getArea());
                        url = livro.getImgDownload();
                        livroNome = livro.getNome();
                        linkLivro = livro.getLinkDownload();
                        livro.setDataVisitado(updateLabelData());
                        Insert insert =  new Insert(this);
                        insert.saveLastSee(idUsuario,livro);
                        bookFile = new File(getFilesDir(),idLivro);
                        toolbar.setTitle(MyCustomUtil.removeLines(livroNome));
                        checkLocalFile();
                        dimissDialog();
                        getImg();
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                }).addOnFailureListener(e -> dimissDialog()).addOnCanceledListener(this::dimissDialog);
    }

    private void createDialog(){
        dialog =  new ProgressDialog(InfoActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Carregando");
        dialog.create();
        dialog.show();
    }

    private void dimissDialog(){
        dialog.dismiss();

    }

    private void checkLocalFile(){
        if (bookFile.exists()) {
            txtSituacao.setTextColor(getResources().getColor(R.color.green));
            txtSituacao.setText(R.string.livro_baixado);
            buttonBaixar.setEnabled(false);
            buttonDeleteLocal.setEnabled(true);
        } else {
            txtSituacao.setTextColor(getResources().getColor(R.color.red));
            txtSituacao.setText(R.string.livro_n_baixado);
            buttonBaixar.setEnabled(true);
            buttonDeleteLocal.setEnabled(false);
        }
        //se esta conectado e o arquivo existe
        if (isConected(this)&&bookFile.exists()){
            buttonOpen.setEnabled(true);
        }else if (!isConected(this) && bookFile.exists()) {
            buttonBaixar.setEnabled(false);
            buttonOpen.setEnabled(true);
        }else if (!isConected(this) && !bookFile.exists()) {
            buttonBaixar.setEnabled(false);
            buttonOpen.setEnabled(false);        }
    }

    private  void downloadFile(String url, final String nomeLivro) {
        String mNome = MyCustomUtil.removeSpaces(nomeLivro);
        mNome = MyCustomUtil.unaccent(mNome);
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/livro.pdf");
        bookFile = new File(getFilesDir(), mNome);
        progressDialogHorizontal = new ProgressDialog(InfoActivity.this);
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
            Insert insert =  new Insert(this);
            insert.bookUserOffline(idUsuario,livro);
            txtSituacao.setTextColor(getResources().getColor(R.color.green));
            txtSituacao.setText(R.string.livro_baixado);
            buttonBaixar.setEnabled(false);
            buttonDeleteLocal.setEnabled(true);
            progressDialogHorizontal.dismiss();
        }).addOnFailureListener(exception -> {
            exception.getCause();
            exception.printStackTrace();
            progressDialogHorizontal.dismiss();
            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookFile != null) {
            checkLocalFile();
        }
    }

    private boolean deleteLocalFile(String id){
        bookFile = new File(getFilesDir(), id);
        Delete delete =  new Delete(this);
        delete.deleteOffline(idLivro,livro);
        return bookFile.delete();
    }

    public Date updateLabelData() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        return myCalendar.getTime();
    }

    public Date updateLabelHora() {
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        return myCalendar.getTime();

    }

    private void getImg(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        if (!this.isFinishing ()) {
            if (imageViewThumbnail == null){
                imageViewThumbnail.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
            Glide.with(this).using(new FirebaseImageLoader())
                    .load(storageReference)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageViewThumbnail.setVisibility(View.VISIBLE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageViewThumbnail.setVisibility(View.VISIBLE);
                            return false;
                        }}).into(imageViewThumbnail);
        }
    }

    public static boolean isConected(Context cont){
        ConnectivityManager conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) return true;

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) return true;
        }
        return false;
    }

    public void closeActivity(){
        this.finish();
    }
}
