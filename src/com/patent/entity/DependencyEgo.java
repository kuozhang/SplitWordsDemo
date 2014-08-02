package com.patent.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyEgo {
	private int technology;                 //ר�����������е�ĳ���
	private Set<Integer> technologyInput;         //����technology�����ļ�������
	private Set<Integer> technologyOutput;  //��������technology�ļ������ϣ�	
	private List<Integer> adjacent;
	public DependencyEgo(){
		this.technologyInput=new HashSet<Integer>();
		this.technologyOutput=new HashSet<Integer>();		
		this.adjacent=new ArrayList<Integer>();
	}
	
	public int getTechnology() {
		return technology;
	}
	public void setTechnology(int technology) {
		this.technology = technology;
	}
	
	public void setAdjacent(List<Integer> adjacent) {
		this.adjacent = adjacent;
	}
	public List<Integer> getAdjacent() {
		return adjacent;
	}

	public Set<Integer> getTechnologyInput() {
		return technologyInput;
	}

	public void setTechnologyInput(Set<Integer> technologyInput) {
		this.technologyInput = technologyInput;
	}

	public Set<Integer> getTechnologyOutput() {
		return technologyOutput;
	}

	public void setTechnologyOutput(Set<Integer> technologyOutput) {
		this.technologyOutput = technologyOutput;
	}

	
	
	

}
