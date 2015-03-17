package com.example.fuerthwiki;

public class Item {	
	private String name;
	private String count;
	
	public Item(){

	}
	public Item(String name,String count){
		this.name = name;
		if(count!=null)
			this.count = count;
		else
			this.count ="";
	}
	public String getCount(){
		return count;
	}
	public String getName() {
		return name;
	}
	public void setCount(String count){
		this.count=count;
	}
	public void setName(String name) {
		this.name = name;
	}
}