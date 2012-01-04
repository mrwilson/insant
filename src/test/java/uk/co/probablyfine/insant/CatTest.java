package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.Cat;
import uk.co.probablyfine.insant.annotations.Dog;

public class CatTest {

	@Dog
	public static void main(String[] args) {
		method1();
		foo();
	}
	
	@Cat
	public static int method1() {
		int i = 1;
		return i;
	}
	
	@Dog
	public static int foo() {
		int j = 2;
		return j;
	}
	
}
