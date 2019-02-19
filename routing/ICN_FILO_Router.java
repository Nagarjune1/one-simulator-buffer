/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import core.Settings;
import core.Message;
import core.DTNHost;
import core.Connection;

import util.Tuple;

/**
 * request response router to handle requests and return message packet
 */
public class ICN_FILO_Router extends ActiveRouter {
	/**
	 * Constructor. Creates a new request response router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ICN_FILO_Router(Settings s) {
		super(s);
		//TODO: read&use request response router specific settings (if any)
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ICN_FILO_Router(ICN_FILO_Router r) {
		super(r);
		//TODO: copy request response settings here (if any)
	}

	// from perspective of receiving host
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message m = super.messageTransferred(id, from);
		// only check buffer messages if received message is an interest packet
		if (m.getProperty("type").equals("request")) {
			String idToFind = (String) m.getProperty("target");
			if (hasMessage(idToFind)) {
				Message match = getMessage(idToFind);
				// set request packet
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
		ArrayList<Message> transferBuffer = new ArrayList<Message>();
		for (Message m : this.getMessageCollection()) {
			// add message to buffer if buffer is empty
			if (transferBuffer.size() == 0) {
				transferBuffer.add(m);
				continue;
			}

			String type = (String) m.getProperty("type");

			if (m.isResponse()) {
				transferBuffer.add(0,m);
			} else if (type.equals("request")) {
				// request packets with higher priority will be placed at the front
				int messagePriority = (int) m.getProperty("priority");
				for (int i = transferBuffer.size()-1; i >= 0; i--) {
					Integer listPriority = (Integer) transferBuffer.get(i).getProperty("priority");
					if (listPriority == null) {
						transferBuffer.add(i+1,m);
						break;
					} else if (listPriority >= messagePriority) {
						transferBuffer.add(i+1,m);
						break;
					}
				}
			}
		}

		this.tryMessagesToConnections(transferBuffer, this.getConnections());
	}

	@Override
	protected Message getNextMessageToRemove(boolean excludeMsgBeingSent) {
		Collection<Message> messages = this.getMessageCollection();
		Message newest = null;
		for (Message m : messages) {
			if (excludeMsgBeingSent && isSending(m.getId())) {
				continue; // skip the message(s) that router is sending
			}
			if (newest == null) {
				newest = m;
			} else if (newest.getReceiveTime() < m.getReceiveTime()) {
				newest = m;
			}
		}
		
		return newest;
	}
	
	@Override
	public ICN_FILO_Router replicate() {
		return new ICN_FILO_Router(this);
	}

}