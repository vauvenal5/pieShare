package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api;

import org.jgroups.Receiver;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IReceiver extends Receiver {

	void setExecutorService(IExecutorService service);
	void setClusterName(String name);
	void setPassword(EncryptedPassword password);
}
