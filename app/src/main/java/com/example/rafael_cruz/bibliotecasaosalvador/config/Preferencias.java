/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.bibliotecasaosalvador.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO    = "usuario.preferencias";
    private int                         MODE = 0;
    private SharedPreferences.Editor    editor;
    //Chaves finais
    //usuario
    private String CHAVE_NOME       = "nome";
    private String CHAVE_SOBRENOME  = "sobrenome";
    private String CHAVE_EMAIL      = "email";
    private String CHAVE_SENHA      = "senha";
    private String CHAVE_IDENTIFICADOR      = "identificador";
    private String CHAVE_IMAGEM_LINK      = "linkImg";
    //preferencias
    private String CHAVE_MODO_NOTURNO = "modo.noturno";
    private String CHAVE_ENTRAR_AUTOMATICAMENTE = "entrar.automaticamente";




    public Preferencias(Context contextoParametro) {
        contexto    = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor      = preferences.edit();
    }

    public void salvarDados(String nome,String sobrenome, String email, String senha,String id,String linkImg){
        editor.putString(CHAVE_NOME,nome        );
        editor.putString(CHAVE_SOBRENOME,sobrenome);
        editor.putString(CHAVE_EMAIL,email);
        editor.putString(CHAVE_SENHA,senha      );
        editor.putString(CHAVE_IDENTIFICADOR, id);
        editor.putString(CHAVE_IMAGEM_LINK,linkImg);
        editor.commit();
    }

    public void salvarModoNoturno(boolean modoNoturno){
        editor.putBoolean(CHAVE_MODO_NOTURNO,modoNoturno);
        editor.commit();
    }
    public void salvarEntrarAutomaticamente(boolean entrarAutomaticamente){
        editor.putBoolean(CHAVE_ENTRAR_AUTOMATICAMENTE,entrarAutomaticamente);
        editor.commit();
    }

    public HashMap<String,String> getDadosUsuario(){
        HashMap<String,String> dadosUsuario =  new HashMap<>();
        dadosUsuario.put        (CHAVE_NOME,preferences.getString(CHAVE_NOME,null));
        dadosUsuario.put        (CHAVE_EMAIL,preferences.getString(CHAVE_EMAIL,null));
        dadosUsuario.put      (CHAVE_SENHA,preferences.getString(CHAVE_SENHA,null));
        return dadosUsuario;
    }

    public String getNome(){
        return preferences.getString(CHAVE_NOME, null);
    }
    public String getEmail(){
        return preferences.getString(CHAVE_EMAIL, null);
    }
    public String getId(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }
    public String getSobrenome(){
        return  preferences.getString(CHAVE_SOBRENOME,null);
    }
    public boolean getModoNoturno(){
        return  preferences.getBoolean(CHAVE_MODO_NOTURNO,false);
    }
    public boolean getEntrarAutmaticamente(){
        return  preferences.getBoolean(CHAVE_ENTRAR_AUTOMATICAMENTE,true);
    }
    public String getLinkImg(){
        return  preferences.getString(CHAVE_IMAGEM_LINK,null);
    }

}
