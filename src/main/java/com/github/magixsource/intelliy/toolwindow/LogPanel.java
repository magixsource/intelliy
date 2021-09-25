package com.github.magixsource.intelliy.toolwindow;

import com.github.magixsource.intelliy.idp.Env;
import com.github.magixsource.intelliy.idp.IdpService;
import com.github.magixsource.intelliy.idp.Pod;
import com.github.magixsource.intelliy.setting.YxSettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.CollectionComboBoxModel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LogPanel extends SimpleToolWindowPanel {
    private JComboBox projectComboBox;
    private JComboBox envComboBox;
    private JComboBox instanceComboBox;
    private JTextArea logTextArea;
    private JButton stopButton;
    private JButton clearButton;
    private JPanel rootContainer;

    private static final int timeoutMillis = 3 * 1000;
    private static final int maxTimes = 1000;
    private static boolean isStart = true;

//    private Thread consoleThread = null;
    IdpService idpService = new IdpService(this);

    public void logTail(String log) {
        logTextArea.append(log);
        logTextArea.append("\n");
        // scroll down
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }


    public LogPanel(Project project) {
        super(false);
        System.out.println("Log panel init");
        logTextArea.append("Hello from LogPanel! \n");

//        stopButton.addActionListener(e -> {
//            if (isStart) {
//                isStart = false;
//                // stopButton.setText("Stop");
//                stopButton.setIcon(AllIcons.Actions.Resume);
//                consoleThread.stop();
//            } else {
//                isStart = true;
//                // stopButton.setText("Start");
//                stopButton.setIcon(AllIcons.Actions.Restart);
//                consoleThread.start();
//            }
//        });


        clearButton.addActionListener(e -> {
            logTextArea.setText("");
        });

//        this.initThread();
        // consoleThread.start();

        initProject();

    }

    /**
     * init project by private token
     */
    private void initProject() {
        String privateToken = Objects.requireNonNull(YxSettings.getInstance()).getPrivateToken();
        String baseApi = Objects.requireNonNull(YxSettings.getInstance().getBaseApi());
        // query projects by private token
        List<com.github.magixsource.intelliy.idp.Project> list = idpService.getProjects(baseApi, privateToken);
        assert list != null;
        ComboBoxModel aModel = new CollectionComboBoxModel(list);
        projectComboBox.setModel(aModel);
        projectComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                com.github.magixsource.intelliy.idp.Project project = (com.github.magixsource.intelliy.idp.Project) projectComboBox.getSelectedItem();
                initEnv(baseApi, privateToken, project);
            }
        });
    }

    private void initEnv(String baseApi, String privateToken, com.github.magixsource.intelliy.idp.Project project) {
        // query projects by private token
        Integer projectId = project.getId();
        List<Env> envs = idpService.getEnvs(baseApi, privateToken, projectId);
        ComboBoxModel aModel = new CollectionComboBoxModel(envs);
        envComboBox.setModel(aModel);
        envComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                getPods(baseApi, privateToken, project);
            }
        });
        if (envs.size() == 1) {
            envComboBox.setSelectedIndex(0);
            getPods(baseApi, privateToken, project);
        }
    }


    private void initPod(String baseApi, String privateToken, com.github.magixsource.intelliy.idp.Project project, Env env) {
        Integer projectId = project.getId();
        Integer envId = env.getId();
        // List<Instance> instances = idpService.getInstances(baseApi, privateToken, projectId, envId);
        List<Pod> pods = idpService.getPods(baseApi, privateToken, projectId, envId);
        ComboBoxModel aModel = new CollectionComboBoxModel(pods);
        instanceComboBox.setModel(aModel);
        instanceComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                getLogs(baseApi, privateToken, project, env);
            }
        });

        if (pods.size() > 0) {
            instanceComboBox.setSelectedIndex(0);
            getLogs(baseApi, privateToken, project, env);
        }
    }

    private void getPods(String baseApi, String privateToken, com.github.magixsource.intelliy.idp.Project project) {
        Env env = (Env) envComboBox.getSelectedItem();
        initPod(baseApi, privateToken, project, env);
    }

    private void getLogs(String baseApi, String privateToken, com.github.magixsource.intelliy.idp.Project project, Env env) {
        Pod pod = (Pod) instanceComboBox.getSelectedItem();
        idpService.getLogs(baseApi, privateToken, project, env, pod);
    }

//    private void initThread() {
//        consoleThread = new Thread(() -> {
//            try {
//                while (true) {
//                    // console.print("Hello, time now is: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), ConsoleViewContentType.NORMAL_OUTPUT);
//                    logTextArea.append("Hello, time now is: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
//                    Thread.sleep(timeoutMillis);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//    }


    public void setData(LogPanel data) {

    }

    public void getData(LogPanel data) {
    }

    public JPanel getContent() {
        return rootContainer;
    }

}
