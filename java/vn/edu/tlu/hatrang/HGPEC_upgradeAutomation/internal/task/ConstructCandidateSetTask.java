package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;


public class ConstructCandidateSetTask implements Task {
	CyNetworkManager networkManager;
	private volatile boolean interrupted = false;
	private int[] myInts;
	private int length;
	private int dist;

	public ConstructCandidateSetTask(CyNetworkManager networkManager, int dist) {
		// TODO Auto-generated constructor stub
		this.networkManager = networkManager;
		this.dist = dist;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		this.interrupted = true;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		taskMonitor.setTitle("Constructing Candidate Set");
		taskMonitor.setProgress(1);

		taskMonitor.setStatusMessage("Constructing Neighboring Candidate Set...");

		MainData.AllTestGenes = Common.findAllNeighboringGenesOfKnownGenesInNetwork(networkManager, dist);

		taskMonitor.setProgress(100);

	}

}
