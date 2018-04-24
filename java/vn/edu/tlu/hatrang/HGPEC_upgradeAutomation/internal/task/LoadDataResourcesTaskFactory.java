package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class LoadDataResourcesTaskFactory extends AbstractTaskFactory {
	private CyNetworkManager cyNetworkManager;
    private CyNetworkReaderManager cyNetworkReaderManager;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming namingUtil;
    
    
    
	public LoadDataResourcesTaskFactory(CyNetworkManager cyNetworkManager, CyNetworkReaderManager cyNetworkReaderManager,
			CyNetworkFactory cyNetworkFactory, CyNetworkNaming namingUtil) {
		super();
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkReaderManager = cyNetworkReaderManager;
		this.cyNetworkFactory = cyNetworkFactory;
		this.namingUtil = namingUtil;
	}



	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new LoadDataResourcesTask(cyNetworkManager, cyNetworkReaderManager, cyNetworkFactory, namingUtil));
	}
}
