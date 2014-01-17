package org.pieTools.piePlate.service.cluster.jgroupcluster.api;

import org.jgroups.JChannel;

/**
 * Created by Svetoslav on 13.12.13.
 */
public interface IChannelFactory {
    JChannel getDefaultChannel() throws Exception;
}
