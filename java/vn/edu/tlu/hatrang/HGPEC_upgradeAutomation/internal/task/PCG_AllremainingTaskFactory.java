package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class PCG_AllremainingTaskFactory implements NetworkTaskFactory {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	private TaskManager cyTaskManager;
	
	

	public PCG_AllremainingTaskFactory(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			TaskManager cyTaskManager) {
		super();
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.cyTaskManager = cyTaskManager;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new PCG_AllRemainingTask(networkFactory, networkManager, arg0, cyTaskManager));
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
