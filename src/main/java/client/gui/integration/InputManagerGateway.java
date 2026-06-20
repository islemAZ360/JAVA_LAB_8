package main.java.client.gui.integration;

import main.java.client.gui.core.InputManager;
import main.java.client.gui.core.RequestSender;
import main.java.client.auth.Account;
import main.java.client.gui.model.CarUiModel;
import main.java.client.gui.model.CoordinatesUiModel;
import main.java.client.gui.model.HumanBeingUiModel;
import main.java.client.gui.model.WeaponTypeUi;
import main.java.common.Request;
import main.java.common.Response;
import main.java.common.models.Car;
import main.java.common.models.Const;
import main.java.common.models.Coordinates;
import main.java.common.models.HumanBeing;
import main.java.common.models.WeaponType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
*   InputManagerGateway — adapter connecting the GUI to the backend.

*   Clear separation of concerns:

*   InputManager.handleCommand() = main gateway, used for commands that do not require stdin from the GUI:
*   show, info, removeById, removeGreater, clear

*   reqSender.sendRequest() = direct bypass, used when the GUI already has the data ready:
*   login, register, add, update, addIfMax, addIfMin
*   (these commands in InputManager read from Scanner -- unusable in GUI)

*   Flow:
*   [UI] -> Lab7CommandGateway -> InputManagerGateway -> InputManager / RequestSender -> Server
*/
public class InputManagerGateway implements Lab7CommandGateway {

    private final RequestSender sender;
    private final InputManager  inputManager;
    private String currentUser = null;

    // -
    // Initialization
    // -

    public InputManagerGateway(RequestSender sender) {
        this.sender       = sender;
        // Empty Scanner — InputManager requires it in the constructor,
        // but the commands we use (show, info...) do not read stdin
        this.inputManager = new InputManager(new Scanner(System.in), sender);
    }

    /**
     * Opens a TCP connection, drains the initial handshake, and returns the ready gateway.
     * Throws IOException if the main.java.server does not respond — App decides the fallback behavior.
     */
    public static InputManagerGateway connect(String host, int port) throws IOException {
        Socket           socket = new Socket(host, port);
        DataOutputStream dos    = new DataOutputStream(socket.getOutputStream());
        DataInputStream  dis    = new DataInputStream(socket.getInputStream());
        RequestSender    sender = new RequestSender(dos, dis);

        // Drain handshake sent by the main.java.server immediately upon main.java.client connection
        int    size = dis.readInt();
        byte[] data = new byte[size];
        dis.readFully(data);

        return new InputManagerGateway(sender);
    }

    /** Shortcut using Const.host / Const.port. */
    public static InputManagerGateway connectDefault() throws IOException {
        return connect(Const.host, Const.port);
    }

    // -
    // AUTH — bypass InputManager (it reads Scanner, GUI already has the data ready)
    // -

    @Override
    public AuthResult login(String username, String password) {
        try {
            Response resp = sender.sendRequest(
                new Request("login", null, new Account(username, password))
            );
            if (resp.isSuccess()) {
                currentUser = username;
                return AuthResult.ok(username);
            }
            return AuthResult.error(resp.getMessage());
        } catch (IOException e) {
            return AuthResult.error("Ошибка соединения: " + e.getMessage());
        }
    }

    @Override
    public AuthResult register(String username, String password) {
        try {
            Response resp = sender.sendRequest(
                new Request("register", null, new Account(username, password))
            );
            if (resp.isSuccess()) {
                currentUser = username;
                return AuthResult.ok(username);
            }
            return AuthResult.error(resp.getMessage());
        } catch (IOException e) {
            return AuthResult.error("Ошибка соединения: " + e.getMessage());
        }
    }

    // -
    // READ — use InputManager.handleCommand() since it doesn't read stdin
    // -

    @Override
    @SuppressWarnings("unchecked")
    public List<HumanBeingUiModel> show() {
        try {
            Response resp = inputManager.handleCommand("show");
            Object   raw  = resp.getData();
            System.out.println(raw);

            if (raw instanceof List<?> list) {
                List<HumanBeingUiModel> result = new ArrayList<>(list.size());
                for (Object item : list) {
                    if (item instanceof HumanBeing hb) result.add(toUiModel(hb));
                }
                return result;
            }
            return List.of(); // old main.java.server returns text, not a List — UI displays empty
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public String info() {
        try {
            return inputManager.handleCommand("info").getMessage();
        } catch (IOException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    // -
    // WRITE — bypass InputManager (it reads Scanner to get HumanBeing)
    // GUI already has the object ready -> pass it directly as objectArgument
    // -

    @Override
    public HumanBeingUiModel add(HumanBeingUiModel model) {
        if (model == null) return null;
        try {
            Response resp = sender.sendRequest(
                new Request("add", null, toDomain(model))
            );
            if (!resp.isSuccess()) return null;
            if (resp.getData() instanceof HumanBeing saved) return toUiModel(saved);
            return model;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public HumanBeingUiModel update(long id, HumanBeingUiModel model) {
        if (model == null) return null;
        try {
            // Step 1 — check ID (InputManager sends a null object, main.java.server returns CONTINUE)
            Response check = sender.sendRequest(
                new Request("update", String.valueOf(id), null)
            );
            if (check.getStatusCode() != main.java.common.StatusCode.CONTINUE) return null;

            // Step 2 — send the new object (GUI already exists, no Scanner read needed)
            Response resp = sender.sendRequest(
                new Request("update", String.valueOf(id), toDomain(model))
            );
            if (!resp.isSuccess()) return null;
            if (resp.getData() instanceof HumanBeing saved) return toUiModel(saved);
            return model;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean removeById(long id) {
        try {
            // InputManager handles "remove\_by\_id " without reading stdin further -> usable
            Response resp = inputManager.handleCommand("remove_by_id " + id);
            return resp.isSuccess();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int clearMine() {
        try {
            // Step 1 — InputManager sends "clear", main.java.server returns CONTINUE + asks for confirmation
            Response check = sender.sendRequest(new Request("clear", null, null));
            if (check.getStatusCode() != main.java.common.StatusCode.CONTINUE) return 0;

            // Step 2 — GUI automatically confirms "yes" (no need to prompt the user again)
            Response resp = sender.sendRequest(new Request("clear", "yes", null));
            return resp.isSuccess() ? 1 : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public HumanBeingUiModel addIfMax(HumanBeingUiModel model) {
        if (model == null) return null;
        try {
            // Step 1 — check condition
            Response check = sender.sendRequest(
                new Request("add_if_max", String.valueOf(model.id()), null)
            );
            if (check.getStatusCode() != main.java.common.StatusCode.CONTINUE) return null;

            // Step 2 — send object
            Response resp = sender.sendRequest(
                new Request("add_if_max", String.valueOf(model.id()), toDomain(model))
            );
            return resp.isSuccess() ? model : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public HumanBeingUiModel addIfMin(HumanBeingUiModel model) {
        if (model == null) return null;
        try {
            Response check = sender.sendRequest(
                new Request("add_if_min", String.valueOf(model.id()), null)
            );
            if (check.getStatusCode() != main.java.common.StatusCode.CONTINUE) return null;

            Response resp = sender.sendRequest(
                new Request("add_if_min", String.valueOf(model.id()), toDomain(model))
            );
            return resp.isSuccess() ? model : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int removeGreater(HumanBeingUiModel model) {
        if (model == null) return 0;
        try {
            // InputManager handles "remove\_greater" with objectArgument — direct bypass
            Response resp = sender.sendRequest(
                new Request("remove_greater", null, toDomain(model))
            );
            return resp.isSuccess() ? 1 : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    // выполняем произвольную команду через InputManager — те команды, что не читают stdin
    @Override
    public CommandResult executeRawCommand(String command) {
        if (command == null || command.isBlank()) {
            return CommandResult.fail("Пустая команда");
        }
        try {
            Response resp = inputManager.handleCommand(command);
            if (resp == null) {
                return CommandResult.fail("Нет ответа от сервера");
            }
            return new CommandResult(
                    resp.getMessage() == null ? "" : resp.getMessage(),
                    resp.isSuccess()
            );
        } catch (IOException e) {
            return CommandResult.fail("Ошибка соединения с сервером: " + e.getMessage());
        } catch (Exception e) {
            return CommandResult.fail("Ошибка выполнения команды: " + e.getMessage());
        }
    }

    // -
    // MAPPING: HumanBeingUiModel -> HumanBeing (domain)
    // -

    private HumanBeing toDomain(HumanBeingUiModel m) {
        return new HumanBeing(
                m.name(),
                new Coordinates(m.coordinates().x(), m.coordinates().y()),
                m.realHero(),
                m.hasToothpick(),
                m.impactSpeed(),
                "Default Track", // soundtrackName — поля в UI нет, ставим значение по умолчанию, иначе сервер отбивает объект
                (int) m.minutesOfWaiting(), // domain uses int, UI uses long
                toWeaponDomain(m.weaponType()),
                new Car(m.car().cool()) // Car domain only has cool, no name
        );
    }

    // -
    // MAPPING: HumanBeing (domain) -> HumanBeingUiModel
    // -

    private HumanBeingUiModel toUiModel(HumanBeing hb) {
        Car domainCar = hb.getCar();
        return new HumanBeingUiModel(
            hb.getId(),
            hb.getName(),
            new CoordinatesUiModel(
                hb.getCoordinates().getX(),
                hb.getCoordinates().getY()
            ),
            hb.getCreationDate() != null ? hb.getCreationDate() : LocalDateTime.now(),
            hb.isRealHero(),
            hb.isHasToothpick(),
            hb.getImpactSpeed(),
            hb.getMinutesOfWaiting(),
            toWeaponUi(hb.getWeaponType()),
            new CarUiModel("", domainCar != null && domainCar.isCool()),
            // берём реального владельца из доменного объекта, а не текущего пользователя
            hb.getUserId() != null ? String.valueOf(hb.getUserId()) : "unknown"
        );
    }

    // -
    // ENUM MAPPING — 1-to-1 mapping after synchronizing WeaponTypeUi with WeaponType
    // -

    private WeaponType toWeaponDomain(WeaponTypeUi ui) {
        if (ui == null) return null;
        return switch (ui) {
            case HAMMER      -> WeaponType.HAMMER;
            case RIFLE       -> WeaponType.RIFLE;
            case KNIFE       -> WeaponType.KNIFE;
            case MACHINE_GUN -> WeaponType.MACHINE_GUN;
        };
    }

    private WeaponTypeUi toWeaponUi(WeaponType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case HAMMER      -> WeaponTypeUi.HAMMER;
            case RIFLE       -> WeaponTypeUi.RIFLE;
            case KNIFE       -> WeaponTypeUi.KNIFE;
            case MACHINE_GUN -> WeaponTypeUi.MACHINE_GUN;
        };
    }
}
