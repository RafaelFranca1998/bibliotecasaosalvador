package com.example.rafael_cruz.bibliotecasaosalvador.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rafael_cruz.bibliotecasaosalvador.R;

public class CustomHolder extends RecyclerView.ViewHolder {
    public TextView     title,categoria;
    public ImageView    imgLivro;

    public CustomHolder(View itemView) {
        super(itemView);
        title       = itemView.findViewById(R.id.text_livro_cardview);
        imgLivro    = itemView.findViewById(R.id.imagem_livro);
        categoria   = itemView.findViewById(R.id.text_categoria_cardview);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public ImageView getImgLivro() {
        return imgLivro;
    }

    public TextView getCategoria() {
        return categoria;
    }

    public void setCategoria(TextView categoria) {
        this.categoria = categoria;
    }

    public void setImgLivro(ImageView imgLivro) {
        this.imgLivro = imgLivro;
    }
}
