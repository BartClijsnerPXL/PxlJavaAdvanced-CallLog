package be.pxl.calllog;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CallLogUtil {

	/**
	 * Print a Collection to standard output
	 * @param callLogCollection Collection<CallLog>
	 */
	public static void printCollection(Collection<CallLog> callLogCollection) {
		callLogCollection.forEach(System.out::println);
		System.out.println(callLogCollection.size());
		
	}

	/**
	 * Fill a Collection with CallLogs based on an array of coll log lines
	 * @param callLogCollection Collection<CallLog>
	 * @param callLogLines String[]
	 */
	public static void fillCollection(Collection<CallLog> callLogCollection, String[] callLogLines) {
		boolean first=true;
		for(String callLogLine : callLogLines) {
			if(first) {
				first=false;
				continue;
			}
			callLogCollection.add(CallLogFactory.createCallLog(callLogLine));
		}
	}

	/**
	 * Fill a Collection with CallLogs from a path with csv files
	 * @param callLogCollection Collection<CallLog>
	 * @param callLogLines String[]
	 */
	public static void fillCollection(Collection<CallLog> callLogCollection, Path callLogPath) throws IOException {
		Files.find(
				callLogPath, 
				1, 
				(file, attr) -> attr.isRegularFile() && file.getFileName().toString().endsWith(".csv"),
				new FileVisitOption[0])
			.forEach(
					file -> {
							try (BufferedReader reader = Files.newBufferedReader(file)) {
								String calllogLine;
								while((calllogLine=reader.readLine())!=null) {
									callLogCollection.add(CallLogFactory.createCallLog(calllogLine));
								}
							} catch (IOException e) {
								System.err.println("Exception reading file " + file.getFileName());
							}
					}
			);
	}

	/**
	 * Get a String array of CallLog lines from a newline separated String
	 * @param callLogData String
	 * @return String[]
	 */
	public static String[] getCallLogLines(String callLogData) {
		return callLogData.split("\n");
	}

	/**
	 * Create a Map of CallLogs by key Person
	 * @param callLogList Collection<CallLog>
	 * @return Map<String, List<CallLog>>
	 */
	public static Map<String, List<CallLog>> createMapByPerson(Collection<CallLog> callLogList) {
		return callLogList.stream().collect(Collectors.groupingBy((CallLog cl) -> cl.getNaam()));
	}
	
	/**
	 * Create a map of CallLogs by key year_month_day
	 * @param callogList
	 * @return Map<String, List<CallLog>>
	 */
	public static Map<String, List<CallLog>> createMapByDate(List<CallLog> callLogList) {
		return callLogList.stream().collect(Collectors.groupingBy((CallLog cl) -> {
			LocalDate localDate = cl.getLocalDate();
			return localDate.getYear() + "_"+ localDate.getMonthValue() + "_" + localDate.getDayOfMonth();
		}));
	}
	
	/**
	 * een manier om een collection op te splitsen in 2 collections op basis van een voorwaarde via streams
	 * @param callLogList
	 */
	public static void splitToProcessWithStream(Collection<CallLog> callLogList) {
		Predicate<CallLog> isClosed = (CallLog cl) -> cl.getStatus() == CallLogStatus.CLOSED;
		
		Map<Boolean,List<CallLog>> partionedList = callLogList.stream().collect(Collectors.partitioningBy(isClosed));
		
		List<CallLog> closedCallLogList = partionedList.get(true);
		List<CallLog> toProcessCallLogList = partionedList.get(false);
		
		System.out.println("---- ARCHIVED ---");
		CallLogUtil.printCollection(closedCallLogList);
		System.out.println("---- TO PROCESS ---");
		CallLogUtil.printCollection(toProcessCallLogList);
	}

	
}
