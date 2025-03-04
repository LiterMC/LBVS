package com.github.litermc.lbvs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.EnumSet;
import java.util.stream.Stream;

public enum Direction26 {
	Z1(new Vec3i(0, 0, 1), 1),
	ZN1(new Vec3i(0, 0, -1), 0),

	Y1(new Vec3i(0, 1, 0), 5),
	Y1Z1(new Vec3i(0, 1, 1), 7),
	Y1ZN1(new Vec3i(0, 1, -1), 6),

	YN1(new Vec3i(0, -1, 0), 2),
	YN1Z1(new Vec3i(0, -1, 1), 4),
	YN1ZN1(new Vec3i(0, -1, -1), 3),

	X1(new Vec3i(1, 0, 0), 17),
	X1Z1(new Vec3i(1, 0, 1), 19),
	X1ZN1(new Vec3i(1, 0, -1), 18),

	X1Y1(new Vec3i(1, 1, 0), 23),
	X1Y1Z1(new Vec3i(1, 1, 1), 25),
	X1Y1ZN1(new Vec3i(1, 1, -1), 24),

	X1YN1(new Vec3i(1, -1, 0), 20),
	X1YN1Z1(new Vec3i(1, -1, 1), 22),
	X1YN1ZN1(new Vec3i(1, -1, -1), 21),

	XN1(new Vec3i(-1, 0, 0), 8),
	XN1Z1(new Vec3i(-1, 0, 1), 10),
	XN1ZN1(new Vec3i(-1, 0, -1), 9),

	XN1Y1(new Vec3i(-1, 1, 0), 14),
	XN1Y1Z1(new Vec3i(-1, 1, 1), 16),
	XN1Y1ZN1(new Vec3i(-1, 1, -1), 15),

	XN1YN1(new Vec3i(-1, -1, 0), 11),
	XN1YN1Z1(new Vec3i(-1, -1, 1), 13),
	XN1YN1ZN1(new Vec3i(-1, -1, -1), 12);

	private static final Direction26[] VALUES = values();

	private final Vec3i offset;
	private final int opposite;
	private final Pos pos;

	private Direction26(final Vec3i offset, final int opposite) {
		this.offset = offset;
		this.pos = switch (Math.abs(this.offset.getX()) + Math.abs(this.offset.getY()) + Math.abs(this.offset.getZ())) {
			case 1 -> Pos.FACE;
			case 2 -> Pos.EDGE;
			case 3 -> Pos.CORNER;
			default -> throw new IllegalArgumentException();
		};
		this.opposite = opposite;
	}

	public Vec3i offset() {
		return this.offset;
	}

	public Pos pos() {
		return this.pos;
	}

	public Direction26 opposite() {
		return VALUES[this.opposite];
	}

	public int getStepX() {
		return this.offset.getX();
	}

	public int getStepY() {
		return this.offset.getY();
	}

	public int getStepZ() {
		return this.offset.getZ();
	}

	public BlockPos offsetOf(final BlockPos origin) {
		return origin.offset(this.offset);
	}

	public static Stream<Direction26> stream() {
		return Stream.of(values());
	}

	public static Stream<Vec3i> streamAllOffsets() {
		return stream().map(Direction26::offset);
	}

	public static Stream<Direction26> streamWith(final Pos pos) {
		return stream().filter(d -> d.pos() == pos);
	}

	public static Stream<Direction26> streamFaceOffsets() {
		return streamWith(Pos.FACE);
	}

	public static Stream<Direction26> streamEdgeOffsets() {
		return streamWith(Pos.EDGE);
	}

	public static Stream<Direction26> streamCornerOffsets() {
		return streamWith(Pos.CORNER);
	}

	public static Stream<BlockPos> streamAllOffsetsOf(final BlockPos origin) {
		return stream().map(d -> d.offsetOf(origin));
	}

	public static Stream<BlockPos> streamOffsetsOfWith(final BlockPos origin, final Pos pos) {
		return streamWith(pos).map(d -> d.offsetOf(origin));
	}

	public static EnumSet<Direction26> createEmptySet() {
		return EnumSet.noneOf(Direction26.class);
	}

	public static enum Pos {
		FACE, EDGE, CORNER;
	}
}
