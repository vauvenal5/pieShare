package org.pieShare.pieTools.piePlate.service.helper;

import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;

/**
 * Created by Svetoslav on 14.12.13.
 */
public abstract class ClusterServiceTestHelper implements Runnable {

    private IClusterService service;

    private boolean done = false;

    public ClusterServiceTestHelper(IClusterService service) {
        this.service = service;
    }

    public IClusterService getService() {
        return this.service;
    }

    public boolean getDone() {
        return this.done;
    }

    public void setDone() {
        this.done = true;
    }
}
