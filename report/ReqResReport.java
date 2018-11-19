/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.HashMap;
import java.util.Vector;
import java.util.ArrayList;

import core.ConnectionListener;
import core.DTNHost;
import core.Settings;
import core.Message;

public class ReqResReport extends Report implements ConnectionListener {
	public static final int DEFAULT_REPORT_INTERVAL = 10;

	private double lastRecord = Double.MIN_VALUE;
	private int interval;

	/**
	 * Constructor.
	 */
	public ReqResReport() {
		init();
	}

	@Override
	protected void init() {
		super.init();
	}

	public void hostsConnected(DTNHost host1, DTNHost host2) {
		ArrayList<Message> host1M = new ArrayList<Message>(host1.getMessageCollection());
		ArrayList<Message> host2M = new ArrayList<Message>(host2.getMessageCollection());

		ArrayList<String> requests = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();

		for (Message m1 : host1M) {
			if (m1.getProperty("type") != null) {
				requests.add(m1.getId());
			}
		}
		for (Message m2 : host2M) {
			if (m2.getProperty("type") == null) {
				contents.add(m2.getId());
			}
		}

		// interest packet matches content request
		boolean found = false;
		String itemsFound = "Found: ";
		for (String request : requests) {
			for (String content : contents) {
				if (request.substring(1).equals(content.substring(1))) {
					if (!found) found = true;
					itemsFound += content + ",";
				}
			}
		}
		if (found) {
			write("host:" + host1 + "-" + requests);
			write("other:" + host2 + "-" + contents);
			write(itemsFound + "\n");
		}
	}

	public void hostsDisconnected(DTNHost host1, DTNHost host2) {
		// write(host1 + "," + host2 + ": down");
	}

	// public void updated(List<DTNHost> hosts) {
	// 	if (SimClock.getTime() - lastRecord >= interval) {
	// 		lastRecord = SimClock.getTime();
	// 		printLine(hosts);
	// 	}
	// }
}