package com.kii.beehive.portal.jdbc.helper;

import javax.sql.DataSource;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.entity.DBEntity;


public  class BindClsFullUpdateTool extends SqlUpdate {


	private Logger log= LoggerFactory.getLogger(BindClsFullUpdateTool.class);

	private static final String updateSqlTemplate="update ${0} set  ${1} where ${2}  =  ? ";

	private BeanWrapper wrapper;


	private String pk;

	private String tableName;


	public BindClsFullUpdateTool(DataSource ds,String tableName) {
			setDataSource(ds);
			this.tableName=tableName;
	}

	public void setPkField(String pk) {
			this.pk = pk;
	}

	private boolean ignoreNull=true;

	public void setNotIgnoreNull(){
		ignoreNull=false;
	}

	public void compileWithClass(Class<? extends DBEntity> cls){

		List<SqlParameter> paramList=new ArrayList<>();

		StringBuilder fields=new StringBuilder();

		wrapper= PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));


		for(PropertyDescriptor descriptor: wrapper.getPropertyDescriptors()){

			JdbcField fieldDesc=descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if(fieldDesc==null){
				continue;
			}

			SqlParameter param = new SqlParameter(fieldDesc.column(), fieldDesc.type().getSqlType());
			paramList.add(param);

			fields.append(fieldDesc.column()).append(" =  ?, ");

		}

		String fullSql= StrTemplate.gener(updateSqlTemplate,tableName,fields.toString(),pk);

		super.setSql(fullSql);

		paramList.forEach(super::declareParameter);

		super.compile();

	}

	public int executeUpdate(DBEntity entity){

			List<SqlParameter> paramList=new ArrayList<>();

			StringBuilder fields=new StringBuilder();

			wrapper= PropertyAccessorFactory.forBeanPropertyAccess(entity);


			for(PropertyDescriptor descriptor: wrapper.getPropertyDescriptors()){

				JdbcField fieldDesc=descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

				if(fieldDesc==null){
					continue;
				}
				Object val=wrapper.getPropertyValue(descriptor.getName());
				if(val==null  && ignoreNull) {
					continue;
				}

				SqlParameter param = new SqlParameter(fieldDesc.column(), fieldDesc.type().getSqlType());
				paramList.add(param);

				fields.append(fieldDesc.column()).append(" =  ?, ");

				log.debug(" fill update param "+fieldDesc.column()+ " with "+val);
			}

			String fullSql= StrTemplate.gener(updateSqlTemplate,tableName,fields.toString(),pk);

			setSql(fullSql);

			paramList.forEach(this::declareParameter);

			compile();

			return update(entity);
		}

		public <T extends DBEntity> int execute(T  entity) {
			return update(entity);
		}


}