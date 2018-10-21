package com.example.rafael_cruz.bibliotecasaosalvador.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.recycler_view.CustomAdapter;
import com.example.rafael_cruz.bibliotecasaosalvador.recycler_view.UserModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private Context context;
    private String  FINAL_LIVRO      = "Nome: ";
    private String  FINAL_CATEGORIA  = "Categoria: ";

//    ListView listView;
//    ItemListView itemListView;
//    private List<ItemListView> itens;

    RecyclerView            listView;
    UserModel               itemListView;
    ArrayList<UserModel>    itens;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView   = inflater.inflate(R.layout.fragment_main, container, false);
        listView        =  rootView.findViewById(R.id.recycler_view_layour_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);

        itens = new ArrayList<UserModel>();

        addLivro("Cachorro perdido","Cachorros",R.drawable.lupa_icon_black);

        addLivro("Coleta de Lixo","Lixo",R.drawable.icons8_rss_50);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);

        addLivro("Orientação Objeto","java",R.drawable.broken_heart);



        CustomAdapter adapter =  new CustomAdapter(itens);

        listView.setAdapter(adapter);

//        listView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // Configurando o gerenciador de layout para ser uma lista horizontal.
        LinearLayoutManager layoutMan
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(layoutMan);


        listView.setOnClickListener(view -> mostraToast());
        return rootView;

    }


    /**
     * Metodo para adicionar eventos.
     * @param livro
     * @param categoria
     * @param imagem
     */
    public void addLivro(String livro, String categoria, int imagem){

        itemListView = new UserModel();

        itemListView.setTextoLivro(FINAL_LIVRO +livro);

        itemListView.setTextoCategoria(FINAL_CATEGORIA +categoria);

        itemListView.setIconeRid(imagem);

        itens.add(itemListView);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    public void mostraToast(){
        Toast.makeText(context,"#Bolsonaro2018",Toast.LENGTH_LONG).show();

    }
}


