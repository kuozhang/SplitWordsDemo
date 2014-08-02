package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class keyword{
	private String word="";
	private int frequency=0;
	public keyword(String w,int f){
		this.word=w;
		this.frequency=f;
	}
	public void setWord(String w){
		this.word=w;
	}
	public String getWord(){
		return this.word;
	}
	public void setFrequency(int f){
		this.frequency=f;
	}
	public int getFrequency(){
		return this.frequency;
	}
}
public class ExtractCoWordArray {
	ArrayList<keyword> keywords=new ArrayList<keyword>();
	public int[][] getCowordArray1(File[] filename,File keyword){
		int m=0;
		try {
			BufferedReader br=new BufferedReader(new FileReader(keyword));
			String s="";
			while((s=br.readLine())!=null){
				String[] temp=s.trim().split("  ");
//				System.out.println(temp[0]+"+++"+temp[1]);
				keyword te=new keyword(temp[0],Integer.parseInt(temp[1]));
				keywords.add(te);
				m++;
			}
		int[][] cowordArray=new int[m][m];
		for(int i1=0;i1<m;i1++)
			for(int j1=0;j1<m;j1++)
				cowordArray[i1][j1]=0;
		for(int i=0;i<filename.length;i++){
			String temp="";
			BufferedReader brfile=new BufferedReader(new FileReader(filename[i]));
			String t="";
			while((t=brfile.readLine())!=null){
				temp+=t.trim();
			}
			for(int j=0;j<m;j++){
				for(int k=0;k<m;k++){			
					if(temp.contains(keywords.get(j).getWord())&&temp.contains(keywords.get(k).getWord()))
				    {
						cowordArray[j][k]++;
						continue;
					}
				}		
			}
			}
		for(int i=0;i<m;i++){
			for(int j=0;j<m;j++){
				if(i==j)
					cowordArray[i][j]=keywords.get(i).getFrequency();
				System.out.print(cowordArray[i][j]+" ");
				
				}
			System.out.println();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for(String s:keywords)
//			System.out.println(s);
		
		
		return null;
		
	}
	public int[][] getCowordArray(File[] filename,File keyword){
		int m=0;
		try {
			BufferedReader br=new BufferedReader(new FileReader(keyword));
			String s="";
			while((s=br.readLine())!=null){
				keyword te=new keyword(s.trim(),0);
				keywords.add(te);
				m++;
			}
		int[][] cowordArray=new int[m][m];
		double[][] relatedArray=new double[m][m];
		int[][] binaryArray=new int[m][m];
		for(int i1=0;i1<m;i1++)
			for(int j1=0;j1<m;j1++)
				cowordArray[i1][j1]=0;
		for(int i=0;i<filename.length;i++){
			String temp="";
			BufferedReader brfile=new BufferedReader(new FileReader(filename[i]));
			String t="";
			while((t=brfile.readLine())!=null){
				CountFrequency(t);
				temp+=t.trim();
			}
			for(int j=0;j<m;j++)
				for(int k=0;k<m;k++){			
					int flag=IsExistCoword(temp,keywords.get(j).getWord(),keywords.get(k).getWord());
					if(flag==1)
						cowordArray[j][k]++;
				}		
			}
		for(int i=0;i<m;i++)
			for(int j=0;j<m;j++){
				if(i==j)
					cowordArray[i][j]=keywords.get(i).getFrequency();
			}		
		
		File result=new File("relatedArray.txt");
		if(!result.exists())
			result.createNewFile();
		BufferedWriter bw=new BufferedWriter(new FileWriter(result));
		String r="";
//		for(keyword k:keywords){
//		r+=k.getFrequency()+"\n";
//		}
		DecimalFormat df=new DecimalFormat("0.000");
		for(int j=0;j<m;j++){
			for(int k=0;k<m;k++){
				relatedArray[j][k]=cowordArray[j][k]/(Math.sqrt(cowordArray[j][j])*Math.sqrt(cowordArray[k][k]));
				r+=df.format(relatedArray[j][k])+" ";
			}
		    r+="\n";
		}
		bw.write(r);
		bw.flush();
		bw.close();			
		System.out.println("相似矩阵已经写入result.txt中");
		
		File result1=new File("binaryArray.txt");
		if(!result1.exists())
			result1.createNewFile();
		BufferedWriter bwb=new BufferedWriter(new FileWriter(result1));
		String rb="";
		for(int k1=0;k1<m;k1++){
			for(int k2=0;k2<m;k2++)
			{
				if(relatedArray[k1][k2]>=0.09)
					binaryArray[k1][k2]=1;
				else 
					binaryArray[k1][k2]=0;
				rb+=binaryArray[k1][k2];
			}
			rb+="\n";
		}
		bwb.write(rb);
		bwb.flush();
		bwb.close();
		System.out.println("0-1矩阵已经写入binaryArray.txt中");
		
		File result2=new File("drawdata.txt");
		if(!result2.exists())
			result2.createNewFile();
		BufferedWriter bfd=new BufferedWriter(new FileWriter(result2));
		String rt="";
		ArrayList<String> list=new ArrayList<String>();
		for(int k3=0;k3<m;k3++)
			for(int k4=0;k4<=k3;k4++)
			{
				if(binaryArray[k3][k4]==1&&k3!=k4){
					if(list.isEmpty()){
						list.add(keywords.get(k3).getWord());
						list.add(keywords.get(k4).getWord());
					}
					else{
						if(!list.contains(keywords.get(k3).getWord()))
						list.add(keywords.get(k3).getWord());
					    if(!list.contains(keywords.get(k4).getWord()))
						list.add(keywords.get(k4).getWord());
					}
					
				}			
			}
		list.add("BOF");
		list.add("EOF");
		rt+="*Vertices "+list.size()+"\n";
		for(int k7=0;k7<list.size();k7++)
			rt+=(k7+1)+" \""+list.get(k7)+"\"\n";
		rt+="*arcs\n";
		for(int k5=0;k5<m;k5++)
			for(int k6=0;k6<m;k6++)
			{
				if(binaryArray[k5][k6]==1&&k5!=k6){
					rt+=(list.indexOf(keywords.get(k5).getWord())+1)+" "+(list.indexOf(keywords.get(k6).getWord())+1)+" 1\n";
				}
			}
		bfd.write(rt);
		bfd.flush();
		bfd.close();	
		System.out.println("生成网络的数据已经写入drawdata.txt中");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;		
	}
	//用正则计算一个句子中含的关键词的个数；
    private void CountFrequency(String sentence)
    {
    	int count;
    	for(int i=0;i<keywords.size();i++){
    		count=0;
    		keyword temp=keywords.get(i);
    		if(temp.getWord().contains("、")){
    			String[] words=temp.getWord().split("、");
    			int[] freq=new int[words.length];
    			for(int j=0;j<words.length;j++){
    				freq[j]=0;
    				Pattern p=Pattern.compile(words[j]);
            		Matcher m=p.matcher(sentence);
            		while(m.find())
            			freq[j]++; 
    			}
    			for(int k1=0;k1<words.length-1;k1++)
    				for(int k2=k1+1;k2<words.length;k2++)
    				{
    					if(words[k1].contains(words[k2]))
    						freq[k1]=0;
    					else if(words[k2].contains(words[k1]))
    							freq[k2]=0;
    				}
    			for(int k=0;k<words.length;k++)
    				count+=freq[k];
    			
    		}
    		else{
    			Pattern p=Pattern.compile(temp.getWord());
        		Matcher m=p.matcher(sentence);
        		while(m.find())
        			count++;
        		
    		}
    		keywords.get(i).setFrequency(temp.getFrequency()+count);
    	}
    }
    //判断文本temp中是否同时含关键词word和word2;
    private int IsExistCoword(String temp, String word, String word2) {
		// TODO Auto-generated method stub
    	String[] t1=word.split("、");
    	String[] t2=word2.split("、");
    	for(int i=0;i<t1.length;i++){
    		for(int j=0;j<t2.length;j++){
    			if(temp.contains(t1[i])&&temp.contains(t2[j])){
    				return 1;
    			}
    		}
    	}
		return 0;
	}
}
