package br.com.kuroki.paizinhovirgula2.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;
import br.com.kuroki.paizinhovirgula2.util.UlTagHandler;

public class BlogExibirItemActivity extends AppCompatActivity {
    private ImageView image;
    private TextView title, pubdate, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_exibir_item);

        Bundle parametros = getIntent().getExtras();
        Item itemSelecionado = (Item) parametros.get("itemSelecionado");

        image = (ImageView) findViewById(R.id.abei_image);
        title = (TextView) findViewById(R.id.abei_title);
        pubdate = (TextView) findViewById(R.id.abei_pubdate);
        content = (TextView) findViewById(R.id.abei_content);

        if (itemSelecionado != null) {
            title.setText(itemSelecionado.getTitle());
            pubdate.setText(DateUtil.converteLongToDate(itemSelecionado.getPubDate(), "dd 'de' MMM 'de' yyyy"));

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
    }
}
