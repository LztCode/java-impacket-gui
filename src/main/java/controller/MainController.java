package controller;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.JFXTabPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;


public class MainController {
    @FXML
    private MenuItem proxySetupBtn;

    @FXML
    private JFXTabPane Tools;

    @FXML
    private Tab Wmiexec;

    @FXML
    private Tab Psexec;

    @FXML
    private Tab SMBexec;

    @FXML
    private Tab ATexec;

    @FXML
    private Tab DCOMexec;


    // 设置相关信息保存
    public static final Map<String, Object> settingInfo = new HashMap<>();

    // 监听菜单事件
    private void initToolbar() {
        //代理 设置
        this.proxySetupBtn.setOnAction((event) -> {
            Alert inputDialog = new Alert(Alert.AlertType.NONE);
            Window window = inputDialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest((e) -> {
                window.hide();
            });
            inputDialog.setTitle("代理设置");
            ToggleGroup statusGroup = new ToggleGroup();
            RadioButton enableRadio = new RadioButton("启用");
            RadioButton disableRadio = new RadioButton("禁用");
            enableRadio.setToggleGroup(statusGroup);
            disableRadio.setToggleGroup(statusGroup);
            disableRadio.setSelected(true);
            HBox statusHbox = new HBox();
            statusHbox.setSpacing(10.0D);
            statusHbox.getChildren().add(enableRadio);
            statusHbox.getChildren().add(disableRadio);
            GridPane proxyGridPane = new GridPane();
            proxyGridPane.setVgap(15.0D);
            proxyGridPane.setPadding(new Insets(20.0D, 20.0D, 0.0D, 10.0D));
            Label typeLabel = new Label("类型：");
            ComboBox typeCombo = new ComboBox();
            typeCombo.setItems(FXCollections.observableArrayList("HTTP", "SOCKS"));
            typeCombo.getSelectionModel().select(0);
            Label IPLabel = new Label("IP地址：");
            TextField IPText = new TextField("127.0.0.1");
            Label PortLabel = new Label("端口：");
            TextField PortText = new TextField("8080");
            Label userNameLabel = new Label("用户名：");
            TextField userNameText = new TextField();
            Label passwordLabel = new Label("密码：");
            TextField passwordText = new TextField();
            Button cancelBtn = new Button("取消");
            Button saveBtn = new Button("保存");


            try {
                Proxy proxy = (Proxy) settingInfo.get("proxy");
                if (proxy != null) {
                    enableRadio.setSelected(true);
                } else {
                    disableRadio.setSelected(true);
                }

                if (settingInfo.size() > 0) {
                    String type = (String) settingInfo.get("type");
                    if (type.equals("HTTP")) {
                        typeCombo.getSelectionModel().select(0);
                    } else if (type.equals("SOCKS")) {
                        typeCombo.getSelectionModel().select(1);
                    }

                    String ip = (String) settingInfo.get("ip");
                    String port = (String) settingInfo.get("port");
                    IPText.setText(ip);
                    PortText.setText(port);
                    String username = (String) settingInfo.get("username");
                    String password = (String) settingInfo.get("password");
                    userNameText.setText(username);
                    passwordText.setText(password);
                }


            } catch (Exception ignored) {
            }

            saveBtn.setOnAction((e) -> {
                if (disableRadio.isSelected()) {
                    settingInfo.put("proxy", null);
                    inputDialog.getDialogPane().getScene().getWindow().hide();
                } else {

                    final String type;
                    if (!userNameText.getText().trim().equals("")) {
                        final String proxyUser = userNameText.getText().trim();
                        type = passwordText.getText();
                        Authenticator.setDefault(new Authenticator() {
                            public PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(proxyUser, type.toCharArray());
                            }
                        });
                    } else {
                        Authenticator.setDefault(null);
                    }

                    settingInfo.put("username", userNameText.getText());
                    settingInfo.put("password", passwordText.getText());
                    InetSocketAddress proxyAddr = new InetSocketAddress(IPText.getText(), Integer.parseInt(PortText.getText()));

                    settingInfo.put("ip", IPText.getText());
                    settingInfo.put("port", PortText.getText());
                    String proxy_type = typeCombo.getValue().toString();
                    settingInfo.put("type", proxy_type);
                    Proxy proxy;
                    if (proxy_type.equals("HTTP")) {
                        proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
                        settingInfo.put("proxy", proxy);
                    } else if (proxy_type.equals("SOCKS")) {
                        proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
                        settingInfo.put("proxy", proxy);
                    }

                    inputDialog.getDialogPane().getScene().getWindow().hide();
                }
            });

            cancelBtn.setOnAction((e) -> {
                inputDialog.getDialogPane().getScene().getWindow().hide();
            });
            proxyGridPane.add(statusHbox, 1, 0);
            proxyGridPane.add(typeLabel, 0, 1);
            proxyGridPane.add(typeCombo, 1, 1);
            proxyGridPane.add(IPLabel, 0, 2);
            proxyGridPane.add(IPText, 1, 2);
            proxyGridPane.add(PortLabel, 0, 3);
            proxyGridPane.add(PortText, 1, 3);
            proxyGridPane.add(userNameLabel, 0, 4);
            proxyGridPane.add(userNameText, 1, 4);
            proxyGridPane.add(passwordLabel, 0, 5);
            proxyGridPane.add(passwordText, 1, 5);
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(20.0D);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().add(cancelBtn);
            buttonBox.getChildren().add(saveBtn);
            GridPane.setColumnSpan(buttonBox, 2);
            proxyGridPane.add(buttonBox, 0, 6);
            inputDialog.getDialogPane().setContent(proxyGridPane);
            inputDialog.showAndWait();
        });
    }

    public static String scriptPath = "Wmiexec";
    //FXML初始化
    @FXML
    public void initialize() throws IOException {
        // 解决tls协议的问题  如天融信防火墙无法访问.
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
        Security.setProperty("jdk.certpath.disabledAlgorithms", "");
        Tools.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab.getId().equals("Wmiexec")) {
                scriptPath = "Wmiexec";
//                System.out.println("进入WMIExecController");
                // 导航到WMIExecController，执行相关逻辑操作
            } else if (newTab.getId().equals("Psexec")) {
                scriptPath = "Psexec";
//                System.out.println("进入PsexecExecController");
                // 导航到PsexecExecController，执行相关逻辑操作
            } else if (newTab.getId().equals("SMBexec")) {
                scriptPath = "SMBexec";
//                System.out.println("进入SMBexecExecController");
                // 导航到PsexecExecController，执行相关逻辑操作
            } else if (newTab.getId().equals("ATexec")) {
                scriptPath = "ATexec";
//                System.out.println("进入ATexecExecController");
                // 导航到PsexecExecController，执行相关逻辑操作
            } else if (newTab.getId().equals("DCOMexec")) {
                scriptPath = "DCOMexec";
//                System.out.println("进入DCOMexecExecController");
                // 导航到PsexecExecController，执行相关逻辑操作
            }

        });
        this.initToolbar();

//        //Wmi
//        FXMLLoader wmiexecLoader = new FXMLLoader(getClass().getResource("Impacketexec.fxml"));
//        wmiexecLoader.load();
//        AttController wmiexecController = wmiexecLoader.getController();
//
//        //PS
//        FXMLLoader psexecLoader = new FXMLLoader(getClass().getResource("Psexec.fxml"));
//        psexecLoader.load();
//        AttController psexecController = psexecLoader.getController();
//
//        //SMB
//        FXMLLoader smbexecLoader = new FXMLLoader(getClass().getResource("SMBexec.fxml"));
//        smbexecLoader.load();
//        AttController smbexecController = smbexecLoader.getController();
//
//        //AT
//        FXMLLoader atexecLoader = new FXMLLoader(getClass().getResource("ATexec.fxml"));
//        atexecLoader.load();
//        AttController ATexecController = atexecLoader.getController();
//
//
//        //DCOM
//        FXMLLoader DCOMexecLoader = new FXMLLoader(getClass().getResource("DCOMexec.fxml"));
//        DCOMexecLoader.load();
//        AttController DCOMexecController = DCOMexecLoader.getController();
//
//        Tools.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
//            if (newTab.getId().equals("Wmiexec")) {
//                System.out.println("进入WMIExecController");
//                wmiexecController.initialize();
//                // 导航到WMIExecController，执行相关逻辑操作
//            } else if (newTab.getId().equals("Psexec")) {
//                System.out.println("进入PsexecExecController");
//                psexecController.initialize();
//                // 导航到PsexecExecController，执行相关逻辑操作
//            } else if (newTab.getId().equals("SMBexec")) {
//                System.out.println("进入SMBexecExecController");
//                smbexecController.initialize();
//                // 导航到PsexecExecController，执行相关逻辑操作
//            } else if (newTab.getId().equals("ATexec")) {
//                System.out.println("进入ATexecExecController");
//                ATexecController.initialize();
//                // 导航到PsexecExecController，执行相关逻辑操作
//            } else if (newTab.getId().equals("DCOMexec")) {
//                System.out.println("进入DCOMexecExecController");
//                DCOMexecController.initialize();
//                // 导航到PsexecExecController，执行相关逻辑操作
//            }
//        });
    }
}
