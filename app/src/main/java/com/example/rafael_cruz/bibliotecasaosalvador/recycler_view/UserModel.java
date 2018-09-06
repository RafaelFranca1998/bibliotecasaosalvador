package com.example.rafael_cruz.bibliotecasaosalvador.recycler_view;

public class UserModel {
    private String textoLivro, textoCategoria;
    private int iconeRid;

    /**
     * Construtor da classe
     */
    public UserModel() {
        this("","", -1);
    }

    /**
     * Inicializa a Classe UserModel já com as instryções necessárias.
     * @param textoLivro
     * @param textoCategoria
     * @param iconeRid
     */
    public UserModel(String textoLivro, String textoCategoria, int iconeRid) {
        this.textoLivro     = textoLivro;
        this.textoCategoria = textoCategoria;
        this.iconeRid       = iconeRid;
    }

    public String getTextoLivro() {
        return textoLivro;
    }

    public void setTextoLivro(String textoLivro) {
        this.textoLivro = textoLivro;
    }

    public String getTextoCategoria() {
        return textoCategoria;
    }

    public void setTextoCategoria(String textoCategoria) {
        this.textoCategoria = textoCategoria;
    }

    public int getIconeRid() {
        return iconeRid;
    }

    public void setIconeRid(int iconeRid) {
        this.iconeRid = iconeRid;
    }
}
