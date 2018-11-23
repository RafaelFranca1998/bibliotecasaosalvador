package com.example.rafael_cruz.bibliotecasaosalvador.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.google.firebase.auth.FirebaseAuth;


public class TabInformacoesFragment extends Fragment {
    private TextView emailUser;
    private TextView nameUser;
    private TextView lastnameUser;
    private FirebaseAuth user;
    private String idUser;


    public TabInformacoesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_informacoes,container,false);
        emailUser = view.findViewById(R.id.txt_email_account);
        nameUser = view.findViewById(R.id.txt_account_username);
        lastnameUser =  view.findViewById(R.id.txt_acount_lastname);

        user = FirebaseAuth.getInstance();
        if (user != null){
            idUser = user.getUid();
        }
        Preferencias preferencias = new Preferencias(getContext());
        emailUser.setText(preferencias.getEmail());
        nameUser.setText(preferencias.getNome());
        lastnameUser.setText(preferencias.getSobrenome());
        return view;
    }
}
