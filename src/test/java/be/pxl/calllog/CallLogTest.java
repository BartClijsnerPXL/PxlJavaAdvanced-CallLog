package be.pxl.calllog;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

public class CallLogTest {

	
	CallLog calllog;
	String testline="1;Lindsey Craft;21/08/2014;04:21:14;T-Network;speed else ill;7;IN PROGRESS";
	
	@Before
	public void setUp() throws Exception {
		calllog = CallLogFactory.createCallLog(testline);
	}
	
	
	@Test
	public void test_calllog_creation () {
			assertTrue(CallLogFactory.createCallLog(testline)!=null);
	}
	
	@Test
	public void test_calllog_id () {
		assertEquals(calllog.getId(),new Integer(1));
	}

	@Test
	public void test_calllog_naam () {
		assertEquals(calllog.getNaam(),"Lindsey Craft");
	}

	@Test
	public void test_calllog_datum () {
		assertEquals(CallLogFactory.DATEFORMAT.format(calllog.getDatum()),"21/08/2014 04:21:14");
	}

	@Test
	public void test_calllog_bedrijf () {
		assertEquals(calllog.getBedrijf(),"T-Network");
	}

	@Test
	public void test_calllog_omschrijving () {
		assertEquals(calllog.getOmschrijving(),"speed else ill");
	}

	@Test
	public void test_calllog_prio () {
		assertEquals(calllog.getPrio(),7);
	}

	@Test
	public void test_calllog_status () {
		assertEquals(calllog.getStatus(),CallLogStatus.IN_PROGRESS);
	}

}
