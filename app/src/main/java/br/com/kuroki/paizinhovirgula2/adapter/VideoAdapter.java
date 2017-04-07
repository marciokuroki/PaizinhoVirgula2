package br.com.kuroki.paizinhovirgula2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Item> items = Collections.emptyList();
    // --Commented out by Inspection (16/03/17 16:27):private final Context context;
    private final LayoutInflater inflater;
    private OnItemClickListener listener;


    public VideoAdapter(List<Item> list, Context context) {
        this.items = list;
        //this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position, payloads);
        holder.bind(items.get(position));
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_video_item, parent, false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view);

        return videoViewHolder;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setListener(OnItemClickListener l) {
        listener = l;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView title;
        final TextView subtitle;
        final ImageView imagem;

        public VideoViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.fvi_title);
            subtitle = (TextView) itemView.findViewById(R.id.fvi_subtitle);
            imagem = (ImageView) itemView.findViewById(R.id.fvi_image);

            itemView.setOnClickListener(this);
        }

        //TODO item
        public void bind (final Item item) {
            title.setText(item.getTitle());
            subtitle.setText(DateUtil.converteLongToDate(item.getPubDate(), "dd 'de' MMMM 'de' yyyy"));
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
                Log.i("Video_Adapter", "item_selecionado: " + items.get(getPosition()));
                listener.onItemClick(v, items.get(getPosition()));
            }
        }
    }
}