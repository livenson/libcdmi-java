package eu.venusc.cdmi;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CDMITestSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite("Test for eu.venusc.cdmi");
		suite.addTestSuite(ContainerOperationsTest.class);
		suite.addTestSuite(BlobOperationsTest.class);
		suite.addTestSuite(NonCDMIContainerOperationsTest.class);
		suite.addTestSuite(NonCDMIBlobOperationsTest.class);
		return suite;
	}
}