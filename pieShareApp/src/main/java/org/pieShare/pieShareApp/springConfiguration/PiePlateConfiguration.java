/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.springConfiguration;

import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.ObjectBasedReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
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
	@Scope(value="prototype")
	public JChannel jChannel() throws Exception {
		return new JChannel();
	}
	
	@Bean
	@Lazy
	@Scope(value="prototype")
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
	@Scope(value="prototype")
	public SymmetricEncryptedChannel symmetricEncryptedChannel() {
		SymmetricEncryptedChannel channel = new SymmetricEncryptedChannel();
		channel.setEncoderService(this.utilitiesConfiguration.encodeService());
		channel.setSerializerService(this.jacksonSerializerService());
		return channel;
	}
}
