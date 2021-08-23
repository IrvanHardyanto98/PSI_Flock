
package com.skripsi.psi_flock.pdf;
import java.util.ArrayList;
/**
 *
 * @author Irvan Hardyanto
 */
public class StringTreeNode {
	private String value;
	private ArrayList<StringTreeNode> children;

	public StringTreeNode(String value) {
		this.value = value;
		this.children = new ArrayList<>();
	}

	public String getValue() {
		return value;
	}
	
	public int getChildrenNum(){
		return this.children.size();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<StringTreeNode> getAllChildren() {
		return children;
	}
	
	public StringTreeNode getChildren(int idx) {
		return children.get(idx);
	}
	
	public void setChildren(String text) {
		this.children.add(new StringTreeNode(text));
	}
}
