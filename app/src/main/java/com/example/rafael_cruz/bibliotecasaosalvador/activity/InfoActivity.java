package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Base64Custom;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Insert;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class InfoActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
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
    private File bookFile;
    private ProgressDialog dialog;
    private ProgressDialog progressDialogHorizontal;
    private double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //------------------------------------------------------------------------------------------
        String TAG = getString(R.string.tag_debug);
        KEY = getString(R.string.tag_id);

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

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e(TAG,"Não está null");
            idLivro = extra.getString(KEY);
            Preferencias preferencias =  new Preferencias(InfoActivity.this);
            idUsuario = preferencias.getId();
        }

        getDatabase1();

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
            }
        });

        buttonFavoritar.setOnClickListener(v ->{
            Insert insert =  new Insert(InfoActivity.this);
            insert.saveUserFav(idUsuario,livro);
            insert.addOnSuccessListener(taskSnapshot ->
                    buttonFavoritar.setBackgroundColor(getResources().getColor(R.color.red)));
        });

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
                        bookFile = new File(getFilesDir(), Base64Custom.renoveSpaces(livro.getNome()));
                        checkLocalFile();
                        dimissDialog();
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
        String mNome = Base64Custom.renoveSpaces(nomeLivro);
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url + "/" + mNome);
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

    private boolean deleteLocalFile(String nomeLivro){
        String mNome = Base64Custom.renoveSpaces(nomeLivro);
        bookFile = new File(getFilesDir(), mNome);
        return bookFile.delete();
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
