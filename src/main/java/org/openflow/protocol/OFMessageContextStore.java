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

package org.openflow.protocol;

public class OFMessageContextStore<V> {
	protected OFMessage msg;
	String namespace;

	public OFMessageContextStore(final OFMessage msg, final String namespace) {
		this.msg = msg;
		this.namespace = namespace;
	}

	@SuppressWarnings("unchecked")
	public V get(final String key) {
		return (V) this.msg.getMessageStore().get(this.namespace + "|" + key);
	}

	public void put(final String key, final V value) {
		this.msg.getMessageStore().put(this.namespace + "|" + key, value);
	}
}
