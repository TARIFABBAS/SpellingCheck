package com.tesu.example;

public class test {
	private static int tableSize = 50000;

	public static void main(String[] args) {
		String str = "#";
		System.out.println(str.charAt(0));

		int index = hashFunction("");
		System.out.println(index);
	}

	private static int hashFunction(String s) {
		int hashKey = 0;

		for (int x = 0; x < s.length(); x++) {
			int askey = Math.abs(s.charAt(x) - 47);
			System.out.println(askey);
			hashKey = (hashKey * 47 + askey) % tableSize;
		}
		return hashKey;

	}
}
