package GUI;

import java.util.*;

import javax.swing.*;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.event.*;
import java.awt.*;

import Data.Clustering;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;

public class ConfusionsPlot extends JFrame implements IPlot {

	Seurat seurat;

	ConfusionsPanel panel;

	JMenuItem item;

	public ConfusionsPlot(Seurat seurat, String method1, String method2,
			Clustering Experiments1, Clustering Experiments2) {
		super("Confusion Matrix: ( " + method1 + " , " + method2 + " )");
		this.seurat = seurat;

		panel = new ConfusionsPanel(seurat, this, method1, method2,
				Experiments1, Experiments2);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);

		this.setBounds(400, 0, (int) Math.min(800, Experiments1.clusters.size()
				* 80 + panel.abstandLinks + panel.abstandRechts), (int) Math
				.min(800, Experiments2.clusters.size() * 80 + 30)+30);

		this.setVisible(true);

		this.addKeyListener(panel);

		seurat.windows.add(this);

		item = new JMenuItem("Confusions Matrix");
		seurat.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.seurat.windowMenu.remove(item);
			}
		});

		this.setVisible(true);

	}

	public void updateSelection() {
		panel.updateSelection();

	}

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}

	public void print() {
		// TODO Auto-generated method stub
		panel.print();
	}

}

class ConfusionsPanel extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {



	Seurat seurat;

	JMenuItem item = new JMenuItem("");

	ConfusionsPanel confusionsPanel = this;

	Point point1, point2;

	Image image;

	int abstandLinks = 80;
	int abstandRechts = 5;

	int abstandOben = 20;

	int BinHeigth = 20;

	int abstandString = 5;

	double groupParam = 0.2;

	JPopupMenu menu;

	int[][] dist1;
	int[][] dist2;

	Vector<Point> points;

	boolean VIEW = true;

	double gen = 4;

	Vector<Integer> Columns = new Vector();
	Vector<Integer> Rows = new Vector();

	Clustering Experiments1;
	Clustering Experiments2;

	int[][] matrix;

	int[][] isSelected;

	Vector<Vector<Integer>> ClusterRows;
	Vector<Vector<Integer>> ClusterColumns;

	ConfusionsPlot plot;

	int count = -1;

	public ConfusionsPanel(Seurat seurat, ConfusionsPlot plot, String method1,
			String method2, Clustering Experiments1, Clustering Experiments2) {

		this.seurat = seurat;

		this.plot = plot;

		this.Experiments1 = Experiments1;
		this.Experiments2 = Experiments2;

		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			Columns.add(new Integer(i));

		}

		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			Rows.add(new Integer(i));
		}

		// System.out.println(Columns.size() + "  "+ Rows.size());

		isSelected = new int[Columns.size()][Rows.size()];

		this.setVisible(true);

		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				updateSelection();

			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	int calcCount() {
		int res = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				res += matrix[i][j];
			}
		}
		return res;
	}

	public boolean isPointInRect(int x, int y, Point point1, Point point2) {
		if ((point1.x <= x) && (point2.x >= x) && (point1.y <= y)
				&& (point2.y >= y))
			return true;
		else
			return false;
	}

	public boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
			int Sy1, int Sx2, int Sy2) {
		if (isLineInRect(x1, y1, x2, y2, Sx1, Sy1, Sx2, Sy2))
			return true;
		if (isLineInRect(x1, y2, x2, y1, Sx1, Sy1, Sx2, Sy2))
			return true;
		if (isLineInRect(x2, y1, x1, y2, Sx1, Sy1, Sx2, Sy2))
			return true;
		if (isLineInRect(x2, y2, x1, y1, Sx1, Sy1, Sx2, Sy2))
			return true;
		if (isLineInRect(Sx1, Sy1, Sx2, Sy2, x1, y1, x2, y2))
			return true;
		if (isLineInRect(Sx2, Sy1, Sx1, Sy2, x1, y1, x2, y2))
			return true;
		if (isLineInRect(Sx1, Sy2, Sx2, Sy1, x1, y1, x2, y2))
			return true;
		if (isLineInRect(Sx2, Sy2, Sx1, Sy1, x1, y1, x2, y2))
			return true;

		return false;
	}

	public boolean isLineInRect(int x1, int y1, int x2, int y2, int Rx1,
			int Ry1, int Rx2, int Ry2) {
		for (int i = x1; i <= x2; i++) {
			if (x1 != x2)
				if (isPointInRect(i, y1 + (y2 - y1) * (i - x1) / (x2 - x1),
						Rx1, Ry1, Rx2, Ry2))
					return true;

		}
		if (x1 == x2)
			if (isPointInRect(x1, y2, Rx1, Ry1, Rx2, Ry2))
				return true;

		return false;
	}

	public boolean isPointInRect(int x, int y, int Rx1, int Ry1, int Rx2,
			int Ry2) {
		if ((Rx1 <= x) && (Rx2 >= x) && (Ry1 <= y) && (Ry2 >= y))
			return true;
		else
			return false;
	}

	public void addSelection(Point point1, Point point2) {

		int width = this.getWidth() - abstandLinks - abstandRechts;
		int height = this.getHeight() - 25;

		int[][] counts = new int[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				counts[i][j] = matrix[Columns.elementAt(i)][Rows.elementAt(j)];

			}
		}

		int max = 0;

		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[0].length; j++) {
				if (max < counts[i][j])
					max = counts[i][j];
			}
		}

		seurat.dataManager.selectAll();
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			for (int j = 0; j < ((Vector) Experiments1.clusters.elementAt(i).items)
					.size(); j++) {
				((ISelectable) ((Vector) Experiments1.clusters.elementAt(i).items)
						.elementAt(j)).unselect(true);
			}

		}

		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			for (int j = 0; j < ((Vector) Experiments2.clusters.elementAt(i).items)
					.size(); j++) {
				((ISelectable) ((Vector) Experiments2.clusters.elementAt(i).items)
						.elementAt(j)).unselect(true);
			}

		}

		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {

				int x = width * i / counts.length;
				int y = height * j / counts[0].length + 20;
				int w = (int) Math.round((double) 3 / 4 * width / counts.length
						* Math.sqrt(((double) counts[i][j] / max)));
				int h = (int) Math.round((double) 3 / 4 * height
						/ counts[0].length
						* Math.sqrt((double) counts[i][j] / max));

				int b = width / counts.length;
				int c = height / counts[0].length;

				if (isPointInRect(x + (b - w) / 2 + abstandLinks, y + (c - h)
						/ 2, point1, point2)
						&& isPointInRect(x + (b - w) / 2 + abstandLinks + w, y
								+ (c - h) / 2 + h, point1, point2)) {
					selectRect(i, j);
				}

			}
		}

		seurat.repaintWindows();

	}

	public void selectRect(int col, int row) {

		Vector cluster1 = (Vector) Experiments1.clusters.elementAt(Columns
				.elementAt(col)).items;

		Vector cluster2 = (Vector) Experiments2.clusters.elementAt(Rows
				.elementAt(row)).items;

		for (int ii = 0; ii < cluster1.size(); ii++) {
			if (cluster2.indexOf(cluster1.elementAt(ii)) != -1)
				((ISelectable) cluster1.elementAt(ii)).select(true);

		}

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();

		if (!e.isShiftDown())
			seurat.dataManager.deleteSelection();

		this.addSelection(point1, point2);
		point1 = null;
		point2 = null;

	}

	public void mouseClicked(MouseEvent e) {
		point1 = e.getPoint();

		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();

			JMenuItem item = new JMenuItem("Original Order");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					originalOrder();
					
				}
			});
			menu.add(item);

			item = new JMenuItem("Permute Matrix");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					permute();

				}
			});
			menu.add(item);

			menu.addSeparator();

			item = new JMenuItem("find Group");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					findGroup();

				}
			});
			menu.add(item);

			item = new JMenuItem("delete Groups");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					deleteGroups();

				}
			});
			menu.add(item);

			menu.addSeparator();

			item = new JMenuItem("Print");
			item.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					print();

				}
			});
			menu.add(item);

			menu.show(this, e.getX(), e.getY());
			return;
		}

		int width = this.getWidth() - abstandLinks - abstandRechts;
		int height = this.getHeight() - 25;

		int[][] counts = new int[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				counts[i][j] = matrix[Columns.elementAt(i)][Rows.elementAt(j)];

			}
		}

		for (int i = 0; i < isSelected.length; i++) {
			for (int j = 0; j < isSelected[0].length; j++) {
				if (!e.isShiftDown())
					isSelected[i][j] = 0;
			}
		}

		int i = counts.length * (e.getX() - abstandLinks) / width;
		int j = (e.getY() - 20) * counts[0].length / height;

		isSelected[Columns.elementAt(i)][Rows.elementAt(j)] = counts[i][j];

		if (i < 0 || j < 0 || i >= counts.length || j >= counts[0].length)
			return;

		if (!e.isShiftDown())
			seurat.dataManager.deleteSelection();

		seurat.dataManager.selectAll();
		for (int ii = 0; ii < Experiments1.clusters.size(); ii++) {
			for (int jj = 0; jj < ((Vector) Experiments1.clusters.elementAt(ii).items)
					.size(); jj++) {
				((ISelectable) ((Vector) Experiments1.clusters.elementAt(ii).items)
						.elementAt(jj)).unselect(true);
			}

		}

		for (int ii = 0; ii < Experiments2.clusters.size(); ii++) {
			for (int jj = 0; jj < ((Vector) Experiments2.clusters.elementAt(ii).items)
					.size(); jj++) {
				((ISelectable) ((Vector) Experiments2.clusters.elementAt(ii).items)
						.elementAt(jj)).unselect(true);
			}

		}

		Vector cluster1 = (Vector) Experiments1.clusters.elementAt(Columns
				.elementAt(i)).items;

		Vector cluster2 = (Vector) Experiments2.clusters.elementAt(Rows
				.elementAt(j)).items;

		for (int ii = 0; ii < cluster1.size(); ii++) {
			if (cluster2.indexOf(cluster1.elementAt(ii)) != -1)
				((ISelectable) cluster1.elementAt(ii)).select(true);

		}

		seurat.repaintWindows();

	}

	public void originalOrder() {
		Columns = new Vector();
		Rows = new Vector();

		for (int i = 0; i < matrix.length; i++) {
			Columns.add(new Integer(i));
		}

		for (int i = 0; i < matrix[0].length; i++) {
			Rows.add(new Integer(i));
		}

		ClusterColumns = null;
		ClusterRows = null;
		
		repaint();
	}

	public void permute() {
		VIEW = false;

		double crit = -1;
		System.out.println("Permute Matrix");
		while (crit != calculateCriterium(Columns, Rows)) {
			crit = calculateCriterium(Columns, Rows);
			Matrix4 m = new Matrix4(Columns, Rows, false);
			Columns = m.Columns;
			Rows = m.Rows;
			System.out.println("--->  " + crit);

		}
		repaint();

	}

	public void findGroup() {
		calculateGroups();
		repaint();
	}

	public void deleteGroup() {
		repaint();
	}

	public void deleteGroups() {
		ClusterColumns = null;
		ClusterRows = null;
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();
		this.repaint();

	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void keyPressed(KeyEvent e) {

		

		this.repaint();

	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
	}

	public void calculateGroups() {

		int col = -1, row = -1;
		double max = 0;
		int aa = -1;

		count = this.calcCount();

		if (ClusterColumns == null || ClusterColumns.size() < 1) {
			ClusterColumns = new Vector();
			ClusterRows = new Vector();

			Vector v = new Vector();

			for (int i = 0; i < Columns.size(); i++) {

				v.add(Columns.elementAt(i));
			}

			ClusterColumns.add(v);

			v = new Vector();

			for (int i = 0; i < Rows.size(); i++) {

				v.add(Rows.elementAt(i));

			}
			ClusterRows.add(v);

		}

		for (int a = 0; a < ClusterColumns.size(); a++) {

			System.out.println(" a:  " + a);

			Vector<Integer> ClusterCol = ClusterColumns.elementAt(a);
			Vector<Integer> ClusterRow = ClusterRows.elementAt(a);

			for (int i = 1; i < ClusterCol.size(); i++) {
				for (int j = 1; j < ClusterRow.size(); j++) {

					Vector ClusterRowsNew = new Vector();
					Vector ClusterColumnsNew = new Vector();

					Vector<Integer> Col1 = new Vector();
					for (int ii = 0; ii < i; ii++) {
						Col1.add(ClusterCol.elementAt(ii));
					}

					Vector<Integer> Col2 = new Vector();
					for (int ii = i; ii < ClusterCol.size(); ii++) {
						Col2.add(ClusterCol.elementAt(ii));
					}

					Vector<Integer> Row1 = new Vector();
					for (int ii = 0; ii < j; ii++) {
						Row1.add(ClusterRow.elementAt(ii));
					}

					Vector<Integer> Row2 = new Vector();
					for (int ii = j; ii < ClusterRow.size(); ii++) {
						Row2.add(ClusterRow.elementAt(ii));
					}

					for (int k = 0; k < ClusterColumns.size(); k++) {
						if (k != a) {
							ClusterColumnsNew.add(ClusterColumns.elementAt(k));
							ClusterRowsNew.add(ClusterRows.elementAt(k));
						} else {
							ClusterColumnsNew.add(Col1);
							ClusterColumnsNew.add(Col2);

							ClusterRowsNew.add(Row1);
							ClusterRowsNew.add(Row2);
						}
					}

					double crit = groupsKriterium(ClusterColumnsNew,
							ClusterRowsNew);
					System.out.println(max + " ____ " + crit + " i: " + i
							+ "  j:  " + j);

					if (crit > max) {
						max = crit;
						col = i;
						row = j;
						aa = a;
					}

				}
			}

		}

		Vector ClusterRowsNew = new Vector();
		Vector ClusterColumnsNew = new Vector();

		Vector<Integer> ClusterCol = ClusterColumns.elementAt(aa);
		Vector<Integer> ClusterRow = ClusterRows.elementAt(aa);

		Vector<Integer> Col1 = new Vector();
		for (int ii = 0; ii < col; ii++) {
			Col1.add(ClusterCol.elementAt(ii));
		}

		Vector<Integer> Col2 = new Vector();
		for (int ii = col; ii < ClusterCol.size(); ii++) {
			Col2.add(ClusterCol.elementAt(ii));
		}

		Vector<Integer> Row1 = new Vector();
		for (int ii = 0; ii < row; ii++) {
			Row1.add(ClusterRow.elementAt(ii));
		}

		Vector<Integer> Row2 = new Vector();
		for (int ii = row; ii < ClusterRow.size(); ii++) {
			Row2.add(ClusterRow.elementAt(ii));
		}

		for (int k = 0; k < ClusterColumns.size(); k++) {
			if (k != aa) {
				ClusterColumnsNew.add(ClusterColumns.elementAt(k));
				ClusterRowsNew.add(ClusterRows.elementAt(k));
			} else {
				ClusterColumnsNew.add(Col1);
				ClusterColumnsNew.add(Col2);

				ClusterRowsNew.add(Row1);
				ClusterRowsNew.add(Row2);
			}
		}

		ClusterColumns = ClusterColumnsNew;
		ClusterRows = ClusterRowsNew;

	}

	public double groupsKriterium(Vector<Vector<Integer>> Columns,
			Vector<Vector<Integer>> Rows) {

		int in = 0;
		for (int i = 0; i < Columns.size(); i++) {
			Vector<Integer> ColCluster = Columns.elementAt(i);
			Vector<Integer> RowCluster = Rows.elementAt(i);
			for (int ii = 0; ii < ColCluster.size(); ii++) {

				for (int j = 0; j < RowCluster.size(); j++) {
					in += matrix[ColCluster.elementAt(ii)][RowCluster
							.elementAt(j)];
				}

			}
		}

		return in;

	}

	public void updateSelection() {

		for (int i = 0; i < isSelected.length; i++) {
			for (int j = 0; j < isSelected[0].length; j++) {
				isSelected[i][j] = 0;
			}
		}

		matrix = new int[Experiments1.clusters.size()][Experiments2.clusters
				.size()];
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			Vector cluster1 = (Vector) Experiments1.clusters.elementAt(i).items;

			for (int j = 0; j < Experiments2.clusters.size(); j++) {

				Vector cluster2 = (Vector) Experiments2.clusters.elementAt(j).items;

				for (int ii = 0; ii < cluster1.size(); ii++) {
					if (cluster2.indexOf(cluster1.elementAt(ii)) != -1) {
						matrix[i][j]++;
						if (((ISelectable) cluster1.elementAt(ii)).isSelected())
							isSelected[i][j]++;
					}

				}

			}
		}

		this.repaint();

	}

	@Override
	public void paint(Graphics g) {

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (matrix == null) {
			this.updateSelection();
			count = this.calcCount();

			

		}
		
		
		
		int maxNameLength = 0;

		for (int i = 0; i < Experiments2.clusters.size(); i++) {

		//	System.out.println("-->>" + Experiments2.Names.elementAt(i));
			
			int len = getLength(Experiments2.clusters.elementAt(i).name, this
					.getGraphics());
			if (len > maxNameLength)
				maxNameLength = len;
		}

		this.abstandLinks = (int) Math.min(maxNameLength + 10, 120);
		
		
		

		g.setColor(Color.black);

		int width = this.getWidth() - abstandLinks - abstandRechts;
		int height = this.getHeight() - 25;

		g.drawLine(abstandLinks, 22, this.getWidth() - abstandRechts, 22);

		g.drawLine(abstandLinks, 22, abstandLinks, this.getHeight() - 5);

		int[][] counts = new int[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				counts[i][j] = matrix[Columns.elementAt(i)][Rows.elementAt(j)];

			}
		}

		int max = 0;

		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[0].length; j++) {
				if (max < counts[i][j])
					max = counts[i][j];
			}
		}

		for (int j = 0; j < counts[0].length; j++) {

			g.setColor(Color.black);
			String s = cutLabels(Experiments2.
					clusters.elementAt(Rows.elementAt(j)).name, abstandLinks - 10, g);

			g.drawString(s, abstandLinks - getLength(s, g) - 5, height * j
					/ counts[0].length + 20 + height / counts[0].length / 2);

		}

		for (int i = 0; i < counts.length; i++) {
			g.setColor(Color.black);

			String s = ""
					+ cutLabels(Experiments1.clusters.elementAt(Columns
							.elementAt(i)).name, (width - 10) / counts.length, g);

			g.drawString(s, width * i / counts.length + abstandLinks
					+ (width / counts.length - getLength(s, g)) / 2, 17);

			for (int j = 0; j < counts[0].length; j++) {

				g.setColor(Color.black);

				int x = width * i / counts.length;
				int y = height * j / counts[0].length + 20;
				int w = (int) Math.round((double) 3 / 4 * width / counts.length
						* Math.sqrt(((double) counts[i][j] / max)));
				int h = (int) Math.round((double) 3 / 4 * height
						/ counts[0].length
						* Math.sqrt((double) counts[i][j] / max));

				int b = width / counts.length;
				int c = height / counts[0].length;

				g.drawLine(width * (i + 1) / counts.length + abstandLinks, 22,
						width * (i + 1) / counts.length + abstandLinks, this
								.getHeight() - 5);

				g.drawLine(abstandLinks, height * (j + 1) / counts[0].length
						+ 20, this.getWidth() - abstandRechts, height * (j + 1)
						/ counts[0].length + 20);

				g.setColor(Color.GRAY);
				// else g.setColor(new Color(144,155,222));

				g.fillRect(x + (b - w) / 2 + abstandLinks, y + (c - h) / 2, w,
						h);

				g.setColor(Color.RED);
				if (isSelected[Columns.elementAt(i)][Rows.elementAt(j)] > 0) {
					double koeff = (double) isSelected[Columns.elementAt(i)][Rows
							.elementAt(j)]
							/ counts[i][j];

					g.fillRect(x + (b - w) / 2 + abstandLinks, (int) Math
							.round(y + (c + h) / 2 - h * koeff), w, (int) Math
							.round(h * koeff));

				}

				g.setColor(Color.black);

				if (w != 0)
					g.drawRect(x + (b - w) / 2 + abstandLinks, y + (c - h) / 2,
							w, h);

				/*
				 * else { g.setColor(Color.black); g.drawString(matrix
				 * [i][j]+"", x + b/2+22, y+c/2);
				 * 
				 * }
				 */

				// if (counts [i][j] * 100 / points.size()>=1)
				// g.setColor(Color.red);
				// else g.setColor(Color.black);
				// g.drawString((double)Math.round((double)counts [i][j] * 10000
				// / points.size())/100 + " ", this.getWidth()* i/11+ 3
				// +(int)Math.round((double) 3/4 *this.getWidth()/11 * counts
				// [i][j] / max), (this.getHeight() - this.getHeight() * j/11)-
				// this.getHeight() /22);
			}
		}

		int colShift = 0;
		int rowShift = 0;
		if (ClusterColumns != null) {

			for (int i = 0; i < ClusterColumns.size(); i++) {
				int shiftCol = ClusterColumns.elementAt(i).size();
				int shiftRows = ClusterRows.elementAt(i).size();

				g.setColor(Color.RED);

				g.drawRect(width * colShift / counts.length + abstandLinks,

				height * rowShift / counts[0].length + 20,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				g.drawRect(width * colShift / counts.length + abstandLinks + 1,

				height * rowShift / counts[0].length + 20 + 1,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				g.drawRect(width * colShift / counts.length + abstandLinks - 1,

				height * rowShift / counts[0].length + 20 - 1,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				colShift += shiftCol;
				rowShift += shiftRows;

			}

		}

		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}

	}

	public String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		String ss = this.cutLabelsHelp(s, availablePlace, g);
		if (ss.length() < 5)
			return ss;

		int Width = 0;
		for (int i = 0; i < ss.length(); i++)
			Width += g.getFontMetrics().charWidth(ss.charAt(i));
		if (Width < availablePlace)
			return ss;

		while (Width > availablePlace) {
			Width = 0;
			ss = ss.substring(0, ss.length() - 1);
			for (int i = 0; i < ss.length(); i++)
				Width += g.getFontMetrics().charWidth(ss.charAt(i));

		}

		return ss;

	};

	public int getLength(String s, Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));

		return Width;
	}

	public String cutLabelsHelp(String s, int availablePlace, Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));
		if (Width < availablePlace)
			return s;

		Width = 0;
		/*
		 * String[] split = s.split(" "); String cutS = ""; if (split.length >
		 * 1) {
		 * 
		 * for (int i = 0; i < split.length; i++) if (split[i].length() > 1)
		 * cutS += split[i].substring(0, 1); else cutS += split[i]; for (int i =
		 * 0; i < cutS.length(); i++) Width +=
		 * g.getFontMetrics().charWidth(cutS.charAt(i)); if (Width <
		 * availablePlace) return cutS; }
		 */

		s = s.replaceAll("ck", "c");
		String cutS = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// if (c != 'e' && c != 'u' && c != 'i' && c != 'o' && c != '¸'
			// && c != 'a' && c != 'ˆ' && c != '‰' && c != 'y')
			cutS += c;
		}

		return cutS;

	}

	/**
	 * 
	 * Bestrafung falscher Ausrichtung
	 * 
	 * 
	 * */

	public double calculateCriterium(Vector<Integer> ClusterColumns,
			Vector<Integer> ClusterRows) {
		double crit = 0;

		for (int i = 1; i < ClusterColumns.size(); i++) {
			for (int j = 0; j < ClusterRows.size() - 1; j++) {

				for (int ii = 0; ii < i; ii++) {
					for (int jj = j + 1; jj < ClusterRows.size(); jj++) {

						int a = matrix[ClusterColumns.elementAt(i)][ClusterRows
								.elementAt(j)];
						int b = matrix[ClusterColumns.elementAt(ii)][ClusterRows
								.elementAt(jj)];

						crit += a * b;

					}

				}
			}

		}
		
		
		
		/*
		for (int i = 0; i < ClusterColumns.size()-1; i++) {
			for (int j = 0; j < ClusterRows.size() - 1; j++) {

				for (int ii = i+2; ii < ClusterColumns.size(); ii++) {
					
						int a = matrix[ClusterColumns.elementAt(i)][ClusterRows
								.elementAt(j)];
						int b = matrix[ClusterColumns.elementAt(ii)][ClusterRows
								.elementAt(j)];

						crit += a * b*Math.abs(i-ii);

					

				}
				
				
				for (int jj = j + 2; jj < ClusterRows.size(); jj++) {

					int a = matrix[ClusterColumns.elementAt(i)][ClusterRows
							.elementAt(j)];
					int b = matrix[ClusterColumns.elementAt(i)][ClusterRows
							.elementAt(jj)];

					crit += a * b*Math.abs(j-jj);

				}
				
				
				
				
			}

		}
*/		
		
		
		
		
		/**Nach barchaften in den Zeilen oder Spalten**/

		return crit;

	}

	@Override
	public String getToolTipText(MouseEvent e) {

		if (!e.isControlDown())
			return null;

		int width = this.getWidth() - abstandLinks - abstandRechts;
		int height = this.getHeight() - 25;

		int[][] counts = new int[matrix.length][matrix[0].length];
		int[][] countsS = new int[matrix.length][matrix[0].length];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				counts[i][j] = matrix[Columns.elementAt(i)][Rows.elementAt(j)];
				countsS[i][j] = isSelected[Columns.elementAt(i)][Rows.elementAt(j)];

			}
		}

		int i = counts.length * (e.getX() - abstandLinks) / width;
		int j = (e.getY() - 20) * counts[0].length / height;

		if (i < 0 || j < 0 || i >= counts.length || j >= counts[0].length)
			return null;

		if (e.isControlDown() && counts[i][j]!=0) return "selected: "+ countsS[i][j] + "/"+ counts[i][j] + " (" + countsS[i][j]*100/counts[i][j] +"%)";
		
		
		return null;
	}
	
	
	

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}

	

	class Matrix4 {

		Vector<Integer> Columns;
		Vector<Integer> Rows;

		Vector<Vector<Integer>> perCols;
		Vector<Vector<Integer>> perRows;

		public Matrix4(Vector<Integer> Columns, Vector<Integer> Rows,
				boolean optimum) {

			this.Columns = Columns;
			this.Rows = Rows;

			// newCluster();
			if (optimum) {
				optimum();
				System.out.println("Optimum  "
						+ calculateCriterium(this.Columns, this.Rows));
			} else {

				newCluster();
				System.out.println("Heuristik  "
						+ calculateCriterium(this.Columns, this.Rows));
			}

		}

		public Vector<Vector<Integer>> CalcAllPemutations(
				Vector<Integer> Columns) {
			Vector<Vector<Integer>> permutations = new Vector();
			if (Columns.size() == 0) {
				permutations.add(new Vector());
				return permutations;
			}

			for (int i = 0; i < Columns.size(); i++) {
				Integer col = Columns.elementAt(i);
				Vector<Integer> rest = remove(Columns, i);
				Vector<Vector<Integer>> temp = CalcAllPemutations(rest);
				for (int j = 0; j < temp.size(); j++) {
					Vector<Integer> per = temp.elementAt(j);
					per.add(col);
					permutations.add(per);
				}
			}
			return permutations;
		}

		public void optimum() {
			Vector<Vector<Integer>> perC = CalcAllPemutations(Columns);
			Vector<Vector<Integer>> perR = CalcAllPemutations(Rows);

			Vector<Integer> bestPerC = null;
			Vector<Integer> bestPerR = null;
			double crit = calculateCriterium(Columns, Rows);

			System.out.println("Anzahl der Permutationen  :  " + perC.size()
					+ "   " + perR.size());

			Vector<Double> values = new Vector();

			for (int i = 0; i < perC.size(); i++) {
				System.out.println(i);

				for (int j = 0; j < perR.size(); j++) {
					Vector<Integer> C = perC.elementAt(i);
					Vector<Integer> R = perR.elementAt(j);

					double value = calculateCriterium(C, R);
					values.add(value);

					if (value < crit) {
						crit = value;
						bestPerC = C;
						bestPerR = R;
					}
				}
			}

			if (bestPerC != null) {
				Columns = bestPerC;
				Rows = bestPerR;
			}

			double[] valuesD = new double[values.size()];
			for (int i = 0; i < values.size(); i++) {
				valuesD[i] = values.elementAt(i);
			}

			// new Histogram(seurat,null,"Werteverteilung",valuesD);

		}

		public Vector<Integer> remove(Vector<Integer> Columns, int index) {
			Vector v = new Vector();
			for (int i = 0; i < Columns.size(); i++) {
				if (i != index)
					v.add(Columns.elementAt(i));
			}
			return v;
		}

		/**
	 * 
	 */
		public void newCluster() {

			int ColI = -2;
			int ColJ = -2;
			int RowI = -2;
			int RowJ = -2;

			double colValue = calculateCriterium(Columns, Rows);
			;
			double rowValue = colValue;

			// find max
			for (int i = -1; i < Columns.size(); i++) {
				for (int j = 0; j <= Columns.size(); j++) {
					Vector<Integer> perColumns = vertausche(Columns, i, j);
					double crit = calculateCriterium(perColumns, Rows);
					if (crit < colValue) {
						colValue = crit;
						ColI = i;
						ColJ = j;
					}

				}
			}

			for (int i = -1; i < Rows.size(); i++) {
				for (int j = 0; j <= Rows.size(); j++) {
					Vector<Integer> perRows = vertausche(Rows, i, j);
					double crit = calculateCriterium(Columns, perRows);
					if (crit < rowValue) {
						rowValue = crit;
						RowI = i;
						RowJ = j;
					}

				}
			}

			if (colValue <= rowValue && ColI != -2) {
				Vector<Integer> perColumns = vertausche(Columns, ColI, ColJ);
				Columns = perColumns;
				System.out.println("Columns  " + ColI + "  " + ColJ + "   "
						+ calculateCriterium(Columns, Rows));
			}

			if (colValue > rowValue) {
				Vector<Integer> perRows = vertausche(Rows, RowI, RowJ);
				Rows = perRows;
				System.out.println("Rows  " + RowI + "  " + RowJ + "   "
						+ calculateCriterium(Columns, Rows));
			}

			int I = -1;

			int J = -1;

			double CValue = calculateCriterium(Columns, Rows);
			;
			double RValue = colValue;

			for (int i = 0; i < Columns.size(); i++) {

				Vector<Integer> perColumns = drehe(Columns, i);
				double crit = calculateCriterium(perColumns, Rows);
				if (crit < CValue) {
					CValue = crit;
					I = i;

				}

			}

			for (int i = 0; i < Rows.size(); i++) {
				Vector<Integer> perRows = drehe(Rows, i);
				double crit = calculateCriterium(Columns, perRows);
				if (crit < RValue) {
					RValue = crit;
					J = i;
				}

			}

			if (CValue <= RValue && I != -1) {
				Vector<Integer> perColumns = drehe(Columns, I);
				Columns = perColumns;
				System.out.println("Columns Rotation  " + I + "  "
						+ calculateCriterium(Columns, Rows));
			}

			if (CValue > RValue) {
				Vector<Integer> perRows = drehe(Rows, J);
				Rows = perRows;
				System.out.println("Rows Rotation " + RowJ + "   "
						+ calculateCriterium(Columns, Rows));
			}

			/* Zeile oder Spalten von einer in eine andere Stelle infügen */

			ColI = -2;
			ColJ = -2;
			RowI = -2;
			RowJ = -2;

			colValue = calculateCriterium(Columns, Rows);
			;
			rowValue = colValue;

			// find max
			for (int i = 0; i < Columns.size(); i++) {
				for (int j = 0; j < Columns.size(); j++) {
					Vector<Integer> perColumns = insert(Columns, i, j);
					double crit = calculateCriterium(perColumns, Rows);
					if (crit < colValue) {
						colValue = crit;
						ColI = i;
						ColJ = j;
					}

				}
			}

			for (int i = 0; i < Rows.size(); i++) {
				for (int j = 0; j < Rows.size(); j++) {
					Vector<Integer> perRows = insert(Rows, i, j);
					double crit = calculateCriterium(Columns, perRows);
					if (crit < rowValue) {
						rowValue = crit;
						RowI = i;
						RowJ = j;
					}

				}
			}

			if (colValue <= rowValue && ColI != -2) {
				Vector<Integer> perColumns = insert(Columns, ColI, ColJ);
				Columns = perColumns;
				System.out.println("Insert Columns  " + ColI + "  " + ColJ
						+ "   " + calculateCriterium(Columns, Rows));
			}

			if (colValue > rowValue) {
				Vector<Integer> perRows = insert(Rows, RowI, RowJ);
				Rows = perRows;
				System.out.println("Insert Rows  " + RowI + "  " + RowJ + "   "
						+ calculateCriterium(Columns, Rows));
			}

			/*** Gleichzeitiges Vertauchen zweier Zeilen und Spalten **/

			ColI = -2;
			ColJ = -2;
			RowI = -2;
			RowJ = -2;

			double Value = calculateCriterium(Columns, Rows);
			;

			// find max
			for (int i = -1; i < Columns.size(); i++) {
				for (int j = 0; j <= Columns.size(); j++) {
					Vector<Integer> perColumns = vertausche(Columns, i, j);

					for (int ii = -1; ii < Rows.size(); ii++) {
						for (int jj = 0; jj <= Rows.size(); jj++) {
							Vector<Integer> perRows = vertausche(Rows, ii, jj);

							double crit = calculateCriterium(perColumns,
									perRows);
							if (crit < Value) {
								Value = crit;
								ColI = i;
								ColJ = j;
								RowI = ii;
								RowJ = jj;
							}

						}
					}

				}
			}

			if (colValue <= rowValue && ColI != -2) {
				Vector<Integer> perColumns = vertausche(Columns, ColI, ColJ);
				Vector<Integer> perRows = vertausche(Rows, RowI, RowJ);
				Columns = perColumns;
				Rows = perRows;
				System.out.println("Columns + Row  " + ColI + "  " + ColJ
						+ "   " + calculateCriterium(Columns, Rows));
			}

		}

		public Vector<Integer> insert(Vector<Integer> Columns, int col, int pos) {
			Vector<Integer> res = new Vector();
			for (int i = 0; i < Columns.size(); i++) {
				if (i != col)
					res.add(Columns.elementAt(i));

			}
			int newpos = 0;
			if (col >= pos)
				newpos = pos;
			else
				newpos = pos - 1;
			res.add(newpos, Columns.elementAt(col));
			return res;

		}

		public Vector<Integer> vertausche(Vector<Integer> Columns, int i, int j) {
			Vector<Integer> v = new Vector();

			if (i == j)
				return Columns;

			if (i == -1 && j != Columns.size()) {
				v.add(Columns.elementAt(j));
				for (int a = 0; a < Columns.size(); a++) {
					if (a != j)
						v.add(Columns.elementAt(a));
				}
				return v;
			}

			if (j == Columns.size() && i != -1) {

				for (int a = 0; a < Columns.size(); a++) {
					if (a != i)
						v.add(Columns.elementAt(a));
				}
				v.add(Columns.elementAt(i));
				return v;
			}

			for (int a = 0; a < Columns.size(); a++) {
				if (a == i)
					v.add(Columns.elementAt(j));
				if (a == j)
					v.add(Columns.elementAt(i));
				if ((a != i) && (a != j))
					v.add(Columns.elementAt(a));

			}

			return v;
		}

		public Vector<Integer> drehe(Vector<Integer> Columns, int j) {
			Vector<Integer> v = new Vector();

			for (int a = j; a < Columns.size() + j; a++) {
				v.add(Columns.elementAt(a % Columns.size()));

			}

			return v;
		}

	
	}

	public void print() {
		// TODO Auto-generated method stub
		try {
			PrintJob prjob = getToolkit().getPrintJob(plot, null, null);
			Graphics pg = prjob.getGraphics();
			paint(pg);
			pg.dispose();
			prjob.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
