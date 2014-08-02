package com.patent.read;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import com.patent.entity.Word;
import com.patent.entity.WordList;

public class ReadFile {
	File myfile;
	File originalfile;
	int threshold;
	public ReadFile(File file1,File file2,int t){
		originalfile=file1;//originalfile Դ�ļ�
		myfile=file2;//myfile �ִʽ���洢�ļ�
		threshold=t;
	}
	
	public StringBuilder KExtract() {
		
		StringBuilder sb=new StringBuilder();
		try{
			BufferedReader in=new BufferedReader(new FileReader(myfile));
			String s;
			while((s=in.readLine())!=null){
				String a="";                        
				for(int i=0;i<s.length()-1;i++){                           //ɾ������Ŀո�
					
					if((int)s.charAt(i)==32&&(int)s.charAt(i+1)==32){
						continue;
					}
					a+=s.charAt(i);									
					}
				sb.append(a.trim()+" ");			
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}
//		System.out.println(sb.toString());
		StringBuilder text=new StringBuilder();
		try{
			BufferedReader in=new BufferedReader(new FileReader(originalfile));
			String s;
			while((s=in.readLine())!=null){
				text.append(s+"&");	
				}
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		ArrayList<String> mylist1=new ArrayList<String>(Arrays.asList((sb.toString()).split(" "))); //�дʵ��б�
		ArrayList<String> mylist=new ArrayList<String>();
		for(int i=0;i<mylist1.size();i++)
		{
			if(mylist1.get(i).trim().equals(""))
				continue;
			String s=mylist1.get(i).trim();
			String a="";
			for(int j=0;j<s.length();j++){                           //ɾ������Ŀո�
				if((int)s.charAt(j)==32){
					continue;
				}	
				a+=s.charAt(j);
			}
			mylist.add(a);
		}
		ArrayList<String> textlist1=new ArrayList<String>(Arrays.asList(text.toString().split("&")));
		ArrayList<String> textlist=new ArrayList<String>();
		for(int i=0;i<textlist1.size();i++)
		{
			if(textlist1.get(i).trim().equals(""))
				continue;
			String s=textlist1.get(i).trim();
			String a="";
			for(int j=0;j<s.length();j++){                           //ɾ������Ŀո�
				if((int)s.charAt(j)==32){
					continue;
				}	
				a+=s.charAt(j);
			}
			textlist.add(a);
		}
		
		WordList wordlist=new WordList();	//���ı�˳�����дʺ�Ĵ�
		WordList mergelist=new WordList();	//��źϲ���ʵ������м��÷ָ����ֿ�
		WordList finallist=new WordList();	//��Źؼ��ʺ�ѡ�ʵ�����
		
		String word,word_value;
		int freq;

		for(int i=0;i<mylist.size();i++)  //���ı�˳�򹹽��дʺ� �ʵ�����
		{
			ArrayList<String> temp=new ArrayList<String>(Arrays.asList(mylist.get(i).split("/")));
			word=temp.get(0);
			word_value=temp.get(1);
			if(word_value.equals("n")||word_value.equals("v")||word_value.equals("a")||word_value.equals("x")||word_value.equals("ng")||word_value.equals("vi"))
			{
				freq=wordFrequence1(word,textlist);
			}
			else
			{
				freq=0;
			}
			wordlist.add(word,word_value,freq," ");
		}
		Word p=wordlist.head,q,r;
		
		while(p.next.next!=null)
		{
			q=p.next;
			r=q.next;
			if(p.freq>=threshold&& q.freq>=threshold)
			{
				String strtemp=p.word.concat(q.word);
				int freqtemp=wordFrequence1(strtemp,textlist);
				
				String valuetemp;
				if(q.part_of_speech.equals("n")||q.part_of_speech.equals("ng"))
//				if(q.part_of_speech.equals("n"))	
				{	
					valuetemp=q.part_of_speech;
				}
				else
					valuetemp="@";
				mergelist.add(strtemp,valuetemp,freqtemp,originalfile.getName());
			}
			else
			{
				if(p.freq<threshold && q.freq>=threshold &&r.freq<threshold)
				{	if(finallist.isExist(q)==false)
						finallist.add(q.word,q.part_of_speech,q.freq,originalfile.getName());
				}
				else
				if(mergelist.head!=null&&!mergelist.last().word.equals("*"))
					{
						mergelist.add("*"," ",0," ");
					}
			}
			p=p.next;
		}
		
		//��mergelist���½��д���
		while(mergelist.size>=2)
		{
		
			WordList templist=new WordList();	//���mergelist,Ȼ�����mergelist
		
			templist.head=mergelist.head;	//��mergelist������templist
			templist.size=mergelist.size;
			mergelist.clean();	//���mergelist
//			System.out.println(templist.size+"/n"+mergelist.size);
			p=templist.head; 
			while(p.next.next!=null)
			{
				q=p.next;
				r=q.next;
				if(p.freq>=threshold&&q.freq>=threshold)
				{
					int i=0,j=0;
					while(i<p.word.length())
					{
						if(p.word.charAt(i)!=q.word.charAt(j))
							i++;
						else
							j++;
					}
					word=p.word.concat(q.word.substring(j));
					word_value=q.part_of_speech;
					freq=wordFrequence(p.word,textlist);
					mergelist.add(word,word_value,freq,originalfile.toString());
				}
				else
				{
					if(p.freq<threshold&&q.freq>=threshold&&r.freq<threshold)
					{
						if(finallist.isExist(q)==false)
							finallist.add(q.word,q.part_of_speech, q.freq,originalfile.getName());
					}
					else
						if(mergelist.head!=null&&!mergelist.last().word.equals("x"))
							{
								mergelist.add("x"," ",0," ");
							}
				}
				p=p.next;
			}
		}		
		try{
				finallist.filter();	//��һ�ι��ˣ����˵����ʺ�һЩ������
		        finallist.filter2();
				
		}catch(NullPointerException e){
			System.out.println(e.toString());
		}
	
		return finallist.output();
	}
	
	
	
	//��ʵ�Ƶ��
	static int wordFrequence(String str,ArrayList textlist)
	{	
		int T=5,A=2,D=4,C=1;
		
		int count=0;
		Pattern p=Pattern.compile(str);
		int len=textlist.size();
		for(int i=0;i<len;i++)
		{
			Matcher m=p.matcher(textlist.get(i).toString());
			while(m.find())
			{
				count++;
			}
			switch(i)
			{
				case 0:count+=T;break;
				case 1:count+=A;break;
				case 2:count+=D;break;
				case 3:break;
				case 4:count+=C;break;
			}
		}
		return count;	
	}
//	��ʵ�ʵ�ʵ�Ƶ��
	static int wordFrequence1(String str,ArrayList textlist)
	{
		int count=0;
		Pattern p=Pattern.compile(str);
		int len=textlist.size();
		for(int i=0;i<len;i++)
		{			
			Matcher m=p.matcher(textlist.get(i).toString());
			while(m.find())
			{
				count++;
			}
		}
		return count;
		
	}

}
