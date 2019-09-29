package com.hyberbin.dubbo.client;

import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.dao.SqliteDao;
import com.hyberbin.dubbo.client.ui.frames.DubboUIFrame;
import javax.swing.UIManager;
import lombok.SneakyThrows;

public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
          .getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
        UIManager.put("RootPane.setupButtonVisible", false);
      }
    } catch (Throwable ex) {
      java.util.logging.Logger.getLogger(DubboUIFrame.class.getName())
          .log(java.util.logging.Level.SEVERE, null, ex);
    }

    java.awt.EventQueue.invokeLater(new Runnable() {
      @SneakyThrows
      public void run() {
        try {
          org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
          CoderQueenModule.getInstance(SqliteDao.class).saveUserLog();
        } catch (Exception e) {
        }
        DubboUIFrame CoderQueenUIFrame=CoderQueenModule.getInstance(DubboUIFrame.class);
        CoderQueenUIFrame.setVisible(true);
      }
    });
  }
}
