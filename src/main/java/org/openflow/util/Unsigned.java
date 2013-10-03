/*******************************************************************************
 * Copyright (c) 2013 Open Networking Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
/**
 *    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
 *    University
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package org.openflow.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/*****
 * A util library class for dealing with the lack of unsigned datatypes in Java
 * 
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */

public class Unsigned {
	/**
	 * Get an unsigned byte from the current position of the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the byte from
	 * @return an unsigned byte contained in a short
	 */
	public static short getUnsignedByte(final ByteBuffer bb) {
		return (short) (bb.get() & (short) 0xff);
	}

	/**
	 * Get an unsigned byte from the specified offset in the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the byte from
	 * @param offset
	 *            the offset to get the byte from
	 * @return an unsigned byte contained in a short
	 */
	public static short getUnsignedByte(final ByteBuffer bb, final int offset) {
		return (short) (bb.get(offset) & (short) 0xff);
	}

	/**
	 * Put an unsigned byte into the specified ByteBuffer at the current
	 * position
	 * 
	 * @param bb
	 *            ByteBuffer to put the byte into
	 * @param v
	 *            the short containing the unsigned byte
	 */
	public static void putUnsignedByte(final ByteBuffer bb, final short v) {
		bb.put((byte) (v & 0xff));
	}

	/**
	 * Put an unsigned byte into the specified ByteBuffer at the specified
	 * offset
	 * 
	 * @param bb
	 *            ByteBuffer to put the byte into
	 * @param v
	 *            the short containing the unsigned byte
	 * @param offset
	 *            the offset to insert the unsigned byte at
	 */
	public static void putUnsignedByte(final ByteBuffer bb, final short v,
			final int offset) {
		bb.put(offset, (byte) (v & 0xff));
	}

	/**
	 * Get an unsigned short from the current position of the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the byte from
	 * @return an unsigned short contained in a int
	 */
	public static int getUnsignedShort(final ByteBuffer bb) {
		return bb.getShort() & 0xffff;
	}

	/**
	 * Get an unsigned short from the specified offset in the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the short from
	 * @param offset
	 *            the offset to get the short from
	 * @return an unsigned short contained in a int
	 */
	public static int getUnsignedShort(final ByteBuffer bb, final int offset) {
		return bb.getShort(offset) & 0xffff;
	}

	/**
	 * Put an unsigned short into the specified ByteBuffer at the current
	 * position
	 * 
	 * @param bb
	 *            ByteBuffer to put the short into
	 * @param v
	 *            the int containing the unsigned short
	 */
	public static void putUnsignedShort(final ByteBuffer bb, final int v) {
		bb.putShort((short) (v & 0xffff));
	}

	/**
	 * Put an unsigned short into the specified ByteBuffer at the specified
	 * offset
	 * 
	 * @param bb
	 *            ByteBuffer to put the short into
	 * @param v
	 *            the int containing the unsigned short
	 * @param offset
	 *            the offset to insert the unsigned short at
	 */
	public static void putUnsignedShort(final ByteBuffer bb, final int v,
			final int offset) {
		bb.putShort(offset, (short) (v & 0xffff));
	}

	/**
	 * Get an unsigned int from the current position of the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the int from
	 * @return an unsigned int contained in a long
	 */
	public static long getUnsignedInt(final ByteBuffer bb) {
		return bb.getInt() & 0xffffffffL;
	}

	/**
	 * Get an unsigned int from the specified offset in the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the int from
	 * @param offset
	 *            the offset to get the int from
	 * @return an unsigned int contained in a long
	 */
	public static long getUnsignedInt(final ByteBuffer bb, final int offset) {
		return bb.getInt(offset) & 0xffffffffL;
	}

	/**
	 * Put an unsigned int into the specified ByteBuffer at the current position
	 * 
	 * @param bb
	 *            ByteBuffer to put the int into
	 * @param v
	 *            the long containing the unsigned int
	 */
	public static void putUnsignedInt(final ByteBuffer bb, final long v) {
		bb.putInt((int) (v & 0xffffffffL));
	}

	/**
	 * Put an unsigned int into the specified ByteBuffer at the specified offset
	 * 
	 * @param bb
	 *            ByteBuffer to put the int into
	 * @param v
	 *            the long containing the unsigned int
	 * @param offset
	 *            the offset to insert the unsigned int at
	 */
	public static void putUnsignedInt(final ByteBuffer bb, final long v,
			final int offset) {
		bb.putInt(offset, (int) (v & 0xffffffffL));
	}

	/**
	 * Get an unsigned long from the current position of the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the long from
	 * @return an unsigned long contained in a BigInteger
	 */
	public static BigInteger getUnsignedLong(final ByteBuffer bb) {
		final byte[] v = new byte[8];
		for (int i = 0; i < 8; ++i) {
			v[i] = bb.get(i);
		}
		return new BigInteger(1, v);
	}

	/**
	 * Get an unsigned long from the specified offset in the ByteBuffer
	 * 
	 * @param bb
	 *            ByteBuffer to get the long from
	 * @param offset
	 *            the offset to get the long from
	 * @return an unsigned long contained in a BigInteger
	 */
	public static BigInteger getUnsignedLong(final ByteBuffer bb,
			final int offset) {
		final byte[] v = new byte[8];
		for (int i = 0; i < 8; ++i) {
			v[i] = bb.get(offset + i);
		}
		return new BigInteger(1, v);
	}

	/**
	 * Put an unsigned long into the specified ByteBuffer at the current
	 * position
	 * 
	 * @param bb
	 *            ByteBuffer to put the long into
	 * @param v
	 *            the BigInteger containing the unsigned long
	 */
	public static void putUnsignedLong(final ByteBuffer bb, final BigInteger v) {
		bb.putLong(v.longValue());
	}

	/**
	 * Put an unsigned long into the specified ByteBuffer at the specified
	 * offset
	 * 
	 * @param bb
	 *            ByteBuffer to put the long into
	 * @param v
	 *            the BigInteger containing the unsigned long
	 * @param offset
	 *            the offset to insert the unsigned long at
	 */
	public static void putUnsignedLong(final ByteBuffer bb, final BigInteger v,
			final int offset) {
		bb.putLong(offset, v.longValue());
	}
}
