package com.zzg.spiderNews.parse;

import java.util.BitSet;

public class BloomFilter implements java.io.Serializable  {
	
	private static final long serialVersionUID = 6104455865572455753L;
	private  final int DEFAULT_SIZE = (2 << 30)-1;
	private  final int[] seeds = new int[] { 5, 7, 9, 11, 13, 31, 37, 61 };
	private BitSet bits = new BitSet(DEFAULT_SIZE);
	private SimpleHash[] func = new SimpleHash[seeds.length];
	public BloomFilter() {
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
	}

	 
	
	public void add(String value) {
		for (SimpleHash f : func) {
			bits.set(f.hash(value), true);
		}
	}

	public boolean contains(String value) {
		if (value == null) {
			return true;
		}
		boolean ret = true;
		for (SimpleHash f : func) {
			ret = ret && bits.get(f.hash(value));
			if(ret == false) return false;
		}
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		/*String[] value = new String[]{"1","2","1"};
		BloomFilter bf = new BloomFilter();
		if(!bf.contains(value[0])){
			bf.add(value[0]);
		}
		if(!bf.contains(value[1])){
			bf.add(value[1]);
		}
		if(!bf.contains(value[2])){
			bf.add(value[2]);
		}*/
		int a = -2147483647;
		System.out.println( Math.abs(a));
	}

	public static class SimpleHash implements java.io.Serializable {
		private static final long serialVersionUID = 8352127853333280904L;
		private int cap;
		private int seed;

		public SimpleHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}

		public int hash(String value) {
			int result = 0;
			int len = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			//return (cap - 1) & result;
			if(result < 0){
				result = result + 1;
			}
			return Math.abs(result)%cap;
		}
	}

}
