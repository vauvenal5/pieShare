package org.pieShare.pieTools.pieCeption.service.core;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieUtilities.service.commandParser.Argparse4jService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IAction;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 19.03.14.
 */
public class PieCeptionServiceIntegrationTest {

    private ApplicationContext context = null;

    @Before
    public void before() {
        context = new ClassPathXmlApplicationContext("pieCeption_test_context_file.xml");
    }


    @Test
    public void testParseArgs() throws Exception {

        final String programmName = "pieTest";
        final String commandName = "pieDo";

        IPieCeptionService pieCeptionService = context.getBean(PieCeptionService.class);
        ClusterConnectorService clusterConnectorService = context.getBean(ClusterConnectorService.class);
        ICommandParserService commandParserService = context.getBean(Argparse4jService.class);

        IAction action = new IAction() {



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

        IClusterService clusterService = Mockito.mock(IClusterService.class);
        Mockito.when(clusterService.isConnectedToCluster()).thenReturn(true);
        Mockito.when(clusterService.getMembersCount()).thenReturn(2);

        clusterConnectorService.setClusterService(clusterService);
        clusterConnectorService.setServiceName("ServiceName");

        String[] args  = new String[4];
        args[0] = commandName;
        args[1] = "5";
        args[2] = "--foo";
        args[3] = "MieMie";

        pieCeptionService.parseArgs(args);
    }


}
