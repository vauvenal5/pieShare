package org.pieTools.piePlate.controller.api;

import org.pieTools.piePlate.controller.exception.ClusterException;

public interface ICluster {
    void joinCluster(String cloudName) throws ClusterException;
    void leafCluster(String cloudName) throws ClusterException;
}
