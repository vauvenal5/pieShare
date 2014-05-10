/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.beanService;

/**
 * This class is only for avoiding code duplication where possible!!
 * @author Svetoslav
 */
public abstract class BeanServiceUser {
    protected IBeanService beanService;
    
    public void setBeanService(IBeanService service) {
        this.beanService = service;
    }
}
