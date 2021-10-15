package com.skripsi.psi_flock.model;

import java.util.HashSet;
import java.util.Objects;

/**
 *
 * @author Irvan Hardyanto
 */
public class InvertedIndexValue {
	private int patternID;
	private HashSet<Integer> entityID;

	public InvertedIndexValue(int patternID, HashSet<Integer> entityID) {
		this.patternID = patternID;
		this.entityID = new HashSet<>(entityID);
	}

	public int getPatternID() {
		return patternID;
	}

	public void setPatternID(int patternID) {
		this.patternID = patternID;
	}

	public HashSet<Integer> getEntityID() {
		return entityID;
	}

	public void setEntityID(HashSet<Integer> entityID) {
		this.entityID = entityID;
	}
	
	public HashSet<Integer> countIntersection(HashSet<Integer> other){
		HashSet<Integer> temp = new HashSet<>(other);
		temp.retainAll(this.entityID);// resiko tinggi ngaco reference nya
		return temp;
	}
	
	public String toString(){
		String s="";
		s+="ID FP: "+this.patternID+"\n";
		s+="entitas di dalamnya: "+this.entityID.toString();
		s+= "\n";
		return s;
	}
	
	@Override
    public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof InvertedIndexValue){
			InvertedIndexValue other = (InvertedIndexValue) o;
			
			return this.patternID == other.patternID && this.entityID.equals(other.entityID);
		}
		return false;
	}

	@Override
	public int hashCode(){
		return Objects.hash(this.patternID)+Objects.hash(this.entityID);
	}
}

