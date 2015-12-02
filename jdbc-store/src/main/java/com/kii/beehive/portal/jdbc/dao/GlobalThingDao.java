package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingEntity;
import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@Repository
public class GlobalThingDao {


	public static final String TABLE_NAME = "t_global_thing";
	private JdbcTemplate jdbcTemplate;


	private SimpleJdbcInsert insertTool;


	private BindClsRowMapper  rowMapper=new BindClsRowMapper(GlobalThingEntity.class);

	@Autowired
	public void setDataSource(DataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.insertTool=new SimpleJdbcInsert(dataSource)
				.withTableName(TABLE_NAME)
				.usingGeneratedKeyColumns("id");
	}


	public void test(){

		jdbcTemplate.execute("select sysdate() from dual");

	}

	public long insertThing(GlobalThingEntity entity){

		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);


		Number id=insertTool.executeAndReturnKey(parameters);

		return id.longValue();

	}

	private String getByIDTemplate="select * from {0} where {1} = ? ";
	public GlobalThingEntity getThingByID(long id){

		String sql= StrTemplate.gener(getByIDTemplate,TABLE_NAME,"thing_id");

		List<GlobalThingEntity> list= jdbcTemplate.query("select * from ",new Object[]{id},rowMapper);

		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}
	}


}
