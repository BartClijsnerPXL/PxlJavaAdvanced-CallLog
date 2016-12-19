package be.pxl.calllog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallLogFactory {
	
	public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static final String DEFAULT_CALLLOG_ITEM_SEPARATOR = ";";
	
	/**
	 * Create a CallLog object from a call log line (with ; as item separator)
	 * @param callLogLine String
	 * @return CallLog
	 */
	public static CallLog createCallLog(String callLogLine) {
		return createCallLog(callLogLine, DEFAULT_CALLLOG_ITEM_SEPARATOR);
	}
	
	/**
	 * Create a CallLog object from a call log line
	 * @param callLogLine String
	 * @param itemSeparator String
	 * @return CallLog
	 */
	public static CallLog createCallLog(String callLogLine, String itemSeparator) {
		
		String[] callLogArray = callLogLine.split(itemSeparator);

		CallLog callLog = new CallLog();
		callLog.setId(Integer.parseInt(callLogArray[0]));
		callLog.setNaam(callLogArray[1]);
		
		try {
			callLog.setDatum(DATEFORMAT.parse(callLogArray[2] + " " + callLogArray[3]));
		} catch (ParseException e) {
			System.err.println("Error parsing date for callog " + callLog.getId());
		}
		
		callLog.setBedrijf(callLogArray[4]);
		callLog.setOmschrijving(callLogArray[5]);
		callLog.setPrio(Integer.parseInt(callLogArray[6]));
		callLog.setStatus(CallLogStatus.getCallLogStatusType(callLogArray[7]));
		
		return callLog;
		
	}

	/**
	 * Create a call log line from a CallLog object (with ; as iten separator)
	 * @param callLog
	 * @return
	 */
	public static String createCallLogLine(CallLog callLog) {
		return createCallLogLine(callLog, DEFAULT_CALLLOG_ITEM_SEPARATOR);
	}
	
	/**
	 * Create a call log line from a CallLog object
	 * @param callLog CallLog
	 * @param itemSeparator String
	 * @return String
	 */
	public static String createCallLogLine(CallLog callLog, String itemSeparator) {
		StringBuffer callLogBuffer = new StringBuffer();
		
		callLogBuffer.append(callLog.getId());					// 0
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(callLog.getNaam());				// 1
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(getDate(callLog.getDatum()));		// 2
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(getTime(callLog.getDatum()));		// 3
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(callLog.getBedrijf());				// 4
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(callLog.getOmschrijving());		// 5
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(callLog.getPrio());				// 6
		callLogBuffer.append(itemSeparator);
		callLogBuffer.append(callLog.getStatus().getValue());	// 7
		callLogBuffer.append(itemSeparator);
		
		return callLogBuffer.toString();
		
	}

	private static String getDate(Date datum) {
		try {
			return DATEFORMAT.format(datum).split(" ")[0];
		} catch (Exception e) {
			System.err.println("Error formatting date "+e.getMessage());
			return "";
		}
	}
	
	private static String getTime(Date datum) {
		try {
			return DATEFORMAT.format(datum).split(" ")[1];
		} catch (Exception e) {
			System.err.println("Error formatting time "+e.getMessage());
			return "";
		}

	}

}
