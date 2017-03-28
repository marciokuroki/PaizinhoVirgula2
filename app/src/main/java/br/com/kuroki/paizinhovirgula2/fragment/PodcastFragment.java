package br.com.kuroki.paizinhovirgula2.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.activity.PodcastExibirItemActivity;
import br.com.kuroki.paizinhovirgula2.adapter.PodcastAdapter;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.PodcastRSSReader;

public class PodcastFragment extends Fragment implements OnItemClickListener{

    private RecyclerView recyclerView;
    private TextView emptyView;

    private PodcastAdapter adapter;
    private List<Item> listPodcast;

    private PaizinhoDataBaseHelper dataBaseHelper;

    PodcastRSSReader podcastRSSReader;

    public PodcastFragment() { }


    private PaizinhoDataBaseHelper getHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = new PaizinhoDataBaseHelper(getActivity());
        }

        return dataBaseHelper;
    }

    @Override
    public void onItemClick(View v, Item item) {
        Intent intent = new Intent(getActivity(), PodcastExibirItemActivity.class);
        intent.putExtra("itemID", item.getId());
        getActivity().startActivity(intent);
    }

    private PodcastRSSReader getPodcastRSSReader() {
        if (podcastRSSReader == null)
            podcastRSSReader = new PodcastRSSReader(getActivity(), Item.PODCAST_TIPO_TRICO_DE_PAIS);
        return podcastRSSReader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_podcast);
        emptyView = (TextView) view.findViewById(R.id.empty_view_podcast);

        try {
            //listPodcast = getHelper().getItemDao().queryForAll();
            listPodcast = getHelper().getItemDao().queryForEq(Item.NMCP_TIPO, Item.PODCAST_TIPO_TRICO_DE_PAIS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //PodcastRSSReader podcastRSSReader = new PodcastRSSReader(getActivity(), Item.PODCAST_TIPO_TRICO_DE_PAIS);
        //podcastRSSReader.execute("http://paizinhovirgula.com/feed/podcast");
        if (listPodcast == null || listPodcast.size() == 0) {
            getPodcastRSSReader().execute("http://paizinhovirgula.com/feed/podcast");
        }

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new PodcastAdapter(listPodcast, getActivity());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (listPodcast.size() <= 0) {
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
