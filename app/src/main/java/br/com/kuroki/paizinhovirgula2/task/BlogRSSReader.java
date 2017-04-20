package br.com.kuroki.paizinhovirgula2.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;

/**
 * Created by marciokuroki on 13/03/17.
 */

public class BlogRSSReader extends AsyncTask<String, Void, List<Item>> {
    private static final String TAG = BlogRSSReader.class.getName();

    private List<Item> feedItems;
    private URL url;
    private String address = "";

    private final Context context;
    private ProgressDialog dialog;
    private PaizinhoDataBaseHelper baseHelper;
    //private List<Item> list;
    private List<Item> listBanco;

    public BlogRSSReader(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Carregando", "Aguarde...", true);
        try{
            listBanco = getHelper().getItemDao().queryForAll();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);
        feedItems = items;
        if (items != null) {
            for (Item item: items) {
                Item aux = item;
                if (!listBanco.contains(aux)) {
                    try {
                        aux = getHelper().getItemDao().createIfNotExists(aux);
                        listBanco.add(aux);
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        dialog.dismiss();
    }

    private PaizinhoDataBaseHelper getHelper() {
        if (baseHelper == null)
            baseHelper = new PaizinhoDataBaseHelper(context);
        return baseHelper;
    }

    /***
     * pos 0 - URL
     * @param params
     * @return
     */
    @Override
    protected List<Item> doInBackground(String... params) {
        Log.i(TAG, params[0]);
        address = params[0];

        try {
            feedItems = processXml(getdata());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feedItems;
    }

    private Document getdata() {
        try {
            //Log.i(TAG, address);
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (MalformedURLException e) {
            Log.e(TAG, address);
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            Log.e(TAG, "protocol Error");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e(TAG, "IOEXception");
            e.printStackTrace();
            return null;
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "ParserConfiguration Problem");
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            Log.e(TAG, "SAX exception");
            e.printStackTrace();
            return null;
        }
    }

    private List<Item> processXml(Document data) throws Exception {
        List<Item> feedItems;

        if (data != null) {
            feedItems = new ArrayList<>();
            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            //List<Categoria> categorias = new ArrayList<Categoria>();
            for (int i = 0; i < items.getLength(); i++) {
                Node cureentchild = items.item(i);
                if (cureentchild.getNodeName().equalsIgnoreCase("item")) {
                    Item item = new Item();
                    item.setResumePosition(0);
                    NodeList itemchilds = cureentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node cureent = itemchilds.item(j);
                        if (cureent.getNodeName().equalsIgnoreCase("title")){
                            item.setTitle(cureent.getTextContent());
                        }else if(cureent.getNodeName().equalsIgnoreCase("enclosure")) {
                            String mediaUrl=cureent.getAttributes().item(0).getTextContent();
                            item.setUrl(mediaUrl);
                            item.setSizeMedia(Long.parseLong(cureent.getAttributes().item(1).getTextContent()));
                        }else if(cureent.getNodeName().equalsIgnoreCase("pubDate")) {
                            Date date = DateUtil.formataData(cureent.getTextContent());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            item.setPubDate(calendar.getTimeInMillis());
                        }else if (cureent.getNodeName().equalsIgnoreCase("category")){
                            /*Categoria categoria = new Categoria(cureent.getTextContent());
                            if (!categorias.contains(categoria)) {
                                categorias.add(categoria);
                            }*/
                        }else if (cureent.getNodeName().equalsIgnoreCase("description")) {
                            item.setDescription(cureent.getTextContent());
                            String aux = cureent.getChildNodes().item(0).getTextContent();
                            Pattern p = Pattern.compile("src=\"(\\S+)\"");
                            Matcher m = p.matcher(aux);
                            if (m.find()) {
                                String result = m.group(1);
                                item.setImage(result);
                            }
                        }else if (cureent.getNodeName().equalsIgnoreCase("content:encoded")) {
                            item.setContent(cureent.getTextContent());
                        }/*else if ((cureent.getNodeName().equalsIgnoreCase("rawvoice:poster") || (cureent.getNodeName().equalsIgnoreCase("media:content")))) {
                            String url=cureent.getAttributes().item(0).getTextContent();
                            item.setImage(url);
                        }*/
                    }

                    /*if (categorias.size() > 0) {
                        ForeignCollection<Categoria> categories = getHelper().getItemDao().getEmptyForeignCollection(Item.NMCP_CATEGORIES);
                        for (Categoria categoria: categorias) {
                            categories.add(categoria);
                        }
                        item.setCategories(categories);
                    }*/
                    feedItems.add(item);
                }
            }

            return feedItems;
        }else {
            return null;
        }
    }
}
