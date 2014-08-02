package com.patent.entity;
import java.util.regex.*;

public class WordList {
	public Word head;
	public int size=0;
	
	//���һ���ڵ�
	public void add(String str,String pos,int freq,String doc)
	{
		Word s=new Word(str,pos,freq,doc);
		if(head==null)
		{
			s.next=null;
			head=s;
		}
		else
		{
			Word p;
			p=head;
			while(p.next!=null)
			{
				p=p.next;
			}
			s.next=null;
			p.next=s;
		}
		size++;
	}
	public void delete(Word w)
	{
		Word p,q;
		if(head.word.equals(w.word))
		{
			head=head.next;
		}
		q=head;
		while(q.next!=null)
		{
			p=q.next;
			if(p.word.equals(w.word))
			{
					q.next=p.next;
					break;
			}
			q=q.next;
		}
		size--;
	}
	
	public void filter()
	{
		if(head.word.length()==1||head.part_of_speech.equals("v")||head.part_of_speech.equals("@"))
		{	
			head=head.next;
		}
		
		Word p=head,q;
		while(p.next!=null)
		{
			q=p.next;
				while(q.word.length()==1||q.part_of_speech.equals("v")||q.part_of_speech.equals("@"))
				{
					p.next=q.next;
					q=q.next;
				}
			p=p.next;
		}
	}
	public void filter2()
	{
		Word p=head,q,r;
		while(p.next!=null)
		{
			q=p.next;
			r=q.next;
			Pattern pt=Pattern.compile(q.word);
			Matcher m;
			while(r!=null)
			{
				m=pt.matcher(r.word);
				if(m.find()&&(q.freq-r.freq<=3))
				{	
					p.next=q.next;	//delete q node
					break;
				}
				r=r.next;
			}
			p=p.next;
		}
	}
	
	
	//����������һ���ڵ�
	public Word last()
	{
		Word p;
		p=head;
		while(p.next!=null)
			p=p.next;
		return p;
	}
	
	//�ж��������Ƿ���ڽڵ�S,���ڷ���true�������ڷ���false
	public boolean isExist(Word s)
	{
		if(head==null)
			return false;
		Word p=head;
		while(p!=null)
		{
			if(s.word.equals(p.word))
				return true;
			else
				p=p.next;
		}
		return false;
	}
	
//	�������
	public StringBuilder output()
	{
		StringBuilder RESULT=new StringBuilder();
		if(head==null)
		{
			System.out.println("�ؼ�������Ϊ��");
			return null;
		}
			Word p=head;
			while(p!=null)
			{
				//---------------��Ҫ�Ķ�������ȫ���ַ���������text�ؼ�����
				RESULT.append(p.word+" "+" "+p.freq+"\r\n");
				//System.out.println(p.word+" "+p.freq);
				p=p.next;
			}
		
			return RESULT;
	}
	
	public void clean()
	{
		head=null;
		size=0;
	}
	public boolean match(String a,String b)
	{
		int i=0,j=0;
		while(i<a.length())
		{
			if(a.charAt(i)!=b.charAt(j))
				i++;
			else
				j++;
		}
		if(i<a.length()&&j<b.length())
			return true;
		else
			return false;
	}
}
