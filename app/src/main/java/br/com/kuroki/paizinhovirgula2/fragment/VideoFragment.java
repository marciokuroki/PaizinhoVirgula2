package br.com.kuroki.paizinhovirgula2.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.kuroki.paizinhovirgula2.R;

public class VideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;


    public VideoFragment() {
        // Required empty public constructor
    }

    //TODO URL = https://www.youtube.com/channel/UC_nsjAbH0GlhCSDafFxIgVw

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_video);
        emptyView = (TextView) view.findViewById(R.id.empty_view_video);

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (true) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        return view;
    }

}