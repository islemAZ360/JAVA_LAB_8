package common.utils;

import server.CollectionManager;

public interface HandleCommandFile {
    void readFileAndRunScripts(CollectionManager collectionManager, CommandManager commandManager, HumanBeingFileManager humanBeingFileManager);
}
