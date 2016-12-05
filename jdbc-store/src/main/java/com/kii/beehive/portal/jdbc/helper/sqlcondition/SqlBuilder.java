package com.kii.beehive.portal.jdbc.helper.sqlcondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SqlBuilder {


	private static class TableInfo{

		private String tableName;

		private String alias;

		private String keyName;

		public TableInfo(String tableName,String alias,String keyName){

			this.tableName=tableName;
			this.alias=alias;
			this.keyName=keyName;
		}

	}

	private StringBuilder sqlTmp=new StringBuilder();

	private StringBuilder fromSeq=new StringBuilder(" from ");

	private StringBuilder selectSeq=new StringBuilder();


	private List<TableInfo> aliasList=new ArrayList<>();

	private AtomicInteger  tableOrder=new AtomicInteger(1);


	private SqlBuilder(){

	}

	public static SqlBuilder getBuilder(String tableName,String keyName){

		SqlBuilder builder=new SqlBuilder();
		builder.fromSeq.append(" ").append(tableName).append(" ");

		String alias="table"+builder.tableOrder.getAndIncrement();

		TableInfo info=new TableInfo(tableName,alias,keyName);
		builder.aliasList.add(info);

		builder.fromSeq.append(" ").append(alias).append(" ");

		return builder;
	}

	
	public SqlBuilder addLeftJoin(String tableName,String keyName){
		return addJoin(tableName,keyName," left ");
	}


	public SqlBuilder addInnerJoin(String tableName,String keyName){
		return addJoin(tableName,keyName," inner ");
	}


	public SqlBuilder addRightJoin(String tableName,String keyName){
		return addJoin(tableName,keyName," right ");
	}


	public SqlBuilder addSelectFieldList(Collection<String> fieldList){


		fieldList.forEach((f)->{

			selectSeq.append(aliasList.get(0).alias).append(".").append(f);

			selectSeq.append(" , ");

		});

		return this;

	}

	public SqlBuilder addFunList(Collection<String> funList){


		funList.forEach((f)->{

			selectSeq.append(f);

			selectSeq.append(" , ");

		});

		return this;

	}

	private SqlBuilder addJoin(String tableName,String keyName,String joinType){
		return addJoinToIdx(tableName,keyName,joinType,tableOrder.get()-1);
	}

	private SqlBuilder addJoinToFirst(String tableName,String keyName,String joinType){
		return addJoinToIdx(tableName,keyName,joinType,0);
	}

	private  SqlBuilder addJoinToIdx(String tableName,String keyName,String joinType,int previous){

		String alias="table"+tableOrder.getAndIncrement();

		fromSeq.append(joinType).append(" join "+tableName+" "+alias);

		TableInfo first=aliasList.get(previous);

		fromSeq.append(" on "+first.alias).append(".").append(first.keyName).append(" = ").append(alias).append(".").append(keyName);

		return this;
	}






}
