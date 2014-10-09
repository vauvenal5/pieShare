package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.apache.commons.lang3.Validate;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

public class JGroupsClusterService implements IClusterService {

	private IReceiver receiver;
	private ISerializerService serializerService;
	private JChannel channel;
	private IExecutorService executorService;

	public JGroupsClusterService() {
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public void setReceiver(IReceiver receiver) {
		this.receiver = receiver;
	}

	public void setSerializerService(ISerializerService service) {
		this.serializerService = service;
	}

	@Override
	public void connect(String clusterName) throws ClusterServiceException {
		try {
			Validate.notNull(this.receiver);

			this.channel.setReceiver(this.receiver);
			this.channel.setDiscardOwnMessages(true);
			this.channel.connect(clusterName);

		} catch (NullPointerException e) {
			throw new ClusterServiceException("Receiver not set!");
		} catch (Exception e) {
			throw new ClusterServiceException(e);
		}
	}

	@Override
	public void sendMessage(IPieMessage msg) throws ClusterServiceException {
		Address ad = null;

		if (msg.getAddress() instanceof JGroupsPieAddress) {
			ad = ((JGroupsPieAddress) msg.getAddress()).getAddress();
		}

		try {
			System.out.println("Sending: " +  msg.getClass());
			this.channel.send(ad, this.serializerService.serialize(msg));
		} catch (Exception e) {
			throw new ClusterServiceException(e);
		}
	}

	@Override
	public int getMembersCount() {
		return this.channel.getView().getMembers().size();
	}

	@Override
	public boolean isConnectedToCluster() {
		return this.channel.isConnected();
	}

	@Override
	public <P extends IPieMessage, T extends IPieEventTask<P>> void registerTask(Class<P> event, Class<T> task) {
		this.executorService.registerTask(event, task);
	}

	@Override
	public void disconnect() throws ClusterServiceException {
		this.channel.disconnect();
		this.channel.close();
	}

	@Override
	public String getName() {
		return channel.getName();
	}
}
