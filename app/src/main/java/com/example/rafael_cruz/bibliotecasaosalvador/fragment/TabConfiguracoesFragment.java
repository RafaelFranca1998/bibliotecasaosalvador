package com.example.rafael_cruz.bibliotecasaosalvador.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.activity.TrocarSenhaActivity;
import com.example.rafael_cruz.bibliotecasaosalvador.config.MyCustomUtil;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.Objects;

public class TabConfiguracoesFragment extends Fragment {

    private TextView txtApagarCache;
    private TextView txtMudarSenha;
    private TextView txtDeletarConta;


    public TabConfiguracoesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_configuracoes, container, false);

        txtApagarCache = view.findViewById(R.id.txt_config_apagar_cache);
        txtMudarSenha =  view.findViewById(R.id.txt_config_mudar_senha);
        txtDeletarConta =  view.findViewById(R.id.txt_config_deletar_conta);

        txtApagarCache.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Apagar Cache")
                .setMessage("Tem certeza que deseja apagar o cache da biblioteca?")
                .setPositiveButton("sim",
                        (dialogInterface, i) -> apagarCache())
                .setNegativeButton("não", null)
                .show());

        txtMudarSenha.setOnClickListener(v -> {
            Intent intent =  new Intent(getContext(),TrocarSenhaActivity.class);
            startActivity(intent);
        });

        txtDeletarConta.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Deletar Conta?")
                .setMessage("Tem certeza que deseja apagar sua conta?")
                .setPositiveButton("sim",
                        (dialogInterface, i) -> deletarConta())
                .setNegativeButton("não", null)
                .show());
        return view;
    }

    private void apagarCache(){
        Preferencias preferencias =  new Preferencias(getActivity());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("usuarios")
                .document(preferencias.getId())
                .collection("offline")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Livro livro;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            livro = document.toObject(Livro.class);
                            deleteLocalFile(livro.getNome());
                            firebaseFirestore
                                    .collection("usuarios")
                                    .document(preferencias.getId())
                                    .collection("offline")
                                    .document(livro.getIdLivro())
                                    .delete();
                        }
                        Toast.makeText(getContext(),"Cache apagado",Toast.LENGTH_LONG).show();
                    } else {
                        Log.w("D", "Error getting documents.", task.getException());
                    }
                });
    }

    private boolean deleteLocalFile(String nomeLivro){
        String mNome = MyCustomUtil.removeSpaces(nomeLivro);
        mNome = MyCustomUtil.unaccent(mNome);
        File bookFile = new File(getContext().getFilesDir(), mNome);
        return bookFile.delete();
    }

    private void deletarConta(){

    }
}
