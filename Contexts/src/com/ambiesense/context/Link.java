/*=================================================================
AmbieSense Context Software GPL Source Code.

Copyright (C) 2005 and onwards by AmbieSense Limited, 
 and/or its subsidiaries and co-founders. AmbieSense Limited
 is an R&D-focused company incorporated in Scotland.

This file is part of the AmbieSense Context Software GPL Source Code.

The AmbieSense Context Software GPL Source Code is free software: 
you can redistribute it and/or modify it under the terms of the 
GNU General Public License Version 3 as published by
the Free Software Foundation.

The AmbieSense Context Software Source Code is distributed in 
the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or 
FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with The AmbieSense Context Software Source Code. 
If not, see <http://www.gnu.org/licenses/>.

In addition, the The AmbieSense Context Software Source Code is also 
subject to certain additional terms. You should have received a copy 
of these additional terms immediately following the terms and 
conditions of the GNU General Public License which 
accompanied the The AmbieSense Context Software Source Code. 
If not, please request a copy in writing from id Software at the address below.

If you have questions concerning this license or the applicable 
additional terms, you may contact in writing: 
AmbieSense Limited, 7 Queens Gardens, Aberdeen, 
AB25 4YD, Scotland; or by email: 
kontactATambiesense.com (replace "AT" with "@").
============================================================ */


package com.ambiesense.context;

import java.io.Serializable;
import java.net.URL;

import com.ambiesense.persistent.AbstractPersistentObject;
import com.ambiesense.persistent.UUIDGenerator;


public class Link extends AbstractPersistentObject implements Serializable{

	//HTML5 link attributes: coords href hreflang media name rel target type timestamp
	private static final long serialVersionUID = 1L;
	private String linkUUID    = UUIDGenerator.createId();
	private String contextUUID = UUIDGenerator.createId();
	private String href        = "";
	private String hreflang    = "";
	private String media       = "";
	private String rel         = "";
	private String target      = "";
	private String type        = "";
	private String linkText    = "";
	private long timestamp   = System.currentTimeMillis();

	public Link(){}
	
	public String getContextUUID() {
		return contextUUID;
	}

	public void setContextUUID(String contextUUID) {
		this.contextUUID = contextUUID;
	}

	public String getLinkUUID() {
		return linkUUID;
	}

	public void setLinkUUID(String linkUUID) {
		this.linkUUID = linkUUID;
		this.setId(this.linkUUID);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		if (href != null) this.href = href;
	}
	public void setHref(URL href){
		if (href != null) this.href = href.toString();
	}

	public String getHreflang() {
		return hreflang;
	}

	public void setHreflang(String hreflang) {
		if (hreflang != null) this.hreflang = hreflang;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		if (media != null) this.media = media;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		if (rel != null) this.rel = rel;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		if (target != null) this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null) this.type = type;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		if (linkText != null) this.linkText = linkText;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContextId() {
		return contextUUID;
	}

	public void setContextId(String id) {
		if (id != null) this.contextUUID = id;			
	}

	public String toString(){
		String a = "    <link ";
		if (!"".equalsIgnoreCase(this.href))     a += " href="     + '"' + this.href + '"';
		if (!"".equalsIgnoreCase(this.hreflang)) a += " hreflang=" + '"' + this.hreflang + '"';
		if (!"".equalsIgnoreCase(this.media))    a += " media="    + '"' + this.media + '"';
		if (!"".equalsIgnoreCase(this.rel))      a += " rel="      + '"' + this.rel + '"';
		if (!"".equalsIgnoreCase(this.target))   a += " target="   + '"' + this.target + '"';
		if (!"".equalsIgnoreCase(this.type))     a += " type="     + '"' + this.type + '"';
		if (!"".equalsIgnoreCase(this.linkText)) a += " linkText=" + '"' + this.linkText + '"';
		a += " />";
		return a;
	}
	
	public String toHtml(){
		String a = "<a ";
		if (!"".equalsIgnoreCase(this.href))     a += " href="     + '"' + this.href + '"';
		if (!"".equalsIgnoreCase(this.hreflang)) a += " hreflang=" + '"' + this.hreflang + '"';
		if (!"".equalsIgnoreCase(this.media))    a += " media="    + '"' + this.media + '"';
		if (!"".equalsIgnoreCase(this.rel))      a += " rel="      + '"' + this.rel + '"';
		if (!"".equalsIgnoreCase(this.target))   a += " target="   + '"' + this.target + '"';
		if (!"".equalsIgnoreCase(this.type))     a += " type="     + '"' + this.type + '"';
		a += ">";
		if (!"".equalsIgnoreCase(this.linkText)) a   += this.linkText;
		a += "</a>";
		return a;
	}
}
