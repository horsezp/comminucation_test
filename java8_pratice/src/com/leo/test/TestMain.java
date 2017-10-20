package com.leo.test;

public class TestMain {

	public static void main(String[] args) {

		Formula formula = new Formula() {
			@Override
			public double calculate(int a) {
				return sqrt(a * 100);
			}
		};

		formula.calculate(100); // 100.0
		System.out.println(formula.sqrt(16)); // 4.0

	}

}
