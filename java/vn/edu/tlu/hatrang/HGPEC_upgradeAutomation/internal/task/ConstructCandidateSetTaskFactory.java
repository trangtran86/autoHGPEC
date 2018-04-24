package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;


import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ConstructCandidateSetTaskFactory extends AbstractTaskFactory{
	CyNetworkManager networkManager;
	int distance;
	
	public ConstructCandidateSetTaskFactory(CyNetworkManager networkManager, int distance) {
		super();
		this.networkManager = networkManager;
		this.distance=distance;
	}


	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new ConstructCandidateSetTask(networkManager,distance));
	}

}
