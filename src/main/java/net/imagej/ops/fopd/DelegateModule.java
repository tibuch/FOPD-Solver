package net.imagej.ops.fopd;

import java.util.Map;

import org.scijava.module.MethodCallException;
import org.scijava.module.Module;
import org.scijava.module.ModuleInfo;

public class DelegateModule implements Module {
	private Module m;

	public void run() {
		m.run();
	}

	public void preview() {
		m.preview();
	}

	public void cancel() {
		m.cancel();
	}

	public void initialize() throws MethodCallException {
		m.initialize();
	}

	public ModuleInfo getInfo() {
		return m.getInfo();
	}

	public Object getDelegateObject() {
		return m.getDelegateObject();
	}

	public Object getInput(String name) {
		return m.getInput(name);
	}

	public Object getOutput(String name) {
		return m.getOutput(name);
	}

	public Map<String, Object> getInputs() {
		return m.getInputs();
	}

	public Map<String, Object> getOutputs() {
		return m.getOutputs();
	}

	public void setInput(String name, Object value) {
		m.setInput(name, value);
	}

	public void setOutput(String name, Object value) {
		m.setOutput(name, value);
	}

	public void setInputs(Map<String, Object> inputs) {
		m.setInputs(inputs);
	}

	public void setOutputs(Map<String, Object> outputs) {
		m.setOutputs(outputs);
	}

	public boolean isInputResolved(String name) {
		return m.isInputResolved(name);
	}

	public boolean isOutputResolved(String name) {
		return m.isOutputResolved(name);
	}

	public void resolveInput(String name) {
		m.resolveInput(name);
	}

	public void resolveOutput(String name) {
		m.resolveOutput(name);
	}

	public void unresolveInput(String name) {
		m.unresolveInput(name);
	}

	public void unresolveOutput(String name) {
		m.unresolveOutput(name);
	}
}