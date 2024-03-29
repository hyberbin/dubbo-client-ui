/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyberbin.dubbo.client.ui.frames;

import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.config.ConfigFactory;
import com.hyberbin.dubbo.client.dao.SqliteDao;
import com.hyberbin.dubbo.client.domain.DubboConfDO;
import com.google.inject.Inject;
import java.awt.Toolkit;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 */
public class DubboConfJFrame extends javax.swing.JFrame {


    private SqliteDao sqliteDao;

    /**
     * Creates new form DubboConfJFrame
     */
    @Inject
    public DubboConfJFrame(SqliteDao sqliteDao) {
        this.sqliteDao=sqliteDao;
        initComponents();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int) (toolkit.getScreenSize().getWidth() - getWidth()) / 2;
        int y = (int) (toolkit.getScreenSize().getHeight() - getHeight()) / 2;
        setLocation(x, y);
        loadConfList();
    }

    private void loadConfList(){
        List<DubboConfDO> allDubboConf = sqliteDao.getAllDubboConf();
        DefaultListModel<String> model = (DefaultListModel) confList.getModel();
        model.removeAllElements();
        allDubboConf.forEach(c->model.addElement(c.getId()));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        confList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        protocol = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        address = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        group = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        confList.setModel(new javax.swing.DefaultListModel());
        confList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                confListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(confList);

        jLabel1.setText("name");

        jLabel2.setText("protocol");

        protocol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "dubbo", "zookeeper" }));

        jLabel3.setText("address");

        jLabel4.setText("group");

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/methodDefined@2x.png"))); // NOI18N
        addButton.setToolTipText("保存");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/commit.png"))); // NOI18N
        saveButton.setToolTipText("保存");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        delButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/methodNotDefined@2x.png"))); // NOI18N
        delButton.setToolTipText("保存");
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(delButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(34, 34, 34)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel1)
                        .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(protocol, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(group, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(saveButton)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(delButton))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(protocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(30, 30, 30)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(36, 36, 36)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(group, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        DubboConfDO dubboConfDO = new DubboConfDO("New", "dubbo", "127.0.0.1:2181", "");
        DefaultListModel<String> model = (DefaultListModel) confList.getModel();
        name.setText(dubboConfDO.getId());
        protocol.setSelectedItem(dubboConfDO.getProtocol());
        address.setText(dubboConfDO.getAddress());
        group.setText(dubboConfDO.getGroup());
        if (!model.contains(dubboConfDO.getId())) {
            model.addElement(dubboConfDO.getId());
            sqliteDao.saveDubboConf(dubboConfDO);
            ConfigFactory.loadDubboConf();
            DubboUIFrame queenUIFrame = CoderQueenModule.getInstance(DubboUIFrame.class);
            queenUIFrame.loadDubboConf(null);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void confListValueChanged(
            javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_confListValueChanged
        String selected = confList.getSelectedValue();
        DubboConfDO dubboConf = ConfigFactory.getDubboConf(selected);
        if(dubboConf!=null){
            name.setText(dubboConf.getId());
            protocol.setSelectedItem(dubboConf.getProtocol());
            address.setText(dubboConf.getAddress());
            group.setText(dubboConf.getGroup());
        }

    }//GEN-LAST:event_confListValueChanged

    private void saveButtonActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (StringUtils.isBlank(name.getText())
                || StringUtils.isBlank((String) protocol.getSelectedItem())
                || StringUtils.isBlank(address.getText())) {
            JOptionPane.showMessageDialog(this, "name,address不允许为空", "必填信息",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String selectedValue = confList.getSelectedValue();
        if (!Objects.equals(name.getText(), selectedValue)
                && ConfigFactory.getDubboConf(name.getText()) != null) {
            JOptionPane.showMessageDialog(this, "name冲突", "错误信息", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DubboConfDO dubboConfDO = new DubboConfDO(name.getText(),
                (String) protocol.getSelectedItem(), address.getText(), group.getText());
        sqliteDao.saveDubboConf(dubboConfDO);
        if(!Objects.equals(name.getText(), selectedValue)){
            sqliteDao.deleteDubboConf(selectedValue);
            DefaultListModel<String> model = (DefaultListModel) confList.getModel();
            model.removeElement(selectedValue);
            model.addElement(name.getText());
            confList.setSelectedValue(name.getText(),true);
            DubboUIFrame queenUIFrame = CoderQueenModule.getInstance(DubboUIFrame.class);
            queenUIFrame.loadDubboConf(name.getText());
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void delButtonActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButtonActionPerformed
        String selectedValue = confList.getSelectedValue();
        sqliteDao.deleteDubboConf(selectedValue);
        DefaultListModel<String> model = (DefaultListModel) confList.getModel();
        model.removeElement(selectedValue);
        ConfigFactory.loadDubboConf();
        DubboUIFrame queenUIFrame = CoderQueenModule.getInstance(DubboUIFrame.class);
        queenUIFrame.loadDubboConf(null);
    }//GEN-LAST:event_delButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField address;
    private javax.swing.JList<String> confList;
    private javax.swing.JButton delButton;
    private javax.swing.JTextField group;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField name;
    private javax.swing.JComboBox<String> protocol;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
