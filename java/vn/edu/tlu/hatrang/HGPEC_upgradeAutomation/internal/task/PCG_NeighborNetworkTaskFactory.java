package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class PCG_NeighborNetworkTaskFactory implements NetworkTaskFactory {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	private TaskManager cyTaskManager;
	SynchronousTaskManager cySynchronousTaskManager;
	

	public PCG_NeighborNetworkTaskFactory(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			TaskManager cyTaskManager, SynchronousTaskManager cySynchronousTaskManager) {
		super();
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.cyTaskManager = cyTaskManager;
		this.cySynchronousTaskManager=cySynchronousTaskManager;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new PCG_NeighborNetworkTask(cyTaskManager, networkFactory, networkManager, arg0, cySynchronousTaskManager));
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
