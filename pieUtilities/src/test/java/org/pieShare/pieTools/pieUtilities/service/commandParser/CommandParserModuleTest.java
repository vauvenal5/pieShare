/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.commandParser;

import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IActionService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;

/**
 *
 * @author vauvenal5
 */
public class CommandParserModuleTest {
    
    public CommandParserModuleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testParseArgs() throws Exception {

        final String programmName = "pieTest";
        final String commandName = "pieDo";

        ICommandParserService commandParserService = new Argparse4jService();

        IActionService action = new IActionService() {
            @Override
            public void doAction(Map<String, Object> args) {
                Assert.assertEquals(((String)args.get("foo")), "MieMie");
                Assert.assertEquals(((int)args.get("bar")), 5);
            }

            @Override
            public String getCommandName() {
                return commandName;
            }

            @Override
            public String getProgramName() {
                return programmName;
            }

            @Override
            public Map<String, Class> getArguments() {
                Map<String, Class> entrys = new HashMap<String, Class>();
                entrys.put("bar", Integer.class);
                entrys.put("--foo", String.class);

                return entrys;
            }
        };

        commandParserService.registerAction(action);

        String[] args  = new String[4];
        args[0] = commandName;
        args[1] = "5";
        args[2] = "--foo";
        args[3] = "MieMie";
        
        commandParserService.parseArgs(args);
    }
}
