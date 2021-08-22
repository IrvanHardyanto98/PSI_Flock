package com.skripsi.psi_flock.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Kelas yang berfungsi menuliskan file PDF menggunakan library Itext versi 7.1.16 (https://itextpdf.com/en/products/itext-7/itext-7-core)
 * 
 * @author Irvan Hardyanto
 */
public class PDFWriter {
	private static final float LIST_SYMBOL_INDENT=12;
	private static final float FONT_SIZE=12;
	private static final float TITLE_FONT_SIZE=24;
	private static final float LEFT_MARGIN=20;
	private static final float RIGHT_MARGIN=20;
	private static final float TOP_MARGIN=20;
	private static final float BOTTOM_MARGIN=20;
	private Document document;
	private PdfFont font;
	private PdfFont boldFont;
	
	public PDFWriter(String path,String title){
		try {
			this.font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			this.boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
			
			PdfWriter writer = new PdfWriter(path);
			PdfDocument pdf = new PdfDocument(writer);
			this.document = new Document(pdf,PageSize.A4);
			
			this.document.setMargins(TOP_MARGIN, RIGHT_MARGIN, BOTTOM_MARGIN, LEFT_MARGIN);
			this.document.add(new Paragraph(title).setFont(this.font).setFontSize(TITLE_FONT_SIZE));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeDocument(){
		this.document.close();
	}
	public void addParagraph(String par) {
		this.document.add(new Paragraph(par).setFont(this.font).setFontSize(FONT_SIZE));
	}
	
	public void addSubTitle(String line,int fontsize){
		this.document.add(new Paragraph(line).setFont(this.font).setFontSize(fontsize));
	}
	public void addBlankLine(){
		this.document.add(new Paragraph());
		document.add(new Paragraph());                           
	}
	public void addBasicList(boolean ordered,String[] listItems){
		List list = new List();
		if(ordered){
			list.setSymbolIndent(LIST_SYMBOL_INDENT).setListSymbol(ListNumberingType.DECIMAL);
		}else{
			list.setSymbolIndent(LIST_SYMBOL_INDENT).setListSymbol("\u2022");
		}
		list.setFont(this.font).setFontSize(FONT_SIZE);
		
		for(String s: listItems){
			list.add(new ListItem(s));
		}
		
		this.document.add(list);
	}
	/**
	 * Tambahkan tabel, dengan posisi header terletak di kolom
	 **/
	public void addTable(String[][] values){
		Table table = new Table(values[0].length);
		for(int i=0;i<values.length;i++){
			String[] rowValue= values[i];
			for(int j = 0;j<values[i].length;j++){
				Cell c = new Cell();
				
				if(j==0){
					c.add(new Paragraph(rowValue[j]).setFont(this.boldFont).setTextAlignment(TextAlignment.CENTER));
				}else{
					c.add(new Paragraph(rowValue[j]).setFont(this.font).setTextAlignment(TextAlignment.CENTER));
				}
				table.addCell(c);
			}
		}
		this.document.add(table);
	}
}
