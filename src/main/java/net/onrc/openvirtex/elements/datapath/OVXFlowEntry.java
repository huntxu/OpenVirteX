/*******************************************************************************
 * Copyright (c) 2013 Open Networking Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package net.onrc.openvirtex.elements.datapath;

import java.util.List;

import net.onrc.openvirtex.messages.OVXFlowMod;
import net.onrc.openvirtex.util.MACAddress;

import org.openflow.protocol.OFMatch;
import org.openflow.protocol.action.OFAction;

/**
 * Class representing a virtual flow entry. Loosely based on FlowEntry
 */
public class OVXFlowEntry implements Comparable<OVXFlowEntry>{
    
    /* relation of this FlowEntry to another FlowEntry during comparison */
    public static int EQUAL = 0;	//exactly same
    public static int SUPERSET = 1;	//more general
    public static int SUBSET = 2;	//more specific
    public static int INTERSECT = 3;	//mix of wildcards and matching fields
    public static int DISJOINT = 4;	//non-matching non-wildcarded fields 
    
    /** of physical switch this entry maps back to */
    protected long dpid;
    
    /* Flow table match and actions */
    protected OFMatch ruleMatch;
    protected List<OFAction> actionsList;
    
    /* useful FlowMod fields */
    protected short priority;
    protected short outPort;
    protected short idleTimeout;
    protected short hardTimeout;
    protected long cookie;
    
    /* Flow table statistics */
    protected int durationSeconds;
    protected int durationNanoseconds;
    protected long packetCount;
    protected long byteCount;
    
    public OVXFlowEntry(OVXFlowMod fm, long dpid) {
	/* fields set from FlowMod */
	this.dpid = dpid;
	this.priority = fm.getPriority();
	this.outPort = fm.getOutPort();
	this.ruleMatch = fm.getMatch();
	this.actionsList = fm.getActions();
	this.idleTimeout = fm.getIdleTimeout();
	this.hardTimeout = fm.getHardTimeout();
	this.cookie = fm.getCookie();
	
	/* initialise stats */
	this.durationSeconds = 0;
	this.durationNanoseconds = 0;
	this.packetCount = 0;
	this.byteCount = 0;
    }
   
    /**
     * Compares this entry against another, and tries to determine if
     * it is a superset, subset, or equal to it. Required for non-strict 
     * matching and overlap checking
     * <p>
     * For each field, we first check wildcard equality.
     * If both are equal, they are either 1 or 0. 
     * If 0, we further check for field equality. If the 
     * fields are not equal, the flow entries are considered
     * disjoint and we exit comparison. 
     * <p>
     * If both wildcards are not equal, we check if one 
     * subsumes the other. 
     * <p>
     * The result is tracked for each field in three ints -
     * equality, superset, and subset. 
     * At the end, either 1) one of the ints are 0x3fffff,
     * or 2) none are.
     * 
     * @param omatch The other FlowEntry to compare this one against. 
     * @param strict whether FlowMod from which the match came was strict or not. 
     * @return Union enum representing the relationship 
     */
    public int compare(OFMatch omatch, boolean strict) {
	//to allow pass by reference...in order: equal, superset, subset
	int [] intersect = new int[] {0, 0, 0};
	
	OFMatch tmatch = this.ruleMatch;
	int twcard = tmatch.getWildcards();
	int owcard = omatch.getWildcards();

	/* inport */
	if ((twcard & OFMatch.OFPFW_IN_PORT) == (owcard & OFMatch.OFPFW_IN_PORT)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_IN_PORT, intersect, 
		    tmatch.getInputPort(), omatch.getInputPort())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_IN_PORT, intersect);
	}
	
	/* L2 */
	if ((twcard & OFMatch.OFPFW_DL_DST) == (owcard & OFMatch.OFPFW_DL_DST)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_DL_DST, intersect, 
		    tmatch.getDataLayerDestination(), omatch.getDataLayerDestination())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_DL_DST, intersect);
	}
	if ((twcard & OFMatch.OFPFW_DL_SRC) == (owcard & OFMatch.OFPFW_DL_SRC)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_DL_SRC, intersect, 
		    tmatch.getDataLayerSource(), omatch.getDataLayerSource())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_DL_SRC, intersect);
	}
	if ((twcard & OFMatch.OFPFW_DL_TYPE) == (owcard & OFMatch.OFPFW_DL_TYPE)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_DL_TYPE, intersect, 
		    tmatch.getDataLayerType(), omatch.getDataLayerType())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_DL_TYPE, intersect);
	}
	if ((twcard & OFMatch.OFPFW_DL_VLAN) == (owcard & OFMatch.OFPFW_DL_VLAN)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_DL_VLAN, intersect, 
		    tmatch.getDataLayerVirtualLan(), omatch.getDataLayerVirtualLan())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_DL_VLAN, intersect);
	}
	if ((twcard & OFMatch.OFPFW_DL_VLAN_PCP) == (owcard & OFMatch.OFPFW_DL_VLAN_PCP)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_DL_VLAN_PCP, intersect, 
		    tmatch.getDataLayerVirtualLanPriorityCodePoint(), 
		    omatch.getDataLayerVirtualLanPriorityCodePoint())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_DL_VLAN_PCP, intersect);
	}
	
	/* L3 */
	if ((twcard & OFMatch.OFPFW_NW_PROTO) == (owcard & OFMatch.OFPFW_NW_PROTO)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_NW_PROTO, intersect, 
		    tmatch.getNetworkProtocol(), omatch.getNetworkProtocol())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_NW_PROTO, intersect);
	}
	if ((twcard & OFMatch.OFPFW_NW_TOS) == (owcard & OFMatch.OFPFW_NW_TOS)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_NW_TOS, intersect, 
		    tmatch.getNetworkTypeOfService(), omatch.getNetworkTypeOfService())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_NW_TOS, intersect);
	}
	if ((twcard & OFMatch.OFPFW_NW_DST_ALL) == (owcard & OFMatch.OFPFW_NW_DST_ALL)) {
	    if (findDisjoint(twcard, (OFMatch.OFPFW_NW_DST_ALL | OFMatch.OFPFW_NW_DST_MASK), 
		    intersect, tmatch.getNetworkDestination(), omatch.getNetworkDestination())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_NW_DST_ALL | OFMatch.OFPFW_NW_DST_MASK, intersect);
	}
	if ((twcard & OFMatch.OFPFW_NW_SRC_ALL) == (owcard & OFMatch.OFPFW_NW_SRC_ALL)) {
	    if (findDisjoint(twcard, (OFMatch.OFPFW_NW_SRC_ALL | OFMatch.OFPFW_NW_SRC_MASK),
		    intersect, tmatch.getNetworkSource(), omatch.getNetworkSource())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_NW_SRC_ALL | OFMatch.OFPFW_NW_SRC_MASK, intersect);
	}
	if ((twcard & OFMatch.OFPFW_TP_SRC) == (owcard & OFMatch.OFPFW_TP_SRC)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_TP_SRC, intersect, 
		    tmatch.getTransportSource(), omatch.getTransportSource())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_TP_SRC, intersect);
	}
	if ((twcard & OFMatch.OFPFW_TP_DST) == (owcard & OFMatch.OFPFW_TP_DST)) {
	    if (findDisjoint(twcard, OFMatch.OFPFW_TP_DST, intersect, 
		    tmatch.getTransportDestination(), omatch.getTransportDestination())) {
		return DISJOINT;
	    }
	} else { /*check if super or subset*/
	    findRelation(twcard, owcard, OFMatch.OFPFW_TP_DST, intersect);
	}
	
	int equal = intersect[EQUAL];
	int superset = intersect[SUPERSET];
	int subset = intersect[SUBSET];	    	   
	
	if (!strict) {
	    equal |= subset;
	}
	if (equal == OFMatch.OFPFW_ALL) {
	    return  EQUAL;
	}
	if (superset == OFMatch.OFPFW_ALL) {
	    return  SUPERSET;
	}
	if (subset == OFMatch.OFPFW_ALL) {
	    return  SUBSET;
	}
	return  INTERSECT;
    }
    
    /**
     * determine if a field is not equal-valued, for non-array fields 
     * first checks if the OFMatch wildcard is fully wildcarded for the 
     * field. If not, it checks the equality of the field value. 
     *   
     * @param wcard
     * @param field
     * @param equal
     * @param val1
     * @param val2
     * @return true if disjoint FlowEntries
     */
    private boolean findDisjoint(int wcard, int field, int [] intersect,
	    Number val1, Number val2) {
	if (((wcard & field) == field) || (val1.equals(val2))) {
	    updateIntersect(intersect, field);
	    return false;
	}
	return true;
    }
    
    /**
     * determine if fields are disjoint, for byte arrays.
     * @param wcard
     * @param field
     * @param equal
     * @param val1
     * @param val2
     * @return
     */
    private boolean findDisjoint(int wcard, int field, int [] intersect, 
	    byte [] val1, byte [] val2) {
	if ((wcard & field) == field) {
	    updateIntersect(intersect, field);	    
	    return false;
	}
	for (int i = 0; i < MACAddress.MAC_ADDRESS_LENGTH; i++) {	
	    if (val1[i] != val2[i]) {
		return true;
	    }
	}
	updateIntersect(intersect, field);
	return false;
    }
    
    private void updateIntersect(int [] intersect, int field) {
	intersect[EQUAL] |= field;
	intersect[SUPERSET] |= field;
	intersect[SUBSET] |= field;	    	    
    }
    
    /**
     * Determines if one or the other field is wildcarded. If this flow entry's
     * field is wildcarded i.e. its wildcard value for the field is bigger, 
     * we are superset; Else, we are subset. 
     * 
     * @param wcard1 our wildcard field
     * @param wcard2 other wildcard field
     * @param field OFMatch wildcard value 
     * @param intersect intersection sets 
     */
    private void findRelation(int wcard1, int wcard2, int field, int [] intersect) {
	if ((wcard1 & field) > (wcard2 & field)) {
	    intersect[SUPERSET] |= field;
	} else {
	    intersect[SUBSET] |= field;	    	    
	}
    }
    
    /* non-stats fields */
    public OFMatch getMatch() {
	return this.ruleMatch;
    }
    
    public OVXFlowEntry setMatch(OFMatch match) {
	this.ruleMatch = match;
	return this;
    }
    
    public long getDPID() {
	return this.dpid;
    }
    
    public OVXFlowEntry setDPID(long dpid) {
	this.dpid = dpid;
	return this;
    }
    
    public short getOutport() {
	return this.outPort;
    }
    
    public OVXFlowEntry setOutport(short oport) {
	this.outPort = oport;
	return this;
    }
    
    public short getPriority() {
	return this.priority;
    }
    
    public OVXFlowEntry setPriority(short prio) {
	this.priority = prio;
	return this;
    }
    
    public long getCookie() {
	return this.cookie;
    }
    
    public OVXFlowEntry setCookie(long cookie) {
	this.cookie = cookie;
	return this;
    }
    
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((actionsList == null) ? 0 : actionsList.hashCode());
	result = prime * result + (int) (dpid ^ (dpid >>> 32));
	result = prime * result + priority;
	result = prime * result
		+ ((ruleMatch == null) ? 0 : ruleMatch.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (this.getClass() != obj.getClass()) {
	    return false;
	}
	final OVXFlowEntry other = (OVXFlowEntry) obj;
	if (this.actionsList == null) {
	    if (other.actionsList != null) {
		return false;
	    }
	} else if (!this.actionsList.equals(other.actionsList)) {
	    return false;
	}
	if (this.dpid != other.dpid) {
	    return false;
	}
	if (this.priority != other.priority) {
	    return false;
	}
	if (this.ruleMatch == null) {
	    if (other.ruleMatch != null) {
		return false;
	    }
	} else if (!this.ruleMatch.equals(other.ruleMatch)) {
	    return false;
	}
	return true;
    }

    @Override
    public int compareTo(final OVXFlowEntry other) {
	// sort on priority, tie break on IDs
	if (this.priority != other.priority) {
	    return other.priority - this.priority;
	}
	return this.hashCode() - other.hashCode();
    }
	
    @Override
    public String toString() {
	return "OVXFlowEntry [dpid=" + this.dpid + ", priority=" + this.priority + 
		", outPort=" + this.outPort +", duration=" + this.durationSeconds + 
		", Timeout[hard/idle]" + this.hardTimeout + "/" + this.idleTimeout +
		"\n Match=" + this.ruleMatch + "\n Actions="+ this.actionsList + "]";
    }
}
