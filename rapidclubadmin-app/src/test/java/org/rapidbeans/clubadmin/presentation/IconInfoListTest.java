/*
 * Rapid Club Admin Application: IconInfoListTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 06.10.2008
 */
package org.rapidbeans.clubadmin.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Date;

import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.datasource.Document;

/**
 * @author Martin Bluemel
 */
public class IconInfoListTest {

	/**
	 * Test method for
	 * {@link org.rapidbeans.clubadmin.presentation.IconInfoList#addIcon(org.rapidbeans.clubadmin.presentation.IconInfo)}.
	 */
	@Test
	public void testAddIcon() {
		Document doc = new Document(new File("src/test/resources/iconlist.xml"));
		IconInfoList iconList = (IconInfoList) doc.getRoot();
		assertEquals(10, iconList.getIcons().size());
		Trainer trainer = new Trainer(new String[] { "Meier", "Sepp" });
		assertNull(iconList.getIconInfo(trainer));
		IconInfo info = new IconInfo();
		info.setTrainerid(trainer.getIdString());
		info.setLastupload(new Date());
		iconList.addIcon(info);
		assertEquals(11, iconList.getIcons().size());
		assertNotNull(iconList.getIconInfo(trainer));
	}

	/**
	 * Test method for
	 * {@link org.rapidbeans.clubadmin.presentation.IconInfoList#removeIcon(org.rapidbeans.clubadmin.presentation.IconInfo)}.
	 */
	@Test
	public void testRemoveIcon() {
		Document doc = new Document(new File("src/test/resources/iconlist.xml"));
		IconInfoList iconList = (IconInfoList) doc.getRoot();
		assertEquals(10, iconList.getIcons().size());
		Trainer trainer = new Trainer(new String[] { "Holler", "Sepp" });
		IconInfo info = iconList.getIconInfo(trainer);
		assertEquals("Holler_Sepp_", info.getTrainerid());
		assertEquals("20071015015320", info.getProperty("lastupload").toString());
		iconList.removeIcon(info);
		assertEquals(9, iconList.getIcons().size());
		assertNull(iconList.getIconInfo(trainer));
	}
}
