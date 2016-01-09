/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.model.entities;

import org.pieShare.pieShareApp.model.entities.api.IFileFilterEntity;

/**
 *
 * @author Richard
 */
public class FilterEntity implements IFileFilterEntity {

    private String pattern;

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}