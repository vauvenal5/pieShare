package org.pieShare.pieTools.pieUtilities.service.cmdLineService;

import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by vauvenal5 on 3/20/14.
 */
@Component
public class PrintEventTask implements IPieEventTask<IPrintableEvent> {

    private IPrintableEvent msg;
    private ICmdLineService cmdService;

    @Autowired
    @Qualifier("cmdLineService")
    public void setCommandLineService(ICmdLineService service) {
        this.cmdService = service;
    }
    
    @Override
    public void setMsg(IPrintableEvent msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        this.cmdService.writeLine(this.msg);
    }
}
