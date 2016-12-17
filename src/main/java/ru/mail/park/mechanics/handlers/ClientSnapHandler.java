package ru.mail.park.mechanics.handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.mail.park.mechanics.GameMechanics;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.HandleException;
import ru.mail.park.websocket.MessageHandler;
import ru.mail.park.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;


@Component
public class ClientSnapHandler extends MessageHandler<ClientSnap> {
    @NotNull
    private GameMechanics gameMechanics;
    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public ClientSnapHandler(@NotNull GameMechanics gameMechanics, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(ClientSnap.class);
        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(ClientSnap.class, this);
    }

    @Override
    public void handle(@NotNull ClientSnap message, @NotNull Long forUser) throws HandleException {
        gameMechanics.addClientSnapshot(forUser, message);
    }
}
