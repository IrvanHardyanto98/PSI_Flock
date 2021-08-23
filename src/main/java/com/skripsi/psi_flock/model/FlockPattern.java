package com.skripsi.psi_flock.model;

import java.util.ArrayList;
import java.util.HashSet;
/**
* Representasi Flock Pattern yang terdiri dari rangkaian flock-flock
* Flock Pattern adalah kumpulan flock-flock yang berisi minimal \mu buah lintasan
* yang terletak saling berdekatan selama periode waktu tertentus
**/

public class FlockPattern{
	private final int flockPatternID;
	private final int startTime;
	private int endTime;
	private ArrayList<Flock> flocks;
	private HashSet<Integer> entityIDList;
	public FlockPattern(int flockPatternID,int startTime){
		this.startTime = startTime;
		this.endTime = 0;
		this.flockPatternID=flockPatternID;
		this.flocks=new ArrayList<>();
		this.entityIDList = new HashSet<>();
	}
	
	public FlockPattern(int flockPatternID,int startTime,ArrayList<Flock> existingFlocks,HashSet<Integer> existingIDs){
		this.startTime = startTime;
		this.endTime = 0;
		this.flockPatternID=flockPatternID;
		this.flocks=new ArrayList<>(existingFlocks);
		this.entityIDList = new HashSet<>(existingIDs);
	}
	
	public HashSet<Integer> getAllID(){
		return this.entityIDList;
	}
	
	/**
	* Tambahkan flock di posisi paling belakang, sesuai urutan waktu nya
	**/
	public boolean addFlock(Flock flock){
		if(!this.flocks.isEmpty()&&flock.getTimestamp()==this.flocks.get(this.flocks.size()-1).getTimestamp()){
			return false;
		}
		flock.setPatternID(this.flockPatternID);
		this.flocks.add(flock);
		this.endTime=flock.getTimestamp();
		//masih perlu diubah
		//this.entityIDList.addAll(flock.getEntityIDSet());
		return true;
	}
	
	public int getFlocksNum(){
		return this.flocks.size();
	}

	public int getStartTime(){
		return this.startTime;
	}

	public void setEndTime(int endTime){
		this.endTime=endTime;
	}

	public int getID(){
		return this.flockPatternID;
	}
	
	//
	public Flock getLastFlock(){
		return this.flocks.get(this.flocks.size()-1);
	}
	
	public ArrayList<Flock> getAllFlock(){
		return this.flocks;
	}
	
	@Override
	public String toString(){
		String s="ID Flock Pattern: "+this.flockPatternID+"\n";
		s+="Waktu mulai: "+this.startTime+"\n";
		s+="Waktu akhir: "+this.endTime+"\n";
		s+="Jumlah flock dalam Flock Pattern: "+this.flocks.size()+"\n";
		return s;
	}
}