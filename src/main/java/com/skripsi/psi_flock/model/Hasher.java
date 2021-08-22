package com.skripsi.psi_flock.model;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.primitives.Ints;
import com.skripsi.psi_flock.hash.*;

/**
* kelas untuk menghitung hash value
**/
public class Hasher{
	private static Hasher instance;
	private int seed;//minimal 10^9
	private HashFunction mh3;
	private Hash sh;
	
	private Hasher(int seed){
		this.seed=seed;
		this.mh3= Hashing.murmur3_128(seed);
		this.sh=SpookyHash.getInstance();
	}
	
	public static Hasher getInstance(int seed){
		if(Hasher.instance==null){
			Hasher.instance = new Hasher(seed);
		}
		return Hasher.instance;
	}
	
	public long doSpookyHash(int key){
		return Integer.toUnsignedLong(sh.hash(Integer.toString(key).getBytes(),Integer.toString(key).getBytes().length,this.seed));
	}
	
	public long doMurMurHash(int key){
		HashCode hashCode= mh3.hashInt(key);
		return Integer.toUnsignedLong(Ints.fromByteArray(hashCode.asBytes()));
	}
}