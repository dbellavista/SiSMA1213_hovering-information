// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.sim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import cartago.*;

/**
 * Simulator artifact: input configuration, configure, start and show the
 * system.
 * 
 * @author Daniele Bellavista
 * 
 */
public class InitiatorArtifact extends Artifact {

	void init() {

	}

	@OPERATION
	void toss_coin(double prob, OpFeedbackParam<Boolean> res) {
		Random r = new Random();
		res.set(r.nextDouble() < prob);
	}

	@OPERATION
	void random_int(int min, int max, OpFeedbackParam<Integer> res) {
		Random r = new Random();
		res.set(r.nextInt((max - min + 1)) + min);
	}

	@SuppressWarnings("unchecked")
	@OPERATION
	void input_data() {
		String file = "simulation.yaml";
		Yaml yaml = new Yaml();
		Object data;
		try {
			data = yaml.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			failed("User Input failed", "fail", file, e.getMessage());
			return;
		}
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) data;
		try {
			for (Entry<String, Object> entry : map.entrySet()) {
				switch (entry.getKey()) {
				case "World":
					defineObsProperty("parameter", "world");
					readWorld((LinkedHashMap<String, Object>) entry.getValue());
					break;
				case "Simulation":
					defineObsProperty("parameter", "simulation");
					readSimulation((LinkedHashMap<String, Object>) entry
							.getValue());
					break;
				case "Analysis":
					defineObsProperty("parameter", "analysis");
					readAnalysis((LinkedHashMap<String, Object>) entry
							.getValue());
					break;
				case "People":
					defineObsProperty("parameter", "people");
					readPeople((ArrayList<Object>) entry.getValue());
					break;
				case "Hovering":
					defineObsProperty("parameter", "hovering");
					readHovering((ArrayList<Object>) entry.getValue());
					break;
				}
			}
		} catch (Exception e) {
			failed("Parameter parsing failed", "fail", file, e.getMessage());
			return;
		}
	}

	@INTERNAL_OPERATION
	void readWorld(LinkedHashMap<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			switch (entry.getKey()) {
			case "width":
				defineObsProperty("parameter", "world", "width",
						entry.getValue());
				break;
			case "height":
				defineObsProperty("parameter", "world", "height",
						entry.getValue());
				break;
			}
		}
	}

	@INTERNAL_OPERATION
	void readSimulation(LinkedHashMap<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			switch (entry.getKey()) {
			case "gui_width":
				defineObsProperty("parameter", "simulation", "gui_width",
						entry.getValue());
				break;
			case "gui_height":
				defineObsProperty("parameter", "simulation", "gui_height",
						entry.getValue());
				break;
			case "gui_refresh_rate":
				defineObsProperty("parameter", "simulation",
						"gui_refresh_rate", entry.getValue());
				break;
			case "dissemination":
				defineObsProperty("parameter", "simulation", "dissemination",
						entry.getValue());
				break;
			}
		}
	}

	@INTERNAL_OPERATION
	void readAnalysis(LinkedHashMap<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			switch (entry.getKey()) {
			case "analysis_rate":
				defineObsProperty("parameter", "analysis", "analysis_rate",
						entry.getValue());
				break;
			}
		}
	}

	@INTERNAL_OPERATION
	@SuppressWarnings("unchecked")
	void readPeople(ArrayList<Object> list) {
		int id = 1;
		defineObsProperty("parameter", "people", list.size());
		for (Object o : list) {
			LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) o;
			for (Entry<String, Object> entry : map.entrySet()) {
				switch (entry.getKey()) {
				case "name":
					defineObsProperty("parameter", "people", id, "name",
							entry.getValue());
					break;
				case "behaviour":
					defineObsProperty("parameter", "people", id, "behaviour",
							entry.getValue());
					break;
				case "xpos":
					defineObsProperty("parameter", "people", id, "xpos",
							entry.getValue());
					break;
				case "ypos":
					defineObsProperty("parameter", "people", id, "ypos",
							entry.getValue());
					break;
				case "device_range":
					defineObsProperty("parameter", "people", id,
							"device_range", entry.getValue());
					break;
				case "device_storage":
					defineObsProperty("parameter", "people", id,
							"device_storage", entry.getValue());
					break;
				}
			}
			id += 1;
		}
	}

	@INTERNAL_OPERATION
	@SuppressWarnings("unchecked")
	void readHovering(ArrayList<Object> list) {
		int id = 1;
		defineObsProperty("parameter", "hovering", list.size());
		for (Object o : list) {
			LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) o;
			for (Entry<String, Object> entry : map.entrySet()) {
				switch (entry.getKey()) {
				case "name":
					defineObsProperty("parameter", "hovering", id, "name",
							entry.getValue());
					break;
				case "xanchor":
					defineObsProperty("parameter", "hovering", id, "xanchor",
							entry.getValue());
					break;
				case "yanchor":
					defineObsProperty("parameter", "hovering", id, "yanchor",
							entry.getValue());
					break;
				case "anchor_radius":
					defineObsProperty("parameter", "hovering", id,
							"anchor_radius", entry.getValue());
					break;
				case "data_size":
					defineObsProperty("parameter", "hovering", id, "data_size",
							entry.getValue());
					break;
				case "data":
					defineObsProperty("parameter", "hovering", id, "data",
							entry.getValue());
					break;
				}
			}
			id += 1;
		}
	}
}
