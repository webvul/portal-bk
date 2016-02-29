package com.kii.beehive.portal.store.entity.trigger;

public class SummaryExpress {

	private String stateName;

	private SummaryFunctionType function;

	private String summaryAlias;

//	private String sumField;
//
//	private String countField;

//	public String getSumField() {
//		return sumField;
////	}
//
//	public void setSumField(String sumField) {
//		this.sumField = sumField;
//	}
//
//	public String getCountField() {
//		return countField;
//	}
//
//	public void setCountField(String countField) {
//		this.countField = countField;
//	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public SummaryFunctionType getFunction() {
		return function;
	}

	public void setFunction(SummaryFunctionType function) {
		this.function = function;
	}



	public String getSummaryAlias() {
		return summaryAlias;
	}

	public void setSummaryAlias(String summaryAlias) {
		this.summaryAlias = summaryAlias;
	}


//	@JsonIgnore
//	public List<SummaryExpress> generAdditionExp(){
//
//		List<SummaryExpress> expList=new ArrayList<>();
//
//		expList.add(getSumExpress());
//		expList.add(getCountExpress());
//
//		return expList;
//	}

//	private SummaryExpress getSumExpress(){
//
//		SummaryExpress sumExp=new SummaryExpress();
//		sumExp.setStateName(getStateName());
//		sumExp.setSummaryAlias(getSummaryAlias()+"_sum");
//		sumExp.setFunction(SummaryFunctionType.Sum);
//
////		this.sumField=sumExp.getSummaryAlias();
//
//		return sumExp;
//
//	}

//	private SummaryExpress getCountExpress(){
//
//		SummaryExpress countExp=new SummaryExpress();
//		countExp.setStateName(getStateName());
//		countExp.setSummaryAlias(getSummaryAlias()+"_count");
//		countExp.setFunction(SummaryFunctionType.Count);
//
////		this.countField=countExp.getSummaryAlias();
//		return countExp;
//
//	}


}
