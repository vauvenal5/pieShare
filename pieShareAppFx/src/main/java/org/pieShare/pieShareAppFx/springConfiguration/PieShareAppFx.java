/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareAppFx.springConfiguration;

import javafx.fxml.FXMLLoader;
import org.pieShare.pieShareAppFx.FXMLController;
import org.pieShare.pieShareAppFx.controller.LoginController;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppFx {
    @Autowired
    private PieUtilitiesConfiguration utilities;
    @Autowired
    private PieShareAppService services;
    
    @Bean
    @Lazy
    public FXMLLoader fxmlLoader() {
        return new FXMLLoader();
    }
    
    @Bean
    @Lazy
    public FXMLController mainController() {
        FXMLController controller = new FXMLController();
        controller.setBeanService(this.utilities.beanService());
        controller.setFXMLLoader(this.fxmlLoader());
        return controller;
    }
    
    @Bean
    @Lazy
    public LoginController loginController() {
        LoginController controller = new LoginController();
        controller.setLoginCommandService(this.services.loginCommandService());
        return controller;
    }
}
