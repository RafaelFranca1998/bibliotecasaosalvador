/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.actions;

import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;



public class Update {

    private OnUpdateSuccessListener listener;

    public Update() {
    }
    /**
     * salva a categoria ap&oacute;s a edi&ccedil;&atilde;o.
     * @param categoria objeto do tipo {@link Categoria}.
     * @param oldName nome antigo da categoria.
     */
    public void editCategory(Categoria categoria, String oldName){
        try {
            Map < String, Object > editCategory = new HashMap < > ();
            editCategory.putAll(ToHashMap.categoryToHashMap(categoria));
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(oldName)
                    .delete();
            firebaseFirestore =  FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("categorias")
                    .document(categoria.getCategoryName())
                    .set(editCategory).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (listener != null) {
                        listener.onCompleteUpdate(null);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface OnUpdateSuccessListener {void onCompleteUpdate(UploadTask.TaskSnapshot taskSnapshot);}

    public void addOnSuccessListener(OnUpdateSuccessListener listener) {
        this.listener = listener;
    }
}
