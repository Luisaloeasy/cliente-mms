package com.megasoft.merchant.mms.negocio;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class Payment extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private Acquirer acquirer;
	private String nName;
	private String description;
	private int aActive;
	private int globalAcquirerId;
	private int globalPaymentId;
	private boolean mapeado;
	
	public Payment(int id) {
		super();
		this.id = id;
	}
	
	public Payment(int id, int acquirerId, String nName, String description, int aActive){
		super();
		this.id = id;
		this.acquirer = new Acquirer(acquirerId);
		this.nName = nName;
		this.description = description;
		this.aActive = aActive;
	}

	public int getAcquirerId() {
		return acquirer.getId();
	}
	public void setAcquirerId(int acquirerId) {
		this.acquirer = new Acquirer(acquirerId);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNName() {
		return nName;
	}
	public void setNName(String name) {
		nName = name;
	}
	public int getAActive() {
		return aActive;
	}
	public void setAActive(int active) {
		aActive = active;
	}

	public String toString(){
		if(acquirer != null ){
			return " Id:"+id+" AcquirerId:"+acquirer.getId()+" Nname:"+nName+" description:"+description+" aActive:"+aActive;
		}
		return " Id:"+id+" Nname:"+nName+" description:"+description+" aActive:"+aActive;
	}

	public boolean equals(Object obj){
		if (obj instanceof Payment){
			Payment acq = (Payment)obj;
			return (this.id == acq.getId());
	    }
		return false;
	}

	/**
	 * @return the globalAcquirerId
	 */
	public int getGlobalAcquirerId() {
		return globalAcquirerId;
	}

	/**
	 * @param globalAcquirerId the globalAcquirerId to set
	 */
	public void setGlobalAcquirerId(int globalAcquirerId) {
		this.globalAcquirerId = globalAcquirerId;
	}

	/**
	 * @return the globalPaymentId
	 */
	public int getGlobalPaymentId() {
		return globalPaymentId;
	}

	/**
	 * @param globalPaymentId the globalPaymentId to set
	 */
	public void setGlobalPaymentId(int globalPaymentId) {
		this.globalPaymentId = globalPaymentId;
	}

	/**
	 * @return the mapeado
	 */
	public boolean isMapeado() {
		return mapeado;
	}

	/**
	 * @param mapeado the mapeado to set
	 */
	public void setMapeado(boolean mapeado) {
		this.mapeado = mapeado;
	}
	
}
