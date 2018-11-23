package com.example.rafael_cruz.bibliotecasaosalvador.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabPreferenciasFragment extends Fragment {

    private boolean entrarAutomaticamente;
    private boolean modoNoturno;
    private Switch modoNoturnoSwitch;
    private Switch entrarAutomaticamenteSwitch;

    public TabPreferenciasFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab_preferencias, container, false);

        modoNoturnoSwitch =  view.findViewById(R.id.switch_entrar_modo_noturno);
        entrarAutomaticamenteSwitch =  view.findViewById(R.id.switch_entrar_automaticamente);

        Preferencias preferencias =  new Preferencias(getActivity());
        entrarAutomaticamente = preferencias.getEntrarAutmaticamente();
        modoNoturno = preferencias.getModoNoturno();

        modoNoturnoSwitch.setChecked(modoNoturno);
        entrarAutomaticamenteSwitch.setChecked(entrarAutomaticamente);

        modoNoturnoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modoNoturno = isChecked;
                preferencias.salvarModoNoturno(modoNoturno);
            }
        });

        entrarAutomaticamenteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entrarAutomaticamente = isChecked;
                preferencias.salvarEntrarAutomaticamente(entrarAutomaticamente);
            }
        });
        return view;
    }

}
