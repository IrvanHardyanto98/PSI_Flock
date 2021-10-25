package com.skripsi.psi_flock.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/**
* Representasi Flock Pattern yang terdiri dari rangkaian flock-flock
* Flock Pattern adalah kumpulan flock-flock yang berisi minimal \mu buah lintasan
* yang terletak saling berdekatan selama periode waktu tertentus
**/

public class FlockPattern{
	private int flockPatternID;
	private final int startTime;
	private int endTime;
	//private Flock lastFlock;
	//private ArrayList<Flock> flocks;
	private HashSet<Integer> entityID;
	public FlockPattern(int flockPatternID,int startTime){
		this.startTime = startTime;
		this.endTime = 0;
		this.flockPatternID=flockPatternID;
		//this.flocks=new ArrayList<>();
		this.entityID = new HashSet<>();
	}
	
	public FlockPattern(FlockPattern other){
		this.startTime = other.startTime;
		this.endTime = other.endTime;
		this.flockPatternID=other.flockPatternID;
		//this.flocks=new ArrayList<>(existingFlocks);
		this.entityID = new HashSet<>(other.entityID);
	}
	
	/**
	* Tambahkan flock di posisi paling belakang, sesuai urutan waktu nya
	**/
	public boolean addFlock(Flock flock){
		//if(!this.entityID.isEmpty()&&flock.getTimestamp()==this.flocks.get(this.flocks.size()-1).getTimestamp()){
		if(!this.entityID.isEmpty()&&flock.getTimestamp()==this.endTime){
			return false;
		}
		flock.setPatternID(this.flockPatternID);
		if(this.entityID.isEmpty()){
			this.entityID.addAll(flock.getEntityIDSet());
		}else{
			this.entityID.retainAll(flock.getEntityIDSet());
		}
		//this.lastFlock= new Flock(flock);
		this.endTime=flock.getTimestamp();
		return true;
	}
	
	public int getEntityNum(){
		return this.entityID.size();
	}

	public int getStartTime(){
		return this.startTime;
	}

	public void setEndTime(int endTime){
		this.endTime=endTime;
	}
	
	public int getEndTime(){
		return this.endTime;
	}
	
	public void setID(int id){
		this.flockPatternID = id;
	}

	public int getID(){
		return this.flockPatternID;
	}
	
	public HashSet<Integer> getEntityIDSet(){
		return this.entityID;
	}
	
	@Override
	public String toString(){
		String s="ID Flock Pattern: "+this.flockPatternID+"\n";
		s+="Waktu mulai: "+this.startTime+"\n";
		s+="Waktu akhir: "+this.endTime+"\n";
		s+="Entitas di dalam flock pattern adalah:";
		boolean f = true;
		Iterator<Integer> iter = this.entityID.iterator();
		while(iter.hasNext()){
			if(f){
				f=false;
			}else{
				s+=",";
			}
			s+=iter.next();
		}
		s+="\n";
		//s+="Jumlah flock dalam Flock Pattern: "+this.flocks.size()+"\n";
		return s;
	}
	
	public String getSimpleString(){
		String s="";
		boolean f = true;
		Iterator<Integer> iter = this.entityID.iterator();
		while(iter.hasNext()){
			if(f){
				f=false;
			}else{
				s+=" ";
			}
			s+=iter.next();
		}
		s+="\n";
		s+=this.startTime+" "+this.endTime;
		return s;
	}
}