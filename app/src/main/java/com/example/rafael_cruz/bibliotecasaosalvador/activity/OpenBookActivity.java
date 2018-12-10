package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.MyCustomUtil;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Insert;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;

import java.io.File;

/**
 * Open Book Activity.
 * atividade para Abrir um livro.
 *
 * @author Rafael frança
 */
public class OpenBookActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private String idLivro;
    private Livro livro;
    private ProgressDialog dialog;
    private double progress;
    private static boolean isFirstOpen =  true;


    @Override
    protected void onStart() {
        super.onStart();
        if (isFirstOpen){
            getDatabase1();
            isFirstOpen = false;
        }else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        Bundle extra = getIntent().getExtras();
        if (extra!= null){
            Log.e("bundle","Não está null");
            idLivro = extra.getString("id");
        }

    }


    private void getDatabase1(){
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
                        if (livro != null){
                            downloadFile(livro.getLinkDownload(),livro.getNome());
                        }
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                });
    }


    private File bookFile;

    private  void downloadFile(String url, final String nomeLivro) {
        String mNome = MyCustomUtil.removeSpaces(nomeLivro);
        StorageReference islandRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(url+"/livro.pdf");

        bookFile = new File(getFilesDir(), idLivro);

        if (bookFile.exists()) {
            abrirLivro(bookFile.getAbsolutePath());
        } else{

            dialog = new ProgressDialog(OpenBookActivity.this);

            islandRef.getFile(bookFile).addOnProgressListener(taskSnapshot -> {
                if (!dialog.isShowing()) {
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setTitle("Baixando " + livro.getNome());
                    dialog.setMax((int) taskSnapshot.getTotalByteCount());
                    dialog.setCancelable(false);
                    dialog.show();
                }
                progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setProgress((int) progress);
            }).addOnSuccessListener(taskSnapshot -> {
                Log.e("firebase ", ";local tem file created  created " + bookFile.getAbsolutePath());
                Insert insert =  new Insert(this);
                Preferencias preferencias =  new Preferencias(this);
                insert.bookUserOffline(preferencias.getId(),livro);
                abrirLivro(bookFile.getAbsolutePath());
            }).addOnFailureListener(exception -> {
                exception.getCause();
                exception.printStackTrace();
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            });

        }
    }

    private void abrirLivro(String caminhoDoArquivo){
        if (caminhoDoArquivo != null && !caminhoDoArquivo.equals("")) {
            try {
                Preferencias preferencias =  new Preferencias(this);
                Global.Init(OpenBookActivity.this);
                Global.dark_mode = preferencias.getModoNoturno();
                Intent intent = new Intent();
                intent.setClass(this, PDFViewAct.class);
                intent.putExtra("PDFPath", caminhoDoArquivo);
                startActivity(intent);
            } catch (NullPointerException e) {
                Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Selecione um livro.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirstOpen = true;
    }
}
