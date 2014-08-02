package com.patent.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCueWord {
	String path="cueword.txt";
	/*
	 * ¶ÁÈ¡ÏßË÷´Ê
	 */
	public ArrayList<String> readWord(String path){
		ArrayList<String> cue=new ArrayList<String>();
		StringBuilder r=new StringBuilder();
		try {
    		FileReader file = new FileReader(path);
    		BufferedReader br=new BufferedReader(file);
    		String inline;
    		while((inline=br.readLine())!=null)
			{
				 cue.add(inline);
			}    
	   } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}    		
    	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return cue;
	}

}
