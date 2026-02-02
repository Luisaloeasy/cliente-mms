package com.megasoft.merchant.mms.negocio;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class Acquirer extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nName;
	private int aActive;
	private int globalAcquirerId;
	private boolean mapeado;
	
	public Acquirer(int id, String nName, int aActive){
		super();
		this.id = id;
		this.nName = nName;
		this.aActive = aActive;
	}
	
	public Acquirer(int id){
		super();
		this.id = id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
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
	
	public String toString() {
		return " Id: "+id+" Nname: '"+nName+"' Aactive: "+aActive;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof Acquirer){
			Acquirer acq = (Acquirer)obj;
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
