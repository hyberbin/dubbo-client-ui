package com.hyberbin.dubbo.client.ui.component;

import com.hyberbin.dubbo.client.ui.model.ParameterViewTableModel;
import com.hyberbin.dubbo.client.utils.ReflectUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class ParameterTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = -8760925288682050616L;
    private final ParameterViewTableModel tableModel;
    private List<JComponent> editorComponents = new ArrayList();
    private List<EditorDelegate> delegates = new ArrayList();
    private int clickCountToStart = 2;
    private JTable jTable;

    public ParameterTableCellEditor(JTable jTable, ParameterViewTableModel tableModel) {
        this.tableModel = tableModel;
        this.jTable = jTable;

        for (int i = 0; i < tableModel.getTypeList().size(); ++i) {
            Class type = tableModel.getTypeList().get(i);
            JComponent jComponent = null;
            EditorDelegate delegate = null;
            if (type != null && type.isEnum()) {
                Object[] enums = type.getEnumConstants();
                Vector<String> enumsToStr = new Vector();
                enumsToStr.add("");
                Object[] var9 = enums;
                int var10 = enums.length;

                for (int var11 = 0; var11 < var10; ++var11) {
                    Object obj = var9[var11];
                    enumsToStr.add(((Enum) obj).name());
                }

                JComboBox<String> comboBox = new JComboBox(enumsToStr);
                comboBox.setFont(jTable.getFont());
                jComponent = comboBox;
                delegate = new ComboBoxEditorDelegate(comboBox);
                comboBox.addActionListener(delegate);
            } else if (type != null && Date.class.isAssignableFrom(type)) {
                final JTextField textField = new JTextField();
                textField.setFont(jTable.getFont());
                textField.setBorder(new LineBorder(Color.LIGHT_GRAY));
                jComponent = textField;
                delegate = new EditorDelegate() {
                    public void setValue(Object value) {
                        if (StringUtils.isBlank((String) value)) {
                            textField.setText((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"))
                                    .format(new Date()));
                        } else {
                            textField.setText(value.toString());
                        }

                    }

                    public Object getCellEditorValue() {
                        return textField.getText();
                    }
                };
                textField.addActionListener(delegate);
            } else if (ReflectUtil.isBooleanType(type)) {
                Vector<String> options = new Vector();
                if (Boolean.class.equals(type)) {
                    options.add("");
                    options.add("TRUE");
                    options.add("FALSE");
                } else {
                    options.add("");
                    options.add("true");
                    options.add("false");
                }

                JComboBox<String> comboBox = new JComboBox(options);
                comboBox.setFont(jTable.getFont());
                jComponent = comboBox;
                delegate = new ComboBoxEditorDelegate(comboBox);
                comboBox.addActionListener(delegate);
            } else {
                JTextField textField=new JTextField();
                jComponent = textField;
                delegate = new EditorDelegate() {
                    public void setValue(Object value) {
                        textField.setText(value != null ? value.toString() : "");
                    }

                    public Object getCellEditorValue() {
                        return textField.getText();
                    }
                };
                textField.addActionListener(delegate);
            }

            this.editorComponents.add(jComponent);
            this.delegates.add(delegate);
        }

    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        this.delegates.get(row).setValue(value);
        return this.editorComponents.get(row);
    }

    public Object getCellEditorValue() {
        return this.delegates.get(this.jTable.getEditingRow()).getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() >= this.clickCountToStart;
        } else {
            return true;
        }
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return this.delegates.get(this.jTable.getEditingRow()).shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
        return this.jTable.getEditingRow() != -1 && this.delegates.get(this.jTable.getEditingRow())
                .stopCellEditing();
    }

    public void cancelCellEditing() {
        if (this.jTable.getEditingRow() != -1) {
            this.delegates.get(this.jTable.getEditingRow()).cancelCellEditing();
        }
    }

    protected class EditorDelegate implements ActionListener, ItemListener {

        protected Object value;

        protected EditorDelegate() {
        }

        public Object getCellEditorValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount()
                        >= ParameterTableCellEditor.this.clickCountToStart;
            } else {
                return true;
            }
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean startCellEditing(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public void actionPerformed(ActionEvent e) {
            stopCellEditing();
        }

        public void itemStateChanged(ItemEvent e) {
            stopCellEditing();
        }
    }

    protected class ComboBoxEditorDelegate extends EditorDelegate {

        private JComboBox<String> comboBox;

        public ComboBoxEditorDelegate(JComboBox<String> comboBox) {
            super();
            this.comboBox = comboBox;
        }

        public void setValue(Object value) {
            this.comboBox.setSelectedItem(value);
        }

        public Object getCellEditorValue() {
            return this.comboBox.getSelectedItem();
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                MouseEvent e = (MouseEvent) anEvent;
                return e.getID() != 506;
            } else {
                return true;
            }
        }

        public boolean stopCellEditing() {
            if (this.comboBox.isEditable()) {
                this.comboBox.actionPerformed(new ActionEvent(this, 0, ""));
            }
            return super.stopCellEditing();
        }
    }
}
