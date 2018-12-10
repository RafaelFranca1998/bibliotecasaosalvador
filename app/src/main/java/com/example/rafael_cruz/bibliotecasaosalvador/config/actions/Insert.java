/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.actions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;




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
        this.mContext = context;
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
                    .set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onCompleteInsert(null);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveUserFav(String idUsuario, Livro l){
        firebaseFirestore =  null;
        Map < String, Object > newFav = new HashMap < > ();
        newFav.putAll(ToHashMap.livroToHashMap(l));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("favoritos")
                    .document(l.getIdLivro())
                    .set(newFav).addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onCompleteInsert(null);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bookUserOffline(String idUsuario, Livro l){
        firebaseFirestore =  null;
        Map < String, Object > livroOffline = new HashMap < > ();
        livroOffline.putAll(ToHashMap.livroToHashMap(l));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("offline")
                    .document(l.getIdLivro())
                    .set(livroOffline)
                    .addOnSuccessListener(aVoid -> {
                if (listener != null) {
                    listener.onCompleteInsert(null);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveLastSee(String idUsuario, Livro l){
        firebaseFirestore =  null;
        Map < String, Object > newFav = new HashMap < > ();
        newFav.putAll(ToHashMap.livroToHashMap(l));
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("recentes")
                    .document(l.getIdLivro())
                    .set(newFav).addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onCompleteInsert(null);
                        }
                    });
        }catch (Exception e){
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
