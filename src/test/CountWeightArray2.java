package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.read.biff.BiffException;

import com.patent.entity.DependencyEgo;
import com.patent.entity.KEYWORD;
import com.patent.read.ReadPatentDate;


public class CountWeightArray2 {
	public static void main(String args[]) throws IOException{
		
		String path="sourcefile";
		String path2="keywordfile.txt";
		
		List<String> keyword=new ArrayList<String>();//存储技术关键词；
		List<String> keywordSingle=new ArrayList<String>(); //去除同义词存储关键词；
		List<String> textlist=new ArrayList<String>(); //存储专利文本内容；
		Map<String,String> patentdate=new HashMap<String,String>(); //成对存储专利、申请日;
		List<String> datelist=new ArrayList<String>();//存储专利申请日
		List<String> keydate=new ArrayList<String>();//存储关键词申请日
				
		//读入专利文档内容
		File directory=new File(path);
		String[] filename=directory.list();
		textlist=readSource(path,filename,keyword.size());
		
		//读入关键词列表
		keyword=readKeywords(path2);
		keywordSingle=readKeywordsSingle(keyword);
		
		//数组array，其中array[i][j]表示第j个关键词是否在第i篇专利文档中出现，出现为值1，不出现为值0；
    	int[][] array=new int[filename.length][keyword.size()];
    	for(int i=0;i<array.length;i++)
    		for(int j=0;j<array[0].length;j++)
    			array[i][j]=0;  
    	
    	for(int i=0;i<textlist.size();i++){
    		for(int j=0;j<keyword.size();j++){
    			array[i][j]=checkWordInPatent(textlist.get(i),keyword.get(j));
    		}    		
    	}
    	
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
    	 
    	 //计算技术的申请日 or技术在专利中首次出现的时间 	 
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
//    		 System.out.println(tt);
    		 String mindate=readKeywordDate(tt,datelist);
//    		 System.out.println(mindate);
    		 keydate.add(mindate);
    	 }
    	 
    	 
//    	 System.out.println("--------------");
//    	 //专利技术出现日期
//    	 for(String date:keydate)
//    		 System.out.println(date);
    	 
    	 
//    	//绘制某一技术关键词的生长曲线；
//    	 for(int mm=0;mm<filename.length;mm++){
//    		 if(array[mm][210]>0)
//    			 System.out.println(datelist.get(mm)+","+array[mm][210]); //外延
//    	 }
//    	
   
    	 
    	 //--------------------共现矩阵------------------------------
    	 int size=keyword.size();
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
    	 
//    	writeFile("result.txt",array);
//    	writeFile("coocurre.txt",cooccure);
    	
    	//共现矩阵每行的最大值
    	int[] max=new int[cooccure.length];
    	for(int o1=0;o1<cooccure.length;o1++){
    		max[o1]=cooccure[o1][0];
    		for(int o2=o1+1;o2<cooccure.length;o2++){
    			{
    				if(max[o1]<cooccure[o1][o2])
    					max[o1]=cooccure[o1][o2];
    			}
    		}
    	}
    	
//    	for(int i=0;i<max.length;i++)
//		   System.out.println(max[i]);
    	
    	//计算结点权重跟边的权重，floatcoocurre矩阵对角线上为结点权重,其余为边权重；
    	float[][] floatcoocurre=new float[size][size];
    	floatcoocurre=countFloatCoocurre(cooccure,max,array.length);
    	writeFile("floatcoocurrerelation.txt",floatcoocurre);    	
    	
    	//-----------------------阈值为0的共现矩阵,写入.txt中，作为pajek的输入数据，因此要考虑输出格式--------------------------------------；
    	StringBuilder sbs=new StringBuilder();
    	sbs.append("*Vertices "+keyword.size()+"\r\n");               //windows下的文本文件换行符:\r\n
    	StringBuilder sbs2=new StringBuilder();

    	for(int kk=0;kk<keyword.size();kk++)
    		sbs.append(kk+1+" \""+keywordSingle.get(kk)+","+keydate.get(kk)+"\"\r\n");
//    		sbs.append(kk+1+" \""+keywordSingle.get(kk)+"\"\r\n");
//    	sbs.append("*Edges\n");
    	sbs.append("*Arcs\r\n");
    	for(int o1=0;o1<floatcoocurre.length;o1++){
    		for(int o2=o1;o2<floatcoocurre[0].length;o2++){
    			if(o1==o2){
					sbs2.append(o1+1+" "+floatcoocurre[o1][o2]+"\r\n");  //结点权重信息；
				}
				else{
					if(floatcoocurre[o1][o2]> 0.0){						
//						sbs.append(o1+1+" "+(o2+1)+" "+floatcoocurre[o1][o2]+"\r\n");	
						if(keydate.get(o1).compareTo(keydate.get(o2))<0){
							sbs.append(o1+1+" "+(o2+1)+" "+floatcoocurre[o1][o2]+"\r\n");	
						}else{
							sbs.append(o2+1+" "+(o1+1)+" "+floatcoocurre[o1][o2]+"\r\n");	
						}

					}				
			    }
    	    }
    	}
    	
    	
    	BufferedWriter br1;
    	br1=new BufferedWriter(new FileWriter("occurancData.txt"));   //用于生成专利技术网络；
    	br1.write(sbs.toString());
    	br1.close();
    	br1=new BufferedWriter(new FileWriter("vector.txt"));
    	br1.write(sbs2.toString());    	
    	br1.close();
    	
    	//--------------------------------------------------------- 
//    	//阈值为P的共现矩阵；
//    	float p=0.2f;           //p为0.0
//    	List<KEYWORD> keywordlist=new ArrayList<KEYWORD>();    	
//    	
//    	for(int o1=0;o1<floatcoocurre.length;o1++){
//    		for(int o2=o1;o2<floatcoocurre[0].length;o2++){   
//    			if(floatcoocurre[o1][o2]> p){       
//    				KEYWORD word=new KEYWORD(keyword.get(o1),keyword.get(o2),floatcoocurre[o1][o2]);
//    				keywordlist.add(word);
//			    }
//    	    }
//    	}    	
//    	
//    	for(KEYWORD word:keywordlist)
//    	   System.out.println(word.getK1()+";"+word.getK2()+";"+word.getValue());
//
//    	Set<String> word=new HashSet<String>();
//    	for(KEYWORD key:keywordlist){
//    		word.add(key.getK1());
//    		word.add(key.getK2());    		
//    	}    	
//    	System.out.println("------------------------"+word.toString());
//    	    	   	
//    	StringBuilder sbKey=new StringBuilder();
//    	sbKey.append("*Vertices "+word.size()+"\n");
//    	int i=0;
//    	for(String k:word){
//    		sbKey.append((++i)+" "+k+"\n");
//    	}
//    	sbKey.append("*Edges\n");
//    	for(KEYWORD key:keywordlist){
//    		sbKey.append(key.getK1()+" "+key.getK2()+" "+key.getValue());
//    	}
//    	
//    	System.out.println(sbKey.toString());
//    	System.out.println("----------------------");
//    	Iterator it=word.iterator();
//    	int j=1;
//    	String s=sbKey.toString();
//    	while(it.hasNext()){
//    		s.replaceAll(it.next().toString(), Integer.toString(j++));
//    	}
//    	System.out.println(s); 	
    	
    	//-----------------求结节强度----------------------------------
    	List<Float> strength=new ArrayList<Float>();
    	strength=countNodeStrength(floatcoocurre);
    	
//    	for(Float ff:strength)
//    		System.out.println(ff.toString()); 
    	
    	//-------------分析某一项技术节点的技术洞---------------------；
    	List<DependencyEgo> egolist=new ArrayList<DependencyEgo>();
    	egolist=createEgoNetwork(floatcoocurre,keydate);  //计算专利技术网络中每个技术节点的依赖网络，包括技术本身、技术依赖的技术和技术被依赖的技术；
//    	for(DependencyEgo ego:egolist){
//    		System.out.println(keywordSingle.get(ego.getTechnology())+"依赖的技术有：");
//    		System.out.print("指向该技术的技术集合有：");
//    		for(Integer input:ego.getTechnologyInput())
//    			System.out.print(keywordSingle.get(input.intValue())+" ");
//    		System.out.println();
//    		System.out.print("该技术指向的技术集合有：");
//    		for(Integer output:ego.getTechnologyOutput())
//    			System.out.print(keywordSingle.get(output.intValue())+" ");
//    		System.out.println();
//    		System.out.println("该技术依赖的技术有：");
//    		for(Integer t:ego.getAdjacent())
//    			System.out.print(t.intValue()+"、");
//    		System.out.println();
//    	}    	
    	
    	StringBuilder sbbb=new StringBuilder();
    	File percentFile=new File("percent.txt");  //每条边权重占节点强度的比例；
    	if(!percentFile.exists())
    		percentFile.createNewFile();
    	BufferedWriter bww=new BufferedWriter(new FileWriter(percentFile));

    	
    	File directoryy=new File("egoNetwork");
    	if(!directoryy.exists())
    		directoryy.mkdir();
    	for(DependencyEgo ego:egolist){
    		List<KEYWORD> consNet=new ArrayList<KEYWORD>();  //<技术id，依赖技术i，id受i的约束>
    		float constrain=0.0f;          //每个技术的总限制度；
    		int id=ego.getTechnology();      //专利技术网络中的一个节点；
    		List<Integer> adjacent=ego.getAdjacent();
    		Set<Integer> input=ego.getTechnologyInput();
    		Set<Integer> output=ego.getTechnologyOutput();
    		
    		String name="egoNetwork\\"+keywordSingle.get(id)+".txt";
    		String sub="/";    		//文件夹命名不能含:|*|?|\"|<|>|\\|/这些符号;
    		if(name.contains(sub))
    			name=name.replaceAll(sub,"");    		
    		File file=new File(name);
    		if(!file.exists())
    			file.createNewFile();    		
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
//    							if(floatcoocurre[adjacent.get(i)][u2]>0.0){
//    								sbb.append((i+1)+" "+(readOrderId(u2,adjacent)+1)+" "+floatcoocurre[adjacent.get(i)][u2]+"\r\n");
//    							}
    						}else if(u2==id){
    							if(floatcoocurre[u1][adjacent.get(i)]>0.0){
    								if(keydate.get(u1).compareTo(keydate.get(adjacent.get(i)))<0){
        								sbb.append((readOrderId(u1,adjacent)+1)+" "+(i+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}else{
        								sbb.append((i+1)+" "+(readOrderId(u1,adjacent)+1)+" "+floatcoocurre[u1][adjacent.get(i)]+"\r\n");
    								}
    							}
//    							if(floatcoocurre[adjacent.get(i)][u1]>0.0){
//    								sbb.append((i+1)+" "+(readOrderId(u1,adjacent)+1)+" "+floatcoocurre[adjacent.get(i)][u1]+"\r\n");
//    							}
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
    		System.out.println(keywordSingle.get(id)+"技术的总的约束力为："+constrain);  
    		    		
    		bw.write(sbb.toString());
    		bw.close();
    		
    	}    	
    	bww.write(sbbb.toString());
    	bww.close();
    	
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
	 * size:关键词个数
	 */
	private static List<String> readSource(String path,String[] filename,int size) throws IOException {
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


