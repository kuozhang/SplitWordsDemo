
package test;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ICTCLAS.I3S.AC.ICTCLAS50Util;

import com.patent.read.ReadFile;

@SuppressWarnings( "serial" )
class SplitWordsWindow extends Frame
{
    public static void popUpWindow()
    {
        new SplitWordsWindow();
    }

    private Button btn_openFile, btn_extract, btn_generateMatrix, btn_clear, btn_exit;
    private JLabel label_threshold;
    private TextField tf_threadhold;
    private TextArea ta_result;
    private int threshold = 1; // 阀值
    private static String KEYWORD_EXTRACTION = "Keyword Extraction";
    private static String OPEN_FILE = "打开文件";
    private static String INPUT_THREAD = "输入阈值:";
    private static String EXTRACT = "提取";
    private static String GENERATE_MATRIX = "生成共词矩阵";
    private static String CLEAR = "清除";
    private static String EXIT = "退出";

    // Specify the source file, participle file, and keyword file
    String sourceFilePath = "sourcefile\\sourcefile.txt";
    String participleFilePath = "participlefile\\participlefile.txt";
    String keywordFileName = "keywordfile.txt";

    File[] filelist;

    String s = "";

    private SplitWordsWindow()
    {
        super( KEYWORD_EXTRACTION );

        ta_result = new TextArea( 50, 65 );
        btn_openFile = new Button( OPEN_FILE );
        label_threshold = new JLabel( INPUT_THREAD );
        tf_threadhold = new TextField();
        btn_extract = new Button( EXTRACT );
        btn_generateMatrix = new Button( GENERATE_MATRIX );// 后期要改成生成PAJEK图；
        btn_clear = new Button( CLEAR );
        btn_exit = new Button( EXIT );
        setLayout( new FlowLayout() );

        add( btn_openFile );
        add( label_threshold );
        add( tf_threadhold );
        add( btn_extract );
        add( btn_generateMatrix );
        add( btn_clear );
        add( btn_exit );
        add( ta_result );

        btn_extract.addActionListener( new ListenerForExtract() );
        btn_openFile.addActionListener( new ListenerForOpenFile() );
        btn_generateMatrix.addActionListener( new ListenerForGenerateMatrix() );
        btn_clear.addActionListener( new ListenerForClear() );
        btn_exit.addActionListener( new ListenerForExit() );

        setSize( 500, 500 );
        setVisible( true );

        addWindowListener( new WindowListener()
        {
            @Override
            public void windowOpened( WindowEvent e ){}

            @Override
            public void windowIconified( WindowEvent e ){}

            @Override
            public void windowDeiconified( WindowEvent e ){}

            @Override
            public void windowDeactivated( WindowEvent e ){}

            // active close button
            @Override
            public void windowClosing( WindowEvent e )
            {
                dispose();
                System.exit( 0 );
            }

            @Override
            public void windowClosed( WindowEvent e ){}

            @Override
            public void windowActivated( WindowEvent e ){}
        } );
    }

    private String filterString( String str )
    {
        final String regEx = "[`~!@#$%^&*()+=|{}\\[\\]<>/？~！@#￥%……&（）——+|{}【】‘：；”“‘]";
        Pattern p = Pattern.compile( regEx );
        Matcher m = p.matcher( str );
        return m.replaceAll( "" ).trim();
    }

    private void writeToFile( File file, String content )
    {
        BufferedWriter bw = null;

        try
        {
            bw = new BufferedWriter( new FileWriter( file ) );
            bw.write( content );
            bw.flush();
            bw.close();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    private void writeToFile( String filePath, String content )
    {
        writeToFile( new File( filePath ), content );
    }

    private class ListenerForClear implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            if( e.getActionCommand().equals( CLEAR ) )
            {
                tf_threadhold.setText( null );
                ta_result.setText( null );
                threshold = 1;
            }

        }

    }
    private class ListenerForExit implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            dispose();
            System.exit( 0 );
        }
    }

    private class ListenerForExtract implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            if( e.getActionCommand().equals( EXTRACT ) )
            {

                if( filelist == null || filelist.length == 0 )
                {
                    return;
                }

                if( !tf_threadhold.getText().isEmpty() )
                {
                    threshold = Integer.valueOf( tf_threadhold.getText().trim() );// read value from text
                }

                try
                {
                    // 把多个源文件写到sourcefile\sourcefile.txt;
                    File sourceFile = new File( sourceFilePath );

                    if( !sourceFile.exists() )
                    {
                        sourceFile.createNewFile();
                    }

                    StringBuilder temp = new StringBuilder();
                    for( File file : filelist )
                    {
                        BufferedReader br = new BufferedReader( new FileReader( file ) );

                        String line;
                        while( ( line = br.readLine() ) != null )
                        {
                            temp.append( filterString( line) );

                        }

                        br.close();
                    }

                    String sourceText = temp.toString();
                    writeToFile( sourceFile, sourceText );

                    String nativeStr =
                        ICTCLAS50Util.processParagraphWithoutUsrDict( sourceText, ICTCLAS50Util.ENCODE_TYPE_UNKNOW, true );

                    // 将多个文本的切词文件写到participlefile.txt中
                    File participleFile = new File( participleFilePath );
                    if( !participleFile.exists() )
                    {
                        participleFile.createNewFile();
                    }

                    writeToFile( participleFile, nativeStr );

                    // 抽取关键词并写入keywordfile.txt中。
                    ReadFile fr = new ReadFile( sourceFile, participleFile, threshold );
                    String result = fr.KExtract().toString();
//                    System.out.println( threshold );

                    String resultFilePath = "keywordset\\";
                    for( File file : filelist )
                    {
                        resultFilePath += file.getName().replaceAll( "\\..*$", "" ) + "_";
                    }
                    resultFilePath += "keyword.txt";

                    writeToFile( resultFilePath, result );
                    ta_result.setText( result );
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    private class ListenerForGenerateMatrix implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            if( e.getActionCommand().equals( GENERATE_MATRIX ) )
            {
                ExtractCoWordArray coword = new ExtractCoWordArray();
                File keywordFile = new File( keywordFileName );
                coword.getCowordArray( filelist, keywordFile );
            }

        }

    }
    private class ListenerForOpenFile implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            if( e.getActionCommand().equals( OPEN_FILE ) )
            {
                // 打开源文件，并将选择的文件保存到文件数组中
                String path = ( "sourcefile" );
                File directory = new File( path );
                directory.mkdir();
                JFrame frame = new JFrame();
                JFileChooser jfChooser = new JFileChooser( path );
                jfChooser.setMultiSelectionEnabled( true );
                jfChooser.setDialogTitle( "选择源文件" );
                frame.add( jfChooser );
                jfChooser.showOpenDialog( frame );
                frame.setBounds( 100, 200, 400, 300 );
                frame.setVisible( true );
                filelist = jfChooser.getSelectedFiles();
                frame.setVisible( false );
                frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            }

        }

    }
}
