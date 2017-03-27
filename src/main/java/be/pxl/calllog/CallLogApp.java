package be.pxl.calllog;


import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CallLogApp {

	String inputFolder;
	String outputFolder;
	String archiveFolder;
	Date archiveUntilDate;
	
	public CallLogApp() {
		
		long start = System.currentTimeMillis();

		init();
		
		Collection<CallLog> callLogList = createCallLogCollectionMultiThreaded();
//		Collection<CallLog> callLogList = createCallLogCollection();
		
		System.out.println("CallLogs: "+callLogList.size());
		
		CallLogReport callLogReport = new CallLogReport(callLogList);
		
		saveReport(callLogReport,"report2.csv"); // sort on bedrijf (natural order)

		callLogReport.sortByPrioAndDate();
		saveReport(callLogReport,"report1.csv"); // sort by prio and date
		
		createArchive(callLogReport);
		
		long end = System.currentTimeMillis() - start;
		System.out.printf("Application ended in %f seconds",(float)end/1000);
	}

	private Collection<CallLog> createCallLogCollectionMultiThreaded() {
		List<CallLog> callLogList = Collections.synchronizedList(new ArrayList<>());
		Path csvFolder = Paths.get(inputFolder);
		File[] csvFiles = csvFolder.toFile().listFiles((dir, name) -> name.endsWith(".csv"));

		List<Thread> activeThreads = new ArrayList<>();
		
		for (File csvFile : csvFiles) {
			CallLogCollectorThread callLogThread = new CallLogCollectorThread(callLogList, csvFile);
			callLogThread.setName("Thread-"+csvFile.getName());
			callLogThread.start();
			activeThreads.add(callLogThread);
		}
		
		// Wait for threads to finish
		for(Thread activeThread : activeThreads) {
			try {
				activeThread.join();
			} catch (InterruptedException e) {
				System.out.println("Interruption of thread " + Thread.currentThread().getName());
				e.printStackTrace();
			}
		}
		
		return callLogList;
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
			
			String archiveLocation = archiveFolder + File.separator + callLogKey.replaceAll("_", Matcher.quoteReplacement(File.separator)); 
			try {
//				System.out.println("Saving archive "+archiveLocation+"/"+archiveFilename);
				new CallLogReport(archiveMap.get(callLogKey)).saveReportMultiThreaded(archiveLocation, archiveFilename);
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
			callLogReport.saveReportMultiThreaded(outputFolder, reportName);
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
