 // Домашнее задание:
 // 1. Разбор кода, в домашке задавайте вопросы что не ясно
 // 2. На поле должны быть рассыпаны ресурсы, танк должен их собирать
 // когда по ним проезжает

 /**
  * В виде ресурсов выступают поля с цветочками
  * для их создания добавлен класс Flowers, наследуемый от GameObject
  * и реализующий интерфейс Poolable 
  */
public class Flowers extends GameObject implements Poolable {
    private TextureRegion flowersTexture;
    private boolean active;

    // в конструкторе сразу загружаем текстуру,
    // потому что не сразу догадался, что можно сделать более общий
    // класс Resource, а текстуру подгружать в ResourceController
    // в зависимости от типа ресурса
    // тут же задаем случайную позицию, т.к. цветочки двигаться не будут
    // а будут только активироваться/деактивироваться, то и позиция меняться не будет
    public Flowers(GameController gc) {
        super(gc);
        flowersTexture = Assets.getInstance().getAtlas().findRegion("flowers");
        active = true;
        position = new Vector2(MathUtils.random(1200) + 40, MathUtils.random(640) + 40);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(flowersTexture, position.x - 40, position.y - 40);
    }
}

/**
 * Для хранения ресурсов создан класс FlowersController
 * унаследованный от ObjectPool
 */
public class FlowersController extends ObjectPool<Flowers> {
    GameController gc;

    public FlowersController(GameController gc) {
        this.gc = gc;
    }

    // сразу создаем цветочки в активном списке,
    // т.к. они должны появиться при загрузке игры
    public void setup(int flowerFieldCount) {
        for (int i = 0; i < flowerFieldCount; i++) {
            activeList.add(newObject());
        }
    }

    @Override
    protected Flowers newObject() {
        return new Flowers(gc);
    }

    // отрисовываем цветочки только из активного списка
    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
/**
 * В класс GameController добавляем FlowersController
 */
public class GameController {
    //....
    private FlowersController flowersController;

    // ...
    // геттер на FlowersController
    public FlowersController getFlowersController() {
        return flowersController;
    }

    // ...

    // Инициализация игровой логики
    public GameController() {
        // в конструкторе создаем объект FlowersConroller
        // и сразу в нем создаем 5 полей с цветочками 
        this.flowersController = new FlowersController(this);
        this.flowersController.setup(5);
        // ...
    }

    public void update(float dt) {
        // проверяем пул активных ресурсов
        // соответсвенно, те поля, которые деактивированы
        // будут перемещены во freeList и не будут отрисованы при
        // следующем рендере 
        flowersController.checkPool();
    }

    // проверяем наезд танка на поле, а не просто касание (поэтому разница координат 40)
    // вынимаем ссылку activeList во временную переменную, потом пробегаемся по ней
    // сравниваем координаты каждого элемента с координатами танка
    // в случае наезда на поле - деактивируем его
    public void checkCollisions(float dt) {
        List<Flowers> tmp = flowersController.getActiveList();
        for (int i = 0; i < tmp.size(); i++) {
            Flowers f = tmp.get(i);
            if (Math.abs(f.getPosition().x - tank.getPosition().x) < 40
                    && Math.abs(f.getPosition().y - tank.getPosition().y) < 40) {
                f.deactivate();
            }
        }
    }
}
/**
 * В классе WorldRenderer не забываем отрисовать поле с ресурсами
 */
public class WorldRenderer {
// ...
    public void render() {
        // ...
        gc.getFlowersController().render(batch);
        gc.getTank().render(batch);
        // ...
    }
}
