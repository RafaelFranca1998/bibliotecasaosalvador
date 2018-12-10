package com.example.rafael_cruz.bibliotecasaosalvador.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.activity.EditUserInfoActivity;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class TabInformacoesFragment extends Fragment {
    private TextView emailUser;
    private TextView nameUser;
    private TextView lastnameUser;
    private TextView curso;
    private TextView semestre;
    private TextView idade;
    private FirebaseAuth user;
    private String idUser;
    private Button btEdit;
    private Usuario mUsuario;


    public TabInformacoesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_informacoes,container,false);

        emailUser = view.findViewById(R.id.txt_email_account);
        nameUser = view.findViewById(R.id.txt_account_username);
        lastnameUser =  view.findViewById(R.id.txt_acount_lastname);
        curso =  view.findViewById(R.id.txt_account_curso);
        idade =  view.findViewById(R.id.txt_account_idade);
        semestre =  view.findViewById(R.id.txt_account_semestre);
        btEdit = view.findViewById(R.id.bt_edit_user_info);

        user = FirebaseAuth.getInstance();
        if (user != null){
            idUser = user.getUid();
        }
        Preferencias preferencias = new Preferencias(getContext());
        emailUser.setText(preferencias.getEmail());

        btEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditUserInfoActivity.class);
            intent.putExtra("id",preferencias.getId());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDatabase();
    }

    private void getDatabase(){
        mUsuario =  new Usuario();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("usuarios")
                .document(idUser).get().addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    mUsuario = doc.toObject(Usuario.class);
                    nameUser.setText(mUsuario.getNome());
                    lastnameUser.setText(mUsuario.getSobreNome());
                    emailUser.setText(mUsuario.getEmail());
                    curso.setText(mUsuario.getCurso());
                    idade.setText(mUsuario.getIdade());
                    semestre.setText(mUsuario.getSemestre());
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        });
    }
}
