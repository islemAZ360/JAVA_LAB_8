package main.java.common.utils;

import main.java.server.CollectionManager;

public interface HandleCommandFile {
    void readFileAndRunScripts(CollectionManager collectionManager, CommandManager commandManager, HumanBeingFileManager humanBeingFileManager);
}
