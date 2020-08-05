package com.alfardan.ekyc.model;

public class CheckDeviceStatus {
	
	
	
	private boolean isActive;
	private boolean isRegistered;
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isRegistered() {
		return isRegistered;
	}
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
	@Override
	public String toString() {
		return "CheckDeviceStatus [isActive=" + isActive + ", isRegistered=" + isRegistered + "]";
	}
	
	
	
}
