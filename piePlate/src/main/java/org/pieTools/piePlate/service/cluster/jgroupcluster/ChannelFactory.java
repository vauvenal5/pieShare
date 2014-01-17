package org.pieTools.piePlate.service.cluster.jgroupcluster;

import org.jgroups.JChannel;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IChannelFactory;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class ChannelFactory implements IChannelFactory {
    @Override
    public JChannel getDefaultChannel() throws Exception {
        return new JChannel();
    }
}
