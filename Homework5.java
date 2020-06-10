/**
 * Основная работа происходит в GameController
 * сюда добавил функцию checkBulletHit, проверяющую 
 * попадание снаряда во вражеский танк
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {
    // ...

    // ....

    // В update проверяем попадание снаряда
    public void update(float dt) {
        tanksController.update(dt);
        projectilesController.update(dt);
        map.update(dt);
        checkCollisions(dt);
        checkBulletHit(); // <---
        // checkSelection();
    }

    // проверяем попадание снаряда во вражеский танк
    // при попадании уменьшаем хп вражеского танка
    // при уменьшении хп  <= 0 удаляем вражеский танк
    public void checkBulletHit() {
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            for (int j = 0; j < tanksController.getActiveList().size(); j++) {
                Tank t = tanksController.getActiveList().get(j);
                if (t.getOwnerType() == Tank.Owner.AI && p.getPosition().dst(t.getPosition()) < 30) {
                    t.setHp(p.getDamage(), true);
                    if (!t.isActive())
                        tanksController.free(j);
                    p.deactivate();
                }
            }
        }
    }

    // ....
}
/**
 * В танк добавляем геттер и сеттер на хп 
 */
public class Tank extends GameObject implements Poolable {
    // ...

    // проверяем хп танка (можно потом использовать для отрисовки
    // прогресс бара со значением здоровья)
    public int getHp() {
        return hp;
    }

    // устанавливаем хп танка
    // если damage - true, то значение value отнимается от хп
    // если damage - false, то танк будет хилиться (heal) и value будет прибавляться к хп
    public void setHp(int value, boolean damage) {
        if (damage)
            this.hp -= value;
        else
            this.hp  += this.hp < maxHp ? value : 0;
    }

    // ...
}
/**
 * В Projectile добавляем значение damage - будет зависить от типа снаряда
 */
public class Projectile extends GameObject implements Poolable {
    // ...
    // how much damage will be caused to target
    private int damage;

    // ...

    public void setup(Vector2 startPosition, float angle, TextureRegion texture) {
        this.texture = texture;
        this.position.set(startPosition);
        this.angle = angle;
        this.velocity.set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle));
        this.active = true;
        // just for testing purposes
        this.damage = 20;
    }

    // ...
}
/**
 * 
 */
