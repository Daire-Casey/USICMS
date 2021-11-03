/*******************************************************************************
 * Copyright (C) 2021 dapeng
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package org.dapeng.usicms.gui;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.dapeng.usicms.handler.ListTransferHandler;
import org.dapeng.usicms.handler.ProjectLevelConfigs;
import org.dapeng.usicms.model.UserStory;
import org.dapeng.usicms.model.UserStoryStatus;

public class USICMS {

	private JFrame frame;
	private JPopupMenu delPopup;
	private JMenuItem delMenuItem;
	private final Action actionCreateProject = new SwingActionCreateProject();
	private final Action actionCreateUserStory = new SwingActionCreateUserStory();
	private final Action actionLoadProject = new SwingActionLoadProject();

	private DefaultListModel listInProgressModel = new DefaultListModel();
	private DefaultListModel listDoneModel = new DefaultListModel();
	private DefaultListModel listToDoModel = new DefaultListModel();
	private static final USICMS SINGLE_USICMS = new USICMS();

	public static USICMS getInstance() {
		return SINGLE_USICMS;
	}

	public void addListModelElement(UserStory us, UserStoryStatus uss) {
		switch (uss) {
		case TODO:
			listToDoModel.addElement(us.getId() + "--" + us.getName());
			break;
		case INPROGRESS:
			listInProgressModel.addElement(us.getId() + "--" + us.getName());
			break;
		case DONE:
			listDoneModel.addElement(us.getId() + "--" + us.getName());
			break;
		}
	}

	public void clearUserStoryLists() {
		listToDoModel.clear();
		listInProgressModel.clear();
		listDoneModel.clear();
	}

	// Takes string containing project filename
	// Clears user story GUI lists and arraylist
	// Reads user stories
	// Rebuilds user story GUI lists
	public void loadProject(String projFileName) {
		clearUserStoryLists();
		ProjectLevelConfigs.projectName = projFileName;
		ProjectLevelConfigs.userStories = new ArrayList<>();
		UserStory usHelper = new UserStory();
		usHelper.readUserStory();

		for (UserStory us : ProjectLevelConfigs.userStories) {
			addListModelElement(us, us.getStatusEnum());
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					USICMS.getInstance().frame.setVisible(true);
					// USICMS window = new USICMS();
					// window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
//		newProject.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		newProject.setVisible(true);

		try {
			CreateProjectDialog dialog = new CreateProjectDialog();

			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public USICMS() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 530, 295);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNew = new JMenu("Project");
		menuBar.add(mnNew);

		JMenuItem mntmNewProject = new JMenuItem("New Project");
		mntmNewProject.setAction(actionCreateProject);
		mnNew.add(mntmNewProject);

		// Add new action to menu
		JMenuItem mntmLoadProject = new JMenuItem("Load Project");
		mntmLoadProject.setAction(actionLoadProject);
		mnNew.add(mntmLoadProject);

		JMenu mnUserStory = new JMenu("User Story");
		menuBar.add(mnUserStory);

		JMenuItem mntmCreateUserStory = new JMenuItem("Create User Story");
		mntmCreateUserStory.setAction(actionCreateUserStory);
		mnUserStory.add(mntmCreateUserStory);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panelToDo = new JPanel();
		frame.getContentPane().add(panelToDo);
		panelToDo.setLayout(null);

		JLabel lblTodo = new JLabel("ToDo");
		lblTodo.setBounds(58, 0, 57, 24);
		panelToDo.add(lblTodo);

		delPopup = new JPopupMenu();
		delMenuItem = new JMenuItem("Delete User Story");
		delMenuItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				int index = PopupListener.index; // index of list item that was right-clicked
				JMenuItem item = (JMenuItem) evt.getSource();
				JPopupMenu pm = (JPopupMenu) item.getParent();
				JList list = (JList) pm.getInvoker(); // identifies the list in question
				if (index >= 0) {
					UserStory.deleteUserStory((String) list.getModel().getElementAt(index)); // delete story with name
																								// and id as input
				}
			}
		});
		delPopup.add(delMenuItem);

		JList listToDo = new JList(listToDoModel);
		listToDo.setName("ToDo");
		listToDo.setBounds(0, 24, 164, 226);
		listToDo.setVisibleRowCount(12);
		listToDo.setDragEnabled(true);
		listToDo.setTransferHandler(new ListTransferHandler());
		listToDo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					DisplayUserStory dialog = new DisplayUserStory((String) list.getModel().getElementAt(index));
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});

		// Adds the listener for the pop up menu
		listToDo.addMouseListener(new PopupListener(delPopup));

		listToDo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panelToDo.add(listToDo);

		JPanel panelInProgress = new JPanel();
		frame.getContentPane().add(panelInProgress);
		panelInProgress.setLayout(null);

		JLabel lblInProgress = new JLabel("In Progress");
		lblInProgress.setBounds(47, 5, 81, 15);
		panelInProgress.add(lblInProgress);

		JList listInProgress = new JList(listInProgressModel);
		listInProgress.setName("InProgress");
		listInProgress.setDragEnabled(true);
		listInProgress.setTransferHandler(new ListTransferHandler());
		listInProgress.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					DisplayUserStory dialog = new DisplayUserStory((String) list.getModel().getElementAt(index));
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});

		// Adds the listener for the pop up menu
		listInProgress.addMouseListener(new PopupListener(delPopup));

		listInProgress.setBounds(0, 25, 176, 226);
		panelInProgress.add(listInProgress);

		JPanel panelDone = new JPanel();
		frame.getContentPane().add(panelDone);
		panelDone.setLayout(null);

		JLabel lblDone = new JLabel("Done");
		lblDone.setBounds(69, 5, 37, 15);
		panelDone.add(lblDone);

		JList listDone = new JList(listDoneModel);
		listDone.setName("Done");
		listDone.setDragEnabled(true);
		listDone.setTransferHandler(new ListTransferHandler());
		listDone.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					DisplayUserStory dialog = new DisplayUserStory((String) list.getModel().getElementAt(index));
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});

		// Adds the listener for the pop up menu
		listDone.addMouseListener(new PopupListener(delPopup));

		listDone.setBounds(12, 25, 164, 226);
		panelDone.add(listDone);
	}

	private class SwingActionCreateProject extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionCreateProject() {
			putValue(NAME, "New Project");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				CreateProjectDialog dialog = new CreateProjectDialog();

				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	// Uses JFileChooser to select project file
	private class SwingActionLoadProject extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionLoadProject() {
			putValue(NAME, "Load Project");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser f = new JFileChooser(new File(System.getProperty("user.dir")));
				f.setFileFilter(new FileNameExtensionFilter("SESP File", "sesp"));
				int dialogResult = f.showOpenDialog(null);
				if (dialogResult == JFileChooser.APPROVE_OPTION) {
					USICMS.getInstance().loadProject(f.getSelectedFile().getName());
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private class SwingActionCreateUserStory extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionCreateUserStory() {
			putValue(NAME, "Create User Story");
		}

		public void actionPerformed(ActionEvent e) {

			CreateUserStory dialog = new CreateUserStory();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
	}
}
