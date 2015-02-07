package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CountWeightArray {
	
	public static void main(String args[]) throws IOException{
		String path="sourcefile";
		String path2="keywordfile.txt";
		ArrayList<String> keyword=new ArrayList<String>();
		//读入关键词
    	BufferedReader bb; 
    	bb = new BufferedReader(new FileReader(path2));
		String t="";
		while((t=bb.readLine())!=null){
    		if(t.isEmpty())
    			continue;
    		keyword.add(t.trim());
    	}
		
		//读入源文件
		File directory=new File(path);
		String[] filename=directory.list();
    	int[][] array=new int[filename.length][keyword.size()];
    	for(int i=0;i<filename.length;i++)
    		for(int j=0;j<keyword.size();j++)
    			array[i][j]=0;  	
    	String tu="";
    	 for(int m=0;m<array.length;m++){
 	    	BufferedReader br=new BufferedReader(new FileReader(path+"\\"+filename[m]));
 	    	for(int n=0;n<array[0].length;n++){ 
	    			int flag=0;
	    			if(keyword.get(n).contains("=")){
	    				String[] temp=keyword.get(n).split("=|、");
	    				while((tu=br.readLine())!=null)
	    				{
	    						if(tu.isEmpty())
	    							continue;
	    						for(int k=1;k<temp.length;k++){
	    							if(tu.contains(temp[k])){
	    								flag=1;
	    								break;
	    							}
	    						}
	    				}			
	    			}
	    			else{
	    				while((tu=br.readLine())!=null){
	    					if(tu.isEmpty())
	    						continue;
	    					if(tu.contains(keyword.get(n)))
	    						flag=1;
	    				}
	    			} 	
			    array[m][n]=flag;					
			}	
			br.close();
    	 }	
     	for(int i=0;i<array.length;i++){
    		for(int j=0;j<array[0].length;j++)
    			System.out.print(array[i][j]);
    		System.out.println();
    	}
		File result=new File("resultuse.txt");
		if(!result.exists())		
			result.createNewFile();			
	    BufferedWriter bw=new BufferedWriter(new FileWriter(result));
	    StringBuilder sbb=new StringBuilder();
	    for(int i=0;i<array.length;i++){
		    for(int j=0;j<array[0].length;j++)				
			    sbb.append(array[i][j]);
	    sbb.append("\n");
    	}
	    bw.write(sbb.toString());
  	    bw.close();		
	}  
	    	
//	private static int readFreqWord2(BufferedReader br, String s) {
//		int flag=0;
//		// TODO Auto-generated method stub
//		if(s.contains("=")){
//			String[] temp=s.split("=|、");
//			for(int i=1;i<temp.length;i++){
//				if(getCount(br,temp[i])==1)
//				{
//					flag=1;
//					break;
//				}	
//			}	
//			
//		 }else{
//			 System.out.println(s);
//			 flag=getCount(br,s);
//			 System.out.println(flag);
//		 }
//		return flag;
//		
//	}
//	public static int getCount(BufferedReader br,String t){
//		int flag=0;
//		String in="";
//		try {
//			while((in=br.readLine())!=null){
//				if(in.isEmpty())
//					continue;
//				if(in.contains(t)){
//					flag=1;
//					break;
//				}	
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return flag;
//	}
}
