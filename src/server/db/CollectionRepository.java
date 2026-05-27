package server.db;

import java.util.List;

public interface CollectionRepository<T> {
    void add(T element) throws DatabaseException;

    void update(T element) throws DatabaseException;

    void remove(long id) throws DatabaseException;

    void clear(String ownerLogin) throws DatabaseException;

    List<T> loadAll() throws DatabaseException;

    long generateNextId() throws DatabaseException;
}