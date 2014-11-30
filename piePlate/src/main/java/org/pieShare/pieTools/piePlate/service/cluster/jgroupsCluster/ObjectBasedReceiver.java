package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.ITwoWayChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements Receiver {

	private IExecutorService executorService;
	private IBeanService beanService;
	
	private IClusterService clusterService;

	public void setExecutorService(IExecutorService service) {
		this.executorService = service;
	}

	public void setBeanService(IBeanService service) {
		this.beanService = service;
	}

	public void setClusterService(IClusterService clusterService) {
		this.clusterService = clusterService;
	}

	@Override
	public void receive(Message msg) {
		
		for(IIncomingChannel channel: this.clusterService.getIncomingChannels()) {
			ChannelTask task = this.beanService.getBean(ChannelTask.class);
			task.setChannel(channel);
			task.setMessage(msg.getBuffer());
			
			JGroupsPieAddress ad = (JGroupsPieAddress) this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress());
			ad.setAddress(msg.getSrc());
			ad.setClusterName(this.clusterService.getId());
			task.setAddress(ad);
			
			this.executorService.execute(task);
		}
	}

	@Override
	public void viewAccepted(View view) {
		super.viewAccepted(view);
	}
}
