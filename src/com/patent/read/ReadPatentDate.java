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
	 * ��ȡEXCEL�ļ�
	 * @param filenameר�����ƣ�ר���ĵ��е�ר�����ƣ�
	 * @return  ר��������
	 * @throws BiffException
	 * @throws IOException
	 */
	public String readDate(String patentname) throws BiffException, IOException{
		
		String patent=patentname.replaceAll(" ", "");
		
		String date=null;
		
		//����(ר��)���ƶ�ȡ������
		InputStream is =null;
		jxl.Workbook workbook =null;		
		
		is=new FileInputStream(filePath);
		workbook=Workbook.getWorkbook(is);
		
		Sheet sheet=workbook.getSheet(0);
		int column=sheet.getColumns();
		int row=sheet.getRows();
		
//		for(int k=0;k<column;k++)
//			System.out.println(readCell(sheet.getCell(k,0)));

		
		//������Ϊ���Ƶ���i,������Ϊ�����յ���j��
		int i=0;
		int j=0;
		for(int cl=0;cl<column;cl++){
			Cell name=sheet.getCell(cl, 0);
			if(readCell(name).equals("����"))
				i=cl;                              //1
			if(readCell(name).equals("������"))
				j=cl;                              //11
		}
		

		//��ȡ������iֵΪfilename����j�����յ�ֵ��
		for(int c2R=1;c2R<row;c2R++){   //�ӵ�2�п�ʼ���������������������գ�
			Cell patentn=sheet.getCell(i,c2R);
			if(readCell(patentn).equals(patent)){
				date=readCell(sheet.getCell(j,c2R));
			}
		}
				
		workbook.close();
		
		return date;
	}
	/**
	 * ��ȡEXCEL�ļ�
	 * @param filename ר�������б�ר���ĵ��е�ר�����ƣ�
	 * @return  ר���������б�
	 * @throws BiffException
	 * @throws IOException
	 */
	public List<String> readDateList(String[] patentname) throws BiffException, IOException{
		List<String> datelist=null;
		
		//����(ר��)���ƶ�ȡ������
		InputStream is =null;
		jxl.Workbook workbook =null;		
		
		is=new FileInputStream(filePath);
		workbook=Workbook.getWorkbook(is);
		
		Sheet sheet=workbook.getSheet(0);
		int column=sheet.getColumns();
		int row=sheet.getRows();
		
		//������Ϊ���Ƶ���i,������Ϊ�����յ���j��
		int i=0;
		int j=0;
		for(int cl=0;cl<column;cl++){
			Cell name=sheet.getCell(cl,0 );
			if(name.getContents().equals("����"))
				i=cl;
			if(name.getContents().equals("������"))
				j=cl;
		}
			
		//��ȡ������iֵΪfilename����j�����յ�ֵ��
		datelist=new ArrayList<String>();
		for(String patent:patentname){
			String date=null;
			String p=patent.replaceAll("", " ");
			for(int c2R=1;c2R<row;c2R++){
				   //�ӵ�2�п�ʼ���������������������գ�
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
