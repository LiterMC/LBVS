package com.github.litermc.lbvs.util;

import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BitSetProperty extends Property<Integer> {
	private final int bits;
	private final BitsCollection bitsCollection;

	public BitSetProperty(final String name, final int bits) {
		super(name, Integer.class);
		if (bits > 30) {
			throw new IllegalArgumentException("max bits size is 30");
		}
		this.bits = bits;
		this.bitsCollection = new BitsCollection(bits);
	}

	@Override
	public Collection<Integer> getPossibleValues() {
		return this.bitsCollection;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof BitSetProperty p && this.bits == p.bits && super.equals(p));
	}

	@Override
	public int generateHashCode() {
			return 31 * super.generateHashCode() + this.bits;
	}

	@Override
	public Optional<Integer> getValue(String s) {
		int v;
		try {
			v = Integer.valueOf(s);
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
		return 0 <= v && v < (1 << this.bits) ? Optional.of(v) : Optional.empty();
	}

	@Override
	public String getName(Integer v) {
		return v.toString();
	}

	private static final class BitsCollection implements Collection<Integer> {
		private final int bits;

		BitsCollection(final int bits) {
			this.bits = bits;
		}

		@Override
		public boolean add(Integer v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Integer> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object o) {
			return o instanceof Integer v && 0 <= v && v < (1 << this.bits);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object n : c) {
				if (!this.contains(n)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			return this == o || (o instanceof BitsCollection c && this.bits == c.bits);
		}

		@Override
		public int hashCode() {
			return this.bits;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Iterator<Integer> iterator() {
			return this.stream().iterator();
		}

		@Override
		public Stream<Integer> parallelStream() {
			return this.stream().parallel();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 1 << this.bits;
		}

		@Override
		public Spliterator<Integer> spliterator() {
			return this.stream().spliterator();
		}

		@Override
		public Stream<Integer> stream() {
			return IntStream.range(0, 1 << this.bits).mapToObj(Integer::valueOf);
		}

		@Override
		public Object[] toArray() {
			return this.stream().toArray();
		}

		@Override
		public <T> T[] toArray(IntFunction<T[]> generator) {
			return this.stream().toArray(generator);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return this.stream().toArray((n) -> {
				if (n <= a.length) {
					return a;
				}
				return (T[])(java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), n));
			});
		}
	}
}
