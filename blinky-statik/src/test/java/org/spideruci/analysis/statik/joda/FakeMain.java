package org.spideruci.analysis.statik.joda;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInterval;
import org.joda.time.YearMonthDay;
import org.joda.time.Years;

public class FakeMain {

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

	public static void main(String[] args) {
//		System.out.println("This is a TestYears.java dup.");
		
		testConstants();
//		testFactory_years_int();
//		testFactory_yearsBetween_RInstant();
//		testFactory_yearsBetween_RPartial();
//		testFactory_yearsIn_RInterval();
//		testFactory_parseYears_String();
//		testGetMethods();
//		testGetFieldType();
//		testGetPeriodType();
//		testIsGreaterThan();
//		testIsLessThan();
//		testToString();
//		testPlus_int();
//		testPlus_Years();
//		testMinus_int();
//		testMinus_Years();
//		testMultipliedBy_int();
//		testDividedBy_int();
//		testNegated();
//		testAddToLocalDate();
	}
	
	public static void testConstants(){
		System.out.println(Years.ZERO.getYears() == 0 ? "true" : "false");
		System.out.println(Years.ONE.getYears() == 1 ? "true" : "false");
		System.out.println(Years.TWO.getYears() == 2 ? "true" : "false");
		System.out.println(Years.THREE.getYears() == 3 ? "true" : "false");
		System.out.println(Years.MAX_VALUE.getYears() == Integer.MAX_VALUE ? "true" : "false");
		System.out.println(Years.MIN_VALUE.getYears() == Integer.MIN_VALUE ? "true" : "false");
	}
	
	public static void testFactory_years_int(){
		System.out.println(Years.years(0) == Years.ZERO ? "true" : "false");
		System.out.println(Years.years(1) == Years.ONE ? "true" : "false");
		System.out.println(Years.years(2) == Years.TWO ? "true" : "false");
		System.out.println(Years.years(3) == Years.THREE ? "true" : "false");
		System.out.println(Years.years(Integer.MAX_VALUE) == Years.MAX_VALUE ? "true" : "false");
		System.out.println(Years.years(Integer.MIN_VALUE) == Years.MIN_VALUE ? "true" : "false");
		System.out.println(Years.years(-1).getYears() == -1 ? "true" : "false");
		System.out.println(Years.years(4).getYears() == 4 ? "true" : "false");		
	}
	
	public static void testFactory_yearsBetween_RInstant(){
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, PARIS);
        DateTime end1 = new DateTime(2009, 6, 9, 12, 0, 0, 0, PARIS);
        DateTime end2 = new DateTime(2012, 6, 9, 12, 0, 0, 0, PARIS);
        
		System.out.println(Years.yearsBetween(start, end1).getYears() == 3 ? "true" : "false");
		System.out.println(Years.yearsBetween(start, start).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsBetween(end1, end1).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsBetween(end1, start).getYears() == -3 ? "true" : "false");
		System.out.println(Years.yearsBetween(start, end2).getYears() == 6 ? "true" : "false");
	}
	
    @SuppressWarnings("deprecation")
	public static void testFactory_yearsBetween_RPartial(){
        LocalDate start = new LocalDate(2006, 6, 9);
        LocalDate end1 = new LocalDate(2009, 6, 9);
        YearMonthDay end2 = new YearMonthDay(2012, 6, 9);

		System.out.println(Years.yearsBetween(start, end1).getYears() == 3 ? "true" : "false");
		System.out.println(Years.yearsBetween(start, start).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsBetween(end1, end1).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsBetween(end1, start).getYears() == -3 ? "true" : "false");
		System.out.println(Years.yearsBetween(start, end2).getYears() == 6 ? "true" : "false");
	}
    
    public static void testFactory_yearsIn_RInterval(){
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, PARIS);
        DateTime end1 = new DateTime(2009, 6, 9, 12, 0, 0, 0, PARIS);
        DateTime end2 = new DateTime(2012, 6, 9, 12, 0, 0, 0, PARIS);

		System.out.println(Years.yearsIn((ReadableInterval) null).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsIn(new Interval(start, end1)).getYears() == 3 ? "true" : "false");
		System.out.println(Years.yearsIn(new Interval(start, start)).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsIn(new Interval(end1, end1)).getYears() == 0 ? "true" : "false");
		System.out.println(Years.yearsIn(new Interval(start, end2)).getYears() == 6 ? "true" : "false");
    }
    
    public static void testFactory_parseYears_String(){
		System.out.println(Years.parseYears((String) null).getYears() == 0 ? "true" : "false");
		System.out.println(Years.parseYears("P0Y").getYears() == 0 ? "true" : "false");
		System.out.println(Years.parseYears("P1Y").getYears() == 1 ? "true" : "false");
		System.out.println(Years.parseYears("P-3Y").getYears() == -3 ? "true" : "false");
		System.out.println(Years.parseYears("P2Y0M").getYears() == 2 ? "true" : "false");
		System.out.println(Years.parseYears("P2YT0H0M").getYears() == 2 ? "true" : "false");
		
		try{
			Years.parseYears("P1M1D");
		}catch(IllegalArgumentException e){
			System.out.println("true");
		}
		
		try{
			Years.parseYears("P1YT1H");
		}catch(IllegalArgumentException e){
			System.out.println("true");
		}
    }
    
    public static void testGetMethods(){
        Years test = Years.years(20);
        System.out.println(test.getYears() == 20 ? "true" : "false");
    }
    
    public static void testGetFieldType(){
        Years test = Years.years(20);
        System.out.println(test.getFieldType() == DurationFieldType.years() ? "true" : "false");
    }
    
    public static void testGetPeriodType(){
        Years test = Years.years(20);
        System.out.println(test.getPeriodType() == PeriodType.years() ? "true" : "false");
    }
    
    public static void testIsGreaterThan(){
    	System.out.println(Years.THREE.isGreaterThan(Years.TWO) ? "true" : "false");
    	System.out.println(Years.THREE.isGreaterThan(Years.THREE) ? "false" : "true");
    	System.out.println(Years.TWO.isGreaterThan(Years.THREE) ? "false" : "true");
    	System.out.println(Years.ONE.isGreaterThan(null) ? "true" : "false");
    	System.out.println(Years.years(-1).isGreaterThan(null) ? "false" : "true");
    }
    
	private static void testIsLessThan() {
    	System.out.println(Years.THREE.isLessThan(Years.TWO) ? "false" : "true");
    	System.out.println(Years.THREE.isLessThan(Years.THREE) ? "false" : "true");
    	System.out.println(Years.TWO.isLessThan(Years.THREE) ? "true" : "false");
    	System.out.println(Years.ONE.isLessThan(null) ? "false" : "true");
    	System.out.println(Years.years(-1).isLessThan(null) ? "true" : "false");
	}

    public static void testToString() {
        Years test = Years.years(20);
    	System.out.println(test.toString().equals("P20Y")? "true" : "false");
        
        test = Years.years(-20);
    	System.out.println(test.toString().equals("P-20Y")? "true" : "false");
    }
    
    public static void testPlus_int() {
        Years test2 = Years.years(2);
        Years result = test2.plus(3);
        
    	System.out.println(test2.getYears() == 2 ? "true" : "false");
    	System.out.println(result.getYears() == 5 ? "true" : "false");
    	System.out.println(Years.ONE.plus(0).getYears() == 1 ? "true" : "false");
                
        try {
        	Years.MAX_VALUE.plus(1);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testPlus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = test2.plus(test3);
        
    	System.out.println(test2.getYears() == 2 ? "true" : "false");
    	System.out.println(test3.getYears() == 3 ? "true" : "false");
    	System.out.println(result.getYears() == 5 ? "true" : "false");
    	
    	System.out.println(Years.ONE.plus(Years.ZERO).getYears() == 1 ? "true" : "false");
    	System.out.println(Years.ONE.plus((Years) null).getYears() == 1 ? "true" : "false");
        
        try {
        	Years.MAX_VALUE.plus(Years.ONE);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testMinus_int() {
        Years test2 = Years.years(2);
        Years result = test2.minus(3);
        
      	System.out.println(test2.getYears() == 2 ? "true" : "false");
    	System.out.println(result.getYears() == -1 ? "true" : "false");

    	System.out.println(Years.ONE.minus(0).getYears() == 1 ? "true" : "false");
        
        try {
        	Years.MIN_VALUE.minus(1);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testMinus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = test2.minus(test3);
        
      	System.out.println(test2.getYears() == 2 ? "true" : "false");
      	System.out.println(test3.getYears() == 3 ? "true" : "false");
    	System.out.println(result.getYears() == -1 ? "true" : "false");

    	System.out.println(Years.ONE.minus(Years.ZERO).getYears() == 1 ? "true" : "false");
    	System.out.println(Years.ONE.minus((Years) null).getYears() == 1 ? "true" : "false");
        
        try {
        	Years.MIN_VALUE.minus(Years.ONE);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testMultipliedBy_int() {
        Years test = Years.years(2);
        
      	System.out.println(test.multipliedBy(3).getYears() == 6 ? "true" : "false");
      	System.out.println(test.getYears() == 2 ? "true" : "false");
    	System.out.println(test.multipliedBy(-3).getYears() == -6 ? "true" : "false");
    	System.out.println(test.multipliedBy(1) == test ? "true" : "false");
        
        Years halfMax = Years.years(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testDividedBy_int() {
        Years test = Years.years(12);
        
      	System.out.println(test.dividedBy(2).getYears() == 6 ? "true" : "false");
      	System.out.println(test.getYears() == 12 ? "true" : "false");
    	System.out.println(test.dividedBy(3).getYears() == 4 ? "true" : "false");
    	System.out.println(test.dividedBy(4).getYears() == 3 ? "true" : "false");
    	System.out.println(test.dividedBy(5).getYears() == 2 ? "true" : "false");
    	System.out.println(test.dividedBy(6).getYears() == 2 ? "true" : "false");
    	System.out.println(test.dividedBy(1) == test ? "true" : "false");
        
        try {
            Years.ONE.dividedBy(0);
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }
    
    public static void testNegated() {
        Years test = Years.years(12);
        
      	System.out.println(test.negated().getYears() == -12 ? "true" : "false");
      	System.out.println(test.getYears() == 12 ? "true" : "false");
      	
        try {
            Years.MIN_VALUE.negated();
        } catch (ArithmeticException ex) {
			System.out.println("true");
        }
    }

    public static void testAddToLocalDate() {
        Years test = Years.years(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2009, 6, 1);
        
      	System.out.println(date.plus(test).equals(expected) ? "true" : "false");
    }   
}
