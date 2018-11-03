/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.actions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Base64Custom;
import com.example.rafael_cruz.bibliotecasaosalvador.config.DAO;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;



public class Insert {
    private OnSuccessInsertListener listener;
    private OnSuccessSendListener sendListener;
    private Context mContext;
    private Bitmap imagemCapa;
    private ProgressDialog pd;
    private Livro mLivro;
    private Categoria mCategoria;
    private Usuario mUsuario;
    private double progress;
    private FirebaseFirestore firebaseFirestore;

    /**
     * Construtor da classe;
     * @param context contexto da aplicação.
     */
    public Insert(@NonNull Context context) {
        this.listener = null;
        this.sendListener = null;
        mContext = context;
    }


    public void saveUserInFireStore(Usuario usuario){
        mUsuario = usuario;
        firebaseFirestore =  null;
        Map < String, Object > newUser = new HashMap < > ();
        newUser.putAll(ToHashMap.userToHashMap(mUsuario));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(mUsuario.getIdUsuario())
                    .set(newUser).addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onCompleteInsert(null);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map < String, Object > newContact;
    public void saveCategoryFireStore(Categoria categoria){
        mCategoria = categoria;
        newContact = new HashMap < > ();
        newContact.putAll(ToHashMap.categoryToHashMap(mCategoria));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(mContext.getString(R.string.child_category))
                    .document(mCategoria.getCategoryName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    assert documentSnapshot != null;
                    if (!documentSnapshot.exists()){
                        firebaseFirestore
                                .collection(mContext.getString(R.string.child_category))
                                .document(mCategoria.getCategoryName())
                                .set(newContact)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (listener != null) {
                                            listener.onCompleteInsert(null);
                                        }
                                    }
                                });
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }




    public interface OnSuccessInsertListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public interface OnSuccessSendListener {void onCompleteInsert(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnSuccessInsertListener listener) {
        this.listener = listener;
    }

    public void addOnSuccessSendListener(OnSuccessSendListener listener) {
        this.sendListener = listener;
    }
}
