package bv.view;

import bv.component.SwitchButton;
import bv.service.CICOService;
import bv.service.CICOServiceImpl;

import static bv.utils.CompUtils.*;

import bv.utils.Constant;
import bv.utils.FileUtils;
import bv.utils.PopupUtils;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AutoCICOFrame extends JFrame {
    private SwitchButton toggleButton;
    private JTextField accessTokenField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton saveButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton loginButton;
    private JCheckBox checkBoxButton;
    private Map<String, Object> settingData;
    private final CICOService cicoService;


    public AutoCICOFrame() {
        cicoService = CICOServiceImpl.getInstance();
        initData();
        initComponents();
        initEvents();
    }

    private void initData() {
        this.settingData = Optional.of(FileUtils.loadFromFile(Constant.SETTING_FILE))
                .stream()
                .filter(Predicate.not(String::isBlank))
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1], (s1, s2) -> s2));
    }

    private void initComponents() {
        this.setTitle("My BV Clone");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 150);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(null);
        this.setResizable(false);


        JLabel label = new JLabel("Access Token:");
        label.setBounds(10, 10, getPreWidth(label), 20);
        accessTokenField = new JTextField(20);
        accessTokenField.setBounds(getPreWidth(label) + 15, 10, getPreWidth(accessTokenField) + 20, 20);
        saveButton = new JButton("Save");
        saveButton.setBounds(this.getWidth() / 2 - getPreWidth(saveButton) / 2, label.getY() + 25, getPreWidth(saveButton), 20);
        JLabel loginLabel = new JLabel("Login?");
        loginLabel.setBounds(10, saveButton.getY() + 30, getPreWidth(loginLabel), 20);
        toggleButton = new SwitchButton();
        toggleButton.setBounds(getPreWidth(loginLabel) + 15, saveButton.getY() + 30, getPreWidth(toggleButton), 20);
        usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(10, loginLabel.getY() + 30, getPreWidth(usernameLabel), 20);
        usernameField = new JTextField(20);
        usernameField.setBounds(getPreWidth(usernameLabel) + 15, loginLabel.getY() + 30, getPreWidth(usernameField), 20);
        passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(10, usernameLabel.getY() + 25, getPreWidth(passwordLabel), 20);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(usernameField.getX(), usernameLabel.getY() + 25, getPreWidth(passwordField), 20);
        checkBoxButton = new JCheckBox("Remember Account");
        checkBoxButton.setBounds(passwordField.getX(), passwordLabel.getY() + 25, getPreWidth(checkBoxButton), 20);
        loginButton = new JButton("Login");
        loginButton.setBounds(this.getWidth() / 2 - getPreWidth(loginButton) / 2, checkBoxButton.getY() + 25, getPreWidth(loginButton), 20);



        this.add(label);
        this.add(accessTokenField);
        this.add(saveButton);
        this.add(loginLabel);
        this.add(toggleButton);
        this.add(usernameLabel);
        this.add(usernameField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(loginButton);
        this.add(checkBoxButton);

        passwordLabel.setVisible(false);
        usernameLabel.setVisible(false);
        passwordField.setVisible(false);
        usernameField.setVisible(false);
        loginButton.setVisible(false);
        checkBoxButton.setVisible(false);

        checkBoxButton.setSelected(Boolean.parseBoolean(settingData.getOrDefault("remember_account", false).toString()));
    }

    private void initEvents() {

        checkBoxButton.addActionListener(e -> {
            settingData.put("remember_account", checkBoxButton.isSelected());
            String setting = settingData.entrySet().stream()
                    .map(s -> s.getKey() + "=" + s.getValue())
                    .collect(Collectors.joining(","));
            FileUtils.saveToFile(setting, Constant.SETTING_FILE);
            FileUtils.saveToFile(checkBoxButton.isSelected() ?
                    usernameField.getText() + "," + passwordField.getText()
                    : "", Constant.ACCOUNT_FILE);
        });

        Optional.ofNullable(FileUtils.loadFromFile(Constant.ACCOUNT_FILE))
                .filter(Predicate.not(String::isBlank))
                .map(s -> s.split(","))
                .ifPresentOrElse(s -> {
                    usernameField.setText(s[0]);
                    passwordField.setText(s[1]);
                }, () -> {
                    usernameField.setText("");
                    passwordField.setText("");
                });


        String fileData = FileUtils.loadFromFile(Constant.TOKEN_FILE);
        accessTokenField.setText(fileData);

        saveButton.addActionListener(e -> {
            FileUtils.saveToFile(accessTokenField.getText(), Constant.TOKEN_FILE);
            if (!cicoService.checkinCheckoutWithToken(accessTokenField.getText())) {
                cicoService.checkinCheckoutWithUser(null, null);
                accessTokenField.setText(FileUtils.loadFromFile(Constant.TOKEN_FILE));
            } else {
                PopupUtils.showSuccess();
            }
        });

        toggleButton.addEventSelected(selected -> {
            this.setSize(this.getWidth(), this.getHeight() + (selected ? 100 : -100));

            passwordLabel.setVisible(selected);
            usernameLabel.setVisible(selected);
            passwordField.setVisible(selected);
            usernameField.setVisible(selected);
            loginButton.setVisible(selected);
            checkBoxButton.setVisible(selected);
        });

        loginButton.addActionListener(e -> {
            cicoService.checkinCheckoutWithUser(usernameField.getText(), passwordField.getText());
            FileUtils.saveToFile(usernameField.getText() + "," + passwordField.getText(), Constant.ACCOUNT_FILE);
            accessTokenField.setText(FileUtils.loadFromFile(Constant.TOKEN_FILE));
        });

    }
}
