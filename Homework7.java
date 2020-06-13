/**
 * До конца реализовать не удалось, ресурсы харветсеры не сбрасывают,
 * удалось релизовать сбор ресурсов и заезад на базу
 * но, так как сброс дожне происходить в области buildingEntrance, 
 * а сам Building не знает где у него вход, пока не нашел простого способа
 * направить туда харвестер
 * буду еще дорабатывать )))
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.BattleMap;
import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.controllers.BuildingsController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic extends BaseLogic {
    private float timer;

    private List<BattleTank> tmpAiBattleTanks;
    // список сборщиков ИИ
    private List<Harvester> tmpAiHarvesters;
    private List<Harvester> tmpPlayerHarvesters;
    private List<BattleTank> tmpPlayerBattleTanks;

    public AiLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.tmpAiBattleTanks = new ArrayList<>();
        this.tmpAiHarvesters = new ArrayList<>();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpPlayerBattleTanks = new ArrayList<>();
        this.timer = 10000.0f;
    }

    public void update(float dt) {
        timer += dt;
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanks(tmpAiBattleTanks, gc.getUnitsController().getAiUnits(), UnitType.BATTLE_TANK);
            // collect AI harvesters
            gc.getUnitsController().collectTanks(tmpAiHarvesters, gc.getUnitsController().getAiUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerBattleTanks, gc.getUnitsController().getPlayerUnits(), UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpAiBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpAiBattleTanks.get(i);
                aiBattleTank.commandAttack(findNearestTarget(aiBattleTank, tmpPlayerBattleTanks));
            }
            for (int i = 0; i < tmpAiHarvesters.size(); i++) {
                Harvester harvester = tmpAiHarvesters.get(i);
                if (!harvester.isFull()) {
                    collectResources(harvester);
                } else {
                    dropResourses(harvester);
                }
            }
        }
    }

    public <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < possibleTargetList.size(); i++) {
            T possibleTarget = possibleTargetList.get(i);
            float currentDst = currentTank.getPosition().dst(possibleTarget.getPosition());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }

    public Vector2 findNearestResource(Vector2 unitPosition) {
        Vector2 resourcePosition = new Vector2(unitPosition);
        Vector2 tmp = new Vector2();
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < gc.getMap().COLUMNS_COUNT; i++) {
            for (int j = 0; j < gc.getMap().ROWS_COUNT; j++) {
                tmp.set(i * gc.getMap().CELL_SIZE, j * gc.getMap().CELL_SIZE);
                if (gc.getMap().getResourceCount(tmp) > 1) {
                    // можно еще реализовать выбор наиболее "богатой" клетки
                    float curDistance = unitPosition.dst(tmp);
                    if (curDistance < minDistance) {
                        minDistance = curDistance;
                        resourcePosition.set(tmp);
                    }

                }
            }
        }
        return resourcePosition;
    }

    public void collectResources(Harvester harvester) {
        Vector2 tmp;
        tmp = harvester.getPosition();
        // сдвигаем в центр клетки
        tmp.x += 40;
        tmp.y += 40;
        harvester.commandMoveTo(findNearestResource(tmp));
    }

    public void dropResourses(Harvester harvester) {
        BuildingsController bc = gc.getBuildingsController();
        List<Building> buildings = bc.getActiveList();
        for (Building b : buildings) {
            if (b.getOwnerLogic().getOwnerType() == Owner.AI) {
                harvester.commandMoveTo(b.getPosition());
            }
        }
    }
}
