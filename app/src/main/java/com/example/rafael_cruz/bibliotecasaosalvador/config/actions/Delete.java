/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.MyCustomUtil;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class Delete {
    private OnSuccessDeleteListener listener;
    private Context mContext;
    private Livro mLivro;

    public Delete(Context context) {
        this.listener = null;
        mContext = context;
    }

    public void deleteBook(Livro livro){
        mLivro = livro;
        StorageReference deleteRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getLinkDownload()+"/"+MyCustomUtil.removeSpaces(mLivro.getNome()));
        deleteRef.delete().addOnSuccessListener(aVoid -> {
            deleteThumbnail(mLivro);
            Toast.makeText(mContext,R.string.delete_successful,Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e ->
                Toast.makeText(mContext,R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show());

    }

    public void deleteUserFav(String idUsuario, Livro l){
        FirebaseFirestore firebaseFirestore =  null;
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("favoritos")
                    .document(l.getIdLivro()).delete().addOnSuccessListener(aVoid -> {
                if (listener != null) {
                    listener.onCompleteInsert(null);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteOffline(String idUsuario, Livro l){
        FirebaseFirestore firebaseFirestore =  null;
        try {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("offline")
                    .document(l.getIdLivro())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onCompleteInsert(null);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteThumbnail(Livro livro){
        mLivro = livro;
        StorageReference deleteTumbnailRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getImgDownload());
        deleteTumbnailRef
                .delete()
                .addOnSuccessListener(aVoid -> {
                    deleteDataFiresTore(mLivro);
                    Toast.makeText(mContext,R.string.delete_successful+
                            " "+R.string.deleted_image,Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e ->
                Toast.makeText(mContext,
                        R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show());
    }


    private void deleteDataFiresTore(Livro livro){
        mLivro = livro;
        FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseFirestore.collection(mContext.getString(R.string.child_book))
                .document(mLivro.getIdLivro())
                .delete()
                .addOnCompleteListener(task -> deleteThumbnail(mLivro))
                .addOnFailureListener(e ->
                        Toast.makeText(mContext,R.string.error_delete+" "+e.getMessage(),
                                Toast.LENGTH_LONG).show())
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onCompleteInsert(aVoid);
                    }
                });
    }

    public interface OnSuccessDeleteListener {
        void onCompleteInsert(@NonNull Void aVoid);
    }

    public void addOnSuccessListener(OnSuccessDeleteListener listener) {
        this.listener = listener;
    }

}
