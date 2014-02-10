package org.example.droidgame;

import java.util.Random;

import org.example.shootinggame.R;

public enum DroidType {
	type1(R.drawable.img0, 10, 1.0f, 2 * 2 * 2 * 2 * 2 * 1.0f), 
	type2(R.drawable.img1, 15, 1.0f, 2 * 2 * 2 * 2 * 1.0f),
	type3(R.drawable.img0, 20, 1.5f, 2 * 2 * 2 * 1.0f),
	type4(R.drawable.img1, 25, 1.0f, 2 * 2 * 2 * 2 * 2 * 1.0f),
	type5(R.drawable.img0, 30, 1.0f, 2 * 2 * 2 * 2 * 2 * 2 * 1.0f);

	private int image_id;
	private int point;
	private float size;
	private float speed;

	private DroidType(int image_id, int point, float speed, float size) {
		this.image_id = image_id;
		this.point = point;
		this.speed = speed;
		this.size = size;
	}
	
	public static DroidType getRandomType(){
		DroidType[] droidTypes = values();
		int rnd = new Random().nextInt(values().length);
		return droidTypes[rnd];
		
	}

	public int getImageId() {
		return image_id;
	}

	public int getPoint() {
		return point;
	}

	public float getSpeed() {
		return speed;
	}

	public float getSize() {
		return size;
	}

}
