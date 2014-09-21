
package ICTCLAS.I3S.AC;


public class Test
{

    public static void main( String[] args )
    {
        ICTCLAS50Util.processFileWithoutUsrDict( "test.txt", "foo.txt" );
    }

    private static void run()
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

        String sInput = "点击下载超女纪敏佳深受观众喜爱。禽流感爆发在非典之后。";

        // import user dict
        int nCount = 0;
        String usrdir = "usrdir.txt"; // 用户字典路径
        byte[] usrdirb = usrdir.getBytes();
        // 第一个参数为用户字典路径，第二个参数为用户字典的编码类型(0:type unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
        nCount = ictclas.ICTCLAS_ImportUserDictFile( usrdirb, 3 );
        System.out.println( "导入用户词个数" + nCount );
        nCount = 0;

        // process paragraph
        byte nativeBytes[] = ictclas.ICTCLAS_ParagraphProcess( sInput.getBytes( "GB2312" ), 0, 1 );
        System.out.println( nativeBytes.length );
        String nativeStr = new String( nativeBytes, 0, nativeBytes.length, "GB2312" );
        System.out.println( "The result is ：" + nativeStr );

        // exit and release resources
        ictclas.ICTCLAS_Exit();
        }
        catch( Exception e ){}
    }
}
