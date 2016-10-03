package org.spideruci.analysis.statik.test;

import org.joda.time.Years;

public class FakeMain {


	public static void main(String[] args) {
//		System.out.println("This is a TestYears.java dup.");
		
		testConstants();
	}
	
	public static void testConstants(){
		System.out.println(Years.ZERO.getYears() == 0 ? "true" : "false");
		System.out.println(Years.ONE.getYears() == 1 ? "true" : "false");
		System.out.println(Years.TWO.getYears() == 2 ? "true" : "false");
		System.out.println(Years.THREE.getYears() == 3 ? "true" : "false");
		System.out.println(Years.MAX_VALUE.getYears() == Integer.MAX_VALUE ? "true" : "false");
		System.out.println(Years.MIN_VALUE.getYears() == Integer.MIN_VALUE ? "true" : "false");
	}
	
}
