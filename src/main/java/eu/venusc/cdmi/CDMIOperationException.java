package eu.venusc.cdmi;

public class CDMIOperationException extends Exception{

	private int responseCode;
	
	/**
	 * 
	 * @param message
	 * @param responseCode
	 */
	public CDMIOperationException(String message, int responseCode) {
		super(message);
		this.responseCode = responseCode;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResponseCode() {
		return responseCode;
	}
	
	/**
	 * 
	 * @param responseCode
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
}
