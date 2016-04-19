package com.kii.beehive.portal.entitys;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.google.common.base.Objects;

public class PatternSet {


	private String name="";

	private Map<String,PatternSet> subSet=new HashMap<>();

	public PatternSet(Set<String> patterns){

		this(patterns.iterator().next());

		patterns.forEach((v)-> {
			PatternSet pattern=new PatternSet(v);
			this.merge(pattern);
		});
	}

	private void merge(PatternSet pattern){
		if (!name.equals(pattern.name)) {
			throw new IllegalArgumentException();
		}


		pattern.subSet.forEach((k,v)->{
			PatternSet curr=subSet.get(k);
			if(curr==null){
				subSet.put(k,v);
			}else{
				curr.merge(v);
			}
		});

	}


	private PatternSet(String  path){
		if(StringUtils.isEmpty(path)){
			throw new NullPointerException();
		}

		if(path.indexOf(".")==-1){
			this.name=path;
			return;
		}

		name=path.substring(0,path.indexOf("."));
		String tail=path.substring(path.indexOf(".")+1);

		PatternSet pattern=new PatternSet(tail);

		subSet.put(pattern.getName(),pattern);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PatternSet that = (PatternSet) o;
		return Objects.equal(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	public String getName() {
		return name;
	}

	public Set<String> getNextLevel(){
		return subSet.keySet();
	}

	public PatternSet getNextPattern(String name) {
		return subSet.get(name);
	}
}
