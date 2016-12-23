package be.pxl.calllog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class CallLogCollectorThread extends Thread {

	List<CallLog> callLogs;
	File csvFile;
	
	public CallLogCollectorThread(List<CallLog> callLogs, File csvFile) {
		this.callLogs = callLogs;
		this.csvFile = csvFile;
	}
	
	@Override
	public void run() {
		try (BufferedReader buffer = new BufferedReader(new FileReader(csvFile))) {
			String callLogLine = null;
			while((callLogLine = buffer.readLine()) != null) {
				System.out.printf("[%s] Reading calllog line ...\n",Thread.currentThread().getName());
				callLogs.add(CallLogFactory.createCallLog(callLogLine));
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
			}
		} catch (IOException e) {
			System.err.println("Exception in parsing csv file "+csvFile.getName());
			e.printStackTrace();
		}
	}
	
}
