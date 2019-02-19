/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.List;
import java.util.ArrayList;

import core.Settings;
import core.DTNHost;
import core.Message;
import core.MessageListener;

/**
 * Reports information about all created messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class ICNReport extends Report implements MessageListener {
	private float excess;
	private float total;

	private Settings s = new Settings("Group");
	private int nrofHosts = s.getInt("nrofHosts");
	private int[][] hostRates = new int[nrofHosts][2];

	private int[][] responseMatch = new int[nrofHosts][2];

	/**
	 * Constructor.
	 */
	public ICNReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		this.excess = 0;
		this.total = 0;
	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}

	public void messageTransferred(Message m, DTNHost f, DTNHost t, boolean firstDelivery) {
		String type = (String) m.getProperty("type");

		// response rate
		if (type.equals("request") && m.getFrom() == f) {
			hostRates[f.getAddress()][0]++;
		}
		if (m.isResponse() && m.getTo() == t) {
			hostRates[t.getAddress()][1]++;
		}
		if (m.isResponse()) {
			if (m.getTo() == t) {
				responseMatch[t.getAddress()][1]++;
			} else {
				responseMatch[t.getAddress()][0]++;
			}
		}

		// efficiency
		if (!isWarmupID(m.getId()) && type.equals("data")) {
			// total transfers
			this.total++;
			// transfers that are not to the destination
			if (!firstDelivery) {
				this.excess++;
			}
		}
	}
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		// response rate
		float total = 0;
		float total_matched = 0;
		float count = 0;
		float count_matched = 0;
		for (int i = 0; i < hostRates.length; i++) {
			float receive = hostRates[i][1];
			float transmit = hostRates[i][0];

			float response = responseMatch[i][0];
			float matched = responseMatch[i][1];

			if (transmit != 0) {
				count++;
				total += (receive/transmit);
			}
			if (response != 0) {
				count_matched++;
				total_matched += (matched/response);
			}
		}
		write("=========== response rate ==========");
		write("average = " + (total*100/count) + "%");
		write("matched = " + (total_matched*100/count_matched) + "%");

		// efficiency
		String percentage = String.format("%.4g%n", (this.total-this.excess)*100/this.total);
		write("=========== efficiency ==========");
		write("Excess: " + this.excess);
		write("Total: " + this.total);
		write("Efficiency (%): " + percentage);
		
		super.done();
	}
}
