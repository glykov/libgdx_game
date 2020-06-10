/**
 * Попытался реализовать логику ИИ, однако,
 * столкнувшись с тем, что ИИшный харвестер так и не поехал
 * на поиски ресурсов, понял, что запутался в общей
 * архитектуре проекта и не совсем понимаю кто за что отвечает,
 * а главное как классы взаимодейтсвуют друг с другом
 * Было бы здорово, увидеть общую картину в виде схемы классов и их взаимодействия,
 * с Вашими комментариями по архитектуре
 */

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic {
    GameController gc;

    public AiLogic(GameController gc) {
        this.gc = gc;
    }

    public void unitProcessing(AbstractUnit unit) {
        // харвестеры должны искать и собирать ресурсы
        if (unit.getUnitType() == UnitType.HARVESTER) {
            if (gc.getMap().getResourceCount(unit.getPosition()) < 1) {
                // проверять клетки, пока не найдутся ресурсы
                Vector2 destination = findResource(unit.getPosition());
                if (destination != null) unit.moveBy(destination);
            }
        }

        // боевые танки должны стрелять по врагу
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            //
        }
        // все должны уходить из-под обстрела
    }

    // вспомогательная функция для поиска ресурсов
    // если харвестер не стоит на ресурсах, то обследуются
    // соседние клетки (сперва со сдвигом на 1 клетку, далее на 2, 3 и т.д.)
    // вспомогательная функция для поиска ресурсов
    public Vector2 findResource(Vector2 unitPosition) {
        Vector2 result = new Vector2();
        float cx = unitPosition.x;
        float cy = unitPosition.y;
        // ищем ресурсы в 10 клетках во всех направлениях
        for  (int i = 1; i <= 10; i++){
            // добавил функцию getCellSize()  в BattleMap
            int offset = gc.getMap().getCellSize();
            offset *= i;
            for (int adjustmentX = -1; adjustmentX <= 1; adjustmentX++) {
                for (int adjustmentY = -1; adjustmentY <= 1; adjustmentY++) {
                    float x = cx + adjustmentX * offset;
                    float y = cy + adjustmentY * offset;
                    // check bounds
                    x = x < 0 ? 0 : x;
                    x = x > 1200 ? 1200 : x;
                    y = y < 0 ? 0 : y;
                    y = y > 640 ? 640 : y;
                    result.set(x, y);
                    if (gc.getMap().getResourceCount(result) > 0) {
                        return result;
                    }
                } 
            }
        }
        return null;
    }


    public void update(float dt) {
        List<AbstractUnit> ul = gc.getUnitsController().getAiUnits();
        for (int i = 0; i < ul.size(); i++) {
            AbstractUnit u = ul.get(i);
            unitProcessing(u);
        }
    }
}
