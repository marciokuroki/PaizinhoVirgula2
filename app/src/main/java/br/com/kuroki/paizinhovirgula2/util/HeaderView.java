package br.com.kuroki.paizinhovirgula2.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.kuroki.paizinhovirgula2.R;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by marciokuroki on 31/03/17.
 */

public class HeaderView extends LinearLayout {
    @BindView(R.id.header_view_title)
    TextView title;

    @BindView(R.id.header_view_subtitle)
    TextView subTitle;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String title) {
        bindTo(title, "");
    }

    public void bindTo(String title, String subtitle) {
        hideOrSetText(this.title, title);
        hideOrSetText(this.subTitle, subtitle);
    }

    private void hideOrSetText(TextView tv, String text) {
        if (text == null || text.equals(""))
            tv.setVisibility(GONE);
        else
            tv.setText(text);
    }
}
