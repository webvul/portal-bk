package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;

public class TestTagIndexDao extends TestTemplate{

	@Autowired
	private TagIndexDao dao;
	
	private TagIndex  tag =new TagIndex();
	
	@Before
	public void init(){
		tag.setDisplayName("DisplayName");
		tag.setTagType(TagType.Custom);
		tag.setDescription("Description");
		long id=dao.saveOrUpdate(tag);
		tag.setId(id);
	}
	
	@Test
	public void testFindByID(){

		TagIndex  entity=dao.findByID(tag.getId());
		assertEquals(tag.getDisplayName(),entity.getDisplayName());
		assertEquals(tag.getTagType(),entity.getTagType());
		assertEquals(tag.getDescription(),entity.getDescription());

	}
	
	@Test
	public void testFindByIDs(){
		TagIndex  tag2 =new TagIndex();
		tag2.setDisplayName("DisplayName2");
		tag2.setTagType(TagType.Location);
		tag2.setDescription("Description2");
		long id2=dao.saveOrUpdate(tag2);
		
		List<TagIndex>  list=dao.findByIDs(new Object[]{tag.getId(),id2});

		assertEquals(2,list.size());
	}
	
	@Test
	public void testDelete(){
		dao.deleteByID(tag.getId());
		TagIndex  entity=dao.findByID(tag.getId());
		assertNull(entity);
	}
	
	@Test
	public void testIsIdExist(){
		boolean b = dao.IsIdExist(tag.getId());
		assertTrue(b);
	}
	
	
}
