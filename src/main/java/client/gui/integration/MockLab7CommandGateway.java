package main.java.client.gui.integration;

import main.java.client.gui.model.HumanBeingUiModel;
import main.java.client.gui.model.SampleData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MockLab7CommandGateway implements Lab7CommandGateway {
    private final List<HumanBeingUiModel> items = new ArrayList<>(SampleData.humans());
    private String currentUser = "user1";

    @Override
    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank()) return AuthResult.error("Введите логин");
        currentUser = username.trim();
        return AuthResult.ok(currentUser);
    }

    @Override
    public AuthResult register(String username, String password) {
        if (username == null || username.isBlank()) return AuthResult.error("Введите логин");
        currentUser = username.trim();
        return AuthResult.ok(currentUser);
    }

    @Override
    public List<HumanBeingUiModel> show() {
        return List.copyOf(items);
    }

    @Override
    public String info() {
        long mine = items.stream().filter(h -> h.ownerLogin().equals(currentUser)).count();
        return "Collection size: " + items.size() + ", current user: " + currentUser + ", my objects: " + mine;
    }

    @Override
    public HumanBeingUiModel add(HumanBeingUiModel humanBeing) {
        if (humanBeing == null) return null;
        items.add(humanBeing);
        return humanBeing;
    }

    @Override
    public HumanBeingUiModel update(long id, HumanBeingUiModel humanBeing) {
        if (humanBeing == null) return null;
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
        if (humanBeing == null) return null;
        double max = items.stream()
                .map(HumanBeingUiModel::impactSpeed)
                .max(Comparator.naturalOrder())
                .orElse(Double.NEGATIVE_INFINITY);
        if (humanBeing.impactSpeed() > max) {
            items.add(humanBeing);
            return humanBeing;
        }
        return null;
    }

    @Override
    public HumanBeingUiModel addIfMin(HumanBeingUiModel humanBeing) {
        if (humanBeing == null) return null;
        double min = items.stream()
                .map(HumanBeingUiModel::impactSpeed)
                .min(Comparator.naturalOrder())
                .orElse(Double.POSITIVE_INFINITY);
        if (humanBeing.impactSpeed() < min) {
            items.add(humanBeing);
            return humanBeing;
        }
        return null;
    }

    @Override
    public int removeGreater(HumanBeingUiModel humanBeing) {
        if (humanBeing == null) return 0;
        int before = items.size();
        items.removeIf(h -> h.ownerLogin().equals(currentUser)
                && h.impactSpeed() > humanBeing.impactSpeed());
        return before - items.size();
    }
}
