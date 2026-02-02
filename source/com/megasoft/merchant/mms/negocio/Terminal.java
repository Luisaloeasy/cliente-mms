package com.megasoft.merchant.mms.negocio;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class Terminal extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private Client client;
	private String description;
	private MerchantPay merchantPay;
	private String tid;
	private int aActive;
	private String activity;
	private int terminalTmpID;
	
	public Terminal(int id){
		super();
		this.id = id;
	}
	
	public Terminal(int id, int clientId){
		super();
		this.id = id;
		this.client = new Client(clientId);
	}
	
	public Terminal(int id, String tid, String description){
		super();
		this.id = id;
		this.tid = tid;
		this.description = description;
	}

	public Terminal(int id, int clientid, int merchantPayid, String tid, int aActive){
		super();
		this.id = id;
		this.client= new Client(clientid);
		this.merchantPay = new MerchantPay(merchantPayid);
		this.tid = tid;
		this.aActive = aActive;
	}

	/**
	 * 
	 * @param id
	 * @param merchantPayid
	 * @param tid
	 * @param aActive
	 */
	public Terminal(int id, int merchantPayid, String tid, int aActive,String activity,int terminalTmpID){
		super();
		this.id = id;
		this.client = null;
		this.merchantPay = new MerchantPay(merchantPayid);
		this.tid = tid;
		this.aActive = aActive;
		this.activity = activity;
		this.terminalTmpID = terminalTmpID;
	}
	
	public Terminal(int id, int merchantPayid, String tid, int aActive){
		super();
		this.id = id;
		this.client = null;
		this.merchantPay = new MerchantPay(merchantPayid);
		this.tid = tid;
		this.aActive = aActive;
	}
	
	
	public int getClientId() {
		return client.getId();
	}
	public void setClientId(int clientId) {
		this.client = new Client(clientId);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMerchantPayId() {
		return merchantPay.getId();
	}
	public void setMerchantPayId(int merchantPayId) {
		this.merchantPay = new MerchantPay(merchantPayId);
	}
	public String getTID() {
		return tid;
	}
	public void setTID(String tid) {
		this.tid = tid;
	}
	public int getAActive() {
		return aActive;
	}
	public void setAActive(int active) {
		aActive = active;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString(){
		if (client != null && merchantPay != null ){
			return "Id:"+id
				   +" clientId:"+client.getId()
				   +" MerchantPayId:"+merchantPay.getId()
				   +" TID:'"+tid+"' " 
				   +"aActive:"+aActive;
		}
		return "Id:"+id
			   +" TID:'"+tid+"' " 
			   +"aActive:"+aActive;
	}

	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the terminalTmpID
	 */
	public int getTerminalTmpID() {
		return terminalTmpID;
	}

	/**
	 * @param terminalTmpID the terminalTmpID to set
	 */
	public void setTerminalTmpID(int terminalTmpID) {
		this.terminalTmpID = terminalTmpID;
	}

	

}
