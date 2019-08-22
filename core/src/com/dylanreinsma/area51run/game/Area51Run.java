package com.dylanreinsma.area51run.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class Area51Run extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture[] deadMan;
	int manState = 0;
	int deadManState = 0;
	int pause = 0;
	float gravity = 0.35f;
	float velocity = 0;
	int manY = 0;
	Random random;
	BitmapFont font;

	int score = 0;
	int gameState;

	Rectangle manRectangle;

	ArrayList<Integer> alienXs = new ArrayList<Integer>();
	ArrayList<Integer> alienYs = new ArrayList<Integer>();
	ArrayList<Rectangle> alienRectangles = new ArrayList<Rectangle>();
	Texture[] alien;
	//Texture alien;
	int alienCount;
	int alienState;

	ArrayList<Integer> badAlienXs = new ArrayList<Integer>();
	ArrayList<Integer> badAlienYs = new ArrayList<Integer>();
	ArrayList<Rectangle> badAlienRectangles = new ArrayList<Rectangle>();
	Texture badAlien;
	int badAlienCount;

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("bg2.jpg");

		man = new Texture[9];
		man[0] = new Texture("Run__000.png");
		man[1] = new Texture("Run__001.png");
		man[2] = new Texture("Run__002.png");
		man[3] = new Texture("Run__003.png");
		man[4] = new Texture("Run__004.png");
		man[5] = new Texture("Run__005.png");
		man[6] = new Texture("Run__006.png");
		man[7] = new Texture("Run__007.png");
		man[8] = new Texture("Run__008.png");

		deadMan = new Texture[9];
		deadMan[0] = new Texture("Dead__000.png");
		deadMan[1] = new Texture("Dead__001.png");
		deadMan[2] = new Texture("Dead__002.png");
		deadMan[3] = new Texture("Dead__003.png");
		deadMan[4] = new Texture("Dead__004.png");
		deadMan[5] = new Texture("Dead__005.png");
		deadMan[6] = new Texture("Dead__006.png");
		deadMan[7] = new Texture("Dead__007.png");
		deadMan[8] = new Texture("Dead__008.png");

		manY = Gdx.graphics.getHeight() / 2;

		//alien = new Texture("green__0000_idle_1.png");
		alien = new Texture[3];
		alien[0] = new Texture("green__0000_idle_1.png");
		alien[1] = new Texture("green__0001_idle_2.png");
		alien[2] = new Texture("green__0002_idle_3.png");

		badAlien = new Texture("green__0033_attack_3.png");

		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeAlien() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		alienYs.add((int)height);
		alienXs.add(Gdx.graphics.getWidth());
	}

	public void makeBadAlien() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		badAlienYs.add((int) height);
		badAlienXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			//GAME LIVE

			//GOOD ALIEN ANIMATION TIMER
			if (alienCount < 150) {
				alienCount++;
			} else {
				alienCount = 0;
				makeAlien();
			}

			//ENEMY ALIEN ANIMATION TIMER
			if (badAlienCount < 500) {
				badAlienCount++;
			} else {
				badAlienCount = 0;
				makeBadAlien();
			}

			//GOOD ALIEN SCREEN SPAWN
			alienRectangles.clear();
			for (int i=0; i<alienXs.size(); i++) {
				batch.draw(alien[0], alienXs.get(i), alienYs.get(i));
				//batch.draw(alien, alienXs.get(i), alienYs.get(i));
				alienXs.set(i, alienXs.get(i) - 4);
				alienRectangles.add(new Rectangle(alienXs.get(i), alienYs.get(i), alien[0].getWidth(), alien[0].getHeight()));
			}

			//ENEMY ALIEN SCREEN SPAWN
			badAlienRectangles.clear();
			for (int i=0; i<badAlienXs.size(); i++) {
//			batch.draw(alien[alienState], alienXs.get(i), alienYs.get(i));
				batch.draw(badAlien, badAlienXs.get(i), badAlienYs.get(i));
				badAlienXs.set(i, badAlienXs.get(i) - 7);
				badAlienRectangles.add(new Rectangle(badAlienXs.get(i), badAlienYs.get(i), badAlien.getWidth(), badAlien.getHeight()));
			}

			//JUMP
			if (Gdx.input.justTouched()) {
				velocity = -13;
			}

			//ANIMATION TIMING
			if (pause < 5) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
				if (alienState <3) {
					alienState++;
				} else {
					alienState = 0;
				}
				if (deadManState < 3) {
					deadManState++;
				} else {
					deadManState = 0;
				}
			}

			//PHYSICS
			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			}

		} else if (gameState == 0) {
			//GAME IDLE
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			//GAME OVER
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				alienXs.clear();
				alienYs.clear();
				alienRectangles.clear();
				alienCount = 0;
				badAlienXs.clear();
				badAlienYs.clear();
				badAlienRectangles.clear();
				badAlienCount = 0;
			}
		}

		//MAN CHARACTER
		if (gameState == 2) {

			batch.draw(deadMan[deadManState],Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

		} else {

			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i=0; i <alienRectangles.size(); i++){
			if (Intersector.overlaps(manRectangle, alienRectangles.get(i))) {
				score++;

				alienRectangles.remove(i);
				alienXs.remove(i);
				alienYs.remove(i);
				break;
			}
		}

		for (int i=0; i <badAlienRectangles.size(); i++){
			if (Intersector.overlaps(manRectangle, badAlienRectangles.get(i))) {
				gameState = 2;
			}
		}

        font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
