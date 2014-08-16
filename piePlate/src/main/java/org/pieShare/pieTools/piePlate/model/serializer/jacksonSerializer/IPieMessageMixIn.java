/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pieShare.pieTools.piePlate.model.IPieAddress;

/**
 *
 * @author Svetoslav
 */
public interface IPieMessageMixIn {

	@JsonIgnore
	IPieAddress getAddress();
}
