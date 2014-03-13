package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IChannelFactory;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class ChannelFactory implements IChannelFactory {
    @Override
    public JChannel getDefaultChannel() throws Exception {
        return new JChannel();
    }
}
