package com.skripsi.psi_flock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tuliskan file output ke file TXT, untuk keperluan uji kualitatif
 * @author Irvan Hardyanto
 */
public class TXTWriter {
	private File outputFile=null;
	private FileWriter fw=null;
	private BufferedWriter writer=null;
	
	public TXTWriter(String path){
		this.outputFile = new File(path);
		try{
			this.fw = new FileWriter(outputFile,false);
			this.writer = new BufferedWriter(fw);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void addLine(String line){
		try{
			this.writer.append(line+"\n");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void addBlankLine(){
		try{
			this.writer.append("\n");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void closeFile(){
		try {
			this.writer.close();
		} catch (IOException ex) {
			Logger.getLogger(TXTWriter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
