/**
 * Tank.java
 */
//package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tank extends GameObject implements Poolable {
    public enum Owner {
        PLAYER, AI
    }

    private Owner ownerType;
    private Weapon weapon;
    private Vector2 destination;
    private TextureRegion[] textures;
    private TextureRegion progressbarTexture;
    // шрифт
    private BitmapFont font16;

    private int hp;
    private float angle;
    private float speed;
    private float rotationSpeed;
    // по умолчанию танк не выбран
    private boolean selected;

    private float moveTimer;
    private float timePerFrame;
    private int container;
    private static final int CONTAINER_CAPACITY = 50;

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    // сброс / установка выбора танка
    public void select(boolean selection) { this.selected = selection;}

    // проверка выбран ли танк
    public boolean isSelected() {
        return selected;
    }

    public Tank(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        // загружаем шрифт, созданный в Assets.java вызовом
        // public void loadAssets() {
        // ...
        //createStandardFont(16);
        // ... }
        this.font16 = Assets.getInstance().getAssetManager().get("fonts/font16.ttf");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 90.0f;
        this.selected = false;
        this.container = 0;
    }

    public void setup(Owner ownerType, float x, float y) {
        this.textures = Assets.getInstance().getAtlas().findRegion("tankanim").split(64,64)[0];
        this.position.set(x, y);
        this.ownerType = ownerType;
        this.speed = 120.0f;
        this.hp = 100;
        this.weapon = new Weapon(Weapon.Type.HARVEST, 3.0f, 1);
        this.destination = new Vector2(position);
    }

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public void update(float dt) {
        // если щелкнули левой кнопкой мышки на танке, то он выбран
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            select(true);
        }
        // если танк выбран, то он едет
        if (selected) {
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                destination.set(Gdx.input.getX(), 720 - Gdx.input.getY());
            }
            if (position.dst(destination) > 3.0f) {
                float angleTo = tmp.set(destination).sub(position).angle();
                if (Math.abs(angle - angleTo) > 3.0f) {
                    if (angle > angleTo) {
                        if (Math.abs(angle - angleTo) <= 180.0f) {
                            angle -= rotationSpeed * dt;
                        } else {
                            angle += rotationSpeed * dt;
                        }
                    } else {
                        if (Math.abs(angle - angleTo) <= 180.0f) {
                            angle += rotationSpeed * dt;
                        } else {
                            angle -= rotationSpeed * dt;
                        }
                    }
                }
                if (angle < 0.0f) {
                    angle += 360.0f;
                }
                if (angle > 360.0f) {
                    angle -= 360.0f;
                }

                moveTimer += dt;
                tmp.set(speed, 0).rotate(angle);
                position.mulAdd(tmp, dt);
                if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {
                    position.mulAdd(tmp, -dt);
                }
            }
            updateWeapon(dt);
            checkBounds();
        }
    }

    public void updateWeapon(float dt) {
        if (weapon.getType() == Weapon.Type.HARVEST) {
            if (gc.getMap().getResourceCount(this) > 0) {
                int result = weapon.use(dt);
                // ограничиваем сбор ресурсов размерами контейнера
                if (result > -1 && container < CONTAINER_CAPACITY) {
                    container += gc.getMap().harvestResource(this, result);
                }
            } else {
                weapon.reset();
            }
        }
    }

    public void checkBounds() {
        if (position.x < 40) {
            position.x = 40;
        }
        if (position.y < 40) {
            position.y = 40;
        }
        if (position.x > 1240) {
            position.x = 1240;
        }
        if (position.y > 680) {
            position.y = 680;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(textures[getCurrentFrameIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
        if (weapon.getType() == Weapon.Type.HARVEST && weapon.getUsageTimePercentage() > 0.0f) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * weapon.getUsageTimePercentage(), 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        }
        // рисуем заполненность контейнера
        font16.draw(batch, "" + container, position.x + 40, position.y + 40);
    }
}
/**
 * TanksController
 */
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TanksController extends ObjectPool<Tank> {
    private GameController gc;

    @Override
    protected Tank newObject() {
        return new Tank(gc);
    }

    public TanksController(GameController gc) {
        this.gc = gc;
    }

    // рисуем танки в обоих списках
    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).render(batch);
        }
    }

    public void setup(float x, float y, Tank.Owner ownerType) {
        //Tank t = getActiveElement();
        // создаем танки в freeList
        Tank t = newObject();
        t.setup(ownerType, x, y);
        freeList.add(t);
    }

    // если танк выбран, должен перекидываться в activeList
    // и получать управление
    // но, у меня ни один из танков не едет
    // не успел разобраться в чем проблема
    public void update(float dt) {
        for (int i = 0; i < freeList.size(); i++) {
            if (freeList.get(i).isSelected()) {
                // activeList должен содкржать только один танк, тот которым ведется управление
                if (activeList.size() == 1) {
                    activeList.get(0).select(false);
                    free(0);
                }
                activate(i);
            }
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}

