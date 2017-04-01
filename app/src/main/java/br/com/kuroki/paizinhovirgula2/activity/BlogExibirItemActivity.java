package br.com.kuroki.paizinhovirgula2.activity;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;
import br.com.kuroki.paizinhovirgula2.util.HeaderView;
import br.com.kuroki.paizinhovirgula2.util.UlTagHandler;

public class BlogExibirItemActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private ImageView image;
    private TextView title, pubdate, content;
    private Toolbar toolbar;
    private HeaderView toolbarHeaderView;
    private HeaderView floatHeaderView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean isHideToolBarView = false;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_exibir_item);
        toolbar = (Toolbar) findViewById(R.id.abei_toolBar);

        appBarLayout = (AppBarLayout) findViewById(R.id.abei_appBar);

        setSupportActionBar(toolbar);

        Bundle parametros = getIntent().getExtras();
        Item itemSelecionado = (Item) parametros.get("itemSelecionado");

        image = (ImageView) findViewById(R.id.abei_image);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.abei_collapsingToolBar);
        collapsingToolbarLayout.setTitle(" ");

        toolbarHeaderView = (HeaderView) findViewById(R.id.toolbar_header_view);
        floatHeaderView = (HeaderView) findViewById(R.id.float_header_view);

        content = (TextView) findViewById(R.id.abei_content);

        if (itemSelecionado != null) {
            toolbarHeaderView.bindTo(itemSelecionado.getTitle(), DateUtil.converteLongToDate(itemSelecionado.getPubDate(), "dd 'de' MMM 'de' yyyy"));
            floatHeaderView.bindTo(itemSelecionado.getTitle(), DateUtil.converteLongToDate(itemSelecionado.getPubDate(), "dd 'de' MMM 'de' yyyy"));

            if (Build.VERSION.SDK_INT >= 24) {
                content.setText(Html.fromHtml(itemSelecionado.getContent(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM, null, new UlTagHandler()));
            } else {
                String aux = itemSelecionado.getContent();
                aux = aux.replace("<li>", "-");
                aux = aux.replace("</li>", "<br>");
                content.setText(Html.fromHtml(aux));
                content.setMovementMethod(LinkMovementMethod.getInstance());
            }

            Picasso.with(this)
                    .load(itemSelecionado.getImage())
                    .placeholder(R.mipmap.ic_paizinho)
                    .error(R.mipmap.ic_paizinho)
                    .resize(600, 0)
                    .into(image);
        }

        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolBarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolBarView = !isHideToolBarView;

        } else if (percentage < 1f && !isHideToolBarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolBarView = !isHideToolBarView;
        }
    }
}
