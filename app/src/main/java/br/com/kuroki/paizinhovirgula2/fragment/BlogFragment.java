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
import br.com.kuroki.paizinhovirgula2.adapter.BlogAdapter;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.BlogRSSReader;

public class BlogFragment extends Fragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private BlogAdapter adapter;
    private ArrayList<Item> listBlog = new ArrayList<>();

    private PaizinhoDataBaseHelper baseHelper;

    private BlogRSSReader blogRSSReader;

    public BlogFragment() { }

    @Override
    public void onItemClick(View view, Item item) {
        Intent intent = new Intent(getActivity(), BlogExibirItemActivity.class);
        Log.i("BLOG_FRAGMENT", "ITEM: "+ item);
        intent.putExtra("itemSelecionado", item);
        startActivity(intent);
    }

// --Commented out by Inspection START (16/03/17 16:27):
//    private PaizinhoDataBaseHelper getHelper(){
//        if (baseHelper == null)
//            baseHelper = new PaizinhoDataBaseHelper(getContext());
//        return baseHelper;
//    }
// --Commented out by Inspection STOP (16/03/17 16:27)

    private BlogRSSReader getBlogRssReader() {
        if (blogRSSReader == null)
            blogRSSReader = new BlogRSSReader(getActivity());
        return blogRSSReader;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("blogItens", listBlog);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view_blog);

        if (savedInstanceState == null || !savedInstanceState.containsKey("blogItens")) {
            //TODO Criar um Enum ou arquivo de configuração para armazenar os feeds
            getBlogRssReader().execute("http://paizinhovirgula.com/category/blog/feed");

            try {
                listBlog.addAll(blogRSSReader.get());
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }catch (Exception e) {
                listBlog.clear();
                emptyView.setText("Desculpe, Site do Paizinho com problemas. Favor, tente mais tarde.");
            }
        }else {
            listBlog = savedInstanceState.getParcelableArrayList("blogItens");
        }



        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_blog);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new BlogAdapter(listBlog, getActivity());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (listBlog == null || listBlog.size() <= 0) {
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