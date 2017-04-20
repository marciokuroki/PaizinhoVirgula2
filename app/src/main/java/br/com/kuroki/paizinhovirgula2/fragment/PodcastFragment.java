package br.com.kuroki.paizinhovirgula2.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.activity.PodcastExibirItemActivity;
import br.com.kuroki.paizinhovirgula2.adapter.PodcastAdapter;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.PodcastRSSReader;

public class PodcastFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, PodcastAdapter.OnLoadMoreListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private PodcastAdapter adapter;
    private List<Item> listPodcast = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefresh;

    private PaizinhoDataBaseHelper dataBaseHelper;

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
        /*if (podcastRSSReader == null)
            podcastRSSReader = new PodcastRSSReader(getActivity(), Item.PODCAST_TIPO_TRICO_DE_PAIS);
        return podcastRSSReader;*/
        return new PodcastRSSReader(getActivity(), Item.PODCAST_TIPO_TRICO_DE_PAIS);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (listPodcast == null || listPodcast.size() == 0)
            loadData();
        if (adapter != null) {
            adapter.addAll(listPodcast);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view_podcast);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_podcast);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_podcast);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PodcastAdapter(this);
        adapter.setLinearLayoutManager(linearLayoutManager);
        adapter.setRecyclerView(recyclerView);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this);

        /*try {
            listPodcast = getHelper().getItemDao().queryForEq(Item.NMCP_TIPO, Item.PODCAST_TIPO_TRICO_DE_PAIS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapter = new PodcastAdapter(listPodcast, getActivity());
        adapter.setListener(this);

        View view = inflater.inflate(R.layout.fragment_podcast, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_podcast);

        emptyView = (TextView) view.findViewById(R.id.empty_view_podcast);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        if (listPodcast == null || listPodcast.size() == 0) {
            try{
                getPodcastRSSReader().execute("http://paizinhovirgula.com/feed/podcast");
                listPodcast.addAll(getPodcastRSSReader().get());
                adapter.notifyItemRangeInserted(0, listPodcast.size());
            }catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch (Exception e) {
                listPodcast.clear();
                emptyView.setText("Desculpe, Site do Paizinho com problemas. Favor, tente mais tarde.");
            }
        }

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (listPodcast.size() <= 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }*/

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onRefresh() {
        Log.d("PodcastFragment","onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
                loadData();
            }
        },1000);
    }

    @Override
    public void onLoadMore() {
        Log.d("PoscastFragment_","onLoadMore");
        adapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("PoscastFragment_","run");
                listPodcast.clear();
                adapter.setProgressMore(false);
                int start = linearLayoutManager.findLastVisibleItemPosition();
                /*for (int i = start + 1; i <= end; i++) {
                    listBlog.add(new Item("Item " + i));
                }*/
                Log.i("TESTE ", "Position: " + linearLayoutManager.findLastVisibleItemPosition());
                Log.i("TESTE ", "id: " + adapter.getItemIdAtPosition(start-1));
                try {
                    listPodcast = getHelper().getItemDao().pesquisarNItens(10, adapter.getItemIdAtPosition(start-1), true, Item.PODCAST_TIPO_TRICO_DE_PAIS);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapter.addItemMore(listPodcast);
                adapter.setMoreLoading(false);
            }
        },2000);
    }

    private void loadData(){
        listPodcast.clear();
        try {
            getPodcastRSSReader().execute("http://paizinhovirgula.com/feed/podcast");
            listPodcast = getHelper().getItemDao().pesquisarNItens(10, 0, true, Item.PODCAST_TIPO_TRICO_DE_PAIS);
            adapter.addAll(listPodcast);
            adapter.notifyItemInserted(0);
        } catch (SQLException e) {
            e.printStackTrace();
            listPodcast.clear();
            if (emptyView != null)
                emptyView.setText("Desculpe, Site do Paizinho com problemas. Favor, tente mais tarde.");
        }
    }
}
