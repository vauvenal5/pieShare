package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements IReceiver {

	private ISerializerService serializerService;
	private IExecutorService executorService;
	private IBeanService beanService;
	private String clusterName;
	private IEncodeService encoderService;
	private EncryptedPassword password;

	public void setSerializerService(ISerializerService service) {
		this.serializerService = service;
	}

	public void setEncoderService(IEncodeService encoderService) {
		this.encoderService = encoderService;
	}

	@Override
	public void setPassword(EncryptedPassword password) {
		if(this.password != null) {
			return;
		}
		this.password = password;
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
			byte[] decMsg = this.encoderService.decrypt(password, msg.getBuffer());
			IPieMessage pieMsg = this.serializerService.deserialize(decMsg);
			PieLogger.debug(this.getClass(), "Recived: {}", pieMsg.getClass());
			JGroupsPieAddress ad = (JGroupsPieAddress) this.beanService.getBean(PiePlateBeanNames.getJgroupsPieAddress());
			ad.setAddress(msg.getSrc());
			ad.setClusterName(this.clusterName);
			pieMsg.setAddress(ad);
			this.executorService.handlePieEvent(pieMsg);
		} catch (SerializerServiceException | PieExecutorTaskFactoryException e) {
			//todo-sv: fix error handling!
			e.printStackTrace();
		} catch (Exception ex) {
			PieLogger.error(this.getClass(), "Exception in ClusterReader!", ex);
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
