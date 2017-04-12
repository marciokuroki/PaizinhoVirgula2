package br.com.kuroki.paizinhovirgula2.persistence.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.persistence.dao.interfaces.ItemDao;

/**
 * Created by marciokuroki on 08/04/17.
 */

public class ItemDaoImpl extends BaseDaoImpl<Item, Long> implements ItemDao {

    public ItemDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Item.class);
    }

    @Override
    public List<Item> pesquisarNItens(long quantidade, long idUltPos, boolean isNext) throws SQLException {
        Item itemUltPos;

        QueryBuilder<Item, Long> queryBuilder = this.queryBuilder();
        Where<Item, Long> where = queryBuilder.where();
        where.isNull(Item.NMCP_TIPO);

        if (this.idExists(idUltPos)) {
            where.and();
            itemUltPos = this.queryForId(idUltPos);
            if (isNext)
                where.lt(Item.NMCP_PUBDATE, itemUltPos.getPubDate());
            else
                where.gt(Item.NMCP_PUBDATE, itemUltPos.getPubDate());
        }
        //queryBuilder.orderBy(Item.NMCP_ID, false);
        queryBuilder.orderBy(Item.NMCP_PUBDATE, false);
        queryBuilder.limit(quantidade);

        PreparedQuery<Item> preparedQuery = queryBuilder.prepare();

        return this.query(preparedQuery);
    }
}
