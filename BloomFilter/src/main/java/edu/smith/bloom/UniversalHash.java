package edu.smith.bloom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hash anything based on Java's hashCode.
 * @author jfoley
 *
 * @param <T>
 */
public class UniversalHash<T> {
	UniversalIntHash hasher;
	
	// just get random numbers
	public UniversalHash() {
		this(ThreadLocalRandom.current());
	}

	// control the random numbers (for predictable tests)
	public UniversalHash(Random rand) {
		this(rand.nextLong(), rand.nextLong());
	}
	
	// control the exact parameters (for extremely predictable tests)
	public UniversalHash(long a, long b) {
		this.hasher = new UniversalIntHash(a, b);
	}
	
	// get a hash from this function for any Java object that correctly implements hashCode
	public int hash(T object) {
		return Math.abs(this.hasher.hash(object.hashCode()));
	}
	
	/**
	 * re-hash any int you want
	 * @author jfoley
	 *
	 */
	private class UniversalIntHash {
		// Carter and Wegman:
		// https://en.wikipedia.org/wiki/Universal_hashing#Hashing_integers
		// m = 32
		// p = NinthMersennePrime
		public final long a;
		public final long b;
		// https://en.wikipedia.org/wiki/Mersenne_prime
		// (1 << 61) - 1; = 2**61 - 1
		public static final long NinthMersennePrime = 2305843009213693951L;
		
		public UniversalIntHash(long a, long b) {
			this.a = a;
			this.b = b;
		}
		
		public int hash(int input) {
			return (int) (((a * input) + b) % NinthMersennePrime);
		}
	}
	
	public static void main(String[] args) {
		List<String> data = Arrays.asList("a", "b", "c", "d", "e");
		List<UniversalHash<String>> hashers = new ArrayList<>();
		
		// num-hashes
		final int K = 5;
		// num-buckets
		final int B = 11;
		
		for (int i=0; i<K; i++) {
			hashers.add(new UniversalHash<>());
		}
		
		for (String x : data) {
			System.out.print(x);
			for (int i=0; i<5; i++) {
				System.out.print(" "+hashers.get(i).hash(x) % B);
			}
			System.out.println();
		}
		
	}
}