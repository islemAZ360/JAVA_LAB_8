package client.gui.integration;

import client.gui.model.HumanBeingUiModel;

import java.util.List;

/**
 * Adapter boundary between JavaFX GUI and old Lab 7 client-server logic.
 * Implement this interface by calling your existing Request/Response commands.
 */
public interface Lab7CommandGateway {
    AuthResult login(String username, String password);
    AuthResult register(String username, String password);

    List<HumanBeingUiModel> show();
    String info();
    HumanBeingUiModel add(HumanBeingUiModel humanBeing);
    HumanBeingUiModel update(long id, HumanBeingUiModel humanBeing);
    boolean removeById(long id);
    int clearMine();
    HumanBeingUiModel addIfMax(HumanBeingUiModel humanBeing);
    HumanBeingUiModel addIfMin(HumanBeingUiModel humanBeing);
    int removeGreater(HumanBeingUiModel humanBeing);
}
