/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.config;

import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.task.AllFilesCompleteTask;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
@Configuration
public class LoadTestConfig {

    @Bean
    @Scope(value="prototype")
    public AllFilesCompleteMessage allFilesCompleteMessage() {
        AllFilesCompleteMessage message = new AllFilesCompleteMessage();
        message.setAddress(new JGroupsPieAddress());
        return message;
    }

    @Bean
    @Scope(value="prototype")
    public AllFilesCompleteTask allFilesCompleteTask() {
        AllFilesCompleteTask task = new AllFilesCompleteTask();
        task.setTaskCounter(iTTasksCounter());
        return task;
    }

    @Bean
    @Lazy
    public ITTasksCounter iTTasksCounter() {
        return new ITTasksCounter();
    }

}
