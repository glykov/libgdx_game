/**
 * Реализация класса Hrojectile
 */
package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private TextureRegion bulletTexture;
    private Vector2 position;
    private Vector2 velocity;
    private boolean readyToFire;

    public Projectile(TextureAtlas atlas) {
        this.bulletTexture = new TextureRegion(atlas.findRegion("bullet"));
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.readyToFire = true;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setup(Vector2 startPosition, float angle) {
        velocity.set(50.0f * MathUtils.cosDeg(angle), 50.0f * MathUtils.sinDeg(angle));
        position.set(startPosition);
    }

    public void update(float dt) {
        position.add(velocity);
    }

    public boolean canFire() {
        return ((position.x < 0 && position.y < 0) && (position.x > 1280 || position.y > 1280));
    }

    public void render(SpriteBatch batch) {
        batch.draw(bulletTexture, position.x, position.y);
    }
}

/**
 * Добавляем пулю в танк
 */
public class Tank {
    // ...
    private Projectile bullet;
    // ...

    public Tank(TextureAtlas atlas, float x, float y) {
        // ...
        // создвем пулю в конструкторе
        this.bullet = new Projectile(atlas);
    }

    // ...
    // создаем геттер для пули, чтобы передаввать ее в контроллер и отрисовщик
    public Projectile getBullet() {return bullet;}

    // ...

    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle -= 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
            moveTimer += dt;
        } else if (getCurrentFrameIndex() != 0)
            moveTimer += dt;
        // устанавливаем позицию пули в соответсвии с позицией танка при нажатии на К
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            bullet.setup(position, angle);
        }
        checkBounds();
    }

    // ...
}
/**
 * Добавляем обновление пули в контроллер
 */
public class GameController {
    // ...
    private Projectile bullet;
    // ...

    public GameController() {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("game.pack"));
        this.map = new BattleMap(atlas);
        this.tank = new Tank(atlas, 200, 200);
        this.bullet = tank.getBullet();
    }

   // ...

    public Projectile getBullet() {
        return bullet;
    }

    public void update(float dt) {
        tank.update(dt);
        bullet.update(dt);
    }
}
/**
 * Получаем пулю из танка в отрисовщике
 */
public class WorldRenderer {
    //...

    public void render() {
        Gdx.gl.glClearColor(0, 0.4f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        gameController.getMap().render(batch);
        gameController.getBullet().render(batch);
        gameController.getTank().render(batch);
        batch.end();
    }
}


