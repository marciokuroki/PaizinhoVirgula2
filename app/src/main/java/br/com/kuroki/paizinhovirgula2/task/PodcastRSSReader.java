package br.com.kuroki.paizinhovirgula2.task;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;

/**
 * Created by marciokuroki on 13/03/17.
 */

public class PodcastRSSReader extends AsyncTask<String, Void, List<Item>> {
    private static final String TAG = PodcastRSSReader.class.getName();

    private List<Item> feedItems;
    private URL url;
    private String address = "";

    private final Context context;
    private PaizinhoDataBaseHelper baseHelper;
    private List<Item> listBanco;
    private final int tipo;

    public PodcastRSSReader(Context context, int tipo) {
        this.context = context;
        this.tipo = tipo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        try{
            //TODO Ajustar para trazer só de um tipo
            //listBanco = getHelper().getItemDao().queryForAll();
            listBanco = getHelper().getItemDao().queryForEq(Item.NMCP_TIPO, tipo);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);

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
        Log.i(TAG, Thread.currentThread().getName());
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
            Log.i(TAG, address);
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
                    item.setTipo(tipo);
                    item.setDuration(0l);
                    NodeList itemchilds = cureentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node cureent = itemchilds.item(j);
                        if (cureent.getNodeName().equalsIgnoreCase("title")){
                            item.setTitle(cureent.getTextContent());
                        }else if(cureent.getNodeName().equalsIgnoreCase("enclosure")) {
                            String mediaUrl=cureent.getAttributes().item(0).getTextContent();
                            /*if (mediaUrl.contains("media.blubrry")) {
                                mediaUrl = mediaUrl.replace("media.blubrry.com/tricodepais/", "");
                            }*/
                            item.setUrl(mediaUrl);
                            item.setSizeMedia(Long.parseLong(cureent.getAttributes().item(1).getTextContent()));
                            if (tipo == Item.PODCAST_TIPO_SINUCA_DE_BICOS) {
                                item.setNomePodcast("Sinuca_de_Bicos");
                            }else {
                                item.setNomePodcast("Trico_de_Pais");
                            }
                            item.setNumeroEpisodio(item.getTitle().substring(item.getTitle().length()-3));
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
                        }else if (cureent.getNodeName().equalsIgnoreCase("content:encoded")) {
                            item.setContent(cureent.getTextContent());
                        }else if (cureent.getNodeName().equalsIgnoreCase("rawvoice:poster")) {
                            String url=cureent.getAttributes().item(0).getTextContent();
                            item.setImage(url);
                        }else if (cureent.getNodeName().equalsIgnoreCase("itunes:duration")) {
                            String duration = cureent.getTextContent();
                            item.setDuration(DateUtil.converterStringToLong(duration));
                        }
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
