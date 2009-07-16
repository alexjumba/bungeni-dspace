package org.un.undesa.bungeni.crosswalk.search;

public class TransferMap implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;
	private Object value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public TransferMap(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	public TransferMap() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
