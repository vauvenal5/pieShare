/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.springConfiguration;

import javax.inject.Provider;
import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleCompleteMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ObjectBasedReceiver;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.ZeroMqClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieDealer;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieRouter;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.ZeroMQUtilsService;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleService;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleCompleteTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PiePlateConfiguration {

    @Autowired
    private PieUtilitiesConfiguration utilities;
	@Autowired
	private ProviderConfiguration providers;

    @Bean
    @Lazy
    public ClusterManagementService clusterManagementService() {
        ClusterManagementService service = new ClusterManagementService();
		service.setClusterServiceProvider(this.clusterServiceProvider());
		service.setLoopHoleFactory(this.loopHoleFactory());
        service.setClusterAddedEventBase(this.utilities.eventBase());
        service.setClusterRemovedEventBase(this.utilities.eventBase());
        service.setMap(this.utilities.javaMap());
        return service;
    }

    @Bean
    @Lazy
    public JacksonSerializerService jacksonSerializerService() {
        return new JacksonSerializerService();
    }

    @Bean
    @Lazy
	@Scope(value = "prototype")
    public ObjectBasedReceiver objectReceiver() {
        ObjectBasedReceiver receiver = new ObjectBasedReceiver();
		receiver.setAddressProvider(this.jgroupsPieAddressProvider());
        receiver.setChannelTaskProvider(this.providers.channelTaskProvider);
        receiver.setExecutorService(this.utilities.pieExecutorService());
        return receiver;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public JChannel jChannel() throws Exception {
        return new JChannel();
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public JGroupsClusterService clusterService() {
		try {
			JGroupsClusterService service = new JGroupsClusterService();
			service.setReceiver(this.objectReceiver());
			service.setChannel(this.jChannel());
			service.setClusterRemovedEventBase(this.utilities.eventBase());
			service.setShutdownService(this.utilities.shutdownService());
			return service;
		} catch (Exception ex) {
			throw new Error("Unexpected JGROUPS error!", ex);
		}
    }
	
	@Bean
	@Lazy
	public Provider<IClusterService> clusterServiceProvider() {
		return new Provider<IClusterService>() {
			@Override
			public IClusterService get() {
				return zeroMqClusterService();
			}
		};
	}

    @Bean
    @Lazy
    public JGroupsPieAddress jgroupsPieAddress() {
        return new JGroupsPieAddress();
    }
	
	@Bean
	@Lazy
	public Provider<IPieAddress> jgroupsPieAddressProvider() {
		return new Provider<IPieAddress>() {
			@Override
			public IPieAddress get() {
				return jgroupsPieAddress();
			}
		};
	}

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public SymmetricEncryptedChannel symmetricEncryptedChannel() {
        SymmetricEncryptedChannel channel = new SymmetricEncryptedChannel();
        channel.setEncoderService(this.utilities.encodeService());
        channel.setSerializerService(this.jacksonSerializerService());
        return channel;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public ChannelTask channelTask() {
        ChannelTask task = new ChannelTask();
        task.setExecutorService(this.utilities.pieExecutorService());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleService loopHoleService() {
        LoopHoleService service = new LoopHoleService();
        service.setLoopHoleListenerTaskProvider(this.providers.loopHoleListenerTaskProvider);
		service.setRegisterMessageProvider(this.providers.registerMessageProvider);
        service.setIdService(utilities.idService());
        service.setSerializerService(jacksonSerializerService());
        service.setExecutorFactory(utilities.pieExecutorTaskFactory());
        service.setExecutorService(utilities.pieExecutorService());
		service.setLoopHoleFactory(this.loopHoleFactory());
		
		PieExecutorTaskFactory factory = this.utilities.pieExecutorTaskFactory();
		factory.registerTaskProvider(LoopHoleConnectionMessage.class, this.providers.loopHoleConnectionTaskProvider);
        factory.registerTaskProvider(LoopHolePunchMessage.class, this.providers.loopHolePuncherTaskProvider);
        factory.registerTaskProvider(LoopHoleAckMessage.class, this.providers.loopHoleAckTaskProvider);
        factory.registerTaskProvider(LoopHoleCompleteMessage.class, this.providers.loopHoleCompleteTaskProvider);
        return service;
    }
	
	@Bean
	@Lazy
	public Provider<ILoopHoleService> loopHoleServiceProvider() {
		return new Provider<ILoopHoleService>() {
			@Override
			public ILoopHoleService get() {
				return loopHoleService();
			}
		};		
	} 

    @Bean
    @Lazy
    public LoopHoleFactory loopHoleFactory() {
        LoopHoleFactory fac = new LoopHoleFactory();
        fac.setLoopHoleServiceProvider(loopHoleServiceProvider());
        fac.setIdService(utilities.idService());
        fac.setSerializerService(jacksonSerializerService());
        fac.setNewLoopHoleConnectionEvent(utilities.eventBase());
        fac.setExecutorFactory(utilities.pieExecutorTaskFactory());
        fac.setExecutorService(utilities.pieExecutorService());
        fac.setUdpPortService(utilities.udpPortService());        
		return fac;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleListenerTask loopHoleListenerTask() {
        LoopHoleListenerTask task = new LoopHoleListenerTask();
        task.setExcuterService(utilities.pieExecutorService());
        task.setSerializerService(jacksonSerializerService());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHolePuncherTask holePuncherTask() {
        LoopHolePuncherTask task = new LoopHolePuncherTask();
        task.setLoopHoleAckMessageProvider(this.providers.loopHoleAckMessageProvider);
        task.setFactory(loopHoleFactory());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleAckTask loopHoleAckTask() {
        LoopHoleAckTask task = new LoopHoleAckTask();
        task.setLoopHoleFactory(loopHoleFactory());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleConnectionTask loopHoleConnectionTask() {
        LoopHoleConnectionTask task = new LoopHoleConnectionTask();
        task.setLoopHoleFactory(this.loopHoleFactory());
        task.setLoopHoleFactory(loopHoleFactory());
		task.setLoopHoleAckMessageProvider(this.providers.loopHoleAckMessageProvider);
		task.setLoopHoleCompleteMessageProvider(this.providers.loopHoleCompleteMessageProvider);
		task.setLoopHolePunchMessageProvider(this.providers.loopHolePunchMessageProvider);
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleCompleteTask loopHoleCompleteTask() {
        LoopHoleCompleteTask task = new LoopHoleCompleteTask();
        task.setLoopholeFactory(loopHoleFactory());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleCompleteMessage loopHoleCompleteMessage() {
        LoopHoleCompleteMessage msg = new LoopHoleCompleteMessage();
        return msg;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public RegisterMessage registerMessage() {
        RegisterMessage message = new RegisterMessage();
        return message;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHolePunchMessage punchMessage() {
        LoopHolePunchMessage msg = new LoopHolePunchMessage();
        return msg;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleConnectionMessage connectionMessage() {
        LoopHoleConnectionMessage msg = new LoopHoleConnectionMessage();
        return msg;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleAckMessage ackMessage() {
        LoopHoleAckMessage msg = new LoopHoleAckMessage();
        return msg;
    }
	
	@Bean
    @Lazy
    public ZeroMQUtilsService zeroMQUtilsService() {
        return new ZeroMQUtilsService();
    }
	
	@Bean
    @Lazy
    public ZeroconfigDiscoveryService discoveryService() {
        ZeroconfigDiscoveryService service = new ZeroconfigDiscoveryService();
		service.setDiscoveredMemberProvider(this.providers.discoveredMemberProvider);
		service.setListener(discoveryListener());
		service.setNetworkService(this.utilities.networkService());
		service.setShutdownService(this.utilities.shutdownService());
		return service;
    }
	
	@Bean
	@Lazy
	public ZeroconfigDiscoveryListener discoveryListener(){
		ZeroconfigDiscoveryListener listener = new ZeroconfigDiscoveryListener();
		listener.setDiscoveredMemberProvider(this.providers.discoveredMemberProvider);
		listener.setMemberDiscoveredEventBase(this.utilities.eventBase());
		return listener;
	}
	
	@Bean
	@Lazy
	public PieRouter pieRouterSocket(){
		PieRouter router = new PieRouter();
		router.setZeroMQUtilsService(zeroMQUtilsService());
		router.setChannelTaskProvider(this.providers.channelTaskProvider);
		router.setExecutorService(this.utilities.pieExecutorService());
		return router;
	}
	
	@Bean
	@Lazy
	public PieDealer pieDealerSocket(){
		PieDealer dealer = new PieDealer();
		dealer.setZeroMQUtilsService(zeroMQUtilsService());
		return dealer;
	}
	
	@Bean
	@Lazy
	public ZeroMqClusterService zeroMqClusterService(){
		ZeroMqClusterService service = new ZeroMqClusterService();
		service.setDiscoveryService(discoveryService());
		service.setNetworkService(this.utilities.networkService());
		service.setPieDealer(pieDealerSocket());
		service.setPieRouter(pieRouterSocket());
		service.setClusterRemovedEventBase(this.utilities.eventBase());
		return service;
	}
	
	@Bean
	@Lazy
	@Scope("prototype")
	public DiscoveredMember discoveredMember(){
		return new DiscoveredMember();
	}
}
