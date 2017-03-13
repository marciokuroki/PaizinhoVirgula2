package br.com.kuroki.paizinhovirgula2.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.com.kuroki.paizinhovirgula2.entity.Item;

/**
 * Created by marciokuroki on 26/01/17.
 */

public class PaizinhoDataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "paizinho.db";

    private static final int DATABASE_VERSION = 1;

    private final String LOG_TAG = "PAIZINHO_DATABASE";

    //DAOs
    private Dao<Item, Long> itemDao = null;
    //private Dao<Categoria, Integer> categoriaDao = null;

    private RuntimeExceptionDao<Item, Long> itemRuntimeDao = null;
    //private RuntimeExceptionDao<Categoria, Integer> categoriaRuntimeDao = null;

    public PaizinhoDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(LOG_TAG, "onCreate");
            //TableUtils.createTable(connectionSource, Categoria.class);
            TableUtils.createTable(connectionSource, Item.class);
        }catch (SQLException e) {
            Log.e(LOG_TAG, "Deu Ruim na hora de criar o Banco");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(LOG_TAG, "onUpdate");
            //TableUtils.dropTable(connectionSource, Categoria.class, true);
            TableUtils.dropTable(connectionSource, Item.class, true);
            this.onCreate(database, connectionSource);
        }catch (SQLException e) {
            Log.e(LOG_TAG, "Deu ruim na hora de fazer o update do banco");
            throw new RuntimeException(e);
        }
    }

    public Dao<Item, Long> getItemDao() throws SQLException {
        if (itemDao == null)
            itemDao = getDao(Item.class);
        return itemDao;
    }

    /*public Dao<Categoria, Integer> getCategoriaDao() throws SQLException {
        if (categoriaDao == null)
            categoriaDao = getDao(Categoria.class);
        return categoriaDao;
    }*/

    public RuntimeExceptionDao<Item, Long> getItemRuntimeDao() {
        if (itemRuntimeDao == null)
            itemRuntimeDao = getRuntimeExceptionDao(Item.class);
        return itemRuntimeDao;
    }

    /*public RuntimeExceptionDao<Categoria, Integer> getCategoriaRuntimeDao() {
        if (categoriaRuntimeDao == null)
            categoriaRuntimeDao = getRuntimeExceptionDao(Categoria.class);
        return categoriaRuntimeDao;
    }*/
}
