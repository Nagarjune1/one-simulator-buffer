/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.List;
import java.util.ArrayList;

import core.Settings;
import core.Message;
import core.DTNHost;
import core.Connection;

import util.Tuple;

/**
 * request response router to handle requests and return message packet
 */
public class ReqResRouter extends ActiveRouter {
	/**
	 * Constructor. Creates a new request response router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ReqResRouter(Settings s) {
		super(s);
		//TODO: read&use request response router specific settings (if any)
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ReqResRouter(ReqResRouter r) {
		super(r);
		//TODO: copy request response settings here (if any)
	}

	// from perspective of receiving host
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message m = super.messageTransferred(id, from);
		// only check buffer messages if received message is an interest packet
		if (m.getProperty("type") != null && m.getProperty("type").equals("request")) {
			String idToFind = (String) m.getProperty("target");
			if (hasMessage(idToFind)) {
				Message match = getMessage(idToFind);
				match.updateProperty("type", "response");
				// keep track of the packet that requested for this information
				match.setRequest(m);
				// set the destination packet of this information to the source of request packet
				match.setTo(m.getFrom());
				// remove interest packet from buffer because it has served its purpose
				removeFromMessages(id);
			}
		}
		return m;
	}
	
	// from perspective of transferring host
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // transferring, don't try other connections yet
		}
		/**
		 * 1) transfer only response and request packets
		 * 2) prioritise response packets to be sent through the same
		 * connection where it received the interest packets
		 */
		ArrayList<Message> messagesToTransfer = new ArrayList<Message>();
		for (Message m : this.getMessageCollection()) {
			String type = (String) m.getProperty("type");
			if (m.isResponse()) {
				// add to the start of buffer
				messagesToTransfer.add(0,m);
			}
			if (type.equals("request")) {
				// add to the end of buffer
				messagesToTransfer.add(m);
			}
		}
		this.tryMessagesToConnections(messagesToTransfer, this.getConnections());
	}
	
	@Override
	public ReqResRouter replicate() {
		return new ReqResRouter(this);
	}

}