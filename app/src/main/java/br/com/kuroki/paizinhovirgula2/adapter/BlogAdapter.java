package br.com.kuroki.paizinhovirgula2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;

/**
 * Created by marciokuroki on 13/03/17.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<Item> items = Collections.emptyList();
    // --Commented out by Inspection (16/03/17 16:27):private final Context context;
    private final LayoutInflater inflater;
    private OnItemClickListener listener;

    public BlogAdapter(List<Item> list, Context context) {
        this.items = list;
        //this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(BlogViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position, payloads);
        holder.bind(items.get(position));
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_blog_item, parent, false);
        BlogViewHolder blogViewHolder = new BlogViewHolder(view);

        return blogViewHolder;
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
                    .placeholder(R.mipmap.ic_paizinho)
                    .error(R.mipmap.ic_paizinho)
                    .resize(300, 300)
                    .into(imagem);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                Log.i("Blog_Adapter", "item_selecionado: " + items.get(getPosition()));
                listener.onItemClick(v, items.get(getPosition()));
            }
        }
    }
}