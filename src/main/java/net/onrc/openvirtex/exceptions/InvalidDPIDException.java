/*******************************************************************************
 * Copyright (c) 2013 Open Networking Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package net.onrc.openvirtex.exceptions;

/**
 * The physical dpid specified by the admin is not a valid dpid.
 */
public class InvalidDPIDException extends IllegalArgumentException {

	private static final long serialVersionUID = 6957434977838246116L;

	public InvalidDPIDException() {
		super();
	}

	public InvalidDPIDException(final String msg) {
		super(msg);
	}

	public InvalidDPIDException(final Throwable msg) {
		super(msg);
	}
}
