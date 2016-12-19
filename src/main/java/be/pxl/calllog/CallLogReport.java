package be.pxl.calllog;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CallLogReport {
	
	Collection<CallLog> callLogList;
	
	public CallLogReport(Collection<CallLog> callLogList) {
		super();
		this.callLogList = callLogList;
	}

	/**
	 * Een print van call logs die ouder zijn dan 2 jaar gesorteerd op datum
	 * @param callLogList
	 * @throws ParseException 
	 */
	public List<CallLog> createArchiveAndSortByDate(Date archiveStartDate) {

		return callLogList.stream()
			.filter(callLog -> callLog.getDatum().before(archiveStartDate))
//			// incl. rapport 4
//			// merk op dat we hier in tegenstelling tot de Sets niet nog eens moeten comparen op de unieke id
			.sorted(Comparator.comparing((CallLog callLog) -> callLog.getDatum()))
			.collect(Collectors.toList());
	}

	/**
	 * Live sort op prio en datum
	 * @param callLogList
	 * @throws ParseException
	 */
	public void sortByPrioAndDate() {
		Set<CallLog> callLogSetAuto = new TreeSet<>(
				Comparator.comparingInt((CallLog cl) -> cl.getPrio())
				.thenComparing((CallLog cl) -> cl.getDatum())
				.thenComparingInt((CallLog cl) -> cl.getId())
				);
		callLogSetAuto.addAll(callLogList);
		callLogList = callLogSetAuto;
	}

	public void saveReport(String folder, String filename) throws IOException {
		if(callLogList==null || callLogList.isEmpty()) { return; }
		
		Files.createDirectories(Paths.get(folder), new FileAttribute[0]);
		
		FileWriter writer = new FileWriter(folder + System.getProperty("file.separator")+filename);
		for (CallLog callLog : callLogList) {
			writer.write(CallLogFactory.createCallLogLine(callLog));
		}
		writer.flush();
		writer.close();
	}

}
