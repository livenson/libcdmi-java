package eu.venusc.cdmi;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CDMITestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for eu.venusc.test");
		suite.addTestSuite(ContainerOperationsTest.class);
		suite.addTestSuite(BlobOperationsTest.class);
		return suite;
	}
}