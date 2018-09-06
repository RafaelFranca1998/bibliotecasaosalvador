/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.rafael_cruz.bibliotecasaosalvador.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rafael_cruz.bibliotecasaosalvador.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Provide views to RecyclerView with data from mUserModel.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomHolder> {
    private static final String TAG = "CustomAdapter";

    private List<UserModel> mUserModel;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView categoria;
        private final ImageView img;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView    = v.findViewById(R.id.text_livro_cardview);
            categoria   = v.findViewById(R.id.text_categoria_cardview);
            img         = v.findViewById(R.id.imagem_livro);
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getCategoria() {
            return categoria;
        }

        public ImageView getImg() {
            return img;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(ArrayList<UserModel> dataSet) {
        mUserModel = dataSet;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public CustomHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_list, viewGroup, false);

        return new CustomHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        String title = mUserModel.get(position).getTextoLivro();
        String categoria = mUserModel.get(position).getTextoCategoria();
        int imagem =  mUserModel.get(position).getIconeRid();

        viewHolder.getTitle().setText(title);
        viewHolder.getImgLivro().setBackgroundResource(imagem);
        //Todo Null pointer error
        viewHolder.getCategoria().setText(categoria);


        viewHolder.title.setText(mUserModel.get(position).getTextoLivro());
        viewHolder.categoria.setText(categoria);
        viewHolder.imgLivro.setBackgroundResource(imagem);

      //  viewHolder.imgLivro.setOnClickListener(view -> updateItem(position));

     //   viewHolder.textCategoria.setOnClickListener(view -> removerItem(position));
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserModel.size();
    }

    /**
     * Método publico chamado para atualziar a lista.
     * @param user Novo objeto que será incluido na lista.
     */
    public void updateList(UserModel user) {
        insertItem(user);
    }

    // Método responsável por inserir um novo usuário na lista
    //e notificar que há novos itens.
    private void insertItem(UserModel user) {
        mUserModel.add(user);
        notifyItemInserted(getItemCount());
    }

    // Método responsável por atualizar um usuário já existente na lista.
//    private void updateItem(int position) {
//        UserModel userModel = mUserModel.get(position);
//        userModel.incrementAge();
//        notifyItemChanged(position);
//    }
    // Método responsável por remover um usuário da lista.
    private void removerItem(int position) {
        mUserModel.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUserModel.size());
    }
}
