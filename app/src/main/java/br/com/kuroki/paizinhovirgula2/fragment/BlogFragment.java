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
    private List<Item> listBlog;

    private PaizinhoDataBaseHelper baseHelper;

    public BlogFragment() { }

    @Override
    public void onItemClick(Item item) {
        Intent intent = new Intent(getActivity(), BlogExibirItemActivity.class);
        intent.putExtra("itemID", item.getId());
        getActivity().startActivity(intent);
    }

    private PaizinhoDataBaseHelper getHelper(){
        if (baseHelper == null)
            baseHelper = new PaizinhoDataBaseHelper(getContext());
        return baseHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            listBlog = getHelper().getItemDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BlogRSSReader blogRSSReader = new BlogRSSReader(getActivity());

        //TODO pegar a URL do feed do BLOG;
        blogRSSReader.execute("http://paizinhovirgula.com/feed");

        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_blog);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new BlogAdapter(listBlog, getActivity());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) view.findViewById(R.id.empty_view_blog);

        //TODO Fazer checagem da consulta da listagem
        //Se a listagem vazia, preenche com o texto
        if (listBlog.size() <= 0) {
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