package br.com.kuroki.paizinhovirgula2.adapter;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;

/**
 * Created by marciokuroki on 13/03/17.
 */

public class BlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> items;
    //private LayoutInflater inflater;
    private OnItemClickListener listener;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    /*public BlogAdapter(List<Item> list, Context context) {
        this.items = list;
        //this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/

    public BlogAdapter(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        items = new ArrayList<>();
        //this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.mLinearLayoutManager=linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount)<= (firstVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isMoreLoading = true;
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position, payloads);
        //holder.bind(items.get(position));

        if (holder instanceof BlogViewHolder) {
            Item singleItem = items.get(position);
            ((BlogViewHolder) holder).bind(singleItem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_blog_item, parent, false);
        BlogViewHolder blogViewHolder = new BlogViewHolder(view);

        return blogViewHolder;*/

        if (viewType == VIEW_ITEM) {
            return new BlogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_blog_item, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }
    }

    public void addAll(List<Item> lst){
        items.clear();
        items.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Item> lst){
        for (Item item: lst) {
            if (!items.contains(item))
                items.add(item);
        }
        //items.addAll(lst);
        notifyItemRangeChanged(0,items.size());
    }

    public Long getItemIdAtPosition(int position) {
        return items.get(position).getId();
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading=isMoreLoading;
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    items.add(null);
                    //items.clear();
                    notifyItemInserted(items.size() - 1);
                }
            });
        } else {
            items.remove(items.size() - 1);
            notifyItemRemoved(items.size());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setListener(OnItemClickListener l) {
        listener = l;
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView title;
        final TextView pubdate;
        final ImageView imagem;

        public BlogViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.fbi_title);
            pubdate = (TextView) itemView.findViewById(R.id.fbi_pubdate);
            imagem = (ImageView) itemView.findViewById(R.id.fbi_image);

            itemView.setOnClickListener(this);
        }

        //TODO item
        public void bind (final Item item) {
            title.setText(item.getTitle());
            pubdate.setText(DateUtil.converteLongToDate(item.getPubDate(), "dd 'de' MMMM 'de' yyyy"));
            Picasso.with(itemView.getContext())
                    .load(item.getImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.mipmap.ic_paizinho)
                    .error(R.mipmap.ic_paizinho)
                    .resize(400, 210)
                    .centerCrop()
                    .into(imagem, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(itemView.getContext())
                                    .load(item.getImage())
                                    .error(R.mipmap.ic_paizinho)
                                    .into(imagem, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
                                            Log.v("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                Log.i("Blog_Adapter", "item_selecionado: " + items.get(getPosition()));
                listener.onItemClick(v, items.get(getPosition()));
            }
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;
        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }
}