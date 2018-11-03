/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.activity.MainActivity;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Livro;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class AdapterRecyclerViewFavoritos extends RecyclerView.Adapter<AdapterRecyclerViewFavoritos.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView imgIcon;
        TextView txtCategoria;
        TextView txtNomeLivro;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            imgIcon = itemView.findViewById(R.id.img_livro_fav);
            txtCategoria = itemView.findViewById(R.id.text_categoria_padrao);
            txtNomeLivro = itemView.findViewById(R.id.text_livro_fav);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, 0, 0, "Ver Info.");
            menu.add(0, 1, 1, "Abrir");
            menu.add(0, 2, 2, "Editar");
            menu.add(0, 3, 3, "deletar");
        }
    }

    private List<Livro> mLivros;
    private Context mContext;
    private String url;
    private ViewHolder mViewHolder;

    public AdapterRecyclerViewFavoritos(Context context, List<Livro> livro) {
        mLivros = livro;
        mContext = context;
    }

    @Override
    public AdapterRecyclerViewFavoritos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.recycler_cell, parent, false);
        listView.setOnClickListener(new MainActivity.MyOnClickListener());
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Livro livro = mLivros.get(position);
        mViewHolder = viewHolder;

        TextView textViewCategoria = mViewHolder.txtCategoria;
        textViewCategoria.setText(livro.getCategoria());
        TextView textViewNome = mViewHolder.txtNomeLivro;
        textViewNome.setText(livro.getNome());
        ImageView imgIcon = mViewHolder.imgIcon;
        url = livro.getImgDownload();
        if (mViewHolder.imgIcon == null){
            mViewHolder.imgIcon.setVisibility(View.GONE);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into(mViewHolder.imgIcon);
        } else {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(viewHolder.imgIcon);
            mViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mLivros.size();
    }
}
