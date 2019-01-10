/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.List;

import core.DTNHost;
import core.Message;
import core.MessageListener;

public class ReqResReport extends Report implements MessageListener {
	private float excess;
	private float total;

	/**
	 * Constructor.
	 */
	public ReqResReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		this.excess = 0;
		this.total = 0;
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
		String type = (String) m.getProperty("type");
		if (!isWarmupID(m.getId()) && type == null) {
			// total transfers
			this.total++;
			// transfers that are not to the destination
			if (!firstDelivery) {
				this.excess++;
			}
		}
	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}

	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		write("Excess: " + this.excess);
		write("Total: " + this.total);

		String percentage = String.format("%.4g%n", (this.total-this.excess)*100/this.total);
		write("Efficiency (%): " + percentage);
		super.done();
	}
}