package test;
import ICTCLAS.I3S.AC.ICTCLAS50;

import java.io.*;
//import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.patent.read.ReadFile;


class MainFrame extends Frame implements ActionListener{
	Button open1,extract,draw,clear,quit;
	JLabel thresh;
	TextField tf;
	TextArea ta;
	ICTCLAS50 testICTCLAS50;
	FileDialog fd;
	private File SOURCEFILE;     //源文件
	private File PARTICIPLEFILE;   //切词文件
	private File KEYWORD;     //关键词
	private int threshold=1;   //阀值  
	File[] filelist;
	String s="";
	MainFrame(){
		super("keyWord Extraction");
		ta=new TextArea(50,65);
		open1=new Button("打开文件");
		thresh=new JLabel("输入阀值：");
		tf=new TextField();
		extract=new Button("提取");
		draw=new Button("生成共词矩阵");//后期要改成生成PAJEK图；
		clear=new Button("清除");
		quit=new Button("退出");
		setLayout(new FlowLayout());
		add(open1);
		add(thresh);
		add(tf);
		add(extract);
		add(draw);
		add(clear);
		add(quit);
		add(ta);
		extract.addActionListener(this);
		open1.addActionListener(this);
		draw.addActionListener(this);
		clear.addActionListener(this);
		quit.addActionListener(this);
		setSize(500,500);
		KEYWORD=new File("keywordfile.txt"); 
		testICTCLAS50=new ICTCLAS50();
		try{
				String argu=".";
				if(testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312"))==true)
			    {
					setVisible(true);
					System.out.println("init true");
			    }
				else System.out.println("init false");
			}catch(Exception ioe){}
    }
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="打开文件")
		{
			//打开源文件，并将选择的文件保存到文件数组中
			String path=("sourcefile");
			File directory=new File(path);
			directory.mkdir();
			JFrame frame=new JFrame();
			JFileChooser jfChooser=new JFileChooser(path);
			jfChooser.setMultiSelectionEnabled(true);
			jfChooser.setDialogTitle("选择源文件");
			frame.add(jfChooser);
			jfChooser.showOpenDialog(frame);
			frame.setBounds(100,200,400,300);
			frame.setVisible(true);
			filelist=jfChooser.getSelectedFiles();
			frame.setVisible(false);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//设置默认的关闭操作			
		}

		if(e.getActionCommand()=="提取")
		{
			
			if(!tf.getText().isEmpty())
			{		
				threshold=Integer.valueOf(tf.getText().trim());//read value from text
			}
				

			String result="";
			String text="";
			try{
				
				//把多个源文件写到sourcefile\sourcefile.txt;
				String s="";
				SOURCEFILE=new File("sourcefile\\sourcefile.txt");
				 if(!SOURCEFILE.exists())
				    	SOURCEFILE.createNewFile();
				FileWriter fw1=new FileWriter(SOURCEFILE);
				BufferedWriter bw1=new BufferedWriter(fw1);
				StringBuilder temp=new StringBuilder();
				for(int i=0;i<filelist.length;i++)
				{
					s=filelist[i].getName();
					FileReader fr1=new FileReader("sourcefile\\"+s);
					BufferedReader br1=new BufferedReader(fr1);			
					while((s=br1.readLine())!=null)
					{
						temp.append(StringFileter(s));
						
					}
				}
				text=temp.toString();
				bw1.write(text.toString());
				bw1.flush();
				bw1.close();
			
				//将多个文本的切词文件写到participlefile.txt中
				PARTICIPLEFILE=new File("participlefile.txt");
				if(!PARTICIPLEFILE.exists())
				   {
					  PARTICIPLEFILE.createNewFile();
				    }
				byte nativeBytes[]=testICTCLAS50.ICTCLAS_ParagraphProcess(text.getBytes("GB2312"),0, 1);
				String nativeStr=new String(nativeBytes,0,nativeBytes.length,"GB2312");
				FileWriter fw_ictcla=new FileWriter(PARTICIPLEFILE);
				BufferedWriter bw_ictcla=new BufferedWriter(fw_ictcla);
				bw_ictcla.write(nativeStr.toString());
				bw_ictcla.flush();
				 
				//抽取关键词并写入keywordfile.txt中。
		      
			   ReadFile fr=new ReadFile(SOURCEFILE,PARTICIPLEFILE,threshold);
			   System.out.println(threshold);
			   String filename;
			   if(filelist.length==1)
				   filename="keywordset\\"+filelist[0].getName()+"keyword.txt";
			   else filename="keywordset\\"+filelist.toString();				   

			   FileWriter fw=new FileWriter(filename);
			   BufferedWriter bw=new BufferedWriter(fw);
			   bw.write(fr.KExtract().toString());
			   bw.flush();
			   fw.close();
			   result=fr.KExtract().toString();
			   ta.setText(result);
			    
			   
			}catch(IOException ie){}
			catch(NullPointerException ioe){}		
			catch(IndexOutOfBoundsException iofe){}
		}
		if(e.getActionCommand()=="生成共词矩阵"){
			ExtractCoWordArray coword=new ExtractCoWordArray();
			   coword.getCowordArray(filelist, KEYWORD);
		}
		if(e.getActionCommand()=="清除")
		{
			tf.setText(null);
			ta.setText(null);
			threshold=1;
		}
		if(e.getActionCommand()=="退出"){
			testICTCLAS50.ICTCLAS_Exit();
			dispose();
			System.exit(0);
		}
	}
	private static String StringFileter(String str) {
		// TODO Auto-generated method stub
		String regEx = "[`~!@#$%^&*()+=|{}\\[\\]<>/？~！@#￥%……&（）——+|{}【】‘：；”“‘]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}

public class TestICTCLAS30{
	public static void main(String[] args){
		new MainFrame();
	}
}


