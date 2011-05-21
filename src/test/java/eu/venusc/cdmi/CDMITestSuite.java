package eu.venusc.cdmi;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CDMITestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for eu.venusc.cdmi");
		suite.addTestSuite(ContainerOperationsTest.class);
		suite.addTestSuite(BlobOperationsTest.class);
		suite.addTestSuite(NCDMIContainerOperationsTest.class);
		suite.addTestSuite(NCDMIBlobOperationsTest.class);
		return suite;
	}
}