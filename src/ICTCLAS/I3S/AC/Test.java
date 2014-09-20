package ICTCLAS.I3S.AC;

import java.io.UnsupportedEncodingException;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void testProcessParagraph() {
		try
		{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			//�ִ�������·��
			String argu = ".";
			
			// init
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}
			else { 
				System.out.println("Init Succeed!");
			}

			String sInput="������س�Ů���������ܹ���ϲ���������б����ڷǵ�֮��";
			
			
			// import user dict
			int nCount = 0;
			String usrdir = "usrdir.txt"; //�û��ֵ�·��
			byte[] usrdirb = usrdir.getBytes();
			//��һ������Ϊ�û��ֵ�·�����ڶ�������Ϊ�û��ֵ�ı�������(0:type unknown;1:ASCII��;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
			nCount = testICTCLAS50.ICTCLAS_ImportUserDictFile(usrdirb, 3);
			System.out.println("�����û��ʸ���"+ nCount);
			nCount = 0;
			
			// process paragraph
			byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0, 1);
			System.out.println(nativeBytes.length);
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			System.out.println("The result is ��" + nativeStr);

			// exit and release resources
			testICTCLAS50.ICTCLAS_Exit();
		}
		catch (Exception ex)
		{
		}
	}
	
	public void testProcessFile() {
		try
		{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();

			String argu = ".";
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}

			// input file name
			String Inputfilename = "test.txt";
			byte[] Inputfilenameb = Inputfilename.getBytes();

			// output file name
			String Outputfilename = "test_result.txt";
			byte[] Outputfilenameb = Outputfilename.getBytes();

			//�ļ��ִ�(��һ������Ϊ�����ļ�����,�ڶ�������Ϊ�ļ���������,����������Ϊ�Ƿ��Ǵ��Լ�1 yes,0 no,���ĸ�����Ϊ����ļ���)
			testICTCLAS50.ICTCLAS_FileProcess(Inputfilenameb, 2, 2,Outputfilenameb);
		}catch (Exception ex) {}
	}
	
	public void test() {
			try{ 
				ICTCLAS50 testICTCLAS50 = new ICTCLAS50();

				//init 
				String argu="";
				if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
					System.out.println("Init Fail!");
					return ;
				}
				System.out.println("Init Success!");
				
				// set Pos map
				int ICT_POS_MAP_FIRST = 1;
				int ICT_POS_MAP_SECOND = 2; 
				int PKU_POS_MAP_SECOND = 3;
				int PKU_POS_MAP_FIRST = 4;

				int nPOSmap = ICT_POS_MAP_FIRST;

				testICTCLAS50.ICTCLAS_SetPOSmap(nPOSmap);
	
				//analysis
				String sInput= "������س�Ů���������ܹ���ϲ���������б����ڷǵ�֮��";
				byte[] nativeBytes = testICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0,1);
				String nativeStr = new String(nativeBytes,0,nativeBytes.length,"GB2312");
		    	System.out.println(nativeStr);
				testICTCLAS50.ICTCLAS_Exit();
		} catch(Exception ex)
		{
		}
	}

}
