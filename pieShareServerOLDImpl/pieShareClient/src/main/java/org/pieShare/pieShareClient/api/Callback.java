/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.api;

import javax.json.JsonObject;

/**
 *
 * @author RicLeo00
 */
public interface Callback {
    void Handle(JsonObject client);
}
