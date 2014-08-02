package com.patent.read;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;



public class ReadPatentDate {
	private String filePath="patentData.xls";
	
	/**
	 * 读取EXCEL文件
	 * @param filename专利名称（专利文档中的专利名称）
	 * @return  专利申请期
	 * @throws BiffException
	 * @throws IOException
	 */
	public String readDate(String patentname) throws BiffException, IOException{
		
		String patent=patentname.replaceAll(" ", "");
		
		String date=null;
		
		//根据(专利)名称读取申请日
		InputStream is =null;
		jxl.Workbook workbook =null;		
		
		is=new FileInputStream(filePath);
		workbook=Workbook.getWorkbook(is);
		
		Sheet sheet=workbook.getSheet(0);
		int column=sheet.getColumns();
		int row=sheet.getRows();
		
//		for(int k=0;k<column;k++)
//			System.out.println(readCell(sheet.getCell(k,0)));

		
		//列属性为名称的列i,列属性为申请日的列j；
		int i=0;
		int j=0;
		for(int cl=0;cl<column;cl++){
			Cell name=sheet.getCell(cl, 0);
			if(readCell(name).equals("名称"))
				i=cl;                              //1
			if(readCell(name).equals("申请日"))
				j=cl;                              //11
		}
		

		//读取名称列i值为filename的列j申请日的值；
		for(int c2R=1;c2R<row;c2R++){   //从第2行开始查找满足条件的行申请日；
			Cell patentn=sheet.getCell(i,c2R);
			if(readCell(patentn).equals(patent)){
				date=readCell(sheet.getCell(j,c2R));
			}
		}
				
		workbook.close();
		
		return date;
	}
	/**
	 * 读取EXCEL文件
	 * @param filename 专利名称列表（专利文档中的专利名称）
	 * @return  专利申请期列表
	 * @throws BiffException
	 * @throws IOException
	 */
	public List<String> readDateList(String[] patentname) throws BiffException, IOException{
		List<String> datelist=null;
		
		//根据(专利)名称读取申请日
		InputStream is =null;
		jxl.Workbook workbook =null;		
		
		is=new FileInputStream(filePath);
		workbook=Workbook.getWorkbook(is);
		
		Sheet sheet=workbook.getSheet(0);
		int column=sheet.getColumns();
		int row=sheet.getRows();
		
		//列属性为名称的列i,列属性为申请日的列j；
		int i=0;
		int j=0;
		for(int cl=0;cl<column;cl++){
			Cell name=sheet.getCell(cl,0 );
			if(name.getContents().equals("名称"))
				i=cl;
			if(name.getContents().equals("申请日"))
				j=cl;
		}
			
		//读取名称列i值为filename的列j申请日的值；
		datelist=new ArrayList<String>();
		for(String patent:patentname){
			String date=null;
			String p=patent.replaceAll("", " ");
			for(int c2R=1;c2R<row;c2R++){
				   //从第2行开始查找满足条件的行申请日；
				Cell patentn=sheet.getCell(i,c2R);
				if(patentn.getContents().equals(p)){
					date=readCell(sheet.getCell(j,c2R));
					datelist.add(date);
				}			 
			}
		}
		return datelist;
	}
	
	private String  readCell(Cell cell){
		String cellresult=null;
		if(cell.getType().equals(CellType.LABEL)){
			LabelCell lc= (LabelCell) cell;
			cellresult=lc.getString();
		}
		else if(cell.getType().equals(CellType.NUMBER)){
			NumberCell nc= (NumberCell) cell;
			cellresult=String.valueOf(nc.getValue());
		}
		else if(cell.getType().equals(CellType.DATE)){
			DateCell dc=(DateCell) cell;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			cellresult=sdf.format(dc.getDate());
		}
		else{
			cellresult=cell.getContents();
		}
		return cellresult;
	}
	
	
	

}
