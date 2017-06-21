package net.imagej.ops.fopd;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.scijava.AbstractBasicDetails;
import org.scijava.convert.ConvertService;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.MethodCallException;
import org.scijava.module.Module;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.widget.SwingInputWidget;
import org.scijava.util.GenericUtils;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

import net.miginfocom.swing.MigLayout;

@Plugin(type = InputWidget.class)
public class SwingListWidget extends SwingInputWidget<List<?>> implements ActionListener {

	@Parameter
	private ModuleService moduleService;
	
	@Parameter
	private WidgetService widgetService;
	
	@Parameter
	private ConvertService convertService;

	@Parameter
	private ObjectService objectService;
	
	private JPanel listPanel;
	
	private int itemCount = 0;

	private Type elementType;

	private List<Object> list;


	@Override
	public List<?> getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doRefresh() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(WidgetModel model) {
		super.set(model);
		Type type = model.getItem().getGenericType();
		elementType = GenericUtils.getTypeParameter(type, List.class, 0);
		
		list = (List<Object>) model.getValue();
		if (list == null) {
			list = new ArrayList<>();
			model.setValue(list);
		}
		listPanel = new JPanel(new MigLayout());
		for ( int i = 0; i < list.size(); i++) {
			addListItem(createElementWidget(model,i));
		}
		
		final JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == add) {
					list.add(null);
					addListItem(createElementWidget(model, list.size()-1));
				}
			}
		});
		
		getComponent().add(listPanel);
		getComponent().add(add);
		
		setToolTip(listPanel);
		refreshWidget();
	}
	
	private InputWidget<?, ? extends Component> createElementWidget(WidgetModel model, int index) {
		final Class<?> type = GenericUtils.getClass(elementType);
		final Module elementModule = wrapModule(model.getModule(), index);
		final ModuleItem<?> elementItem = new DefaultMutableModuleItem<>(model.getModule(), model.getItem().getName() + ":" + index, type);
		WidgetModel elementModel = widgetService.createModel(model.getPanel(), elementModule, elementItem , getObjects(type));
		InputWidget elementWidget = widgetService.create(elementModel);
		if (!(elementWidget.getComponent() instanceof Component)) return null;
		return (InputWidget<?, ? extends Component>) elementWidget;
	}
	
	private Module wrapModule(Module module, int index) {
		
		return new DelegateModule() {
			@Override
			public Object getInput(String name) {
				return list.get(index);
			}
			
			@Override
			public void setInput(String name, Object value) {
				list.set(index, value);
			}
		};
			
	}

	/** Asks the object service and convert service for valid choices */
	@SuppressWarnings("unchecked")
	private List<?> getObjects(final Class<?> type) {
		@SuppressWarnings("rawtypes")
		List compatibleInputs =
			new ArrayList(convertService.getCompatibleInputs(type));
		compatibleInputs.addAll(objectService.getObjects(type));
		return compatibleInputs;
	}

	private void addListItem(final InputWidget<?,? extends Component> widget) {
		final JPanel item = new JPanel(new MigLayout());

		
		final JButton remove = new JButton("-");

		
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == remove && itemCount > 1) {
					listPanel.remove(item);
					listPanel.revalidate();
					refreshWidget();
					itemCount--;
				}
			}
		});
		
		item.add(widget.getComponent());
		item.add(remove);
		
		listPanel.add(item, "wrap");
		listPanel.revalidate();
		this.itemCount++;
		refreshWidget();
	}
	
	@Override
	public boolean supports(WidgetModel data) {
		return super.supports(data) && data.getItem().getType()==ArrayList.class;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateModel();
	}
	
}
