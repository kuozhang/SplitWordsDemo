package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.read.biff.BiffException;

import com.patent.entity.DependencyEgo;
import com.patent.entity.KEYWORD;
import com.patent.read.ReadPatentDate;

public class CountWeightArray3 {
	public static void main(String args[]) throws IOException{
		
		String path="sourcefile";
		String path2="keywordfile.txt";
		
		List<String> keyword=new ArrayList<String>();//存储技术关键词；
		List<String> keywordSingle=new ArrayList<String>(); //去除同义词存储关键词；
		List<String> textlist=new ArrayList<String>(); //存储专利文本内容；
		Map<String,String> patentdate=new HashMap<String,String>(); //成对存储专利、申请日;
		List<String> datelist=new ArrayList<String>();//存储专利申请日

				
		List<String> keydate1=new ArrayList<String>();//存储关键词在1986-2010中首次出现的申请日
		List<String> keydate2=new ArrayList<String>();//存储关键词在2011-2012年首次出现的申请日
				
		

		//读入专利文档内容
		File directory=new File(path);
		String[] filename=directory.list();
		textlist=readSource(path,filename);		
		
		
		//获取每篇专利的名称,利用专利名称到专利信息汇总表中查找申请日期，将专利名称和申请日成对保存到patentdate,申请日保存到datelist中。
    	for(String text:textlist){
    		int start=text.indexOf("发明名称");
    		int end=text.indexOf("摘要");
    		String patentna=text.substring(start+4, end).replaceAll(" ", "");  //专利名称,注意抽取专利名称的方法，专利文档质量影响后面的抽取结果。
//			System.out.println(patentna);
    		ReadPatentDate pd=new ReadPatentDate();
			try {
				String date=pd.readDate(patentna);
//				System.out.println(date);
				patentdate.put(patentna, date);				
				datelist.add(date);				
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}    	
//    	 System.out.println("------输出专利日期--------");
//    	 for(String date:datelist)
//    		 System.out.println(date);
		    	
    	
		//读入关键词列表
		keyword=readKeywords(path2);
		keywordSingle=readKeywordsSingle(keyword);
		
		
		//根据时间，将专利文本列表分成两段：1986-2010，2011-2012；
		List<String> textlist1=new ArrayList<String>(); //存储1986-2010专利文本内容；		
	    List<String> datelist1=new ArrayList<String>(); //存储专利申请日;


		List<String> textlist2=new ArrayList<String>(); //存储2011-2012专利文本内容；
		List<String> datelist2=new ArrayList<String>(); //存储专利申请日;
		
		for(int t=0;t<datelist.size();t++){
			String texttemp=textlist.get(t);
			if(datelist.get(t).compareTo("2011")<0){
				textlist1.add(texttemp);
				datelist1.add(datelist.get(t));
			}else{
				textlist2.add(texttemp);
				datelist2.add(datelist.get(t));
			}
		}
	
//		System.out.println("1986-2010年的专利有"+textlist1.size());
//		System.out.println("2011-2012年的专利有"+textlist2.size());

				
		
		//数组array，其中array[i][j]表示第j个关键词是否在第i篇专利文档中出现，出现为值1，不出现为值0；
    	
		int[][] array1=new int[textlist1.size()][keyword.size()];
    	for(int i=0;i<array1.length;i++)
    		for(int j=0;j<array1[0].length;j++)
    			array1[i][j]=0;  
    	
    	for(int i=0;i<textlist1.size();i++){
    		for(int j=0;j<keyword.size();j++){
    			array1[i][j]=checkWordInPatent(textlist1.get(i),keyword.get(j));
    		}    		
    	}   
    	
    	int[][] array2=new int[textlist2.size()][keyword.size()];
    	for(int i=0;i<array2.length;i++)
    		for(int j=0;j<array2[0].length;j++)
    			array2[i][j]=0;  
    	
    	for(int i=0;i<textlist2.size();i++){
    		for(int j=0;j<keyword.size();j++){
    			array2[i][j]=checkWordInPatent(textlist2.get(i),keyword.get(j));
    		}    		
    	}    
  
    	//计算技术的申请日 or技术在专利中首次出现的时间 	
    	keydate1=countKeyDate(array1,datelist1);  //1986-2010
    	keydate2=countKeyDate(array2,datelist2);  //2011-2012
    	 

    	 
    	 //--------------------共现矩阵------------------------------
    	int[][] cooccure1=new int[keyword.size()][keyword.size()];
    	int[][] cooccure2=new int[keyword.size()][keyword.size()];
    	
    	cooccure1=countCooccure(array1);
    	cooccure2=countCooccure(array2);  	
    	    	 
//    	writeFile("result.txt",array);
//    	writeFile("coocurre.txt",cooccure);
    	
    	
    	
    	int size=keyword.size();
    	//共现矩阵每行的最大值
    	int[] max1=new int[cooccure1.length];
    	for(int o1=0;o1<cooccure1.length;o1++){
    		max1[o1]=cooccure1[o1][0];
    		for(int o2=o1+1;o2<cooccure1.length;o2++){
    			{
    				if(max1[o1]<cooccure1[o1][o2])
    					max1[o1]=cooccure1[o1][o2];
    			}
    		}
    	}
    	
//    	for(int i=0;i<max1.length;i++)
//		   System.out.println(max1[i]);
    	
    	int[] max2=new int[cooccure2.length];
    	for(int o1=0;o1<cooccure2.length;o1++){
    		max2[o1]=cooccure2[o1][0];
    		for(int o2=o1+1;o2<cooccure2.length;o2++){
    			{
    				if(max2[o1]<cooccure2[o1][o2])
    					max2[o1]=cooccure2[o1][o2];
    			}
    		}
    	}
    	
    	
    	//计算结点权重跟边的权重，floatcoocurre矩阵对角线上为结点权重,其余为边权重；
    	float[][] floatcoocurre1=new float[size][size];
    	floatcoocurre1=countFloatCoocurre(cooccure1,max1,array1.length);
    	writeFile("floatcoocurrerelation1.txt",floatcoocurre1);    	
    	
    	float[][] floatcoocurre2=new float[size][size];
    	floatcoocurre2=countFloatCoocurre(cooccure2,max2,array2.length);
    	writeFile("floatcoocurrerelation2.txt",floatcoocurre2);    	
    	
    	
    	
    	//-------------共现矩阵,写入.txt中，作为pajek的输入数据，因此要考虑输出格式----------------------；
    	StringBuilder sbs=new StringBuilder();
    	sbs.append("*Vertices "+keyword.size()+"\r\n");               //windows下的文本文件换行符:\r\n
    	StringBuilder sbs2=new StringBuilder();

    	for(int kk=0;kk<keyword.size();kk++)
    		sbs.append(kk+1+" \""+keywordSingle.get(kk)+"\"\r\n");
    	sbs.append("*Arcs\r\n");
    	for(int o1=0;o1<floatcoocurre1.length;o1++){
    		for(int o2=o1;o2<floatcoocurre1[0].length;o2++){
    			if(o1==o2){
					sbs2.append(o1+1+" "+floatcoocurre1[o1][o2]+"\r\n");  //结点权重信息；
				}
				else{
					if(floatcoocurre1[o1][o2]> 0.0){						
						if(keydate1.get(o1).compareTo(keydate1.get(o2))<0){
							sbs.append(o1+1+" "+(o2+1)+" "+floatcoocurre1[o1][o2]+"\r\n");	
						}else{
							sbs.append(o2+1+" "+(o1+1)+" "+floatcoocurre1[o1][o2]+"\r\n");	
						}

					}				
			    }
    	    }
    	}    	
    	
    	BufferedWriter br1;
    	br1=new BufferedWriter(new FileWriter("occurancData1.txt"));   //用于生成专利技术网络；
    	br1.write(sbs.toString());
    	br1.close();
    	br1=new BufferedWriter(new FileWriter("vector1.txt"));
    	br1.write(sbs2.toString());    	
    	br1.close();
    	
    	//-------------共现矩阵,写入.txt中，作为pajek的输入数据，因此要考虑输出格式----------------------；
    	StringBuilder sbs3=new StringBuilder();
    	sbs3.append("*Vertices "+keyword.size()+"\r\n");               //windows下的文本文件换行符:\r\n
    	StringBuilder sbs4=new StringBuilder();

    	for(int kk=0;kk<keyword.size();kk++)
    		sbs3.append(kk+1+" \""+keywordSingle.get(kk)+"\"\r\n");
    	sbs3.append("*Arcs\r\n");
    	for(int o1=0;o1<floatcoocurre2.length;o1++){
    		for(int o2=o1;o2<floatcoocurre2[0].length;o2++){
    			if(o1==o2){
					sbs4.append(o1+1+" "+floatcoocurre2[o1][o2]+"\r\n");  //结点权重信息；
				}
				else{
					if(floatcoocurre2[o1][o2]> 0.0){						
						if(keydate2.get(o1).compareTo(keydate2.get(o2))<0){
							sbs3.append(o1+1+" "+(o2+1)+" "+floatcoocurre2[o1][o2]+"\r\n");	
						}else{
							sbs3.append(o2+1+" "+(o1+1)+" "+floatcoocurre2[o1][o2]+"\r\n");	
						}

					}				
			    }
    	    }
    	}    	
    	
    	BufferedWriter br2;
    	br2=new BufferedWriter(new FileWriter("occurancData2.txt"));   //用于生成专利技术网络；
    	br2.write(sbs3.toString());
    	br2.close();
    	br2=new BufferedWriter(new FileWriter("vector2.txt"));
    	br2.write(sbs4.toString());    	
    	br2.close();
    	
    
    	
    	//-----------------求结节强度----------------------------------
    	List<Float> strength1=new ArrayList<Float>();
    	strength1=countNodeStrength(floatcoocurre1);
    	    	    	
//    	for(Float ff:strength1)
//    		System.out.println(ff.toString()); 
    	
    	List<Float> strength2=new ArrayList<Float>();
    	strength2=countNodeStrength(floatcoocurre2);
    	    	    	
//    	for(Float ff:strength2)
//    		System.out.println(ff.toString()); 
    	
    	
    	//-------------分析某一项技术节点的技术洞---------------------；
    	List<DependencyEgo> egolist1=new ArrayList<DependencyEgo>();
    	egolist1=createEgoNetwork(floatcoocurre1,keydate1);  //计算专利技术网络中每个技术节点的依赖网络，包括技术本身、技术依赖的技术和技术被依赖的技术；

    	List<DependencyEgo> egolist2=new ArrayList<DependencyEgo>();
    	egolist2=createEgoNetwork(floatcoocurre2,keydate2);  //计算专利技术网络中每个技术节点的依赖网络，包括技术本身、技术依赖的技术和技术被依赖的技术；

    	
    	StringBuilder sbbb1=new StringBuilder();
    	File percentFile1=new File("percent1.txt");  //每条边权重占节点强度的比例；
    	if(!percentFile1.exists())
    		percentFile1.createNewFile();
    	BufferedWriter bww1=new BufferedWriter(new FileWriter(percentFile1));
    	String path3="egoNetwork1";
    	File directory1=new File(path3);
    	if(!directory1.exists())
    		directory1.mkdir();
    	sbbb1=countTechnologyHole(egolist1, keywordSingle, floatcoocurre1, keydate1, strength1,path3);
	
    	bww1.write(sbbb1.toString());
    	bww1.close();
    	    	
    	
    	StringBuilder sbbb2=new StringBuilder();
    	File percentFile2=new File("percent2.txt");  //每条边权重占节点强度的比例；
    	if(!percentFile2.exists())
    		percentFile2.createNewFile();
    	BufferedWriter bww2=new BufferedWriter(new FileWriter(percentFile2));
    	String path4="egoNetwork2";
    	File directory2=new File(path4);
    	if(!directory2.exists())
    		directory2.mkdir();
    	sbbb2=countTechnologyHole(egolist2, keywordSingle, floatcoocurre2, keydate2, strength2,path4);
	
    	bww2.write(sbbb2.toString());
    	bww2.close();
	}
	
	
	private static StringBuilder countTechnologyHole(List<DependencyEgo> egolist,List<String> keywordSingle,float[][] floatcoocurre,List<String> keydate,List<Float> strength,String path) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder sbbb=new StringBuilder();
    	for(DependencyEgo ego:egolist){
    		List<KEYWORD> consNet=new ArrayList<KEYWORD>();  //<技术id，依赖技术i，id受i的约束>
    		float constrain=0.0f;          //每个技术的总限制度；
    		int id=ego.getTechnology();      //专利技术网络中的一个节点；
    		List<Integer> adjacent=ego.getAdjacent();
    		Set<Integer> input=ego.getTechnologyInput();
    		Set<Integer> output=ego.getTechnologyOutput();
    		
    		if(adjacent.size()==0)
    			continue;
    		
    		String name=path+"\\"+keywordSingle.get(id)+".txt";
    		String sub="/";    		//文件夹命名不能含:|*|?|\"|<|>|\\|/这些符号;
    		if(name.contains(sub))
    			name=name.replaceAll(sub,"");    		
    		File file=new File(name);
    		if(!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    		
    		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
    		StringBuilder sbb=new StringBuilder();
    		sbb.append("*Vertices  "+(adjacent.size()+1)+"\r\n");   //依赖网络共有节点数；
    		int num=1;
    		for(Integer wordid:adjacent){
    			sbb.append((num++)+" \""+keywordSingle.get(wordid)+"\"\r\n");  //节点编号及节点名称；
    		}
    		sbb.append(num+" \""+keywordSingle.get(id)+"\"\r\n");
    		sbb.append("*Arcs\r\n");
    		
    		for(int i=0;i<adjacent.size();i++){
    			float p=(floatcoocurre[id][adjacent.get(i).intValue()]+floatcoocurre[adjacent.get(i).intValue()][id])/strength.get(id);
    			sbbb.append(keywordSingle.get(id)+"、"+keywordSingle.get(adjacent.get(i).intValue())+"间依赖关系占"+keywordSingle.get(id)+"强度的比例："+p);
    			sbbb.append("\r\n");
    			float middle=0.0f;
    			for(int j=0;j<adjacent.size();j++){
    				if(j!=i){
    					middle+=((floatcoocurre[adjacent.get(i).intValue()][adjacent.get(j).intValue()]+floatcoocurre[adjacent.get(j).intValue()][adjacent.get(i).intValue()])/strength.get(adjacent.get(j).intValue()))*((floatcoocurre[id][adjacent.get(j).intValue()]+floatcoocurre[adjacent.get(j).intValue()][id])/strength.get(id));
    				}    				
    			}
    			float result=p+middle;
    			    			
    			if(input.contains(adjacent.get(i))){
    				if(consNet.size()>0){
    					for(KEYWORD kk:consNet){
    						int u1=Integer.parseInt(kk.getK1());
    						int u2=Integer.parseInt(kk.getK2());
    						if(u1==id){
    							if(floatcoocurre[u2][adjacent.get(i)]>0.0){
    								if(keydate.get(u2).compareTo(keydate.get(adjacent.get(i)))<0){
        								sbb.append((readOrderId(u2,adjacent)+1)+" "+(i+1)+" "+floatcoocurre[u2][adjacent.get(i)]+"\r\n");
    								}else{
        								sbb.append((i+1)+" "+(readOrderId(u2,adjacent)+1)+" "+floatcoocurre[u2][adjacent.get(i)]+"\r\n");
    								}
    							}
//    							}
    						}else if(u2==id){
    							if(floatcoocurre[u1][adjacent.get(i)]>0.0){
    								if(keydate.get(u1).compareTo(keydate.get(adjacent.get(i)))<0){
        								sbb.append((readOrderId(u1,adjacent)+1)+" "+(i+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}else{
        								sbb.append((i+1)+" "+(readOrderId(u1,adjacent)+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}
    							}
    						}  
        				}
    				}
    				KEYWORD temp=new KEYWORD(adjacent.get(i).toString(),String.valueOf(id),result*result);
    				consNet.add(temp);
    				sbb.append((i+1)+" "+(adjacent.size()+1)+" "+(float)Math.round((result*result)*10000)/10000+"\r\n");    				
    				
    			}else if(output.contains(adjacent.get(i))){
    				if(consNet.size()>0){
    					for(KEYWORD kk:consNet){
    						int u1=Integer.parseInt(kk.getK1());
    						int u2=Integer.parseInt(kk.getK2());
    						if(u1==id){
    							if(floatcoocurre[u2][adjacent.get(i)]>0.0){
    								if(keydate.get(u2).compareTo(keydate.get(adjacent.get(i)))<0){
        								sbb.append((readOrderId(u2,adjacent)+1)+" "+(i+1)+" "+floatcoocurre[u2][adjacent.get(i)]+"\r\n");
    								}else{
        								sbb.append((i+1)+" "+(readOrderId(u2,adjacent)+1)+" "+floatcoocurre[u2][adjacent.get(i)]+"\r\n");
    								}
    							}
    						}else if(u2==id){
    							if(floatcoocurre[u1][adjacent.get(i)]>0.0){
    								if(keydate.get(u1).compareTo(keydate.get(adjacent.get(i)))<0){
        								sbb.append((readOrderId(u1,adjacent)+1)+" "+(i+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}else{
        								sbb.append((i+1)+" "+(readOrderId(u1,adjacent)+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}
    							}				
    						}  
        				}
    				}
    				KEYWORD temp=new KEYWORD(String.valueOf(id),adjacent.get(i).toString(),result*result);
    				consNet.add(temp);
    				sbb.append((adjacent.size()+1)+" "+(i+1)+" "+(float)Math.round((result*result)*10000)/10000+"\r\n");    				
    			}   
    			
    			constrain+=result*result;
    		}
//    		System.out.println(keywordSingle.get(id)+"技术的总的约束力为："+constrain);  
    		    		
    		bw.write(sbb.toString());
    		bw.close();
    		
    	}    
		
		
		
		return sbbb;
	}


	private static int[][] countCooccure(int[][] array) {
		// TODO Auto-generated method stub
		 int size=array[0].length;
    	 int[][] cooccure=new int[size][size];
    	 
    	 //初始化共现矩阵
    	 for(int k1=0;k1<cooccure.length;k1++)
    		 for(int k2=0;k2<cooccure[0].length;k2++)
    			 cooccure[k1][k2]=0;
    	 
    	 for(int i=0;i<array.length;i++){
    		 for(int j=0;j<array[0].length;j++){
    			 for(int k=j;k<array[0].length;k++){
    				 if(j!=k){
    					 if(array[i][j]==1&&array[i][k]==1)
        					 cooccure[j][k]+=1; 
    				 }
    				 else{
    					 if(array[i][j]==1)
    						 cooccure[j][k]+=1;
    				 }
    				 //上述过程可以合并为 一个，而不加分支  				 
    			 }
    		 } 
    	 }  
    	 
//    	for(int o=0;o<size;o++){
//    		for(int p=0;p<size;p++)
//    			System.out.print(cooccure[o][p]+"  ");
//    		System.out.println();
//    	}    
		return cooccure;
	}
	
	
	/*计算技术的申请日 or技术在专利中首次出现的时间 	 
	 * 
	 */
	private static List<String> countKeyDate(int[][] array,List<String> datelist) {
		// TODO Auto-generated method stub		
   	 List<String> keydate=new ArrayList<String>();
   	 for(int col=0;col<array[0].length;col++){    
   		String tt=null;    		 
   		 for(int row=0;row<array.length;row++){
   			 if(array[row][col]==1){
   				 if(null==tt||"".equals(tt))
   					 tt=String.valueOf(row);
   				 else tt=tt+","+String.valueOf(row);
   			 }
   		 }
   		 //求出技术出现的专利集中专利申请日最早的
//   	 System.out.println(tt);  
   		String mindate=readKeywordDate(tt,datelist);
//      System.out.println(mindate);
   		keydate.add(mindate);   		
   		 		
   	 }   	 
   	 
//   	 System.out.println("--------------");
//   	 //专利技术出现日期
//   	 for(String date:keydate)
//   		 System.out.println(date); 
   			
		return keydate;
	}

	private static int readOrderId(int t,List<Integer> adjacent){
		int r=-1;
		for(int i=0;i<adjacent.size();i++){
			if(adjacent.get(i).intValue()==t){
				r=i;
			}
		}
		return r;
	}
	
	private static List<String> readKeywordsSingle(List<String> keyword) {
		// TODO Auto-generated method stub
		List<String> keywordSingle=null;
		
		keywordSingle=new ArrayList<String>();
		
		for(String word:keyword){
			if(word.contains("=")){
				String[] temp=word.split("=");
				keywordSingle.add(temp[0]);
			}
			else{
				keywordSingle.add(word);
			}
		}
		
		return keywordSingle;
	}

	/*计算结点强度
	 * 
	 */
	private static List<Float> countNodeStrength(float[][] floatcoocurre) {
		// TODO Auto-generated method stub
		List<Float> strength=new ArrayList<Float>();
		for(int k1=0;k1<floatcoocurre.length;k1++){
    		float temp=0;
    		for(int k2=floatcoocurre.length-1;k2>k1;k2--){
    			temp+=floatcoocurre[k1][k2];
    		}
    		for(int k3=0;k3<k1;k3++){
    			temp+=floatcoocurre[k3][k1];
    		}
    		Float f=new Float(temp);
    		strength.add((float)Math.round(f*100)/100);
    	}    	
		return strength;
	}

	/*计算边权重及结点权重
	 * 
	 */
	private static float[][] countFloatCoocurre(int[][] cooccure,int[] max,int number) {
		// TODO Auto-generated method stub
		float[][] floatcoocurre=new float[cooccure.length][cooccure[0].length];
		for(int o1=0;o1<cooccure.length;o1++){
    		for(int o2=o1;o2<cooccure[0].length;o2++){   //上三角矩阵存储了数据；
    			{
    				if(o1==o2){
    					float temp1=(float)cooccure[o1][o2]/number;   //number=185，为专利文档的个数；
    					floatcoocurre[o1][o2]=(float)Math.round(temp1*100)/100;
    				}
    				else{
    					if(max[o1]!=0){
//    						float temp2=(float)cooccure[o1][o2]/max[o1];          //利用每行矩阵除最大值计算边权重；
    						float temp2=(float)(cooccure[o1][o2]/(Math.sqrt(cooccure[o1][o1]*cooccure[o2][o2])));  //利用salton计算边权重；
    						floatcoocurre[o1][o2]=(float)Math.round(temp2*100)/100;
    						}
    					else continue;
    				}    					
    			}
    		}
    	}   	
		
		return floatcoocurre;
	}
	
	/*专利patent中是否存在技术关键词technology
	 * @patent：专利文档
	 * @technology:技术关键词
	 */
	private static int checkWordInPatent(String patent, String technology) {
		// TODO Auto-generated method stub
		int flag=0;
		if(technology.contains("=")){
			String[] temp=technology.split("=|、");
			for(int k=1;k<temp.length;k++){
				if(patent.contains(temp[k])){
					flag=1;
					break;
				}
			}
		}
		else{	    				
				if(patent.contains(technology))
					flag=1;
		} 		
		return flag;
	}

	/*读专利文本内容
	 * path:源文件路径
	 * filename:源文件名称列表
	 */
	private static List<String> readSource(String path,String[] filename) throws IOException {
		// TODO Auto-generated method stub
		List<String> text=new ArrayList<String>();
		
    	StringBuilder sb;
    	BufferedReader br;
    	String tu="";
    	    	
    	 for(int m=0;m<filename.length;m++){
    		 sb=new StringBuilder();
 	    	 br=new BufferedReader(new FileReader(path+"\\"+filename[m]));
 	    	while((tu=br.readLine())!=null)
			{
					if(tu.isEmpty())
						continue;
					sb.append(tu.trim());
					
			}		
 	    	br.close();
 	    	text.add(sb.toString());
    	 }
		
		return text;
	}
	
	
	/*读专利文本内容
	 * path:源文件路径
	 * size:关键词个数
	 */
	private static String readSource(String path,String filename) throws IOException {
		// TODO Auto-generated method stub
		String text=null;		
    	StringBuilder sb;
    	BufferedReader br;
    	String tu="";
    	    	
    	sb=new StringBuilder();
 	    br=new BufferedReader(new FileReader(path+"\\"+filename));
 	    while((tu=br.readLine())!=null)
		{
				if(tu.isEmpty())
					continue;
				sb.append(tu.trim());					
		}		
 	    br.close();
 	    text=sb.toString();
     	
		return text;
	}
	
	
		
	/*读取技术关键词列表
	 * @path：关键词文档文件名；
	 */
	public static List<String> readKeywords(String path) throws IOException{
		
		List<String> keyword=new ArrayList<String>();//存储技术关键词；
		
		BufferedReader bb; 
    	bb = new BufferedReader(new FileReader(path));
		String t="";
		while((t=bb.readLine())!=null){
    		if(t.isEmpty())
    			continue;
    		keyword.add(t.trim());
    	}
		return keyword;
	}
	
	/*将数据写入文件中
	 *@filename:数据写入的文件名；
	 *@array:要写入的数据；数据形式为int数组；
	 */
	public static void writeFile(String filename, int[][] array)
			throws IOException {
		File result = new File(filename);
		if (!result.exists())
			result.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));
		StringBuilder sbb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++)
				sbb.append(array[i][j] + "  ");
			sbb.append("\n");
		}
		bw.write(sbb.toString());
		bw.close();
	}

	/*将数据写入文件中
	 *@filename:数据写入的文件名；
	 *@array:要写入的数据；数据形式为float数组；
	 */
	public static void writeFile(String filename, float[][] array)
			throws IOException {
		File result = new File(filename);
		if (!result.exists())
			result.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));
		StringBuilder sbb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++)
				sbb.append(array[i][j] + "  ");
			sbb.append("\n");
		}
		bw.write(sbb.toString());
		bw.close();
	}
		
	public static String readKeywordDate(String file, List<String> datelist){
		String date=null;
		
		//读入专利及申请日
		if(file!=null){                     //专利集中无该关键词
			if(file.contains(",")){
				String[] datel=file.split(",");
				String min=datelist.get(Integer.parseInt(datel[0]));
				for(int m=1;m<datel.length;m++){
					int tm=Integer.parseInt(datel[m]);
					if(datelist.get(tm).compareTo(min)<0)
						min=datelist.get(tm);		
				}
				date=min;
			}else{
				date=datelist.get(Integer.parseInt(file));
			}		
			
		}		
		
		return date;
	}
	public static List<DependencyEgo> createEgoNetwork(float[][] floatcoocurre,List<String> keydate){
		List<DependencyEgo> egolist=null;
		
		egolist=new ArrayList<DependencyEgo>();
		for(int i=0;i<floatcoocurre.length;i++){
			DependencyEgo ego=new DependencyEgo();
			ego.setTechnology(i);
			egolist.add(ego);
		}
		
		for(int o1=0;o1<floatcoocurre.length;o1++){				
    		for(int o2=o1+1;o2<floatcoocurre[0].length;o2++){    			
    			if(floatcoocurre[o1][o2]> 0.0){	
					if(keydate.get(o1).compareTo(keydate.get(o2))<0){
						//技术keyword.get(o1)指向技术keyword.get(o2)
						Set<Integer> temp1=egolist.get(o1).getTechnologyOutput();
						Set<Integer> temp2=egolist.get(o2).getTechnologyInput();						
						List<Integer> temp3=egolist.get(o1).getAdjacent();
						List<Integer> temp4=egolist.get(o2).getAdjacent();
						temp1.add(Integer.valueOf(o2));	
						temp2.add(Integer.valueOf(o1));	
						temp3.add(Integer.valueOf(o2));
						temp4.add(Integer.valueOf(o1));						
					}else{
						//技术keyword.get(o2)指向技术keyword.get(o1)						
						Set<Integer> temp1=egolist.get(o2).getTechnologyOutput();
						Set<Integer> temp2=egolist.get(o1).getTechnologyInput();
						List<Integer> temp3=egolist.get(o2).getAdjacent();
						List<Integer> temp4=egolist.get(o1).getAdjacent();
						temp1.add(Integer.valueOf(o1));		
						temp2.add(Integer.valueOf(o2));		
						temp3.add(Integer.valueOf(o1));
						temp4.add(Integer.valueOf(o2));	
					}
				}	    			
    	    }
    	}
		return egolist;		
	}


}
