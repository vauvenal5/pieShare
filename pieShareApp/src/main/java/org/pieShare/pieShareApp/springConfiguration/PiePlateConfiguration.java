/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.springConfiguration;

import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleCompleteMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ObjectBasedReceiver;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ProtocolFactory;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols.LoopHoleDiscovery;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleService;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleCompleteTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
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
    private PieUtilitiesConfiguration utilitiesConfiguration;

    @Bean
    @Lazy
    public ClusterManagementService clusterManagementService() {
        ClusterManagementService service = new ClusterManagementService();
        service.setBeanService(this.utilitiesConfiguration.beanService());
        service.setClusterAddedEventBase(this.utilitiesConfiguration.eventBase());
        service.setClusterRemovedEventBase(this.utilitiesConfiguration.eventBase());
        service.setMap(this.utilitiesConfiguration.javaMap());
        return service;
    }

    @Bean
    @Lazy
    public JacksonSerializerService jacksonSerializerService() {
        return new JacksonSerializerService();
    }

    @Bean
    @Lazy
    public ObjectBasedReceiver objectReceiver() {
        ObjectBasedReceiver receiver = new ObjectBasedReceiver();
        receiver.setBeanService(this.utilitiesConfiguration.beanService());
        receiver.setExecutorService(this.utilitiesConfiguration.pieExecutorService());
        return receiver;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public JChannel jChannel() throws Exception {
        return new JChannel(this.protocolFactory().getUdpStack());
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public JGroupsClusterService clusterService() throws Exception {
        JGroupsClusterService service = new JGroupsClusterService();
        service.setReceiver(this.objectReceiver());
        service.setChannel(this.jChannel());
        service.setClusterRemovedEventBase(this.utilitiesConfiguration.eventBase());
        return service;
    }

    @Bean
    @Lazy
    public JGroupsPieAddress jgroupsPieAddress() {
        return new JGroupsPieAddress();
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public SymmetricEncryptedChannel symmetricEncryptedChannel() {
        SymmetricEncryptedChannel channel = new SymmetricEncryptedChannel();
        channel.setEncoderService(this.utilitiesConfiguration.encodeService());
        channel.setSerializerService(this.jacksonSerializerService());
        return channel;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public ChannelTask channelTask() {
        ChannelTask task = new ChannelTask();
        task.setExecutorService(this.utilitiesConfiguration.pieExecutorService());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleService loopHoleService() {
        LoopHoleService service = new LoopHoleService();
        service.setBeanService(utilitiesConfiguration.beanService());
        service.setIdService(utilitiesConfiguration.idService());
        service.setSerializerService(jacksonSerializerService());
        service.setExecutorFactory(utilitiesConfiguration.pieExecutorTaskFactory());
        service.setExecutorService(utilitiesConfiguration.pieExecutorService());
        service.setLoopHoleFactory(loopHoleFactory());
        return service;
    }

    @Bean
    @Lazy
    public LoopHoleFactory loopHoleFactory() {
        LoopHoleFactory fac = new LoopHoleFactory();
        fac.setBeanService(utilitiesConfiguration.beanService());
        fac.setIdService(utilitiesConfiguration.idService());
        fac.setSerializerService(jacksonSerializerService());
        fac.setNewLoopHoleConnectionEvent(utilitiesConfiguration.eventBase());
        fac.setExecutorFactory(utilitiesConfiguration.pieExecutorTaskFactory());
        fac.setExecutorService(utilitiesConfiguration.pieExecutorService());
        fac.setUdpPortService(utilitiesConfiguration.udpPortService());
        return fac;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHoleListenerTask loopHoleListenerTask() {
        LoopHoleListenerTask task = new LoopHoleListenerTask();
        task.setExcuterService(utilitiesConfiguration.pieExecutorService());
        task.setSerializerService(jacksonSerializerService());
        return task;
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public LoopHolePuncherTask holePuncherTask() {
        LoopHolePuncherTask task = new LoopHolePuncherTask();
        task.setBeanService(utilitiesConfiguration.beanService());
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
        task.setBeanService(utilitiesConfiguration.beanService());
        task.setLoopHoleFactory(loopHoleFactory());
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
    @Scope(value = "prototype")
	public LoopHoleDiscovery loopHoleDiscovery() {
		LoopHoleDiscovery dis = new LoopHoleDiscovery();
		dis.setLoopHoleFactory(this.loopHoleFactory());
		return dis;
	}
	
	@Bean
	@Lazy
	public ProtocolFactory protocolFactory() {
		ProtocolFactory pf = new ProtocolFactory();
		pf.setBeanService(this.utilitiesConfiguration.beanService());
		return pf;
	}
}
