
package ICTCLAS.I3S.AC;

import java.io.UnsupportedEncodingException;

/**
 * static methods used for invoking the functions of ICTCLAS50
 */
public class ICTCLAS50Util
{

    public static final int ENCODE_TYPE_UNKNOW = 0;
    public static final int ENCODE_TYPE_ASCII = 1;
    public static final int ENCODE_TYPE_GB = 2; // gb2312, GBK, gb18030
    public static final int ENCODE_TYPE_UTF8 = 3;
    public static final int ENCODE_TYPE_BIG5 = 4;

    public static String processParagraphWithoutUsrDict( String input, int encodeType, boolean posTagged )
    {
        String retval = null;

        try
        {
            ICTCLAS50 ictclas = new ICTCLAS50();

            String argu = ".";
            if( ictclas.ICTCLAS_Init( argu.getBytes( "GB2312" ) ) == false )
            {
                System.out.println( "Init Fail!" );
                return retval;
            }

            // process paragraph
            byte nativeBytes[] = ictclas.ICTCLAS_ParagraphProcess( input.getBytes( "GB2312" ), encodeType, ( posTagged ? 1 : 0 ) );
//            System.out.println( nativeBytes.length );
            retval = new String( nativeBytes, 0, nativeBytes.length, "GB2312" );
//            System.out.println( "The result is ：" + nativeStr );

            // exit and release resources
            ictclas.ICTCLAS_Exit();
        }
        catch( Exception ex )
        {
        }

        return retval;
    }

    public static void processParagraphWithUsrDict( String input, String usrDictPath )
    {
        try
        {
            ICTCLAS50 ictclas = new ICTCLAS50();
            // 分词所需库的路径
            String argu = ".";

            // init
            if( ictclas.ICTCLAS_Init( argu.getBytes( "GB2312" ) ) == false )
            {
                System.out.println( "Init Fail!" );
                return;
            }

            // import user dict
            // 第一个参数为用户字典路径
            // 第二个参数为用户字典的编码类型: ENCODE_TYPE_***
            int nCount = ictclas.ICTCLAS_ImportUserDictFile( usrDictPath.getBytes(), ENCODE_TYPE_UTF8 );
//            System.out.println( "导入用户词个数" + nCount );

            // process paragraph
            byte nativeBytes[] = ictclas.ICTCLAS_ParagraphProcess( input.getBytes( "GB2312" ), 0, 1 );
//            System.out.println( nativeBytes.length );
            String nativeStr = new String( nativeBytes, 0, nativeBytes.length, "GB2312" );
//            System.out.println( "The result is ：" + nativeStr );

            // exit and release resources
            ictclas.ICTCLAS_Exit();
        }
        catch( Exception ex )
        {
        }
    }

    // convenient way, use some default parameters
    public static void processFileWithUsrDict( String inputFileName, String outFileName, String usrDictPath )
    {
        processFileWithUsrDict( inputFileName, outFileName, 0, usrDictPath, 0, false );
    }

    /**
     * 
     * @param libPath, "." means the current directory
     * @param inputFileName
     * @param outputFileName
     * @param encodeType
     * @param usrDictPath
     * @param usrDictEncodeType
     * @param posTagged
     */

    public static void processFileWithUsrDict( String inputFileName, String outputFileName, int encodeType, 
                                               String usrDictPath, int usrDictEncodeType, boolean posTagged )
    {
        try
        {
            ICTCLAS50 ictclas = new ICTCLAS50();

            final String currentLoc = ".";
            if (ictclas.ICTCLAS_Init( currentLoc.getBytes("GB2312") ) == false )
            {
                System.out.println( "Init Fail!" );
                return;
            }

            // 第一个参数为用户字典路径
            // 第二个参数为用户字典的编码类型(0:type unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
            ictclas.ICTCLAS_ImportUserDictFile( usrDictPath.getBytes(), usrDictEncodeType );

            // 文件分词
            // 第一个参数为输入文件的名
            // 第二个参数为文件编码类型
            // 第三个参数为是否标记词性集,: 1 yes,0 no
            // 第四个参数为输出文件名
            ictclas.ICTCLAS_FileProcess(inputFileName.getBytes(), encodeType, ( posTagged ? 1 : 0 ), outputFileName.getBytes() );

            ictclas.ICTCLAS_Exit();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static void processFileWithoutUsrDict( String inputFileName, String outFileName )
    {
        processFileWithoutUsrDict( inputFileName, outFileName, 0, false );
    }

    public static void processFileWithoutUsrDict( String inputFileName, String outputFileName, int encodeType, boolean posTagged )
    {
        try
        {
            ICTCLAS50 ictclas = new ICTCLAS50();

            String currentLoc = ".";
            if (ictclas.ICTCLAS_Init( currentLoc.getBytes("GB2312")) == false)
            {
                System.out.println("Init Fail!");
                return;
            }

            ictclas.ICTCLAS_FileProcess( inputFileName.getBytes(), encodeType, ( posTagged ? 1 : 0 ), outputFileName.getBytes() );

            ictclas.ICTCLAS_Exit();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
    }

    public void test()
    {
        try
        {
            ICTCLAS50 testICTCLAS50 = new ICTCLAS50();

            // init
            String argu = "";
            if( testICTCLAS50.ICTCLAS_Init( argu.getBytes( "GB2312" ) ) == false )
            {
                System.out.println( "Init Fail!" );
                return;
            }
            System.out.println( "Init Success!" );

            // set Pos map
            int ICT_POS_MAP_FIRST = 1;
            int ICT_POS_MAP_SECOND = 2;
            int PKU_POS_MAP_SECOND = 3;
            int PKU_POS_MAP_FIRST = 4;

            int nPOSmap = ICT_POS_MAP_FIRST;

            testICTCLAS50.ICTCLAS_SetPOSmap( nPOSmap );

            // analysis
            String sInput = "点击下载超女纪敏佳深受观众喜爱。禽流感爆发在非典之后。";
            byte[] nativeBytes = testICTCLAS50.ICTCLAS_ParagraphProcess( sInput.getBytes( "GB2312" ), 0, 1 );
            String nativeStr = new String( nativeBytes, 0, nativeBytes.length, "GB2312" );
            System.out.println( nativeStr );
            testICTCLAS50.ICTCLAS_Exit();
        }
        catch( Exception ex )
        {
        }
    }
}
