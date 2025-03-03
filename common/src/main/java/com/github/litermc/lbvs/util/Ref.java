package com.github.litermc.lbvs.util;

public class Ref<T> {
	private T value;

	public Ref(T value) {
		this.value = value;
	}

	public T get() {
		return this.value;
	}

	public void set(T value) {
		this.value = value;
	}
}
