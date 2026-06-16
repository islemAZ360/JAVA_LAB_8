package client.ui.template.integration;

import client.ui.template.model.HumanBeingUiModel;
import client.ui.template.model.SampleData;

import java.util.ArrayList;
import java.util.List;

public class MockLab7CommandGateway implements Lab7CommandGateway {
    private final List<HumanBeingUiModel> items = new ArrayList<>(SampleData.humans());
    private String currentUser = "user1";

    @Override
    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank()) return AuthResult.error("Введите логин");
        currentUser = username;
        return AuthResult.ok(username);
    }

    @Override
    public AuthResult register(String username, String password) {
        if (username == null || username.isBlank()) return AuthResult.error("Введите логин");
        currentUser = username;
        return AuthResult.ok(username);
    }

    @Override
    public List<HumanBeingUiModel> show() {
        return List.copyOf(items);
    }

    @Override
    public String info() {
        return "Collection size: " + items.size();
    }

    @Override
    public HumanBeingUiModel add(HumanBeingUiModel humanBeing) {
        items.add(humanBeing);
        return humanBeing;
    }

    @Override
    public HumanBeingUiModel update(long id, HumanBeingUiModel humanBeing) {
        removeById(id);
        items.add(humanBeing);
        return humanBeing;
    }

    @Override
    public boolean removeById(long id) {
        return items.removeIf(h -> h.id() == id);
    }

    @Override
    public int clearMine() {
        int before = items.size();
        items.removeIf(h -> h.ownerLogin().equals(currentUser));
        return before - items.size();
    }

    @Override
    public HumanBeingUiModel addIfMax(HumanBeingUiModel humanBeing) {
        items.add(humanBeing);
        return humanBeing;
    }

    @Override
    public HumanBeingUiModel addIfMin(HumanBeingUiModel humanBeing) {
        items.add(humanBeing);
        return humanBeing;
    }

    @Override
    public int removeGreater(HumanBeingUiModel humanBeing) {
        return 0;
    }
}
