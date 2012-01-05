package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.Cat;
import uk.co.probablyfine.insant.annotations.Dog;

public class CatTest {

	public static void main(String[] args) {
		new CatTest().foo();

	}

	@Cat
	public int method1() {
		int i = 1;
		return i;
	}

	@Dog(breed="Pug")
	public int foo() {
		int j = 2;
		return j;
	}

}
