package org.pieShare.pieTools.pieUtilities.service.commandParser;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IAction;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;

import java.util.Map;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class Argparse4jService implements ICommandParserService {

    private String programName;
    private ArgumentParser parser;
    private Subparsers subParsers;

    private void init(String name) {
        this.programName = name;
        this.parser = ArgumentParsers.newArgumentParser(name);
        this.subParsers = this.parser.addSubparsers();
    }

    @Override
    public void parseArgs(String[] args) throws CommandParserServiceException {
        try {
            Namespace n = parser.parseArgs(args);
            ((IAction)n.get("func")).doAction(n.getAttrs());
        }
        catch (ArgumentParserException ex) {
            throw new CommandParserServiceException("Arguments could not be parsed!", ex);
        }
    }

    @Override
    public void registerAction(IAction action) throws CommandParserServiceException {
        try {
            Validate.notNull(action);
            Validate.notBlank(action.getCommandName());
            Validate.notBlank(action.getProgramName());

            if(this.parser == null) {
                this.init(action.getProgramName());
            }
            else {
                Validate.matchesPattern(action.getProgramName(), this.programName);
            }

            Subparser parser = this.subParsers.addParser(action.getCommandName()).setDefault("func", action);

            for(Map.Entry<String, Class> entry: action.getArguments().entrySet()) {
                parser.addArgument(entry.getKey()).type(entry.getValue());
            }
        }
        catch (NullPointerException | IllegalArgumentException ex) {
            throw new CommandParserServiceException("Null value not allowed plus the program and command name must be set!", ex);
        }
    }
}
