package be.pxl.calllog;

public enum CallLogStatus {
	OPEN("OPEN"),
	IN_PROGRESS("IN PROGRESS"),
	IGNORE("IGNORE"),
	CLOSED("CLOSED");

	String value;
	
	CallLogStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static CallLogStatus getCallLogStatusType(String value) {
		for(CallLogStatus status : CallLogStatus.values()) {
			if(status.getValue().equals(value)) {
				return status;
			}
		}
		return null;
	}
	
}
