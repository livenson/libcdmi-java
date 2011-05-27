package eu.venusc.cdmi;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CDMITestSuite extends TestSuite {

	public static Test suite() {
<<<<<<< HEAD
		TestSuite suite = new TestSuite("Test for eu.venusc.cdmi");
		suite.addTestSuite(ContainerOperationsTest.class);
		suite.addTestSuite(BlobOperationsTest.class);
		suite.addTestSuite(NonCDMIContainerOperationsTest.class);
=======
		TestSuite suite = new TestSuite("Test for eu.venusc.test");
		//suite.addTestSuite(ContainerOperationsTest.class);
		//suite.addTestSuite(BlobOperationsTest.class);
		//suite.addTestSuite(NCDMIContainerOperationsTest.class);
>>>>>>> dev-0.1
		suite.addTestSuite(NonCDMIBlobOperationsTest.class);
		return suite;
	}
}