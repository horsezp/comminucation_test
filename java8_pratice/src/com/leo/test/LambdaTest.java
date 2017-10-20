package com.leo.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/*����ʽ�ӿ� Top

Lambda���ʽ���ƥ��Java������ϵͳ��ÿһ��lambda���ܹ�ͨ��һ���ض��Ľӿڣ���һ�����������ͽ���ƥ�䡣
һ����ν�ĺ���ʽ�ӿڱ���Ҫ�� �ҽ���һ�����󷽷�������ÿ����֮��Ӧ��lambda���ʽ����Ҫ����󷽷���������ƥ�䡣
����Ĭ�Ϸ������ǳ���ģ�������������ĺ���ʽ�ӿ��������� ��Ĭ�Ϸ����� 

����ֻ����һ�����󷽷��Ľӿڣ����Ƕ�������������lambda���ʽ��Ϊ�����㶨��Ľӿ�����Ҫ��
��Ӧ���ڽӿ�ǰ����@FunctionalInterface ��ע����������ע�⵽�����ע��
�����Ľӿ��ж����˵ڶ������󷽷��Ļ������������׳��쳣�� 
*
*/
@FunctionalInterface
interface Converter<F, T> {
	T convert(F from);
}

class Something {
	String startsWith(String s) {
		return String.valueOf(s.charAt(0));
	}
}

class Person {
	String firstName;
	String lastName;

	Person() {
	}

	Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
}

interface PersonFactory<P extends Person> {
	P create(String firstName, String lastName);
}

public class LambdaTest {

	public static void main(String[] args) {

		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return b.compareTo(a);
			}
		});

		Collections.sort(names, (String a, String b) -> {
			return b.compareTo(a);
		});

		Collections.sort(names, (String a, String b) -> b.compareTo(a));

		Collections.sort(names, (a, b) -> b.compareTo(a));

		Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
		Integer converted = converter.convert("123");
		System.out.println(converted);

		Converter<String, Integer> converter2 = Integer::valueOf;
		Integer converted2 = converter2.convert("123");
		System.out.println(converted2); // 123
		//
		// Java 8 ������ͨ��::�ؼ��ֻ�ȡ�������߹��캯���ĵ����á�
		// ��������Ӿ���ʾ���������һ����̬���������ң����ǻ����Զ�һ������ķ����������ã�
		Something something = new Something();
		Converter<String, String> converter3 = something::startsWith;
		String converted3 = converter3.convert("Java");
		System.out.println(converted3);

		// ���ʹ��::�ؼ������ù��캯�����������Ƕ���һ��ʾ��bean��������ͬ�Ĺ��췽����
		// Person::new������һ��Person�๹�캯�������á�
		// Java���������Զ���ѡ����ʵĹ��캯����ƥ��PersonFactory.create������ǩ������ѡ����ȷ�Ĺ��캯����ʽ��
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
		System.out.println(person);

		test2();
		
        //Function�ӿڽ���һ�������������ص�һ�Ľ����Ĭ�Ϸ������Խ������������һ��compse, andThen���� 
		Function<String, Integer> toInteger = Integer::valueOf;
		Function<String, String> backToString = toInteger.andThen(String::valueOf);
		backToString.apply("123"); // "123"
		
		//Supplier�ӿڲ���һ���������͵Ľ������Function��ͬ���ǣ�Supplierû����������� 
		Supplier<Person> personSupplier = Person::new;  
		personSupplier.get();   // new Person  
		
		
		//Consumer��������һ�������������Ҫ���еĲ�����
		Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);  
		greeter.accept(new Person("Luke", "Skywalker"));  

	}

	public static void test2() {
		// ����������ͬ���ǣ�����num������Ҫһ����final������Ĵ�����Ȼ�ǺϷ��ģ�
		// num�ڱ����ʱ����ʽ�ص���final����������
		int num = 1;
		Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);
		stringConverter.convert(2);
	}

}

class Lambda4 {
	static int outerStaticNum;
	int outerNum;

	void testScopes() {
		Converter<Integer, String> stringConverter1 = (from) -> {
			outerNum = 23;
			return String.valueOf(from);
		};

		Converter<Integer, String> stringConverter2 = (from) -> {
			outerStaticNum = 72;
			return String.valueOf(from);
		};
	}
}
