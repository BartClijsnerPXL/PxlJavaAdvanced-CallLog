package be.pxl.calllog;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CallLogApp {

	String inputFolder;
	String outputFolder;
	String archiveFolder;
	Date archiveUntilDate;
	
	public CallLogApp() {

		init();
		
		Collection<CallLog> callLogList = createCallLogCollection();
		
		CallLogReport callLogReport = new CallLogReport(callLogList);
		
		saveReport(callLogReport,"report2.csv"); // sort on bedrijf (natural order)

		callLogReport.sortByPrioAndDate();
		saveReport(callLogReport,"report1.csv"); // sort by prio and date
		
		createArchive(callLogReport);
		
	}

	/**
	 * Save archive sorted by date
	 * @param callLogReport
	 */
	private void createArchive(CallLogReport callLogReport) {
		String archiveFilename = "calllog_report_4.csv";
		
		List<CallLog> archive = callLogReport.createArchiveAndSortByDate(archiveUntilDate);
		Map<String, List<CallLog>> archiveMap = CallLogUtil.createMapByDate(archive);
		
		for (String callLogKey : archiveMap.keySet()) {
			String archiveLocation = archiveFolder + System.getProperty("file.separator") + callLogKey.replaceAll("_", System.getProperty("file.separator")); 
			try {
				System.out.println("Saving archive "+archiveLocation+"/"+archiveFilename);
				new CallLogReport(archiveMap.get(callLogKey)).saveReport(archiveLocation, archiveFilename);
			} catch (IOException e) {
				System.err.println("Exception saving archive "+archiveLocation+"/"+archiveFilename);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save a report
	 * @param callLogReport
	 */
	private void saveReport(CallLogReport callLogReport, String reportName) {
		try {
			callLogReport.saveReport(outputFolder, reportName);
		} catch (IOException e) {
			System.err.println("Exception saving report: "+reportName);
			e.printStackTrace();
		}
	}

	private Collection<CallLog> createCallLogCollection() {
		Collection<CallLog> callLogList = new TreeSet<>(); // CallLogs will have a natural order
		try {
			System.out.println(String.format("Reading files from: %s",inputFolder));
			CallLogUtil.fillCollection(callLogList, Paths.get(inputFolder));
		} catch (IOException e) {
			System.err.println("Exception reading input files " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		return callLogList;
	}

	/**
	 * Initialize properties
	 */
	private void init() {
		Properties properties = getApplicationProperties("src/main/resources/");
		inputFolder = properties.getProperty("inputfolder");
		outputFolder = properties.getProperty("outputfolder");
		archiveFolder = properties.getProperty("archivefolder");
		try {
			archiveUntilDate = new SimpleDateFormat("dd/MM/yyyy").parse(properties.getProperty("archiveUntilDate"));
		} catch (ParseException e) {
			System.out.println("Unable to parse archiveUntilDate property. Will use default 2 years from now");
			archiveUntilDate = get2YearsFromNow();
		}
		
	}

	/**
	 * Get a Date object 2 years from now
	 * @return Date
	 */
	private Date get2YearsFromNow() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, -2);
		return now.getTime();
	}

	/**
	 * Load the application.properties file
	 * @param propertyLocation
	 * @return Properties
	 */
	private Properties getApplicationProperties(String propertyLocation) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertyLocation+System.getProperty("file.separator")+"application.properties"));
		} catch (IOException e) {
			System.err.println("apllication.properties file not found in "+propertyLocation);
			e.printStackTrace();
			System.exit(1);
		}
		return properties;
	}
	
	public static void main(String[] args) {
		new CallLogApp();
	}

}
