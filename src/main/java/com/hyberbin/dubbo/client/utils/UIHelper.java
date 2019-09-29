package com.hyberbin.dubbo.client.utils;

import com.hyberbin.dubbo.client.analyse.BeanProperty;
import com.hyberbin.dubbo.client.analyse.MethodAnalysis;
import com.hyberbin.dubbo.client.ui.component.ParameterTableCellEditor;
import com.hyberbin.dubbo.client.ui.model.ParameterViewTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.apache.commons.collections.CollectionUtils;

public class UIHelper {

    public UIHelper() {
    }

    public static boolean isMacPlatform() {
        return System.getProperty("os.name", "").toUpperCase(Locale.ENGLISH).startsWith("MAC");
    }

    public static boolean isWindowsPlatform() {
        return System.getProperty("os.name", "").toUpperCase(Locale.ENGLISH).startsWith("WINDOWS");
    }


    public static ParameterViewTableModel parseMethodAnalysisToTableModel(
            MethodAnalysis methodAnalysis, String[] header) {
        TableView tableView = new TableView();
        parseBeanAnalysisToObjectArrHelper(tableView, methodAnalysis.getProperties(), 0);
        Object[][] objects = new Object[tableView.objectList.size()][];

        for (int i = 0; i < tableView.objectList.size(); ++i) {
            objects[i] = tableView.objectList.get(i);
        }

        return new ParameterViewTableModel(objects, header, tableView.editable, tableView.typeList,
                methodAnalysis);
    }

    public static void renderParameterViewTable(JTable jTable, ParameterViewTableModel tableModel) {
        jTable.setModel(tableModel);
        ParameterTableCellEditor cellEditor = new ParameterTableCellEditor(jTable, tableModel);
        cellEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                parseTableModelToMethodAnalysis(tableModel);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {

            }
        });
        jTable.getColumnModel().getColumn(1).setCellEditor(cellEditor);
    }

    private static void parseBeanAnalysisToObjectArrHelper(TableView tableView,
            List<BeanProperty> properties, int level) {
        Iterator var3 = properties.iterator();

        while (var3.hasNext()) {
            BeanProperty beanProperty = (BeanProperty) var3.next();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < level; ++i) {
                if (i == level - 1) {
                    sb.append(" |--");
                } else {
                    sb.append(" |  ");
                }
            }

            Object[] objects = new Object[]{sb.toString() + beanProperty.getName(),
                    beanProperty.getValue(), beanProperty.getTypeName()};
            tableView.objectList.add(objects);
            Boolean[] booleans = new Boolean[]{false, null, false};
            if (!CollectionUtils.isEmpty(beanProperty.getSub())) {
                booleans[1] = false;
                tableView.editable.add(booleans);
                tableView.typeList.add(null);
                parseBeanAnalysisToObjectArrHelper(tableView, beanProperty.getSub(), level + 1);
            } else {
                booleans[1] = true;
                tableView.editable.add(booleans);
                tableView.typeList.add(beanProperty.getType());
            }
        }

    }

    public static MethodAnalysis parseTableModelToMethodAnalysis(
            ParameterViewTableModel tableModel) {
        MethodAnalysis methodAnalysis = tableModel.getMethodAnalysis();
        UIHelper.DataHolder<Integer> dateHolder = new UIHelper.DataHolder();
        dateHolder.data = 0;
        parseTableModelToMethodAnalysisHelper(tableModel.getDataVector(),
                methodAnalysis.getProperties(), dateHolder);
        return methodAnalysis;
    }

    private static void parseTableModelToMethodAnalysisHelper(Vector tableValue,
            List<BeanProperty> properties, UIHelper.DataHolder<Integer> rows) {
        Iterator var3 = properties.iterator();

        while (var3.hasNext()) {
            BeanProperty beanProperty = (BeanProperty) var3.next();
            Integer var6 = rows.data;
            Object var7 = rows.data = rows.data + 1;
            beanProperty.setValue((String) ((Vector) tableValue.elementAt(var6)).elementAt(1));
            if (!CollectionUtils.isEmpty(beanProperty.getSub())) {
                parseTableModelToMethodAnalysisHelper(tableValue, beanProperty.getSub(), rows);
            }
        }

    }

    public static String getMetaKey() {
        return isMacPlatform() ? "meta" : "control";
    }

    public static void addUndoSupport(JTextComponent jTextComponent) {
        final UndoManager undo = new UndoManager();
        javax.swing.text.Document doc = jTextComponent.getDocument();
        doc.addUndoableEditListener((evt) -> {
            undo.addEdit(evt.getEdit());
        });
        jTextComponent.getActionMap().put("Undo", new AbstractAction("Undo") {
            private static final long serialVersionUID = 6689762723122033952L;

            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException var3) {
                }

            }
        });
        jTextComponent.getInputMap().put(KeyStroke.getKeyStroke(getMetaKey() + " Z"), "Undo");
        jTextComponent.getActionMap().put("Redo", new AbstractAction("Redo") {
            private static final long serialVersionUID = -9125665162908541926L;

            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException var3) {
                }

            }
        });
        jTextComponent.getInputMap().put(KeyStroke.getKeyStroke(getMetaKey() + " shift Z"), "Redo");
    }

    private static class DataHolder<T> {

        public T data;

        private DataHolder() {
        }
    }

    private static class TableView {

        final List<Object[]> objectList;
        final List<Boolean[]> editable;
        final List<Class> typeList;

        private TableView() {
            this.objectList = new ArrayList();
            this.editable = new ArrayList();
            this.typeList = new ArrayList();
        }
    }
}
