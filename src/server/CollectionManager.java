package server;

import common.HumanBeingReader;
import common.models.Const;
import common.models.HumanBeing;
import server.db.CollectionRepository;
import server.db.DatabaseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

public class CollectionManager extends TreeSet<HumanBeing> {
    private final java.time.LocalDateTime creationTime = java.time.LocalDateTime.now();
    private final CollectionRepository<HumanBeing> repository;

    public CollectionManager(CollectionRepository<HumanBeing> repository) {
        super(new Comparator<HumanBeing>() {
            @Override
            public int compare(HumanBeing o1, HumanBeing o2) {
                return Long.compare(o1.getId(), o2.getId());
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }
        });

        this.repository = repository;
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    /**
     * Старый memory-only метод.
     * Для команд, которые должны сохранять изменения в БД, использовать addToDatabaseAndMemory().
     */
    @Override
    public boolean add(HumanBeing humanBeing) {
        return super.add(humanBeing);
    }

    @Override
    public boolean addAll(Collection<? extends HumanBeing> collection) {
        return super.addAll(collection);
    }

    /**
     * Старый memory-only метод.
     * Для удаления через команды использовать removeFromDatabaseAndMemory().
     */
    @Override
    public boolean remove(Object object) {
        return super.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return super.removeAll(collection);
    }

    /**
     * Старый memory-only метод.
     * Для очистки пользовательских объектов использовать clearDatabaseAndMemory(ownerLogin).
     */
    @Override
    public void clear() {
        super.clear();
    }

    public String getCollectionInfo() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "Информации о коллекции:" +
                "\n>> Тип: " + this.getClass().getGenericSuperclass().getTypeName() +
                "\n>> Дата инициализации: " + this.creationTime +
                "\n>> Количество элементов: " + this.size();
    }

    public String show() {
        return show(this.stream().toList());
    }

    public String show(List<HumanBeing> listHuman) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(
                "%-4s | %-20s | %-11s | %-11s | %-30s | %-10s | %-14s | %-11s | %14s | %16s | %10s | %7s%n",
                Const.FILEHEADER
        ));

        for (HumanBeing human : listHuman) {
            sb.append(String.format(
                    "%-4s | %-20s | %-11s | %-11s | %-30s | %-10s | %-14s | %-11s | %14s | %16s | %10s | %7s%n",
                    HumanBeingReader.extractInfo(human).split(",")
            ));
        }

        return sb.toString().trim();
    }

    public HumanBeing getHumanBy(Predicate<HumanBeing> filter) {
        return this.stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public HumanBeing getHumanById(long id) {
        return this.getHumanBy(human -> human.getId() == id);
    }

    public HumanBeing getHumanByName(String name) {
        return this.getHumanBy(human -> human.getName().equalsIgnoreCase(name));
    }

    /**
     * Старый memory-only метод.
     * Для команд использовать removeFromDatabaseAndMemory(id).
     */
    public boolean removeById(long id) {
        HumanBeing human = getHumanById(id);

        if (human == null) {
            return false;
        }

        return this.remove(human);
    }

    /**
     * Старый memory-only метод.
     * Для команд использовать updateInDatabaseAndMemory(id, newHuman).
     */
    public boolean update(long id, HumanBeing newHuman) {
        HumanBeing oldHuman = getHumanById(id);

        if (oldHuman == null) {
            return false;
        }

        oldHuman.setName(newHuman.getName());
        oldHuman.setCoordinates(newHuman.getCoordinates());
        oldHuman.setRealHero(newHuman.isRealHero());
        oldHuman.setHasToothpick(newHuman.isHasToothpick());
        oldHuman.setImpactSpeed(newHuman.getImpactSpeed());
        oldHuman.setSoundtrackName(newHuman.getSoundtrackName());
        oldHuman.setMinutesOfWaiting(newHuman.getMinutesOfWaiting());
        oldHuman.setWeaponType(newHuman.getWeaponType());
        oldHuman.setCar(newHuman.getCar());
        oldHuman.setOwnerLogin(newHuman.getOwnerLogin());

        return true;
    }

    public HumanBeing getMax() {
        if (this.isEmpty()) {
            return null;
        }

        return this.stream()
                .max(Comparator.comparingLong(HumanBeing::getId))
                .orElse(null);
    }

    public Long getMaxId() {
        HumanBeing max = getMax();
        return max == null ? null : max.getId();
    }

    public HumanBeing getMin() {
        if (this.isEmpty()) {
            return null;
        }

        return this.stream()
                .min(Comparator.comparingLong(HumanBeing::getId))
                .orElse(null);
    }

    /**
     * Старый memory-only метод.
     * Для команд использовать removeGreaterFromDatabaseAndMemory(element).
     */
    public void removeGreater(HumanBeing element) {
        List<HumanBeing> toRemove = new ArrayList<>();

        for (HumanBeing human : this) {
            if (human.compareTo(element) > 0) {
                toRemove.add(human);
            }
        }

        this.removeAll(toRemove);
    }

    /**
     * Загружает коллекцию из БД в память при запуске сервера.
     */
    public void loadFromRepository() throws DatabaseException {
        super.clear();
        super.addAll(repository.loadAll());
    }

    /**
     * Получает следующий id из PostgreSQL sequence.
     */
    public long generateNextId() throws DatabaseException {
        return repository.generateNextId();
    }

    /**
     * Добавление по правилу Issue #3:
     * сначала БД, затем коллекция в памяти.
     */
    public boolean addToDatabaseAndMemory(HumanBeing humanBeing) throws DatabaseException {
        repository.add(humanBeing);
        return super.add(humanBeing);
    }

    /**
     * Обновление по правилу Issue #3:
     * сначала БД, затем объект в памяти.
     */
    public boolean updateInDatabaseAndMemory(long id, HumanBeing newHuman) throws DatabaseException {
        HumanBeing oldHuman = getHumanById(id);

        if (oldHuman == null) {
            return false;
        }

        newHuman.setId(id);

        if (newHuman.getOwnerLogin() == null) {
            newHuman.setOwnerLogin(oldHuman.getOwnerLogin());
        }

        repository.update(newHuman);

        oldHuman.setName(newHuman.getName());
        oldHuman.setCoordinates(newHuman.getCoordinates());
        oldHuman.setRealHero(newHuman.isRealHero());
        oldHuman.setHasToothpick(newHuman.isHasToothpick());
        oldHuman.setImpactSpeed(newHuman.getImpactSpeed());
        oldHuman.setSoundtrackName(newHuman.getSoundtrackName());
        oldHuman.setMinutesOfWaiting(newHuman.getMinutesOfWaiting());
        oldHuman.setWeaponType(newHuman.getWeaponType());
        oldHuman.setCar(newHuman.getCar());
        oldHuman.setOwnerLogin(newHuman.getOwnerLogin());

        return true;
    }

    /**
     * Удаление по правилу Issue #3:
     * сначала БД, затем коллекция в памяти.
     */
    public boolean removeFromDatabaseAndMemory(long id) throws DatabaseException {
        HumanBeing human = getHumanById(id);

        if (human == null) {
            return false;
        }

        repository.remove(id);
        return super.remove(human);
    }

    /**
     * Очистка объектов конкретного пользователя:
     * сначала БД, затем коллекция в памяти.
     */
    public int clearDatabaseAndMemory(String ownerLogin) throws DatabaseException {
        int oldSize = this.size();

        repository.clear(ownerLogin);
        this.removeIf(humanBeing -> ownerLogin.equals(humanBeing.getOwnerLogin()));

        return oldSize - this.size();
    }

    /**
     * Удаляет элементы, которые больше заданного элемента.
     * Сначала удаляет из БД, затем из памяти.
     */
    public int removeGreaterFromDatabaseAndMemory(HumanBeing element) throws DatabaseException {
        List<HumanBeing> toRemove = new ArrayList<>();

        for (HumanBeing human : this) {
            if (human.compareTo(element) > 0) {
                toRemove.add(human);
            }
        }

        for (HumanBeing human : toRemove) {
            repository.remove(human.getId());
        }

        super.removeAll(toRemove);

        return toRemove.size();
    }
}