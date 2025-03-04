package com.github.litermc.lbvs.util;

public class Ref<T> {
	private T value;

	public Ref(final T value) {
		this.value = value;
	}

	public T get() {
		return this.value;
	}

	public void set(final T value) {
		this.value = value;
	}
}
