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
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;

public class CollectionManager extends ConcurrentSkipListSet<HumanBeing> {
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

    public long getNextIdInRepository() {
        return this.generateNextId();
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

    //    Factory design pattern
    public HumanBeing generateNewInstance(HumanBeing humanBeing) {
        return new HumanBeing(
            humanBeing.getName(),
            humanBeing.getCoordinates(),
            humanBeing.isRealHero(),
            humanBeing.isHasToothpick(),
            humanBeing.getImpactSpeed(),
            humanBeing.getSoundtrackName(),
            humanBeing.getMinutesOfWaiting(),
            humanBeing.getWeaponType(),
            humanBeing.getCar()
        );
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
        oldHuman.setUserId(newHuman.getUserId());

        return true;
    }

    public synchronized HumanBeing getMax() {
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

    public synchronized HumanBeing getMin() {
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
    public synchronized void loadFromRepository() throws DatabaseException {
        super.clear();
        super.addAll(repository.loadAll());
    }

    /**
     * Получает следующий id из PostgreSQL sequence.
     */
    public synchronized long generateNextId() throws DatabaseException {
        return repository.generateNextId();
    }

    /**
     * Добавление по правилу Issue #3:
     * сначала БД, затем коллекция в памяти.
     */
    public synchronized long addToDatabaseAndMemory(HumanBeing humanBeing) throws DatabaseException {
        long newId = repository.add(humanBeing);
        humanBeing.setId(newId);
        super.add(humanBeing);
        return newId;
    }

    /**
     * Обновление по правилу Issue #3:
     * сначала БД, затем объект в памяти.
     */
    public synchronized boolean updateInDatabaseAndMemory(long id, HumanBeing newHuman) throws DatabaseException {
        HumanBeing oldHuman = getHumanById(id);

        if (oldHuman == null) {
            return false;
        }

        newHuman.setId(id);

        if (newHuman.getUserId() == null) {
            newHuman.setUserId(oldHuman.getUserId());
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
        oldHuman.setUserId(newHuman.getUserId());

        return true;
    }

    /**
     * Удаление по правилу Issue #3:
     * сначала БД, затем коллекция в памяти.
     */
    public synchronized boolean removeFromDatabaseAndMemory(long id) throws DatabaseException {
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
    public synchronized int clearDatabaseAndMemory(Long userId) throws DatabaseException {
        int oldSize = this.size();

        repository.clear(userId);
        this.removeIf(humanBeing -> userId.equals(humanBeing.getUserId()));

        return oldSize - this.size();
    }

    /**
     * Удаляет элементы, которые больше заданного элемента.
     * Сначала удаляет из БД, затем из памяти.
     */
    public synchronized int removeGreaterFromDatabaseAndMemory(HumanBeing element, Long userId) throws DatabaseException {
        List<HumanBeing> toRemove = new ArrayList<>();

        for (HumanBeing human : this) {
            if (human.compareTo(element) > 0
                    && human.getUserId() != null
                    && human.getUserId().equals(userId)) {
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
