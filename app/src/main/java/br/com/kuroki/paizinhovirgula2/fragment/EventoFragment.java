package br.com.kuroki.paizinhovirgula2.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.kuroki.paizinhovirgula2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;


    public EventoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_evento);
        emptyView = (TextView) view.findViewById(R.id.empty_view_evento);

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
