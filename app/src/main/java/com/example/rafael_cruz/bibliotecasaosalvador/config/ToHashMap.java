/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config;

import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ToHashMap {

    public ToHashMap() {
    }

    public static HashMap<String,String> categoryToHashMap(Categoria object){
        HashMap<String,String> map =  new HashMap<>();
        map.put("categoryName", object.getCategoryName());
        map.put("imgDownload", object.getImgDownload());
        return map;
    }
    public static Categoria categoryToHashMap(HashMap<String,String> m){
        Categoria object =  new Categoria();
        object.setCategoryName(m.get("categoryName"));
        object.setImgDownload( m.get("imgDownload"));
        return object;
    }
    public static HashMap<String,Object> livroToHashMap(Livro object){
        HashMap<String,Object> map =  new HashMap<>();
        try {
            map.put("nome", object.getNome());
            map.put("idLivro", object.getIdLivro());
            map.put("editora", object.getEditora());
            map.put("ano", object.getAno());
            map.put("autor", object.getAutor());
            map.put("categoria", object.getCategoria());
            map.put("area", object.getArea());
            map.put("linkDownload", object.getLinkDownload());
            map.put("imgDownload", object.getImgDownload());
            map.put("dataAdicionado", object.getDataAdicionado());
            map.put("dataVisitado", object.getDataVisitado());
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
    //todo ainda falta
    public static Livro hashMapToLivro(Map<String, Object> map){
        Livro  object =  new Livro();
        try {
            object.setNome(Objects.requireNonNull(map.get("nome")).toString());
            object.setIdLivro(Objects.requireNonNull(map.get("idLivro")).toString());
            object.setEditora(Objects.requireNonNull(map.get("editora")).toString());
            //object.setEdicao(map.get("edicao").toString());
            object.setAno(Objects.requireNonNull(map.get("ano")).toString());
            object.setAutor(Objects.requireNonNull(map.get("autor")).toString());
            object.setCategoria(Objects.requireNonNull(map.get("categoria")).toString());
            object.setArea(Objects.requireNonNull(map.get("area")).toString());
            object.setLinkDownload(Objects.requireNonNull(map.get("linkDownload")).toString());
            object.setImgDownload(Objects.requireNonNull(map.get("imgDownload")).toString());
           // object.setEndereçoLocal(map.get("endereçoLocal").toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return object;
    }

    public static HashMap<String,String> userToHashMap(Usuario object){
        HashMap<String,String> map =  new HashMap<>();
        map.put("nome", object.getNome());
        map.put("sobrenome", object.getSobreNome());
        map.put("email", object.getEmail());
        map.put("idUsuario", object.getIdUsuario());
        map.put("imgDownload", object.getLinkImgAccount());
        return map;
    }
    public static Usuario hashMapToUser(Map<String,Object> m){
        Usuario usuario =  new Usuario();
        usuario.setNome(m.get("nome").toString());
        usuario.setSobreNome( m.get("sobrenome").toString());
        usuario.setEmail( m.get("email").toString());
        usuario.setIdUsuario( m.get("idUsuario").toString());
        usuario.setLinkImgAccount( m.get("imgDownload").toString());
        return usuario;
    }
}
