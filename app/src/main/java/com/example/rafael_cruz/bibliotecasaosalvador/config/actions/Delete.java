/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Base64Custom;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class Delete {
    private OnSuccessDeleteListener listener;
    private Context mContext;
    private Livro mLivro;

    public Delete(Context context,Livro livro) {
        this.listener = null;
        mContext = context;
        mLivro = livro;
    }

    public void deleteBook(){
        StorageReference deleteRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getLinkDownload()+"/"+Base64Custom.renoveSpaces(mLivro.getNome()));
        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteThumbnail();
                Toast.makeText(mContext,R.string.delete_successful,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void deleteThumbnail(){
        StorageReference deleteTumbnailRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mLivro.getImgDownload());
        deleteTumbnailRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteDataFiresTore();
                Toast.makeText(mContext,R.string.delete_successful+" "+R.string.deleted_image,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteDataFiresTore(){
        FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseFirestore.collection(mContext.getString(R.string.child_book))
                .document(mLivro.getIdLivro())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteThumbnail();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,R.string.error_delete+" "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (listener != null) {
                    listener.onCompleteInsert(aVoid);
                }
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
