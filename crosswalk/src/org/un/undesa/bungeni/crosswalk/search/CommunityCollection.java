package org.un.undesa.bungeni.crosswalk.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.core.LogManager;

import com.thoughtworks.xstream.XStream;

public class CommunityCollection {
	private String communityName;
	private String communityHandle;
	private List<CollectionCollection> collection;
	private List<CommunityCollection> community;
	public String getCommunityName() {
		return communityName;
	}
	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}
	public String getCommunityHandle() {
		return communityHandle;
	}
	public void setCommunityHandle(String communityHandle) {
		this.communityHandle = communityHandle;
	}
	public CommunityCollection() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<CollectionCollection> getCollection() {
		return collection;
	}
	public void setCollection(List<CollectionCollection> collection) {
		this.collection = collection;
	}
	public List<CommunityCollection> getCommunity() {
		return community;
	}
	public void setCommunity(List<CommunityCollection> community) {
		this.community = community;
	}
	
	
	
	
	
	
}
