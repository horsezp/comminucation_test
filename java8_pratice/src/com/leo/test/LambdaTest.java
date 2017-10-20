package com.leo.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/*函数式接口 Top

Lambda表达式如何匹配Java的类型系统？每一个lambda都能够通过一个特定的接口，与一个给定的类型进行匹配。
一个所谓的函数式接口必须要有 且仅有一个抽象方法声明。每个与之对应的lambda表达式必须要与抽象方法的声明相匹配。
由于默认方法不是抽象的，因此你可以在你的函数式接口里任意添 加默认方法。 

任意只包含一个抽象方法的接口，我们都可以用来做成lambda表达式。为了让你定义的接口满足要求，
你应当在接口前加上@FunctionalInterface 标注。编译器会注意到这个标注，
如果你的接口中定义了第二个抽象方法的话，编译器会抛出异常。 
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
		// Java 8 允许你通过::关键字获取方法或者构造函数的的引用。
		// 上面的例子就演示了如何引用一个静态方法。而且，我们还可以对一个对象的方法进行引用：
		Something something = new Something();
		Converter<String, String> converter3 = something::startsWith;
		String converted3 = converter3.convert("Java");
		System.out.println(converted3);

		// 如何使用::关键字引用构造函数。首先我们定义一个示例bean，包含不同的构造方法：
		// Person::new来创建一个Person类构造函数的引用。
		// Java编译器会自动地选择合适的构造函数来匹配PersonFactory.create函数的签名，并选择正确的构造函数形式。
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
		System.out.println(person);

		test2();
		
        //Function接口接收一个参数，并返回单一的结果。默认方法可以将多个函数串在一起（compse, andThen）： 
		Function<String, Integer> toInteger = Integer::valueOf;
		Function<String, String> backToString = toInteger.andThen(String::valueOf);
		backToString.apply("123"); // "123"
		
		//Supplier接口产生一个给定类型的结果。与Function不同的是，Supplier没有输入参数。 
		Supplier<Person> personSupplier = Person::new;  
		personSupplier.get();   // new Person  
		
		
		//Consumer代表了在一个输入参数上需要进行的操作。
		Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);  
		greeter.accept(new Person("Luke", "Skywalker"));  

	}

	public static void test2() {
		// 与匿名对象不同的是，变量num并不需要一定是final。下面的代码依然是合法的：
		// num在编译的时候被隐式地当做final变量来处理。
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
