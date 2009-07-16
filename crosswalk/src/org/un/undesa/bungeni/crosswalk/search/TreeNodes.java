package org.un.undesa.bungeni.crosswalk.search;
import java.util.*;

public class TreeNodes {
	private String text = " ";
	private String uri = " ";
	private String cls = "file";
	private String iconCls = "task";
	private boolean leaf;
	private List<TreeNodes> children;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public List<TreeNodes> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNodes> children) {
		if(this.children !=null) {
			List<TreeNodes> finals = new ArrayList<TreeNodes>();
			for(TreeNodes tn : this.children) {
				finals.add(tn);
			}
			
			for(TreeNodes tn : children) {
				finals.add(tn);
			}
			this.children = finals;
		}else {
			this.children = children;
		}
		
	}
	public TreeNodes() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TreeNodes(String name, String uri, String cls, String iconCls, boolean leaf, List<TreeNodes> children) {
		super();
		this.text = name;
		this.uri = uri;
		this.cls = cls;
		this.iconCls = iconCls;
		this.leaf = leaf;
		this.children = children;
	}
	
}
