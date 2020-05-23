package com.dune.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class DuneGame extends ApplicationAdapter {
	private static class Circle {
		private Vector2 position;
		private Texture texture;
		private Random rand;
		private float width, height;

		public Circle() {
			rand = new Random();
			texture = new Texture("circle.png");
			this.width = texture.getWidth();
			this.height = texture.getHeight();
			position = new Vector2(rand.nextInt(1280 - (int)width), rand.nextInt(720 - (int)height));
		}

		public Vector2 getPosition() {
			return position;
		}

		public Vector2 getDimensions() {
			return new Vector2(width, height);
		}

		public Vector2 getCenter() {
			return new Vector2(position.x - width / 2, position.y - height / 2);
		}

		public void update() {
			position.x = rand.nextInt(1280  - (int)width);
			position.y = rand.nextInt(720 - (int)height);
		}

		public void render(SpriteBatch batch) {
			batch.draw(texture, position.x, position.y);
		}

		public void dispose() {
			texture.dispose();
		}
	}

	private static class Tank {
		private Vector2 position;
		private float width, height, halfWidth, halfHeight;
		private Texture texture;
		private float scale;
		private float angle;
		private float speed;

		public Tank(float x, float y) {
			this.position = new Vector2(x, y);
			this.scale = 1.0f;
			texture = new Texture("tank.png");
			this.width = texture.getWidth();
			this.height = texture.getHeight();
			this.halfWidth = this.width / 2;
			this.halfHeight = this.height / 2;
			this.speed = 200.0f;
		}

		public Vector2 getPosition() {
			return position;
		}

		public Vector2 getDimensions() {
			return new Vector2(width, height);
		}

		public Vector2 getCenter() {
			return new Vector2(position.x - width / 2, position.y - height / 2);
		}

		public void update(float dt) {
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				angle += 180.0f * dt;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				angle -= 180.0f * dt;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				if ((angle > 90 && angle < 270) && position.x - width > 0 				// tank moves to the left
						|| (angle < 90 || angle > 270) && position.x + width < 1280)	// tank moves to the right
					position.x += speed * MathUtils.cosDeg(angle) * dt;
				if ((angle > 0 && angle < 180) && position.y + height < 720 			// tank moves up
						|| (angle > 180 && angle < 360) && position.y  - height > 0)	// tank moves down
					position.y += speed * MathUtils.sinDeg(angle) * dt;
			}
		}

		public void render(SpriteBatch batch) {
			batch.draw(texture, position.x, position.y, halfWidth, halfHeight, width, height, scale, scale,
					angle, 0, 0, (int)width, (int)height, false, false);
		}

		public void dispose() {
			texture.dispose();
		}
	}

	private SpriteBatch batch;
	private Tank tank;
	private Circle circle;

	@Override
	public void create () {
		batch = new SpriteBatch();
		tank = new Tank(200, 200);
		circle = new Circle();
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(0, 0.4f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		circle.render(batch);
		tank.render(batch);
		batch.end();
	}

	public void update(float dt) {
		tank.update(dt);
		if (checkCollision(tank, circle)) {
			circle.update();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		tank.dispose();
		circle.dispose();
	}

	private boolean checkCollision(Tank t, Circle c) {
		float tx = t.getCenter().x;
		float ty = t.getCenter().y;
		float tw = t.getDimensions().x;
		float th = t.getDimensions().y;
		float cx = c.getCenter().x;
		float cy = c.getCenter().y;
		float cw = c.getDimensions().x;
		float ch = c.getDimensions().y;
		if ((tx + tw / 2 > cx - cw / 2) && (Math.abs(ty - cy) < th / 2 + ch / 2)		// tank comes from the left side
			|| (tx - tw / 2 < cx + cw / 2) && (Math.abs(ty - cy) < th / 2 + ch / 2)		// tank comes from the right side
			|| (ty - th / 2 < cy + ch / 2) && (Math.abs(tx - cx) < tw / 2 + cw / 2)		// tank comes from the top side
			|| (ty + th / 2 > cy - ch / 2) && (Math.abs(tx - cx) < tw / 2 + cw / 2)) {	// tank comes from the bottom side
			return true;
		}
		return false;
	}
}
