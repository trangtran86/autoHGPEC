package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class HeterogeneousNetworkNormalizationAnalysisTaskFactory extends AbstractTaskFactory {

	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new HeterogeneousNetworkNormalizationAnalysisTask()) ;
	}

}
