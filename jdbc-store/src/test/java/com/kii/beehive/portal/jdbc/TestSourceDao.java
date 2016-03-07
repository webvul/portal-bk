package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.SourceDao;
import com.kii.beehive.portal.jdbc.entity.Source;
import com.kii.beehive.portal.jdbc.entity.SourceType;

public class TestSourceDao extends TestTemplate {

	@Autowired
	private SourceDao dao;

	private Source source = new Source();

	@Before
	public void init() {
		source.setName("SourceNameTest");
		source.setType(SourceType.Web);
		long id = dao.saveOrUpdate(source);
		source.setId(id);
	}

	@Test
	public void testFindByID() {
		Source entity = dao.findByID(source.getId());
		assertEquals(source.getName(), entity.getName());
		assertEquals(source.getType(), entity.getType());

	}

	@Test
	public void testUpdate() {
		source.setName("SourceNameUpdate");
		dao.saveOrUpdate(source);
		Source entity = dao.findByID(source.getId());
		assertEquals("SourceNameUpdate", entity.getName());
		assertEquals(source.getType(), entity.getType());

	}

	@Test
	public void testFindByIDs() {
		Source source2 = new Source();
		source2.setName("SourceNameTest2");
		source2.setType(SourceType.Web);
		long id2 = dao.saveOrUpdate(source2);
		List<Long> ids = new ArrayList<>();
		ids.add(source.getId());
		ids.add(id2);
		List<Source> list = dao.findByIDs(ids);

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		dao.deleteByID(source.getId());
		Source entity = dao.findByID(source.getId());
		assertNull(entity);
	}

	@Test
	public void testIsIdExist() {
		boolean b = dao.IsIdExist(source.getId());
		assertTrue(b);
	}

}
