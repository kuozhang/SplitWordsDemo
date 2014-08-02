package com.patent.read;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

public class ReadPDFtoDatatbase {
	static String[] s={"(10)申请公布号","(21)申请号","(22)申请日","(43)申请公布日","(71)申请人","(72)发明人","(74)专利代理机构","(54)发明名称","(57)摘要","(51)","(19)","(12)","权利要求书","说明书"};
	public static void main(String[] args) throws Exception{
		String filename="test.pdf";
		readPDF(filename);
	}
	private static void readPDF(String filename) throws Exception{	  
		PDDocument pdfdocument=null;
		pdfdocument=PDDocument.load(new File(filename));
		PDFTextStripper stripper=new PDFTextStripper();
//		PDDocumentInformation docInfo=pdfdocument.getDocumentInformation();
		String result=stripper.getText(pdfdocument);
		String t=rePlace(result);
//		for(int i=0;i<s.length;i++)
//		    t=spit(t,s[i]);
//		for(int i=0;i<s.length;i++){
//			lookSubString(t,s,i);
//		}
//		lookSubString(t,s,0);
//		lookSubString(t,s,1);
		System.out.println(t);			
	}
	private static String rePlace(String s){		
			String dest = "";
			if(s != null){
				Pattern p = Pattern.compile("\\s*|\t|\n");
				Matcher m = p.matcher(s);
				dest = m.replaceAll("");
			}
			return dest;
		}
	private static String spit(String t,String sub){
		String e="";
		for(int j=0;j<t.length();j++){
			e=t.replace(sub, "\n"+sub);
			j+=sub.length();		
		}
		return e;
	
	}
	private static void lookSubString(String s,String[] t,int o){
		int i=s.indexOf(t[o]);
		System.out.println(t[o]);
		while(s.charAt(i)!=	'\n')
			i++;
		System.out.println(s.subSequence(s.indexOf(t[o])+t[o].length(), i));
	}
}