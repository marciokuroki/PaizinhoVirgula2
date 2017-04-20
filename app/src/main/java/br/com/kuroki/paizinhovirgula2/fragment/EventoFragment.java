package br.com.kuroki.paizinhovirgula2.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.util.DetectConnection;

public class EventoFragment extends Fragment {

    private WebView webView;

    private ProgressBar progressBar;

    private String url = "http://paizinhovirgula.com/agenda-de-eventos/";

    public EventoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_evento);
        progressBar.setMax(100);

        if (!DetectConnection.checkInternetConnection(getActivity())) {
            Snackbar.make(view, "Sem conexão com a internet. Tente novamente mais tarde.", Snackbar.LENGTH_LONG).show();
        }else {
            webView = (WebView) view.findViewById(R.id.web_view_evento);

            webView.setWebViewClient(new MyWebViewClient());
            progressBar.setVisibility(View.GONE);
            webView.loadUrl(url);
        }
        // Inflate the layout for this fragment
        return view;
    }

    private class MyWebViewClient extends WebViewClient {
        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }*/

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            //return super.shouldOverrideUrlLoading(view, request);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            EventoFragment.this.progressBar.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            EventoFragment.this.progressBar.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }

}
