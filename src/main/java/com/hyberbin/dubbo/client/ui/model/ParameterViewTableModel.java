package com.hyberbin.dubbo.client.ui.model;

import com.hyberbin.dubbo.client.analyse.MethodAnalysis;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ParameterViewTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 5943967077006854656L;
    private final List<Boolean[]> editable;
    private final MethodAnalysis methodAnalysis;
    private final List<Class> typeList;

    public ParameterViewTableModel(Object[][] data, Object[] columnNames, List<Boolean[]> editable,
            List<Class> typeList, MethodAnalysis methodAnalysis) {
        super(data, columnNames);
        this.editable = editable;
        this.methodAnalysis = methodAnalysis;
        this.typeList = typeList;
    }

    public boolean isCellEditable(int row, int column) {
        return this.editable.get(row)[column];
    }

    public MethodAnalysis getMethodAnalysis() {
        return this.methodAnalysis;
    }

    public List<Class> getTypeList() {
        return this.typeList;
    }
}