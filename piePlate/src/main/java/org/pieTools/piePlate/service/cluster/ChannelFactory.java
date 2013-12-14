package org.pieTools.piePlate.service.cluster;

import org.jgroups.JChannel;
import org.pieTools.piePlate.service.cluster.api.IChannelFactory;

/**
 * Created by Svetoslav on 13.12.13.
 */
public class ChannelFactory implements IChannelFactory {
    @Override
    public JChannel getDefaultChannel() throws Exception {
        return new JChannel();
    }
}
