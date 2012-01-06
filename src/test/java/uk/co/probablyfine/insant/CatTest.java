package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.Cat;
import uk.co.probablyfine.insant.annotations.Dog;

public class CatTest {

	public static void main(String[] args) {
		new CatTest().foo();
		new CatTest().bar();
	}

	@Cat
	public void bar() {
		//System.out.println("Calling bar");
		int i = 1;
		System.out.println(i);
	}

	@Dog
	public void foo() {
		System.out.println("Calling foo");
	}

}
