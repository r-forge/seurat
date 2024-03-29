package GUI;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

import javax.swing.table.*;
import Data.DescriptionVariable;
import Data.ISelectable;


public class DescriptionVariableTable extends JTable implements MouseListener,
		ActionListener, KeyListener {
	Seurat amlTool;

	JPopupMenu popupMenu;

	boolean SwapMode = false;

	boolean col1 = false;

	boolean col2 = true;

	boolean col4 = false;

	int[] SelectedRows;

	ExperimentDescriptionFrame descriptionFrame;

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public DescriptionVariableTable(Seurat amlTool,
			ExperimentDescriptionFrame descriptionFrame, Object[][] data,
			String[] columnNames) {
		super(data, columnNames);
		this.descriptionFrame = descriptionFrame;
		this.amlTool = amlTool;
		this.addKeyListener(this);
		addMouseListener(this);
		this.setRowMargin(0);

		this.columnModel.getColumn(0).setMaxWidth(20);
		this.columnModel.getColumn(1).setMaxWidth(150);

		// this.columnModel.getColumn(3).setMaxWidth(60);

		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		getTableHeader().setReorderingAllowed(false);
		getTableHeader().addMouseListener(this);
		getTableHeader().setCursor(Cursor.getPredefinedCursor(12));
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setDragEnabled(true);

		this.setDefaultRenderer(new Object().getClass(),
				new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						if (column == 1) {
							JLabel label = new JLabel("       "
									+ (String) value);
							label.setOpaque(true);
							label.setBackground(Color.WHITE);
							label.setForeground(Color.BLACK);
							if (isSelected) {
								label.setBackground(Color.BLACK);
								label.setForeground(Color.WHITE);

							}
							// label.setBorder(BorderFactory.createEtchedBorder());

							return label;
						} else {
							return (PicCanvas) value;
						}
					}
				}

		);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		popupMenu = new JPopupMenu();
		this.add(popupMenu);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.setBackground(Color.WHITE);

		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {

	}

	public void changeCheckBoxColumn(boolean checked, int col) {

	}

	public void mouseClicked(MouseEvent e) {
		int col = this.columnAtPoint(e.getPoint());
		int row = this.rowAtPoint(e.getPoint());

		if (e.getClickCount() == 2) {
			if (col == 0) {
				DescriptionVariable var = this.descriptionFrame.descriptionVariables
						.elementAt(row);
				if (var.isDouble) {
					if (var.isDiscrete) {
						var.isDiscrete = false;
						this.dataModel.setValueAt(
								this.descriptionFrame.contIcon, row, 0);
					} else {
						var.isDiscrete = true;
						this.dataModel.setValueAt(
								this.descriptionFrame.numIcon, row, 0);

					}
				}
				this.repaint();
			}

			if (col == 1)
				if (!this.amlTool.descriptionFrame.descriptionVariables
						.elementAt(row).isDiscrete) {
					new Histogram(amlTool,
							amlTool.dataManager.Experiments,
							amlTool.descriptionFrame.descriptionVariables
									.elementAt(row).name,
							this.amlTool.descriptionFrame.descriptionVariables
									.elementAt(row).doubleData);
				} else
					new Barchart(this.amlTool,
							this.amlTool.descriptionFrame.descriptionVariables
									.elementAt(row).name,
							this.amlTool.descriptionFrame.descriptionVariables
									.elementAt(row).variables,
							this.amlTool.descriptionFrame.descriptionVariables
									.elementAt(row).stringData);
		}

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		return null;
	}

	@Override
	public String getColumnName(int column) {

		return "";
	}

	@Override
	public boolean isCellEditable(int a, int b) {
		return false;
	}

}
