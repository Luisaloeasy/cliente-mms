package com.megasoft.merchant.mms.negocio;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MerchantPay extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private Client client;
	private Merchant merchant;
	private Payment payment;
	private String nName;
	private int aActive;
	private String activity;
	private int merchantPayTmpId;
	private String mid;

	public MerchantPay(int id){
		super();
		this.id = id;
	}
	
	
	public MerchantPay(int id, int clientId, int merchantId, int paymentId, String nName, int aActive){
		super();
		this.id = id;
		this.client = new Client(clientId);
		this.merchant = new Merchant(merchantId);
		this.payment = new Payment(paymentId);
		this.nName = nName;
		this.aActive = aActive;
	}
	/**
	 * Se agregar constructor con el campo mid, cambio realizado el 16/05/2013
	 * @param id
	 * @param merchantId
	 * @param paymentId
	 * @param nName
	 * @param aActive
	 * @param mid
	 */
	public MerchantPay(int id,  int merchantId, int paymentId, String nName, int aActive, String mid){
		super();
		this.id = id;
		this.merchant = new Merchant(merchantId);
		this.payment = new Payment(paymentId);
		this.nName = nName;
		this.aActive = aActive;
		this.mid = mid;
	}
	
	
	/**
	 * Se agrega constructor con el campo mid
	 * Cambio realizado el día 16/05/2013
	 * @param id
	 * @param merchantId
	 * @param paymentId
	 * @param nName
	 * @param aActive
	 * @param activity
	 * @param merchantPayTmpId
	 * @param mid
	 */
	public MerchantPay(int id,  int merchantId, int paymentId, String nName, int aActive,String activity,int merchantPayTmpId, String mid){
		super();
		this.id = id;
		this.merchant = new Merchant(merchantId);
		this.payment = new Payment(paymentId);
		this.nName = nName;
		this.aActive = aActive;
		this.activity = activity;
		this.merchantPayTmpId = merchantPayTmpId;
		this.mid = mid;
	}
	
	
	/**
	 * Se agrega constructor con el campo Activity
	 * Cambio realizado el día 16/01/2013
	 * @param id
	 * @param merchantId
	 * @param paymentId
	 * @param nName
	 * @param aActive
	 * @param activity
	 * @param merchantPayTmpId
	 */
	public MerchantPay(int id,  int merchantId, int paymentId, String nName, int aActive,String activity,int merchantPayTmpId){
		super();
		this.id = id;
		this.merchant = new Merchant(merchantId);
		this.payment = new Payment(paymentId);
		this.nName = nName;
		this.aActive = aActive;
		this.activity = activity;
		this.merchantPayTmpId = merchantPayTmpId;
	}
	
	public MerchantPay(int id,  int merchantId, int paymentId, String nName, int aActive){
		super();
		this.id = id;
		this.merchant = new Merchant(merchantId);
		this.payment = new Payment(paymentId);
		this.nName = nName;
		this.aActive = aActive;
	}
	
	public MerchantPay(int id,  int merchantId, int paymentId, String nName, int aActive,String nombreMerchant,String nombrePayment){
		super();
		this.id = id;
		this.merchant = new Merchant(merchantId,0,nombreMerchant,"",0);
		this.payment = new Payment(paymentId,0,nombrePayment,"",0);
		this.nName = nName;
		this.aActive = aActive;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAActive() {
		return aActive;
	}
	public void setAActive(int active) {
		aActive = active;
	}
	public int getClientId() {
		return client.getId();
	}
	public void setClientId(int clientId) {
		this.client = new Client(clientId);
	}
	public int getMerchantId() {
		return merchant.getId();
	}
	public void setMerchantId(int merchantId) {
		this.merchant = new Merchant(merchantId);
	}
	public String getNName() {
		return nName;
	}
	public void setNName(String name) {
		nName = name;
	}
	public int getPaymentId() {
		return payment.getId();
	}
	public void setPaymentId(int paymentId) {
		this.payment = new Payment(paymentId);
	}
	
	public String toString(){
		if (client != null && merchant != null && payment != null){
			return "Id:"+id+" ClientId:"+client.getId()+" MerchantId:"+merchant.getId()+" PaymentId:"+payment.getId()+" NName:'"+nName+"' AActive:"+aActive;
		}
		return "Id:"+id+" NName:'"+nName+"' AActive:"+aActive;
	}

	/**
	 * @return the merchant
	 */
	public Merchant getMerchant() {
		return merchant;
	}

	/**
	 * @param merchant the merchant to set
	 */
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	/**
	 * @return the payment
	 */
	public Payment getPayment() {
		return payment;
	}

	/**
	 * @param payment the payment to set
	 */
	public void setPayment(Payment payment) {
		this.payment = payment;
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
	 * @return the merchantPayTmpId
	 */
	public int getMerchantPayTmpId() {
		return merchantPayTmpId;
	}


	/**
	 * @param merchantPayTmpId the merchantPayTmpId to set
	 */
	public void setMerchantPayTmpId(int merchantPayTmpId) {
		this.merchantPayTmpId = merchantPayTmpId;
	}


	/**
	 * @return the mid
	 */
	public String getMid() {
		return mid;
	}


	/**
	 * @param mid the mid to set
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}
}
