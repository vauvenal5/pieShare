package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements IReceiver {

	private ISerializerService serializerService;
	private IExecutorService executorService;
	private IBeanService beanService;
	private String clusterName;

	public void setSerializerService(ISerializerService service) {
		this.serializerService = service;
	}

	@Override
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void setBeanService(IBeanService service) {
		this.beanService = service;
	}

	@Override
	public void receive(Message msg) {
		try {
			IPieMessage pieMsg = this.serializerService.deserialize(msg.getBuffer());
			PieLogger.debug(this.getClass(), "Recived: {}", pieMsg.getClass());
			JGroupsPieAddress ad = (JGroupsPieAddress) this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress());
			ad.setAddress(msg.getSrc());
			ad.setClusterName(this.clusterName);
			pieMsg.setAddress(ad);
			this.executorService.handlePieEvent(pieMsg);
		} catch (SerializerServiceException | PieExecutorServiceException e) {
			//todo-sv: fix error handling!
			e.printStackTrace();
		}
	}

	@Override
	public void viewAccepted(View view) {
		super.viewAccepted(view);
	}

	@Override
	public void setExecutorService(IExecutorService service) {
		this.executorService = service;
	}
}
