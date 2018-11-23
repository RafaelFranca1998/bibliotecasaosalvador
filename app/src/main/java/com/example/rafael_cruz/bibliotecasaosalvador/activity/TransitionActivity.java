/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.ToHashMap;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class TransitionActivity extends AppCompatActivity {
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        try{
            getUser();
        }catch (Exception e){

        }
    }

    private void getUser(){
        try {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            firebaseFirestore
                    .collection("usuarios")
                    .document(user.getUid())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    usuario = ToHashMap.hashMapToUser(documentSnapshot.getData());
                    Preferencias preferencias = new Preferencias(TransitionActivity.this);
                    preferencias.salvarDados(
                            usuario.getNome(),
                            usuario.getSobreNome(),
                            usuario.getEmail(),
                            usuario.getSenha(),
                            usuario.getIdUsuario(),
                            usuario.getLinkImgAccount());
                    closeActivity();
                } else {
                    Log.w("D", "Error getting documents");
                    closeActivity();
                }
            });
        }catch (Exception e){
            closeActivity();
        }
    }

    private void closeActivity(){
        Intent intent =  new Intent(TransitionActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
