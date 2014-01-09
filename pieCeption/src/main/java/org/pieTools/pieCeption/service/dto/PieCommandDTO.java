package org.pieTools.pieCeption.service.dto;

import java.io.Serializable;

/**
 * Created by Svetoslav on 09.01.14.
 */
public class PieCommandDTO implements Serializable {
    private String[] args;

    public PieCommandDTO(String[] args){
        this.args = args;
    }

    public String[] getArgs() {
        return this.args;
    }
}
