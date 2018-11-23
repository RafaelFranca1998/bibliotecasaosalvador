package com.example.rafael_cruz.bibliotecasaosalvador.config.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.rafael_cruz.bibliotecasaosalvador.fragment.TabInformacoesFragment;
import com.example.rafael_cruz.bibliotecasaosalvador.fragment.TabPreferenciasFragment;
import com.example.rafael_cruz.bibliotecasaosalvador.fragment.TabConfiguracoesFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabInformacoesFragment tab1 = new TabInformacoesFragment();
                return tab1;
            case 1:
                TabPreferenciasFragment tab2 = new TabPreferenciasFragment();
                return tab2;
            case 2:
                TabConfiguracoesFragment tab3 = new TabConfiguracoesFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}