package net.imagej.ops.fopd;

import java.util.ArrayList;
import java.util.List;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath="Test>test")
public class TestPlugin implements Command {

	@Parameter
	private ArrayList<Double> list = new ArrayList<>();
	
	public TestPlugin() {
		list.add(5.0);
		list.add(3.3);
	}
	
	@Override
	public void run() {
		System.out.println(list);
	}

}
