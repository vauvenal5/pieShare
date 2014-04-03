package org.pieShare.pieTools.pieUtilities.service.cmdLineService;

import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class PrintEventTask implements IPieEventTask<IPrintableEvent> {

    private IPrintableEvent msg;
    private ICmdLineService cmdService;

    @Override
    public void setMsg(IPrintableEvent msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        this.cmdService.writeLine(this.msg);
    }
}
