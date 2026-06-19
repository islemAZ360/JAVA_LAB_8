package main.java.common.utils;

import main.java.common.models.HumanBeing;
import main.java.server.CollectionManager;

import java.util.Collection;
@Deprecated
public interface HandleHumanBeingFile {
    void readFileAndLoadHumanBeing(CollectionManager collectionManager);
    void save(HumanBeing human);
    void saveOne(HumanBeing human);
    void saveAll(Collection<HumanBeing> collection);
}
