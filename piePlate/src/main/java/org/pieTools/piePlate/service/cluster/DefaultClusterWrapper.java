package org.pieTools.piePlate.service.cluster;

import org.jgroups.JChannel;
import org.pieTools.piePlate.dto.PieMessage;
import org.pieTools.piePlate.service.cluster.api.IClusterWrapper;
import org.pieTools.piePlate.service.exception.ClusterServiceException;

/**
 * Created by Svetoslav on 13.01.14.
 */
public class DefaultClusterWrapper implements IClusterWrapper {

    private JChannel channel;

    public DefaultClusterWrapper() {
    }

    public void setChannel(JChannel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(PieMessage msg) throws ClusterServiceException {
        try {
            this.channel.send(null, msg.getBuffer());
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
}
