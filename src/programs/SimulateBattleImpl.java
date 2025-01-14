package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Логгер для вывода логов после каждой атаки

    public SimulateBattleImpl(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Получаем список юнитов каждой армии
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        // Симуляция до тех пор, пока обе армии имеют юнитов для атаки
        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {
            // Перед каждым раундом сортируем юнитов по убыванию их атакующих характеристик
            sortUnitsByAttack(playerUnits);
            sortUnitsByAttack(computerUnits);

            // Выполняем один раунд боевых действий
            simulateRound(playerUnits, computerUnits);
        }
    }

    // Метод для проверки, есть ли живые юниты в армии
    private boolean hasAliveUnits(List<Unit> units) {
        return units.stream().anyMatch(Unit::isAlive);
    }

    // Метод для сортировки юнитов по атакующему значению (по убыванию)
    private void sortUnitsByAttack(List<Unit> units) {
        units.sort((unit1, unit2) -> Integer.compare(unit2.getBaseAttack(), unit1.getBaseAttack()));
    }

    // Метод, который симулирует один раунд боя
    private void simulateRound(List<Unit> playerUnits, List<Unit> computerUnits) throws InterruptedException {
        // Первая очередь атакует — армия игрока
        performAttacks(playerUnits, computerUnits);

        // Вторая очередь атакует — армия компьютера
        performAttacks(computerUnits, playerUnits);
    }

    // Метод, который осуществляет атаки для указанной армии
    private void performAttacks(List<Unit> attackingUnits, List<Unit> defendingUnits) throws InterruptedException {
        Iterator<Unit> iterator = attackingUnits.iterator();

        while (iterator.hasNext()) {
            Unit attackingUnit = iterator.next();

            // Если юнит мертв, удаляем его из списка
            if (!attackingUnit.isAlive()) {
                iterator.remove();
                continue;
            }

            // Юнит атакует противника
            Unit target = attackingUnit.getProgram().attack();

            // Если атака успешна (цель найдена), логируем атаку
            if (target != null) {
                printBattleLog.printBattleLog(attackingUnit, target);
            }

            // Если юнит не жив, удаляем его из очереди атакующих
            if (!attackingUnit.isAlive()) {
                iterator.remove();
            }
        }
    }
}

// Сортировка юнитов: O(n log n)
//Добавление юнитов: O(n * m) (где m = 11)
//Итоговая сложность: 𝑂(𝑛∗𝑚)
