package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MegerKeywords {
	public static void main(String args[]){
		String path1="beforekeywordmerge.txt";
		String path2="synonymword.txt";
		BufferedReader br=null;
		ArrayList<key> keyword=new ArrayList<key>();
		Map<String,String> keypair=new HashMap<String,String>();
		String s="";
		try {
			//����ؼ���
			br=new BufferedReader(new FileReader(path1));
			while((s=br.readLine())!=null){
				if(s.isEmpty())
					continue;
				key k=new key(s.trim());
				keyword.add(k);		
			}
			br.close();
			//����ͬ��ʱ�
			br=new BufferedReader(new FileReader(path2));
			while((s=br.readLine())!=null){
				String[] temp=s.trim().split("=");
				keypair.put(temp[0], temp[1]);	
			}			
			br.close();
			//ͬ��ʺϲ�������Сд
			for(key k:keyword){
				String tp="";
				for(String t:keypair.keySet()){
					String[] temp=keypair.get(t).split("��");
			        if(temp.length<1)
				       continue;
			        for(String te:temp){
			        	if(k.getKey().trim().equalsIgnoreCase(te)||k.getKey().trim().equalsIgnoreCase(t)){
			        			k.setKey(t);
			        			break;			        		    
				        }
			        	else if(k.getKey().trim().toUpperCase().contains(te)||k.getKey().trim().toUpperCase().contains(t)){
			        			
			        		int i=k.getKey().trim().toUpperCase().indexOf(te);
			        		  	int j=k.getKey().trim().toUpperCase().indexOf(t);
			        		    if(i>=0){			        		    	
			        		    	if(i==0){
			        		    		tp=t+k.getKey().trim().substring(te.length());
			        		    	}else{
				        		    	tp=k.getKey().trim().substring(0, i)+t+k.getKey().trim().substring(i+te.length());

			        		    	}
			        		    	k.setKey(tp);
			        		    	break;
			        		    }			        	  
			        		    if(j>=0){
			        		    	if(k.getKey().trim().contains(t))
			        		    		break;		        		    	        		    	
			        		    	if(j==0){
			        		    		tp=t+k.getKey().trim().substring(t.length());
			        		    	}else{
				        		    	tp=k.getKey().trim().substring(0, j)+t+k.getKey().trim().substring(j+t.length());
			        		    	}
			        		    	 k.setKey(tp);
			        		    	 break;
			        		    }
			        		    
			        	}
			        	
			        }
			        
			        String ttry=k.getKey().replaceFirst(t, "");     //һ���ؼ����ڿ��ܺ����ͬ���ICоƬ��
			        if(ttry.contains(t)){
			        	k.setKey(ttry);
			        }		
					}
			}
			
			//ȥ��
			Set<String> word=new HashSet<String>();
			for(key k:keyword){
				word.add(k.getKey());
			}
			//д���ļ�
			BufferedWriter bw=new BufferedWriter(new FileWriter("aftermergekeyword.txt"));
			bw.write(word.toString());
			bw.close();			
			keyword=new ArrayList<key>();
			Iterator<String> it=word.iterator();
			while(it.hasNext()){
				keyword.add(new key(it.next()));
			}
			
			//����ؼ���
			int i,j;
//			Map<String,String> mergeword=new HashMap<String,String>();
			Hashtable<String,String> mergeword=new Hashtable<String,String>();
			boolean flag=false;
			for(int a=0;a<keyword.size();a++){
				flag=false;
				String k1=keyword.get(a).getKey();
				for(int b=keyword.size()-1;b>a;b--)
				{
					i=0;
					j=0;
					String k2=keyword.get(b).getKey();
					int k=Math.min(k1.length(), k2.length());
					while(i<k){
						if(k1.substring(k1.length()-i-1).equals(k2.substring(k2.length()-i-1)))
							j++;
						else break;
						i++;
					}
					if(j>=2)        //��ȡ��ǰ��
					{	
						flag=true;
						//ȡ�������ƴ�	
						String tmp=match_keyword(k1.substring(0, k1.length()-j),k2.substring(0, k2.length()-j));
//						System.out.println("���Ƶ��Ӵ���"+tmp+k2.substring(k2.length()-j));
						//���hashtable�д�����ͬ��Ԫ�أ���׷�ӵ���ͬԪ�ص�ֵ���棻
						if(mergeword.containsKey(tmp+k2.substring(k2.length()-j)))
						{
							String[] t=mergeword.get(tmp+k2.substring(k2.length()-j)).trim().split("��");
							mergeword.remove(tmp+k2.substring(k2.length()-j));
							Set<String> tt=new HashSet<String>();
							for(int m=0;m<t.length;m++)
									tt.add(t[m]);		
														
							tt.add(k1);
							tt.add(k2);
							StringBuilder sb=new StringBuilder();
							Iterator<String> itt=tt.iterator();
							while(itt.hasNext()){
								sb.append(itt.next()+"��");
							}						
							
							mergeword.put(tmp+k2.substring(k2.length()-j), sb.toString().substring(0,sb.toString().length()-1));
						}
						else{ 
							mergeword.put(tmp+k2.substring(k2.length()-j), k1+"��"+k2);
						}				  
						
					}
					else {
						continue;
//						System.out.println("û�����Ƶ��Ӵ�");
						}
					
				}
				if(!flag)
					System.out.println(k1);
			}
			System.out.println(mergeword.toString());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//�������ַ�������������Ӵ�
	private static String match_keyword(String s1,String s2){
		String max="";
		for(int i=0;i<s1.length();i++){
			for(int j=i+1;j<=s1.length();j++){
				String sub=s1.substring(i, j);
				if((s2.indexOf(sub)!=-1)&&sub.length()>max.length()){
					max=sub;
				}
			}
		}
		return max;
	}

}
class key{
	private String k="";
	key(String k){
		this.k=k;
	}
	public void setKey(String k){
		this.k=k;
	}
	public String getKey(){
		return this.k;
	}
}
