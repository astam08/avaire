package com.avairebot.orion.middleware;

import com.avairebot.orion.Orion;
import com.avairebot.orion.contracts.middleware.AbstractMiddleware;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ProcessCommand extends AbstractMiddleware {

    private final static String COMMAND_OUTPUT = "Executing Command \"%command%\" in \"%category%\" category:"
            + "\n\t\tUser:\t %author%"
            + "\n\t\tServer:\t %server%"
            + "\n\t\tChannel: %channel%"
            + "\n\t\tMessage: %message%";

    private final Pattern argumentsRegEX;

    public ProcessCommand(Orion orion) {
        super(orion);

        this.argumentsRegEX = Pattern.compile("[\\s\"]+|\"([^\"]*)\"", Pattern.MULTILINE);
    }

    @Override
    public boolean handle(Message message, MiddlewareStack stack, String... args) {
        String[] arguments = argumentsRegEX.split(message.getContent());

        orion.logger.info(COMMAND_OUTPUT
                .replace("%command%", stack.getCommand().getName())
                .replace("%category%", stack.getCommandContainer().getCategory().getName())
                .replace("%author%", generateUsername(message))
                .replace("%server%", generateServer(message))
                .replace("%channel%", generateChannel(message))
                .replace("%message%", message.getRawContent())
        );

        return stack.getCommand().onCommand(message, Arrays.copyOfRange(arguments, 1, arguments.length));
    }

    private String generateUsername(Message message) {
        return String.format("%s#%s [%s]",
                message.getAuthor().getName(),
                message.getAuthor().getDiscriminator(),
                message.getAuthor().getId()
        );
    }

    private String generateServer(Message message) {
        if (!message.getChannelType().isGuild()) {
            return "PRIVATE";
        }

        return String.format("%s [%s]",
                message.getGuild().getName(),
                message.getGuild().getId()
        );
    }

    private CharSequence generateChannel(Message message) {
        if (!message.getChannelType().isGuild()) {
            return "PRIVATE";
        }

        return String.format("%s [%s]",
                message.getChannel().getName(),
                message.getChannel().getId()
        );
    }
}
