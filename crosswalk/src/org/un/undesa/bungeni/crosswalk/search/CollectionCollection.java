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


public class CollectionCollection {
	private String collectionName;
	private String collectionHandle;
	private List<CollectionCollection> subCollection;
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getCollectionHandle() {
		return collectionHandle;
	}
	public void setCollectionHandle(String collectionHandle) {
		this.collectionHandle = collectionHandle;
	}
	public CollectionCollection() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<CollectionCollection> getSubCollection() {
		return subCollection;
	}
	public void setSubCollection(List<CollectionCollection> subCollection) {
		this.subCollection = subCollection;
	}
	
	
}
