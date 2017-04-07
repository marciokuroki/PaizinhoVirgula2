package br.com.kuroki.paizinhovirgula2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
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

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder> {

    private List<Item> items = Collections.emptyList();
    private final Context context;
    private final LayoutInflater inflater;
    private OnItemClickListener listener;

    public PodcastAdapter(List<Item> list, Context context) {
        this.items = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position, payloads);
        holder.bind(items.get(position));
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_podcast_item, parent, false);
        PodcastViewHolder podcastViewHolder = new PodcastViewHolder(view);

        return podcastViewHolder;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setListener(OnItemClickListener l) {
        listener = l;
    }

    public class PodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView title;
        final TextView pubdate;
        final ImageView imagem;
        //final ImageButton imageButton;

        public PodcastViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.fpi_title);
            pubdate = (TextView) itemView.findViewById(R.id.fpi_pubdate);
            imagem = (ImageView) itemView.findViewById(R.id.fpi_image);
            /*imageButton = (ImageButton) itemView.findViewById(R.id.fpi_play);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageButton.isActivated()) {
                        imageButton.setActivated(false);
                    } else {
                        imageButton.setActivated(true);
                    }
                }
            });*/

            itemView.setOnClickListener(this);
        }

        public void bind (final Item item) {
            int placeHolder = R.mipmap.ic_trico;
            if (item.getTipo() == Item.PODCAST_TIPO_SINUCA_DE_BICOS)
                placeHolder = R.mipmap.ic_sinuca;

            title.setText(item.getTitle());
            pubdate.setText(DateUtil.converteLongToDate(item.getPubDate(), "dd 'de' MMMM 'de' yyyy"));
            Picasso.with(itemView.getContext())
                    .load(item.getImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.mipmap.ic_paizinho)
                    .error(R.mipmap.ic_paizinho)
                    .resize(600, 0)
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
                listener.onItemClick(v, items.get(getPosition()));
            }
        }
    }
}