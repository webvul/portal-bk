package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingEntity;

@Repository
public class GlobalThingDao {


	public static final String TABLE_NAME = "t_global_thing";
	private JdbcTemplate jdbcTemplate;


	private SimpleJdbcInsert insertTool;


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

	public void insertThing(GlobalThingEntity entity){

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);


		insertTool.executeAndReturnKey(parameters);



	}

}
