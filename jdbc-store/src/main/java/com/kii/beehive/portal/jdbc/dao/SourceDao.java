package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Source;
import com.kii.beehive.portal.jdbc.entity.SourceType;

@Repository
public class SourceDao extends BaseDao<Source> {

	//private Logger log= LoggerFactory.getLogger(SourceDao.class);
	
	public static final String TABLE_NAME = "source";
	public static final String KEY = "source_id";
	
	
	public long update(Source tag) {
		String[] columns = new String[]{
				Source.SOURCE_ID,
				Source.TYPE,
				Source.NAME,
				Source.CREATE_DATE,
				Source.CREATE_BY,
				Source.MODIFY_DATE,
				Source.MODIFY_BY,
		};

        return super.update(tag, columns);
    }

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public List<Source> mapToList(List<Map<String, Object>> rows) {
		List<Source> list = new ArrayList<Source>();
		for (Map<String, Object> row : rows) {
			Source entity = new Source();
			entity.setId((int)row.get(Source.SOURCE_ID));
			entity.setName((String)row.get(Source.NAME));
			entity.setType(SourceType.valueOf((String) row.get(Source.TYPE)));
			mapToListForDBEntity(entity, row);
			list.add(entity);
		}
		return list;
	}
}
