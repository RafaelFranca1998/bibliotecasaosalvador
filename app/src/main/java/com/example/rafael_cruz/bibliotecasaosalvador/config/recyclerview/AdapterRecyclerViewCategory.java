/******************************************************************************
 * Copyright (c) 2018. all rights are reserved to the authors of this project, unauthorized use of this code in
 * other projects may result in legal complications.                          *
 ******************************************************************************/

package com.example.rafael_cruz.bibliotecasaosalvador.config.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.example.rafael_cruz.bibliotecasaosalvador.model.Categoria;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterRecyclerViewCategory extends RecyclerView.Adapter<AdapterRecyclerViewCategory.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCategory;
        TextView txtCategory;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.image_view_list_category);
            txtCategory = itemView.findViewById(R.id.txt_categoria_recyclerlist);
            progressBar = itemView.findViewById(R.id.progress_bar_category);
        }
    }

    private List<Categoria> mCategoriaList;
    private Context mContext;
    private String url;
    private ViewHolder mViewHolder;

    public AdapterRecyclerViewCategory(Context context, List<Categoria> categoriaList) {
        mCategoriaList = categoriaList;
        mContext = context;
    }

    @NonNull
    @Override
    public AdapterRecyclerViewCategory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.recycler_cell_category, parent, false);
        listView.setOnClickListener(new MainActivity.MyOnClickListenerRecomendados());
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Categoria categoria = mCategoriaList.get(position);
        mViewHolder = viewHolder;

       TextView textViewCategoria = mViewHolder.txtCategory;
        textViewCategoria.setText(categoria.getCategoryName());
        mViewHolder.txtCategory.setText(categoria.getCategoryName());
        url = categoria.getImgDownload();
        if (mViewHolder.imgCategory == null){
            try {
                mViewHolder.imgCategory.setVisibility(View.GONE);
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
                                mViewHolder.imgCategory.setVisibility(View.VISIBLE);
                                return false; // important to return false so the error placeholder can be placed
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mViewHolder.progressBar.setVisibility(View.GONE);
                                mViewHolder.imgCategory.setVisibility(View.VISIBLE);
                                return false;
                            }
                        }).into(mViewHolder.imgCategory);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {try {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(viewHolder.imgCategory);
            mViewHolder.progressBar.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    }

    @Override
    public int getItemCount() {
        return mCategoriaList.size();
    }
}
