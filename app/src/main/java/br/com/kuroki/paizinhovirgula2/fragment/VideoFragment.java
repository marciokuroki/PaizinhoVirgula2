package br.com.kuroki.paizinhovirgula2.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.activity.BlogExibirItemActivity;
import br.com.kuroki.paizinhovirgula2.adapter.VideoAdapter;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.task.VideoRSSReader;

public class VideoFragment extends Fragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView emptyView;

    private VideoRSSReader youTubeRSSReader;

    private ArrayList<Item> listVideo = new ArrayList<>();

    private VideoAdapter adapter;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onItemClick(View view, Item item) {
        //TODO mudar pra um YouTubeExibirItemActivity.class
        Intent intent = new Intent(getActivity(), BlogExibirItemActivity.class);
        Log.i("VIDEO_FRAGMENT", "ITEM: "+ item);
        intent.putExtra("itemSelecionado", item);
        startActivity(intent);
    }

    private VideoRSSReader getYouTubeRSSReader() {
        if (youTubeRSSReader == null)
            youTubeRSSReader = new VideoRSSReader(getActivity());
        return youTubeRSSReader;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("videoItens", listVideo);
        super.onSaveInstanceState(outState);
    }

    //TODO URL = https://www.youtube.com/channel/UC_nsjAbH0GlhCSDafFxIgVw

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey("videoItens")) {
            getYouTubeRSSReader().execute("http://paizinhovirgula.com/category/paizinho-no-youtube/feed");

            try {
                listVideo.addAll(youTubeRSSReader.get());
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            listVideo = savedInstanceState.getParcelableArrayList("videoItens");
        }

        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_video);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new VideoAdapter(listVideo, getActivity());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) view.findViewById(R.id.empty_view_video);

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (listVideo == null || listVideo.size() <= 0) {
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