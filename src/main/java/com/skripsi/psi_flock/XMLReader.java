package com.skripsi.psi_flock;

import com.skripsi.psi_flock.model.Trajectory;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Kelas yang berfungsi untuk membaca file .xml dengan memanfaatkan java DOM parser
 * referensi: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 * @author Irvan Hardyanto
 */
public class XMLReader {
	private Document doc;
	public XMLReader(String path){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			 dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			 DocumentBuilder db=dbf.newDocumentBuilder();
			 this.doc=db.parse(new File(path));
		}catch(IOException e){
			e.printStackTrace();
		}catch(SAXException e){
			e.printStackTrace();
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		}
	}
	
	
	//shape nya: mark/disk(sx)
	//hapus dulu bagian dtd di file ipenya
	//belum tau id_entitas dan waktu.
	//Kalau parameter pake Location -> coupling dengan kelas location
	public Point2D[] getPoints(){
		this.doc.getDocumentElement().normalize();
		
		NodeList list = this.doc.getElementsByTagName("use");
		Point2D[] result = new Point2D[list.getLength()];
		for (int temp = 0; temp < list.getLength(); temp++) {
			Node node = list.item(temp);
			Element element = (Element) node;
			String[] attr = element.getAttribute("pos").split("\\s");
			System.out.println(attr[0]+","+attr[1]);
			result[temp]=new Point(Integer.parseInt(attr[0]),Integer.parseInt(attr[1]));
		}
		return result;
	}
	public Trajectory[] readTrajectory(){
		this.doc.getDocumentElement().normalize();
		NodeList list = this.doc.getElementsByTagName("path");//ga semua tag Path itu valid
		Trajectory[] result = new Trajectory[list.getLength()];
		int entityID = 1;
		for (int i = 0; i < list.getLength(); i++) {
			int timestamp = 1;
			Node node = list.item(i);
			Element element = (Element) node;
			String attr = element.getAttribute("stroke");
			if(attr.equals("black")||attr.equals("red")||attr.equals("green")||attr.equals("blue")||attr.equals("turquoise")){
				String row[]=element.getTextContent().trim().split("\\n");
				result[i]=new Trajectory(entityID,timestamp);
				for(String s: row){
					String[] fields = s.split("\\s");
					
					result[i].addLocation(Double.parseDouble(fields[0]),Double.parseDouble(fields[1]),timestamp);

					
					//System.out.println("row :"+s);
					timestamp++;
					
				}
				entityID++;
			}
		}
		
		return result;
	}
}
