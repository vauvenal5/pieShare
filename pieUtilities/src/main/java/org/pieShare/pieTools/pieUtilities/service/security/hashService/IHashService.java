/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.security.hashService;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Svetoslav
 */
public interface IHashService {
    byte[] hash(byte[] data);
    byte[] hashStream(InputStream stream) throws IOException;
    boolean isMD5Equal(byte[] first, byte[] second);
}
