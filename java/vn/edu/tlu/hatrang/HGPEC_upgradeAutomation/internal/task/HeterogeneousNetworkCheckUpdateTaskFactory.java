package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class HeterogeneousNetworkCheckUpdateTaskFactory implements NetworkTaskFactory {
	private CyNetworkManager cyNetworkManager;	
	
	public HeterogeneousNetworkCheckUpdateTaskFactory(CyNetworkManager cyNetworkManager) {
		super();
		this.cyNetworkManager = cyNetworkManager;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new HeterogeneousNetworkCheckUpdateTask(cyNetworkManager));
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	

}
