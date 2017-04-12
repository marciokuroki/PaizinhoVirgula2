package br.com.kuroki.paizinhovirgula2.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Comparator;

import br.com.kuroki.paizinhovirgula2.persistence.dao.ItemDaoImpl;

/**
 * Created by marciokuroki on 13/03/17.
 */

@DatabaseTable(tableName = "tb_item", daoClass = ItemDaoImpl.class)
public class Item implements Comparator<Item>, Parcelable{

    public static final int PODCAST_TIPO_TRICO_DE_PAIS = 1;
    public static final int PODCAST_TIPO_SINUCA_DE_BICOS = 2;

    public static final String NMCP_ID = "id_item";
    public static final String NMCP_TITLE = "title";
    public static final String NMCP_PUBDATE = "pub_date";
    public static final String NMCP_DESCRIPTION = "description";
    public static final String NMCP_CONTENT = "content";
    public static final String NMCP_URL = "url";
    public static final String NMCP_IMAGE = "image";
    public static final String NMCP_LOCAL_DOWNLOAD = "local_download";
    public static final String NMCP_RESUME_POSITION = "resume_position";
    public static final String NMCP_IS_DOWNLOADED = "is_downloaded";
    public static final String NMCP_IS_VIEWED = "is_viewed";
    public static final String NMCP_DURATION = "duration";
    public static final String NMCP_EPISODIO = "episode_number";
    public static final String NMCP_PODCAST_NOME = "podcast_name";
    public static final String NMCP_SIZE_MEDIA = "size_media";
    public static final String NMCP_TIPO = "tipo_podcast";
    //public static final String NMCP_CATEGORIES = "categories";

    @DatabaseField(columnName = NMCP_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = NMCP_TITLE, canBeNull = false)
    private String title;
    @DatabaseField(columnName = NMCP_PUBDATE, canBeNull = false)
    private Long pubDate;
    @DatabaseField(columnName = NMCP_DESCRIPTION)
    private String description;
    @DatabaseField(columnName = NMCP_CONTENT)
    private String content;
    @DatabaseField(columnName = NMCP_URL)
    private String url;
    @DatabaseField(columnName = NMCP_IMAGE)
    private String image;
    @DatabaseField(columnName = NMCP_LOCAL_DOWNLOAD)
    private String localDownload;
    @DatabaseField(columnName = NMCP_RESUME_POSITION)
    private Integer resumePosition;
    @DatabaseField(columnName = NMCP_IS_DOWNLOADED)
    private boolean isDownloaded;
    @DatabaseField(columnName = NMCP_IS_VIEWED)
    private boolean isViewed;
    @DatabaseField(columnName = NMCP_DURATION)
    private Long duration;
    @DatabaseField(columnName = NMCP_EPISODIO)
    private String numeroEpisodio;
    @DatabaseField(columnName = NMCP_PODCAST_NOME)
    private String nomePodcast;
    @DatabaseField(columnName = NMCP_SIZE_MEDIA)
    private Long sizeMedia;
    @DatabaseField(columnName = NMCP_TIPO)
    private Integer tipo;
    /*@ForeignCollectionField(columnName = NMCP_CATEGORIES)
    private ForeignCollection<Categoria> categories;*/

    public Item() {}

    public Item(Parcel parcel) {
        //setId(parcel.readLong());
        setTitle(parcel.readString());
        setPubDate(parcel.readLong());
        setDescription(parcel.readString());
        setContent(parcel.readString());
        setUrl(parcel.readString());
        setImage(parcel.readString());
        setLocalDownload(parcel.readString());
        setResumePosition(parcel.readInt());
        /*setDownloaded(false);
        setViewed(false);*/
        //setDuration(parcel.readLong());
        //setNumeroEpisodio(parcel.readString());
        //setNomePodcast(parcel.readString());
        //setSizeMedia(parcel.readLong());
        //setTipo(parcel.readInt());
    }

    @Override
    public int compare(Item item, Item t1) {
        if (item.getPubDate() < t1.getPubDate()) {
            return -1;
        }else if (item.getPubDate() > t1.getPubDate()) {
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!title.equalsIgnoreCase(item.title)) return false;
        return pubDate.equals(item.pubDate);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + pubDate.hashCode();
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPubDate() {
        return pubDate;
    }

    public void setPubDate(Long pubDate) {
        this.pubDate = pubDate;
    }

    /*public ForeignCollection<Categoria> getCategories() {
        return categories;
    }

    public void setCategories(ForeignCollection<Categoria> categories) {
        this.categories = categories;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getLocalDownload() {
        return localDownload;
    }

    public void setLocalDownload(String localDownload) {
        this.localDownload = localDownload;
    }

    public Integer getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(Integer resumePosition) {
        this.resumePosition = resumePosition;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(String numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public String getNomePodcast() {
        return nomePodcast;
    }

    public void setNomePodcast(String nomePodcast) {
        this.nomePodcast = nomePodcast;
    }

    public Long getSizeMedia() {
        return sizeMedia;
    }

    public void setSizeMedia(Long sizeMedia) {
        this.sizeMedia = sizeMedia;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeLong(getId());
        dest.writeString(getTitle());
        dest.writeLong(getPubDate());
        dest.writeString(getDescription());
        dest.writeString(getContent());
        dest.writeString(getUrl());
        dest.writeString(getImage());
        dest.writeString(getLocalDownload());
        dest.writeInt(getResumePosition());
        /*dest.write setDownloaded(false);
        dest.write setViewed(false);*/
        //dest.writeLong(getDuration());
        //dest.writeString(getNumeroEpisodio());
        //dest.writeString(getNomePodcast());
        //dest.writeLong(getSizeMedia());
        //dest.writeInt(getTipo());
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        @Override
        public Item createFromParcel(Parcel source){
            return( new Item(source));
        }
        @Override
        public Item[] newArray(int size){
            return(new Item[size]);
        }
    };
}
