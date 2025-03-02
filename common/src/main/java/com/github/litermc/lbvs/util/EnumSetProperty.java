package com.github.litermc.lbvs.util;

import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.EnumSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EnumSetProperty<T extends Enum<T>> extends BitSetProperty {
	private final Class<T> clazz;
	private final T[] elems;

	public EnumSetProperty(final String name, final Class<T> clazz) {
		super(name, clazz.getEnumConstants().length);
		this.clazz = clazz;
		this.elems = clazz.getEnumConstants();
	}

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof EnumSetProperty p && this.clazz == p.clazz && super.equals(p));
	}

	@Override
	public int generateHashCode() {
		return 31 * super.generateHashCode() + this.clazz.hashCode();
	}

	public EnumSet<T> getEnums(StateHolder<?, ?> stateHolder) {
		final int bits = this.value(stateHolder).value();
		final EnumSet<T> set = EnumSet.noneOf(this.clazz);
		for (int i = 0; i < this.elems.length; i++) {
			if ((bits & (1 << i)) != 0) {
				set.add(this.elems[i]);
			}
		}
		return set;
	}

	public void setEnums(StateHolder<?, ?> stateHolder, Collection<T> set) {
		int bits = 0;
		for (T e : set) {
			bits |= 1 << e.ordinal();
		}
		stateHolder.setValue(this, bits);
	}
}
