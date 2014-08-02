package com.patent.entity;

public class Word {
	public String word;
	public String part_of_speech;
	public int freq;
	String document;
	public Word next;
	public Word(String str,String pos,int freq,String doc)
	{
		setWord(str);
		setPart_of_speech(pos);
		setFreq(freq);
		setDocument(doc);
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPart_of_speech() {
		return part_of_speech;
	}
	public void setPart_of_speech(String part_of_speech) {
		this.part_of_speech = part_of_speech;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
}
