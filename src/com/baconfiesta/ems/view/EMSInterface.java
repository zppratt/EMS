package com.baconfiesta.ems.view;

import com.baconfiesta.ems.controller.Authenticator;
import com.baconfiesta.ems.controller.EMSAdminController;
import com.baconfiesta.ems.controller.EMSController;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.*;
import com.sun.javafx.application.PlatformImpl;
import com.toedter.calendar.JCalendar;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javassist.tools.rmi.ObjectNotFoundException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * The main user interface window of the EMS system.
 *
 * @author team_bacon_fiesta
 */
public class EMSInterface implements EMSInterfaceConstants {
    private EMSController controller;
    private JFrame frame;
    private JPanel header;
    private JPanel mainframe;
    private JPanel footer;
    private JPanel sidebar;

    private JLabel frameTitle;

    private JButton back;
    private JButton logout;
    private JButton createCase;
    private JButton viewRecords;
    private JButton manageUsers;
    private JButton manageRecords;

    private WebView browser1;
    private WebEngine webEngine1;
    private WebView browser2;
    private WebEngine webEngine2;

    private JFXPanel route1Panel;
    private JFXPanel route2Panel;

    private Instant[] reportDateRange;

    /*
    * Holds "user" if previous window was user options
    * holds "info" if it was enter info
    * holds "route" if it was route selection
    */
    private String previous;

    private EmergencyRecord mainEmergencyRecordTempFile;
    private EmergencyRecord alternateEmergencyRecordTempFile;
    private EmergencyRecord[] recentRecords;

    /**
     * The constructor for EMSInterface
     */
    public EMSInterface() {
        // Initialize the fields
        frame = new JFrame();
        header = new JPanel();
        mainframe = new JPanel();
        footer = new JPanel();
        sidebar = new JPanel();
        frameTitle = new JLabel();
        back = new JButton("Back");
        logout = new JButton("Logout");
        createCase = new JButton("Create a New Emergency Case");
        viewRecords = new JButton("View Emergency Records");
        manageUsers = new JButton("Manage Users");
        manageRecords = new JButton("Manage Records");

        // Set panel properties
        header.setLayout(new BorderLayout());
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, FOOTER_HEIGHT));
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, FOOTER_HEIGHT));
        footer.setPreferredSize(new Dimension(Integer.MAX_VALUE, FOOTER_HEIGHT));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setMaximumSize(new Dimension(SIDEBAR_WIDTH,Integer.MAX_VALUE));
        sidebar.setMinimumSize(new Dimension(SIDEBAR_WIDTH,Integer.MAX_VALUE));
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH,Integer.MAX_VALUE));

        // Set panel borders
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainframe.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sidebar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        footer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Set title
        frameTitle.setFont(new Font(frameTitle.getFont().getName(), Font.BOLD, 20));
        frameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(frameTitle, BorderLayout.CENTER);

        // Add components to frame
        frame.add(header, BorderLayout.NORTH);
        frame.add(mainframe, BorderLayout.CENTER);
        frame.add(sidebar, BorderLayout.EAST);
        frame.add(footer, BorderLayout.SOUTH);

        // Set the login screen
        logIn();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        route1Panel = new JFXPanel();
        route2Panel = new JFXPanel();

        // Open the web browser
        PlatformImpl.setImplicitExit(false);
        PlatformImpl.startup(() -> {
            // First window
            browser1 = new WebView();
            webEngine1 = browser1.getEngine();
            Group root1 = new Group();
            root1.getChildren().add(browser1);
            route1Panel.setScene(new Scene(root1));
            // Second window
            browser2 = new WebView();
            webEngine2 = browser2.getEngine();
            Group root2 = new Group();
            root2.getChildren().add(browser2);
            route2Panel.setScene(new Scene(root2));
        });

        // Set logout actionListener
        logout.addActionListener(e -> {
            // Confirm user logout
            if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?\nAny unsaved data will be lost.", null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();
                header.remove(back);
                header.remove(logout);

                // Go back to login
                logIn();
            }
        });

        // Set createCase actionListener
        createCase.addActionListener(e -> {
            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to next window
            enterInfo();
        });

        // Set viewRecords actionListener
        viewRecords.addActionListener(e -> {
            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to next window
            viewEmergencyRecords(null);
        });

        // Set manageUsers actionListener
        manageUsers.addActionListener(e -> {
            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to next window
            manageUsers();
        });

        // Set manageRecords actionListener
        manageRecords.addActionListener(e -> {
            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to the next window
            manageRecords();
        });

        // Set back button actionListener
        back.addActionListener(e -> {
            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Find the next window
            switch (previous) {
                case "user":
                    back.setEnabled(false);
                    mainEmergencyRecordTempFile = null;
                    alternateEmergencyRecordTempFile = null;
                    userActions();
                    break;
                case "info":
                    enterInfo();
                    break;
                case "route":
                    routeSelection();
                    break;
                case "view":
                    viewEmergencyRecords(null);
                    break;
            }
        });
    }

    /**
     * Show log in screen
     */
    public void logIn() {
        // Create label, textfield, and jbutton local variables
        JLabel title = new JLabel("EMS");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameText = new JTextField();
        JPasswordField passwordText = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Configure local variables
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 20));
        usernameText.setMaximumSize(new Dimension(150, usernameText.getPreferredSize().height));
        passwordText.setMaximumSize(new Dimension(150, usernameText.getPreferredSize().height));
        title.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        passwordLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        usernameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        passwordText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        frameTitle.setText("EMS Login");

        // Add variables to the panels
        mainframe.setLayout(new BoxLayout(mainframe, BoxLayout.Y_AXIS));
        mainframe.add(new JLabel("  "));
        mainframe.add(usernameLabel);
        mainframe.add(usernameText);
        mainframe.add(passwordLabel);
        mainframe.add(passwordText);
        mainframe.add(loginButton);

        // Refresh the window
        refreshWindow();

        loginButton.addActionListener(event -> {

            EMSUser user;
            String username = usernameText.getText();
            char[] password = passwordText.getPassword();

            // Attempt to login as a user using specified info
            try {
                Authenticator.init();
                user = Authenticator.authenticate(username, password);
                if (user == null) {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                    return;
                }
                // blank out password for security
                for (int i = 0; i < password.length; i++) {
                    password[i] = ' ';
                }
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(frame, BURP +  "Something broke." + ASK);
                return;
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "Something broke.");
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, BURP + "Trouble reading user directory." + ASK);
                return;
            }

            // If successful then clear window
            mainframe.removeAll();
            if (user.isAdmin()) {
                // If an administrator user then use adminActions()
                try {
                    controller = new EMSAdminController(user, null);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, BURP + "Something broke." + ASK);
                    return;
                }
                userActions();
            } else {
                // If a normal user then use userActions()
                try {
                    controller = new EMSController(user, null);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    JOptionPane.showMessageDialog(frame, BURP + "Something broke." + ASK);
                    return;
                }
                userActions();
            }
        });
    }

    /**
     * Show screen for user actions
     */
    private void userActions() {

        try {
            recentRecords = controller.getRecentRecords();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, BURP + "Something broke." + ASK);
        }

        // Change the title
        frameTitle.setText("Select an Action");

        // Disable the back button
        back.setEnabled(false);

        // Create local variables
        JList<EmergencyRecord> sidebarList = new JList<>(recentRecords);
        JScrollPane sidebarListScroll = new JScrollPane(sidebarList);

        // Add components to the header
        sidebar.add(sidebarListScroll);

        // Add components to the header
        header.add(back, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // Add components to the footer
        footer.add(createCase);
        footer.add(viewRecords);

        // Refresh the window
        refreshWindow();

        if(controller instanceof EMSAdminController){
            adminActions();
        }

        sidebarList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    EmergencyRecord record = sidebarList.getSelectedValue();
                    if (record != null) {
                        // Clear the window
                        mainframe.removeAll();
                        footer.removeAll();
                        sidebar.removeAll();

                        viewEmergencyRecords(record);
                    }
                }
            }
        });

    }

    /**
     * Show screen for admin actions
     */
    private void adminActions() {
        footer.add(manageUsers);
        footer.add(manageRecords);
    }

    /**
     * Show screen to enter info for an emergency record
     */
    private void enterInfo() {
        // Change the title
        frameTitle.setText("Enter Emergency Info");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Create label, textField, and radioButton local variables
        JLabel callerTitle = new JLabel("Caller Information");
        JLabel descriptionTitle = new JLabel("Description of the Emergency");
        JLabel categorizeTitle = new JLabel("Categorize Emergency");
        JLabel locationTitle = new JLabel("Location of the Emergency");
        JLabel firstnameLabel = new JLabel("First Name:");
        JLabel lastnameLabel = new JLabel("Last Name:");
        JLabel phoneLabel = new JLabel("Phone Number:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel stateLabel = new JLabel("State:");
        JLabel cityLabel = new JLabel("City:");

        JTextField firstnameText = new JTextField();
        JTextField lastnameText = new JTextField();
        JTextField phoneText = new JTextField();
        JTextField addressText = new JTextField();
        JTextField stateText = new JTextField();
        JTextField cityText = new JTextField();

        JTextArea descriptionText = new JTextArea("", 18, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionText);

        JPanel left = new JPanel();
        JPanel middle = new JPanel();
        JPanel right = new JPanel();

        JRadioButton fire = new JRadioButton("Fire");
        JRadioButton crime = new JRadioButton("Crime");
        JRadioButton medical = new JRadioButton("Medical");
        JRadioButton hoax = new JRadioButton("Hoax");
        JRadioButton crash = new JRadioButton("Car Crash");
        ButtonGroup categories = new ButtonGroup();

        JButton selectRoute = new JButton("Select Route");

        // Add radiobuttons to the group
        categories.add(fire);
        categories.add(crime);
        categories.add(medical);
        categories.add(hoax);
        categories.add(crash);

        // Set properties of the fields
        callerTitle.setFont(new Font(callerTitle.getFont().getName(), Font.BOLD, 14));
        descriptionTitle.setFont(new Font(descriptionTitle.getFont().getName(), Font.BOLD, 14));
        categorizeTitle.setFont(new Font(categorizeTitle.getFont().getName(), Font.BOLD, 14));
        locationTitle.setFont(new Font(locationTitle.getFont().getName(), Font.BOLD, 14));

        descriptionTitle.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        descriptionText.setLineWrap(true);


        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height));
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height));
        phoneText.setMaximumSize(new Dimension(200, phoneText.getPreferredSize().height));
        addressText.setMaximumSize(new Dimension(200, addressText.getPreferredSize().height));
        stateText.setMaximumSize(new Dimension(200, stateText.getPreferredSize().height));
        cityText.setMaximumSize(new Dimension(200, cityText.getPreferredSize().height));

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        callerTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        categorizeTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        locationTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        cityLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        cityText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        fire.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crime.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        medical.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        hoax.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crash.setAlignmentX(JFrame.LEFT_ALIGNMENT);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        mainframe.setLayout(new GridLayout(1, 3));

        // Add components to the window
        mainframe.add(left);
        mainframe.add(middle);
        mainframe.add(right);

        // Add components to left part of mainframe
        left.add(callerTitle);
        left.add(new JLabel("  "));
        left.add(firstnameLabel);
        left.add(new JLabel("  "));
        left.add(firstnameText);
        left.add(new JLabel("  "));
        left.add(lastnameLabel);
        left.add(new JLabel("  "));
        left.add(lastnameText);
        left.add(new JLabel("  "));
        left.add(phoneLabel);
        left.add(new JLabel("  "));
        left.add(phoneText);

        // Add components to middle part of mainframe
        middle.add(locationTitle);
        middle.add(new JLabel("  "));
        middle.add(addressLabel);
        middle.add(new JLabel("  "));
        middle.add(addressText);
        middle.add(new JLabel("  "));
        middle.add(cityLabel);
        middle.add(new JLabel("  "));
        middle.add(cityText);
        middle.add(new JLabel("  "));
        middle.add(stateLabel);
        middle.add(new JLabel("  "));
        middle.add(stateText);

        // Add components to right part of mainframe
        right.add(categorizeTitle);
        right.add(new JLabel("  "));
        right.add(fire);
        right.add(new JLabel("  "));
        right.add(crime);
        right.add(new JLabel("  "));
        right.add(medical);
        right.add(new JLabel("  "));
        right.add(hoax);
        right.add(new JLabel("  "));
        right.add(crash);

        // Add components to the sidebar
        sidebar.add(descriptionTitle);
        sidebar.add(new JLabel("  "));
        sidebar.add(descriptionScroll);

        // Add components to footer
        footer.add(selectRoute);

        // Check if there was a temp record to repopulate
        if(mainEmergencyRecordTempFile != null){
            firstnameText.setText(mainEmergencyRecordTempFile.getCaller().getFirstName());
            lastnameText.setText(mainEmergencyRecordTempFile.getCaller().getLastName());
            phoneText.setText(mainEmergencyRecordTempFile.getCaller().getPhone());
            addressText.setText(mainEmergencyRecordTempFile.getLocation().getAddress());
            stateText.setText(mainEmergencyRecordTempFile.getLocation().getState());
            cityText.setText(mainEmergencyRecordTempFile.getLocation().getCity());
            descriptionText.setText(mainEmergencyRecordTempFile.getDescription());
            if(mainEmergencyRecordTempFile.getCategory().equals(Category.FIRE)){
                fire.setSelected(true);
            }else if(mainEmergencyRecordTempFile.getCategory().equals(Category.CRIME)){
                crime.setSelected(true);
            }else if(mainEmergencyRecordTempFile.getCategory().equals(Category.MEDICAL)){
                medical.setSelected(true);
            }else if(mainEmergencyRecordTempFile.getCategory().equals(Category.HOAX)){
                hoax.setSelected(true);
            }else if(mainEmergencyRecordTempFile.getCategory().equals(Category.CRIME)){
                crime.setSelected(true);
            }
        }

        // Refresh the window
        refreshWindow();

        selectRoute.addActionListener(event -> {
            // Create the temp record
            mainEmergencyRecordTempFile = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord(controller.getCurrentUser());
            alternateEmergencyRecordTempFile = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord(controller.getCurrentUser());
            try{
                mainEmergencyRecordTempFile.setCaller(new Caller(firstnameText.getText(), lastnameText.getText(), phoneText.getText()));
                alternateEmergencyRecordTempFile.setCaller(new Caller(firstnameText.getText(), lastnameText.getText(), phoneText.getText()));
                mainEmergencyRecordTempFile.setLocation(new Location(addressText.getText(),stateText.getText(),cityText.getText()));
                alternateEmergencyRecordTempFile.setLocation(new Location(addressText.getText(),stateText.getText(),cityText.getText()));
                mainEmergencyRecordTempFile.setDescription(descriptionText.getText());
                alternateEmergencyRecordTempFile.setDescription(descriptionText.getText());
                if(fire.isSelected()){
                    mainEmergencyRecordTempFile.setCategory(Category.FIRE);
                    alternateEmergencyRecordTempFile.setCategory(Category.FIRE);
                }else if(crime.isSelected()){
                    mainEmergencyRecordTempFile.setCategory(Category.CRIME);
                    alternateEmergencyRecordTempFile.setCategory(Category.CRIME);
                }else if(medical.isSelected()){
                    mainEmergencyRecordTempFile.setCategory(Category.MEDICAL);
                    alternateEmergencyRecordTempFile.setCategory(Category.MEDICAL);
                }else if(hoax.isSelected()){
                    mainEmergencyRecordTempFile.setCategory(Category.HOAX);
                    alternateEmergencyRecordTempFile.setCategory(Category.HOAX);
                }else if(crash.isSelected()){
                    mainEmergencyRecordTempFile.setCategory(Category.CRIME);
                    alternateEmergencyRecordTempFile.setCategory(Category.CRIME);
                }else{
                    throw new InputMismatchException();
                }
                try {
                    controller.determineNearestResponders(mainEmergencyRecordTempFile, alternateEmergencyRecordTempFile);
                    controller.calculateRoute(mainEmergencyRecordTempFile, false);
                    controller.calculateRoute(alternateEmergencyRecordTempFile, true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(frame, "The address you have entered does not exist.\n Please enter a correct address.");
                    return;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Could not retrieve the API key. Please check the 'maps.private.properties' file." + ASK);
                    return;
                }
            } catch (InputMismatchException mismatch){
                JOptionPane.showMessageDialog(frame, "Every field must be filled.");
                return;
            } catch (Exception exception){
                JOptionPane.showMessageDialog(frame, BURP + "Something broke." + ASK);
                return;
            }

            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to the next window
            routeSelection();
        });
    }

    /**
     * Show screen for the user to select the route from responder to the emergency
     */
    private void routeSelection() {
        // Change the title
        frameTitle.setText("Select the Route");

        // Set previous frame
        previous = "info";

        // Enable back button
        back.setEnabled(true);

        // Create local variables
        JLabel summaryTitle = new JLabel("Case Review");

        JTextArea summaryText = new JTextArea("", 18, 20);
        JTextArea route1Text = new JTextArea("", 18, 20);
        JTextArea route2Text = new JTextArea("", 18, 20);

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane route1DirectionsScroll = new JScrollPane(route1Text);
        JScrollPane route2DirectionsScroll = new JScrollPane(route2Text);

        JButton route1 = new JButton("Select Route 1");
        JButton route2 = new JButton("Select Route 2");

        // Set properties of the fields
        route1Text.setEditable(false);
        route2Text.setEditable(false);
        route1Text.setLineWrap(true);
        route2Text.setLineWrap(true);

        summaryText.setEditable(false);
        summaryText.setLineWrap(true);

        summaryTitle.setFont(new Font(summaryTitle.getFont().getName(), Font.BOLD, 14));

        // Open the web browser
        PlatformImpl.startup(() -> {
            webEngine1.load("file:\\" + mainEmergencyRecordTempFile.getRoute().getRoute().getAbsolutePath());
            webEngine2.load("file:\\" + alternateEmergencyRecordTempFile.getRoute().getRoute().getAbsolutePath());
        });

        // Fill in the route directions
        route1Text.setText(mainEmergencyRecordTempFile.getRoute().getRouteDirections());
        route2Text.setText(alternateEmergencyRecordTempFile.getRoute().getRouteDirections());


        // Set the summary
        summaryText.setText(mainEmergencyRecordTempFile.getParagraphForm() + "\nMain Route" + mainEmergencyRecordTempFile.getResponder().getParagraphForm() + "\nAlternate Route" + alternateEmergencyRecordTempFile.getResponder().getParagraphForm());

        // Add to the frame
        mainframe.setLayout(new GridLayout(2, 2));
        mainframe.add(route1Panel);
        mainframe.add(route2Panel);
        mainframe.add(route1DirectionsScroll);
        mainframe.add(route2DirectionsScroll);

        sidebar.add(summaryTitle);
        sidebar.add(summaryScroll);

        footer.add(route1);
        footer.add(route2);

        // Refresh the window
        refreshWindow();

        route1.addActionListener(event -> {
            // Update the emergency object
            mainEmergencyRecordTempFile.getRoute().setAlternateRouteSelected(false);
            alternateEmergencyRecordTempFile.getRoute().setAlternateRouteSelected(false);

            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to the next window
            summaryView();
        });

        route2.addActionListener(event -> {
            // Update the emergency object
            mainEmergencyRecordTempFile.getRoute().setAlternateRouteSelected(true);
            alternateEmergencyRecordTempFile.getRoute().setAlternateRouteSelected(true);

            // Clear the window
            mainframe.removeAll();
            footer.removeAll();
            sidebar.removeAll();

            // Proceed to the next window
            summaryView();
        });
    }

    /**
     * Show a summary of the emergency record to finalize it or cancel
     */
    private void summaryView() {
        // Change the title
        frameTitle.setText("Summary");

        // Set previous frame
        previous = "route";

        // Enable back button
        back.setEnabled(true);

        // Declare local variables
        JLabel summaryTitle = new JLabel("Case Review");

        JTextArea summaryText = new JTextArea("", 18, 20);

        JTextArea routeText = new JTextArea("", 18, 20);

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane routeScroll = new JScrollPane(routeText);

        JButton closeCase = new JButton("Close Case");

        // Set properties of the fields
        routeText.setEditable(false);
        summaryText.setEditable(false);

        routeText.setLineWrap(true);
        summaryText.setLineWrap(true);
        summaryTitle.setFont(new Font(summaryTitle.getFont().getName(), Font.BOLD, 14));

        // Set the summary
        summaryText.setText(mainEmergencyRecordTempFile.getParagraphForm());
        summaryText.setText(alternateEmergencyRecordTempFile.getParagraphForm());

        // Add components to the screen
        mainframe.setLayout(new GridLayout(2, 1));

        if(!mainEmergencyRecordTempFile.getRoute().getAlternateRouteSelected()) {
            mainframe.add(route1Panel);
            routeText.setText(mainEmergencyRecordTempFile.getRoute().getRouteDirections());
        } else {
            mainframe.add(route2Panel);
            routeText.setText(alternateEmergencyRecordTempFile.getRoute().getRouteDirections());
        }
        mainframe.add(routeScroll);

        sidebar.add(summaryTitle);
        sidebar.add(summaryScroll);

        footer.add(closeCase);

        // Refresh the window
        refreshWindow();

        closeCase.addActionListener(event -> {
            // Confirm finalize
            if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to finalize the record\n This can not be undone.", null, JOptionPane.YES_NO_OPTION) == 0) {

                // Save the emergency object
                try {
                    if(!mainEmergencyRecordTempFile.getRoute().getAlternateRouteSelected())
                        controller.finalizeRecord(mainEmergencyRecordTempFile);
                    else
                        controller.finalizeRecord(alternateEmergencyRecordTempFile);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(frame, BURP + "Something broke." + ASK);
                    return;
                }
                // Reset the record
                mainEmergencyRecordTempFile = null;
                alternateEmergencyRecordTempFile = null;

                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                userActions();
            }
        });
    }

    /**
     * Show the screen to view the emergency records
     */
    private void viewEmergencyRecords(EmergencyRecord initialRecord) {
        // Change the title
        frameTitle.setText("View Emergency Records");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Declare local variables
        JLabel summaryTitle = new JLabel("Case Review");
        JLabel routeTitle = new JLabel("Route Taken");

        JTextArea summaryText = new JTextArea("", 18, 20);
        JTextArea routeText = new JTextArea("", 18, 20);

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane routeScroll = new JScrollPane(routeText);

        JPanel left = new JPanel();
        JPanel right = new JPanel();

        JButton generateReportSingleButton = new JButton("Generate Single Record Stats");
        JButton generateReportRangeButton = new JButton("Generate Stats for a Range");

        // Set properties of the variables
        routeText.setEditable(false);
        summaryText.setEditable(false);

        summaryText.setLineWrap(true);
        routeText.setLineWrap(true);

        summaryTitle.setFont(new Font(summaryTitle.getFont().getName(), Font.BOLD, 14));
        routeTitle.setFont(new Font(routeTitle.getFont().getName(), Font.BOLD, 14));

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        // Populate the variables
        JList<EmergencyRecord> sidebarList = new JList<>(recentRecords);
        if (initialRecord != null) {
            sidebarList.setSelectedValue(initialRecord, true);
            summaryText.setText(initialRecord.getParagraphForm());

            // Want only the selected route shown
            if (initialRecord.getRoute() != null && !initialRecord.getRoute().getAlternateRouteSelected()){
                routeText.setText(initialRecord.getRoute().getRouteDirections());
            } else if (initialRecord.getRoute() != null) {
                routeText.setText(initialRecord.getRoute().getRouteDirections());
            }
        }
        JScrollPane sidebarListScrollPane = new JScrollPane(sidebarList);

        // Add components to the screen
        mainframe.setLayout(new GridLayout(1, 2));
        mainframe.add(left);
        mainframe.add(right);

        left.add(routeTitle);
        left.add(routeScroll);

        right.add(summaryTitle);
        right.add(summaryScroll);

        sidebar.add(sidebarListScrollPane);

        footer.add(generateReportSingleButton);
        footer.add(generateReportRangeButton);

        // Refresh the window
        refreshWindow();

        // Should a record be selected, update the screen
        sidebarList.addListSelectionListener(e -> {
            removeAll();

            viewEmergencyRecords(sidebarList.getSelectedValue());

            frame.repaint();
            frame.revalidate();
        });

        generateReportSingleButton.addActionListener(e -> {
            EmergencyRecord record = sidebarList.getSelectedValue();
            if (record != null) {
                try {
                    saveReportFile(record);
                } catch (IOException | ClassNotFoundException | NullPointerException e1) {
                    JOptionPane.showMessageDialog(
                            frame, BURP + "For some reason I couldn't generate the report." + ASK);
                }
            }
        });

        generateReportRangeButton.addActionListener(e -> {
            try {
                showDateRangeChooser();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(
                        frame, BURP + "For some reason I couldn't generate the report." + ASK);
                e1.printStackTrace();
            }
        });
    }

    /**
     * Allows user to choose a range to generate a report
     */
    void showDateRangeChooser() throws Exception {
        // Change the title
        frameTitle.setText("Select Date Range");

        // Set previous frame
        previous = "view";

        // Enable back button
        back.setEnabled(true);

        // Initilize fields
        JLabel fromDateTitle = new JLabel("From");
        JLabel toDateTitle = new JLabel("To");
        fromDateTitle.setFont(new Font(fromDateTitle.getFont().getName(), Font.BOLD, 14));
        toDateTitle.setFont(new Font(toDateTitle.getFont().getName(), Font.BOLD, 14));

        removeAll();
        mainframe.setLayout(new BoxLayout(mainframe,BoxLayout.Y_AXIS));
        JCalendar fromDatePicker = new JCalendar();
        JCalendar toDatePicker = new JCalendar();
        mainframe.add(fromDateTitle);
        mainframe.add(fromDatePicker);
        mainframe.add(toDateTitle);
        mainframe.add(toDatePicker);
        JButton submitDates = new JButton("Submit");
        footer.add(submitDates);
        // Refresh the window
        refreshWindow();
        submitDates.addActionListener(e -> {
            removeAll();
            reportDateRange = new Instant[]{
                    fromDatePicker.getDate().toInstant(),
                    toDatePicker.getDate().toInstant()
            };
            try {
                saveReportFile();
            } catch (IOException | ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(frame, BURP + "For some reason I couldn't generate the report." + ASK);
                e1.printStackTrace();
            }
        });
    }

    /**
     * Show the dialog to save the report to a file for a range of dates
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void saveReportFile() throws IOException, ClassNotFoundException {
        String suffix = ".xls";
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("xls files",suffix);
        fileChooser.setFileFilter(filter);
        int accepted = fileChooser.showDialog(frame, "Save");
        if (accepted == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(suffix)){
                    file = new File(fileChooser.getSelectedFile() + suffix);
                }
                Files.deleteIfExists(file.toPath());
                controller.generateReport(reportDateRange[0], reportDateRange[1], file.getAbsolutePath());
            }
        }
        // Refresh the window
        refreshWindow();
        viewEmergencyRecords(null);
    }

    /**
     * Show the dialog to save a report for a single emergency record
     * @param record the emergency record to save
     * @throws NullPointerException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void saveReportFile(EmergencyRecord record)
            throws NullPointerException, IOException, ClassNotFoundException {
        if (record != null) {
            String suffix = ".xls";
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xls files", suffix);
            fileChooser.setFileFilter(filter);
            int accepted = fileChooser.showDialog(frame, "Save");
            if (accepted == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    if (!fileChooser.getSelectedFile().getAbsolutePath().endsWith(suffix)) {
                        file = new File(fileChooser.getSelectedFile() + suffix);
                    }
                    Files.deleteIfExists(file.toPath());
                    controller.generateReport(record, file.getAbsolutePath());
                }
            }
            viewEmergencyRecords(null);
        } else {
            throw new NullPointerException();
        }
        removeAll();
        // Refresh the window
        refreshWindow();
        viewEmergencyRecords(null);
    }

    /**
     * Admin only: Show the screen to add and remove users
     */
    private void manageUsers() {
        // Change the title
        frameTitle.setText("Manage Users");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Declare local variables
        JRadioButton users = new JRadioButton("Users");
        JRadioButton admins = new JRadioButton("Administrators");

        ButtonGroup buttonGroup = new ButtonGroup();

        JLabel userActivityLabel = new JLabel("User Activity");
        JLabel listTitle = new JLabel("Select user type");
        JTextArea userActivityText = new JTextArea("Select A User", 18, 20);
        JLabel addUserLabel = new JLabel("Add User");
        JLabel firstnameLabel = new JLabel("First Name:");
        JLabel lastnameLabel = new JLabel("Last Name:");
        JLabel passwordLabel = new JLabel("Enter Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel usernameLabel = new JLabel("Username");

        JTextField firstnameText = new JTextField();
        JTextField lastnameText = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField usernameText = new JTextField();

        JPanel left = new JPanel();
        JScrollPane userActivity = new JScrollPane(userActivityText);
        JPanel right = new JPanel();

        JButton addUser = new JButton("Add User");
        JButton upgrade = new JButton("Make Admin");
        JButton downgrade = new JButton("Revoke Admin");
        JButton deleteUser = new JButton("Delete User");

        JList<Object> sidebarList = new JList<>();
        JScrollPane sidebarListScroll = new JScrollPane(sidebarList);

        // Set properties of the fields
        buttonGroup.add(users);
        buttonGroup.add(admins);

        addUserLabel.setFont(new Font(addUserLabel.getFont().getName(), Font.BOLD, 16));
        userActivityLabel.setFont(new Font(userActivity.getFont().getName(), Font.BOLD, 16));

        users.setSelected(true);
        try {
            sidebarList.setListData(controller.getUsers().toArray());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Trouble reading user dictionary.");
        }

        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height));
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height));
        passwordField.setMaximumSize(new Dimension(200, passwordField.getPreferredSize().height));
        confirmPasswordField.setMaximumSize(new Dimension(200, confirmPasswordField.getPreferredSize().height));
        usernameText.setMaximumSize(new Dimension(200, confirmPasswordField.getPreferredSize().height));

        userActivityLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addUserLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        passwordLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        confirmPasswordLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        confirmPasswordField.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        usernameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);

        userActivityText.setEditable(false);

        // Add components to the screen
        mainframe.setLayout(new GridLayout(1, 2));
        mainframe.add(left);
        mainframe.add(right);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JLabel("  "));
        left.add(addUserLabel);
        left.add(new JLabel("  "));
        left.add(firstnameLabel);
        left.add(new JLabel("  "));
        left.add(firstnameText);
        left.add(new JLabel("  "));
        left.add(lastnameLabel);
        left.add(new JLabel("  "));
        left.add(lastnameText);
        left.add(new JLabel("  "));
        left.add(passwordLabel);
        left.add(new JLabel("  "));
        left.add(passwordField);
        left.add(new JLabel("  "));
        left.add(confirmPasswordLabel);
        left.add(new JLabel("  "));
        left.add(confirmPasswordField);
        left.add(usernameLabel);
        left.add(new JLabel("  "));
        left.add(usernameText);

        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new JLabel("  "));
        right.add(userActivityLabel);
        right.add(userActivity);

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(listTitle);
        sidebar.add(users);
        sidebar.add(admins);
        sidebar.add(sidebarListScroll);

        footer.add(addUser);
        footer.add(upgrade);
        footer.add(downgrade);
        footer.add(deleteUser);

        // Refresh the window
        refreshWindow();

        sidebarList.addListSelectionListener(event -> {
            // Get the selected user
            EMSUser user = (EMSUser) sidebarList.getSelectedValue();

            // Populate the panel with the data
            if (user != null) {
                userActivityText.setText(user.getParagraphForm());
            }
            refreshWindow();
        });

        users.addActionListener(event -> {
            // Populate the list with users
            try {
                sidebarList.setListData(controller.getUsers().toArray());
                sidebarList.setSelectedIndex(0);
            } catch (IOException | ClassNotFoundException e1) {
                sidebarList.setListData(new String[]{"No users found."});
            }
            refreshWindow();
        });

        admins.addActionListener(event -> {
            // Populate the list with admins
            try {
                sidebarList.setListData(controller.getAdminUsers().toArray());
                sidebarList.setSelectedIndex(0);
            } catch (IOException | ClassNotFoundException e1) {
                sidebarList.setListData(new String[]{"No admin users found."});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            refreshWindow();
        });

        deleteUser.addActionListener(event -> {
                // Remove the user from the database
            String username = ((EMSUser) sidebarList.getSelectedValue()).getUsername();
            if (JOptionPane.showConfirmDialog(frame, "Sure you want to delete " + username + "?", null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (controller.getCurrentUser().isAdmin()) {
                        ((EMSAdminController) controller).removeUser(username);
                        sidebarList.setListData(admins.isSelected() ?
                                controller.getAdminUsers().toArray() :
                                controller.getUsers().toArray());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, BURP + "For some reason I couldn't read the users." + ASK);
                    ex.printStackTrace();
                }
                refreshWindow();
            }
        });

        upgrade.addActionListener(event -> {
            // Upgrade the user to admin
            String username = ((EMSUser) sidebarList.getSelectedValue()).getUsername();
            try {
                if (controller.getCurrentUser().isAdmin()) {
                    ((EMSAdminController) controller).setUserAdmin(username, true);
                    sidebarList.setListData(admins.isSelected() ?
                            controller.getAdminUsers().toArray() :
                            controller.getUsers().toArray());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, BURP + "For some reason I couldn't read the users." + ASK);
                ex.printStackTrace();
            }
            refreshWindow();
        });

        downgrade.addActionListener(event -> {
            // Downgrade the user from admin
            String username = ((EMSUser) sidebarList.getSelectedValue()).getUsername();
            try {
                if (controller.getCurrentUser().isAdmin()) {
                    ((EMSAdminController) controller).setUserAdmin(username, false);
                    sidebarList.setListData(admins.isSelected() ?
                            controller.getAdminUsers().toArray() :
                            controller.getUsers().toArray());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, BURP + "For some reason I couldn't read the users." + ASK);
            }
            refreshWindow();
        });

        addUser.addActionListener(event -> {
            // Add the user to the database
            try {
                ((EMSAdminController) controller).addUser(
                        firstnameText.getText(),
                        lastnameText.getText(),
                        usernameText.getText(),
                        String.valueOf(passwordField.getPassword()));
                firstnameText.setText(null);
                lastnameText.setText(null);
                usernameText.setText(null);
                passwordField.setText(null);
                confirmPasswordField.setText(null);
            } catch (IOException | ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(frame, BURP + "For some reason I couldn't add the user." + ASK);
            }
            try {
                sidebarList.setListData(controller.getUsers().toArray());
            } catch (IOException | ClassNotFoundException e1) {
                sidebarList.setListData(new String[]{"Couldn't show users."});
            }
            refreshWindow();
        });
    }

    /**
     * Admin only: Show the screen to modify emergency records
     */
    private void manageRecords() {
        // Change the title
        frameTitle.setText("Modify Record");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Change the title
        frameTitle.setText("Manage Records");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Create label, textField, and radioButton local variables
        JRadioButton newestRecords = new JRadioButton("Recent Records");
        JRadioButton allRecords = new JRadioButton("All Records");

        ButtonGroup recordListType = new ButtonGroup();

        JLabel listTitle = new JLabel("Select Record Filter");
        JLabel callerTitle = new JLabel("Caller Information");
        JLabel descriptionTitle = new JLabel("Description of the Emergency");
        JLabel categorizeTitle = new JLabel("Categorize Emergency");
        JLabel locationTitle = new JLabel("Location of the Emergency");
        JLabel firstnameLabel = new JLabel("First Name:");
        JLabel lastnameLabel = new JLabel("Last Name:");
        JLabel phoneLabel = new JLabel("Phone Number:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel stateLabel = new JLabel("State:");
        JLabel cityLabel = new JLabel("City Code:");

        JTextField firstnameText = new JTextField();
        JTextField lastnameText = new JTextField();
        JTextField phoneText = new JTextField();
        JTextField addressText = new JTextField();
        JTextField stateText = new JTextField();
        JTextField cityText = new JTextField();

        JTextArea descriptionText = new JTextArea("", 18, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionText);

        JPanel left = new JPanel();
        JPanel right = new JPanel();

        JRadioButton fire = new JRadioButton("Fire");
        JRadioButton crime = new JRadioButton("Crime");
        JRadioButton medical = new JRadioButton("Medical");
        JRadioButton hoax = new JRadioButton("Hoax");
        JRadioButton crash = new JRadioButton("Car Crash");
        ButtonGroup categories = new ButtonGroup();

        JButton saveRecord = new JButton("Save Record");
        JButton deleteRecord = new JButton("Delete Record");
        JButton backupData = new JButton("Backup Data");
        JButton restoreData = new JButton("Restore Data");


        DefaultListModel<EmergencyRecord> listModel = new DefaultListModel<>();
        for (EmergencyRecord recentRecord : recentRecords) {
            listModel.addElement(recentRecord);
        }
        JList<EmergencyRecord> sidebarList = new JList<>(listModel);
        JScrollPane sidebarListScroll = new JScrollPane(sidebarList);

        // Add radiobuttons to the group
        categories.add(fire);
        categories.add(crime);
        categories.add(medical);
        categories.add(hoax);
        categories.add(crash);

        recordListType.add(newestRecords);
        recordListType.add(allRecords);

        // Set properties of the fields
        newestRecords.setSelected(true);

        callerTitle.setFont(new Font(callerTitle.getFont().getName(), Font.BOLD, 14));
        descriptionTitle.setFont(new Font(descriptionTitle.getFont().getName(), Font.BOLD, 14));
        categorizeTitle.setFont(new Font(categorizeTitle.getFont().getName(), Font.BOLD, 14));
        locationTitle.setFont(new Font(locationTitle.getFont().getName(), Font.BOLD, 14));

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        descriptionText.setLineWrap(true);

        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height));
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height));
        phoneText.setMaximumSize(new Dimension(200, phoneText.getPreferredSize().height));
        addressText.setMaximumSize(new Dimension(200, addressText.getPreferredSize().height));
        stateText.setMaximumSize(new Dimension(200, stateText.getPreferredSize().height));
        cityText.setMaximumSize(new Dimension(200, cityText.getPreferredSize().height));

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        callerTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        categorizeTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        locationTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        cityLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        cityText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        fire.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crime.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        medical.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        hoax.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crash.setAlignmentX(JFrame.LEFT_ALIGNMENT);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        mainframe.setLayout(new GridLayout(1, 2));

        // Add components to the window
        mainframe.add(left);
        mainframe.add(right);

        // Add components to left part of mainframe
        left.add(new JLabel("  "));
        left.add(callerTitle);
        left.add(new JLabel("  "));
        left.add(firstnameLabel);
        left.add(new JLabel("  "));
        left.add(firstnameText);
        left.add(new JLabel("  "));
        left.add(lastnameLabel);
        left.add(new JLabel("  "));
        left.add(lastnameText);
        left.add(new JLabel("  "));
        left.add(phoneLabel);
        left.add(new JLabel("  "));
        left.add(phoneText);
        left.add(new JLabel("  "));
        left.add(locationTitle);
        left.add(new JLabel("  "));
        left.add(addressLabel);
        left.add(new JLabel("  "));
        left.add(addressText);
        left.add(new JLabel("  "));
        left.add(cityLabel);
        left.add(new JLabel("  "));
        left.add(cityText);
        left.add(new JLabel("  "));
        left.add(stateLabel);
        left.add(new JLabel("  "));
        left.add(stateText);

        // Add components to right part of mainframe
        left.add(new JLabel("  "));
        right.add(categorizeTitle);
        right.add(new JLabel("  "));
        right.add(fire);
        right.add(new JLabel("  "));
        right.add(crime);
        right.add(new JLabel("  "));
        right.add(medical);
        right.add(new JLabel("  "));
        right.add(crash);
        right.add(new JLabel("  "));
        right.add(hoax);
        right.add(descriptionTitle);
        right.add(new JLabel("  "));
        right.add(descriptionScroll);

        // Add components to the sidebar
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(listTitle);
        sidebar.add(newestRecords);
        sidebar.add(allRecords);
        sidebar.add(sidebarListScroll);

        // Add components to footer
        footer.add(saveRecord);
        footer.add(deleteRecord);
        footer.add(backupData);
        footer.add(restoreData);

        // Populate the field with the record info
        sidebarList.addListSelectionListener(e ->{
            EmergencyRecord record = sidebarList.getSelectedValue();
            if (record != null) {
                Caller caller = record.getCaller();
                firstnameText.setText(caller.getFirstName());
                lastnameText.setText(caller.getLastName());
                phoneText.setText(caller.getPhone());
                Location location = record.getLocation();
                addressText.setText(location.getAddress());
                stateText.setText(location.getState());
                cityText.setText(location.getCity());
                switch (record.getCategory()) {
                    case FIRE:
                        // categories.setSelected(fire.getModel(), true);
                        fire.setSelected(true);
                        break;
                    case CRIME:
                        crime.setSelected(true);
                        break;
                    case MEDICAL:
                        medical.setSelected(true);
                        break;
                    case HOAX:
                        hoax.setSelected(true);
                        break;
                    case CAR_CRASH:
                        crash.setSelected(true);
                        break;
                    default:
                        categories.setSelected(null, true);
                }
                String description = record.getDescription();
                descriptionText.setText(description);
            }
            refreshWindow();
        });

        newestRecords.addActionListener(event -> {
            // Populate the list with users
            try {
                sidebarList.setListData(controller.getRecentRecords());
                sidebarList.setSelectedIndex(0);
            } catch (IOException | ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(frame, BURP + "Had trouble getting the users, actually." + ASK);
            }
            refreshWindow();
        });

        allRecords.addActionListener(event -> {
            // Populate the list with admins
            try {
                ArrayList<EmergencyRecord> records = controller.getRecords();
                sidebarList.setListData(records.toArray(new EmergencyRecord[records.size()]));
                sidebarList.setSelectedIndex(0);
            } catch (IOException | ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(frame, BURP + "Had trouble getting the users, actually." + ASK);
            }
            refreshWindow();
        });

        saveRecord.addActionListener(event -> {
            // Save the changes
            EmergencyRecord record = sidebarList.getSelectedValue();
            if (record != null) {
                record.setCaller(new Caller(
                        firstnameText.getText(),
                        lastnameText.getText(),
                        phoneText.getText()
                ));
                if (fire.isSelected()) {
                    record.setCategory(Category.FIRE);
                } else if (crime.isSelected()) {
                    record.setCategory(Category.CRIME);
                } else if (medical.isSelected()) {
                    record.setCategory(Category.MEDICAL);
                } else if (hoax.isSelected()) {
                    record.setCategory(Category.HOAX);
                } else if (crash.isSelected()) {
                    record.setCategory(Category.CAR_CRASH);
                }
                Location location = new Location(
                        addressText.getText(),
                        stateText.getText(),
                        cityText.getText()
                );
                try {
                    record.setRoute(new Route(
                            record.getResponder().getAddress(),
                            location.getAddress(),
                            record.getRoute().getAlternateRouteSelected()
                    ));
                } catch(ObjectNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, BURP + "The server is unavailable at the moment. Could not fetch route.\n" +
                            "Check your internet connection and try again later\n" + ASK);
                } catch(IOException e) {
                    JOptionPane.showMessageDialog(frame, "Could not retrieve the API key. Please check the 'maps.private.properties' file." + ASK);
                }
                record.setDescription(descriptionText.getText());
                record.modify();
                try {
                    record.modify();
                    controller.finalizeRecord(record);
                } catch (IOException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, BURP + "Had trouble saving the record, actually." + ASK);
                }
            } else {
                JOptionPane.showMessageDialog(frame, BURP + "Had trouble saving the record, actually." + ASK);
            }
            refreshWindow();
        });

        deleteRecord.addActionListener(event -> {
            // Save the changes
            EmergencyRecord record = sidebarList.getSelectedValue();
            if (record != null) {
                try {
                    ((EMSAdminController)controller).removeRecord(record);
                } catch (IOException | NullPointerException e) {
                    JOptionPane.showMessageDialog(frame, BURP + "Had trouble deleting the record, actually." + ASK);
                }
            }
            listModel.removeElement(record);
            refreshWindow();
        });

        backupData.addActionListener(event -> {
            // Open the file
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showSaveDialog(frame);
            if (response == JFileChooser.APPROVE_OPTION) {
                // Save database to the file
                try {
                    controller.backupData(fileChooser.getSelectedFile());
                } catch (IOException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, BURP + "Had trouble backing up the database." + ASK);
                }
            }
        });

        restoreData.addActionListener(event -> {
            // Open the file
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(frame);
            if (response == JFileChooser.APPROVE_OPTION) {
                // Load database from file
                try {
                    controller.restoreData(fileChooser.getSelectedFile());
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    JOptionPane.showMessageDialog(frame, BURP + "Had trouble backing up the database." + ASK);
                }
            }
        });
        refreshWindow();
    }

    void refreshWindow() {
        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    private void removeAll() {
        mainframe.removeAll();
        sidebar.removeAll();
        footer.removeAll();
    }

}