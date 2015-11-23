package com.baconfiesta.ems.view;

import com.baconfiesta.ems.controller.EMSAdminController;
import com.baconfiesta.ems.controller.EMSController;
import com.baconfiesta.ems.controller.Authenticator;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The main user interface window of the EMS system.
 * @author team_bacon_fiesta
 */
public class EMSInterface implements EMSInterfaceConstants {
    private EMSController controller;
    private JFrame frame;
    private JPanel header;
    private JPanel mainframe;
    private JPanel footer;
    private JPanel sidebar;
    private JList sidebarList;

    private JLabel frameTitle;

    private JButton back;
    private JButton logout;
    private JButton createCase;
    private JButton viewRecords;
    private JButton generateReport;
    private JButton manageUsers;
    private JButton manageData;
    private JButton manageRecords;

    private WebView browser1;
    private WebEngine webEngine1;
    private Group root1;
    private WebView browser2;
    private WebEngine webEngine2;
    private Group root2;

    private JFXPanel route1Panel;
    private JFXPanel route2Panel;

    /*
    * Holds "user" if previous window was user options
    * holds "info" if it was enter info
    * holds "route" if it was route selection
    */
    private String previous;

    private EmergencyRecord tempFile;
    private EmergencyRecord[] recentRecords;

  /**
   * The constructor for EMSInterface
   */
    public EMSInterface(){
        // Initialize the fields
        frame = new JFrame();
        header = new JPanel();
        mainframe = new JPanel();
        footer = new JPanel();
        sidebar = new JPanel();
        sidebarList = new JList(new String[]{"a case record.........................","a case record.........................","a case record.........................","a case record.........................","a case record........................."});
        frameTitle = new JLabel();
        back = new JButton("Back");
        logout = new JButton("Logout");
        createCase = new JButton("Create a New Emergency Case");
        viewRecords = new JButton("View Emergency Records");
        generateReport = new JButton("Generate Reports");
        manageUsers = new JButton("Manage Users");
        manageData = new JButton("Manage Data");
        manageRecords = new JButton("Manage Records");

        // Set panel properties
        header.setLayout(new BorderLayout());
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Set panel borders
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainframe.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sidebar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        footer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Set title
        frameTitle.setFont(new Font(frameTitle.getFont().getName(),Font.BOLD, 20));
        frameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(frameTitle, BorderLayout.CENTER);

        // Add components to frame
        frame.add(header, BorderLayout.NORTH);
        frame.add(mainframe, BorderLayout.CENTER);
        frame.add(sidebar, BorderLayout.EAST);
        frame.add(footer, BorderLayout.SOUTH);

        // Set the login screen
        logIn();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        route1Panel = new JFXPanel();
        route2Panel = new JFXPanel();

        // Open the web browser
        PlatformImpl.setImplicitExit(false);
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
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
            }
        });

        // Set logout actionListener
        logout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Confirm user logout
                if(JOptionPane.showConfirmDialog(frame,"Are you sure you want to logout?\nAny unsaved data will be lost.",null,JOptionPane.YES_NO_OPTION) == 0){
                    // Clear the window
                    mainframe.removeAll();
                    footer.removeAll();
                    sidebar.removeAll();
                    header.remove(back);
                    header.remove(logout);

                    // Go back to login
                    logIn();
                }
            }
        });

        // Set createCase actionListener
        createCase.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to next window
                enterInfo();
            }
        });

        // Set viewRecords actionListener
        viewRecords.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to next window
                displayRecords();
            }
        });

        // Set generateReport actionListener
        generateReport.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to next window
                generateStats();
            }
        });

        // Set manageUsers actionListener
        manageUsers.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to next window
                manageUsers();
            }
        });

        // Set manageData actionListener
        manageData.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                manageData();
            }
        });

        // Set manageRecords actionListener
        manageRecords.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                modifyRecords();
            }
        });

        // Set back button actionListener
        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Find the next window
                if(previous.equals("user")){
                    back.setEnabled(false);
                    if (controller.getCurrentUser().isAdmin()) {
                        adminAcions();
                    } else {
                        userActions();
                    }
                } else if (previous.equals("info")){
                    enterInfo();
                } else if (previous.equals("route")){
                    routeSelection();
                }
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
        title.setFont(new Font(title.getFont().getName(),Font.BOLD, 20));
        usernameText.setMaximumSize(new Dimension(150, usernameText.getPreferredSize().height) );
        passwordText.setMaximumSize(new Dimension(150, usernameText.getPreferredSize().height) );
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
        frame.revalidate();
        frame.repaint();

        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){

                EMSUser user;
                String username = usernameText.getText();
                char[] password = passwordText.getPassword();

                System.out.printf("Attempting to login user: '%s'\n", username); // TODO: remove

                // Attempt to login as a user using specified info
                try {
                    Authenticator.init();
                    user = Authenticator.authenticate(username, password);
                    // blank out password for security
                    for (int i = 0; i < password.length; i++) {
                        password[i] = ' ';
                    }
                } catch (NullPointerException e) {
                    JOptionPane.showMessageDialog(frame, "Sorry, but we could not log you in at this time. Try again " +
                            "in 15 minutes.");
                    return;
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, "Couldn't find user.");
                    return;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Trouble reading user dictionary.");
                    return;
                }

                // If successful then clear window
                mainframe.removeAll();
                if (user.isAdmin()) {
                    // If an administrator user then use adminActions()
                    try {
                        controller = new EMSAdminController(user, null);
                    } catch (Exception e) {
                        userActions();
                        return;
                    }
                    adminAcions();
                } else {
                    // If a normal user then use userActions()
                    userActions();
                }
            }
        });
    }

    /**
     * Show screen for user actions
     */
    private void userActions() {
        // Change the title
        frameTitle.setText("Select an Action");

        // Repopulate the list with the recent records
        sidebarList.setListData(new String[]{"a case record.........................","a case record.........................","a case record.........................","a case record.........................","a case record........................."});
        sidebar.add(sidebarList);

        // Disable the back button
        back.setEnabled(false);

        // Add components to the header
        header.add(back, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // Add components to the footer
        footer.add(createCase);
        footer.add(viewRecords);
        footer.add(generateReport);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Show screen for admin actions
     */
    private void adminAcions() {
        footer.add(manageUsers);
        footer.add(manageData);
        footer.add(manageRecords);
        userActions();
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
        JLabel zipLabel = new JLabel("Zip Code:");

        JTextField firstnameText = new JTextField();
        JTextField lastnameText = new JTextField();
        JTextField phoneText = new JTextField();
        JTextField addressText = new JTextField();
        JTextField stateText = new JTextField();
        JTextField zipText = new JTextField();

        JTextArea descriptionText = new JTextArea("",18,20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionText);

        JPanel left = new JPanel();
        JPanel middle = new JPanel();
        JPanel right = new JPanel();

        JRadioButton fire = new JRadioButton("Fire");
        JRadioButton security = new JRadioButton("Security");
        JRadioButton health = new JRadioButton("Health");
        JRadioButton hoax = new JRadioButton("Hoax");
        JRadioButton crash = new JRadioButton("Car Crash");
        ButtonGroup categories = new ButtonGroup();

        JButton selectRoute = new JButton("Select Route");

        // Add radiobuttons to the group
        categories.add(fire);
        categories.add(security);
        categories.add(health);
        categories.add(hoax);
        categories.add(crash);

        // Set properties of the fields
        callerTitle.setFont(new Font(callerTitle.getFont().getName(),Font.BOLD, 14));
        descriptionTitle.setFont(new Font(descriptionTitle.getFont().getName(),Font.BOLD, 14));
        categorizeTitle.setFont(new Font(categorizeTitle.getFont().getName(),Font.BOLD, 14));
        locationTitle.setFont(new Font(locationTitle.getFont().getName(),Font.BOLD, 14));

        descriptionTitle.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        descriptionText.setLineWrap(true);

        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height) );
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height) );
        phoneText.setMaximumSize(new Dimension(200, phoneText.getPreferredSize().height) );
        addressText.setMaximumSize(new Dimension(200, addressText.getPreferredSize().height) );
        stateText.setMaximumSize(new Dimension(200, stateText.getPreferredSize().height) );
        zipText.setMaximumSize(new Dimension(200, zipText.getPreferredSize().height) );

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        callerTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        categorizeTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        locationTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        zipLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        zipText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        fire.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        security.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        health.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        hoax.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crash.setAlignmentX(JFrame.LEFT_ALIGNMENT);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        mainframe.setLayout(new GridLayout(1,3));

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
        middle.add(stateLabel);
        middle.add(new JLabel("  "));
        middle.add(stateText);
        middle.add(new JLabel("  "));
        middle.add(zipLabel);
        middle.add(new JLabel("  "));
        middle.add(zipText);

        // Add components to right part of mainframe
        right.add(categorizeTitle);
        right.add(new JLabel("  "));
        right.add(fire);
        right.add(new JLabel("  "));
        right.add(security);
        right.add(new JLabel("  "));
        right.add(health);
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

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        selectRoute.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                routeSelection();
            }
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

        JTextArea summaryText = new JTextArea("",18,20);
        JTextArea route1Text = new JTextArea("",18,20);
        JTextArea route2Text = new JTextArea("",18,20);

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane route1DirectionsScroll = new JScrollPane(route1Text);
        JScrollPane route2DirectionsScroll = new JScrollPane(route2Text);

        JButton route1 = new JButton("Select Route 1");
        JButton route2 = new JButton("Select Route 2");

        // Set properties of the fields
        route1Text.setEditable(false);
        route2Text.setEditable(false);
        summaryText.setEditable(false);

        summaryTitle.setFont(new Font(summaryTitle.getFont().getName(),Font.BOLD, 14));

        // Open the web browser
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                webEngine1.load("file:///C:/Users/cchas/Downloads/Maps.html");
                webEngine2.load("file:///C:/Users/cchas/Downloads/Maps.html");
            }
        });


        //
        // Fill in the route directions
        //


        //
        // Fill in the summary
        //

        // Add to the frame
        mainframe.setLayout(new GridLayout(2,2));
        mainframe.add(route1Panel);
        mainframe.add(route2Panel);
        mainframe.add(route1DirectionsScroll);
        mainframe.add(route2DirectionsScroll);

        sidebar.add(summaryTitle);
        sidebar.add(summaryScroll);

        footer.add(route1);
        footer.add(route2);

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        route1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Update the emergency object

                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                summaryView();
            }
        });

        route2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Update the emergency object

                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                summaryView();
            }
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

        JTextArea summaryText = new JTextArea("",18,20);

        JEditorPane routePane = new JEditorPane();

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane routeScroll = new JScrollPane(routePane);

        JButton closecase = new JButton("Close Case");

        // Set properties of the fields
        routePane.setEditable(false);
        summaryText.setEditable(false);

        summaryTitle.setFont(new Font(summaryTitle.getFont().getName(),Font.BOLD, 14));

        // Open the web pages
        //
        // GET THE URL TO SHOW ROUTE
        //
        // Open the web browser
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                webEngine1.load("file:///C:/Users/cchas/Downloads/Maps.html");
            }
        });

        // Add components to the screen
        mainframe.setLayout(new GridLayout(2,1));
        mainframe.add(route1Panel);
        mainframe.add(routeScroll);

        sidebar.add(summaryTitle);
        sidebar.add(summaryScroll);

        footer.add(closecase);

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        closecase.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Update the emergency object

                // Save the emergency object

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
     * Show the screen to generate some statistics about the emergency records
     */
    private void generateStats() {
        // Change the title
        frameTitle.setText("Generate stats");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Show the screen to view the emergency records
     */
    private void displayRecords() {
        // Change the title
        frameTitle.setText("Summary");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Declare local variables
        JLabel summaryTitle = new JLabel("Case Review");

        JTextArea summaryText = new JTextArea("",18,20);

        JEditorPane routePane = new JEditorPane();

        JScrollPane summaryScroll = new JScrollPane(summaryText);
        JScrollPane routeScroll = new JScrollPane(routePane);

        // Set properties of the fields
        routePane.setEditable(false);
        summaryText.setEditable(false);

        //
        // Open the web pages
        //
        // GET THE URL TO SHOW ROUTE
        //
        try {
            URL route1URL = new URL("http://www.google.com");
            routePane.setPage(route1URL);
        } catch (MalformedURLException e) {
            System.out.println("Can't open the url");
        } catch (java.io.IOException e){
            System.out.println("Can't open the url");
        }

        // Add components to the screen
        mainframe.setLayout(new GridLayout(1,2));
        mainframe.add(routeScroll);
        mainframe.add(summaryScroll);

        sidebar.add(sidebarList);

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        // Should a record be selected, update the screen
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
        JTextArea userActivityText = new JTextArea("Select A User",18,20);
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
        JButton selectUser = new JButton(" Select User ");

        // Set properties of the fields
        buttonGroup.add(users);
        buttonGroup.add(admins);

        addUserLabel.setFont(new Font(addUserLabel.getFont().getName(),Font.BOLD, 16));
        userActivityLabel.setFont(new Font(userActivity.getFont().getName(),Font.BOLD, 16));

        users.setSelected(true);
        try {
            sidebarList.setListData(controller.getUsers().stream().map(EMSUser::getUsername).toArray());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Trouble reading user dictionary.");
        }

        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height) );
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height) );
        passwordField.setMaximumSize(new Dimension(200, passwordField.getPreferredSize().height) );
        confirmPasswordField.setMaximumSize(new Dimension(200, confirmPasswordField.getPreferredSize().height) );
        usernameText.setMaximumSize(new Dimension(200, confirmPasswordField.getPreferredSize().height) );

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
        mainframe.setLayout(new GridLayout(1,2));
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
        right.add(userActivityLabel);
        right.add(userActivity);

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(listTitle);
        sidebar.add(users);
        sidebar.add(admins);
        sidebar.add(sidebarList);
        sidebar.add(selectUser);

        footer.add(addUser);
        footer.add(upgrade);
        footer.add(downgrade);
        footer.add(deleteUser);

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        selectUser.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Get the selected user
                //EMSUser tmp = (EMSUser) sidebarList.getSelectedValue();
                String info = "Populate the info";

                // Populate the panel with the data
                userActivityText.setText(info);

                frame.revalidate();
                frame.repaint();
            }
        });

        users.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Populate the list with users
                try {
                    sidebarList.setListData(controller.getUsers().stream().map(EMSUser::getUsername).toArray());
                } catch (IOException | ClassNotFoundException e1) {
                    sidebarList.setListData(new String[]{"No users found."});
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        admins.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Populate the list with admins
                try {
                    sidebarList.setListData(controller.getAdminUsers().stream().map(EMSUser::getUsername).toArray());
                } catch (IOException e1) {
                    sidebarList.setListData(new String[]{"No admin users found."});
                } catch (ClassNotFoundException e1) {
                    sidebarList.setListData(new String[]{"No admin users found."});
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        deleteUser.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Remove the user from the database
                String username = (String) sidebarList.getSelectedValue();
                try {
                    if (controller.getCurrentUser().isAdmin()) {
                        ((EMSAdminController)controller).removeUser(username);
                        sidebarList.setListData(admins.isSelected() ?
                                controller.getAdminUsers().stream().map(EMSUser::getUsername).toArray() :
                                controller.getUsers().stream().map(EMSUser::getUsername).toArray());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, manageUsersErrorMessage);
                    ex.printStackTrace();
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        upgrade.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Upgrade the user to admin
                String username = (String) sidebarList.getSelectedValue();
                try {
                    if (controller.getCurrentUser().isAdmin()) {
                        ((EMSAdminController)controller).setUserAdmin(username, true);
                        sidebarList.setListData(admins.isSelected() ?
                                controller.getAdminUsers().stream().map(EMSUser::getUsername).toArray() :
                                controller.getUsers().stream().map(EMSUser::getUsername).toArray());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, manageUsersErrorMessage);
                    ex.printStackTrace();
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        downgrade.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Downgrade the user from admin
                String username = (String) sidebarList.getSelectedValue();
                try {
                    if (controller.getCurrentUser().isAdmin()) {
                        ((EMSAdminController)controller).setUserAdmin(username, false);
                        sidebarList.setListData(admins.isSelected() ?
                                controller.getAdminUsers().stream().map(EMSUser::getUsername).toArray() :
                                controller.getUsers().stream().map(EMSUser::getUsername).toArray());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, manageUsersErrorMessage);
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        addUser.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Add the user to the database
                try {
                    ((EMSAdminController)controller).addUser(
                            firstnameText.getText(),
                            lastnameText.getText(),
                            usernameText.getText(),
                            String.valueOf(passwordField.getPassword()));
                } catch (IOException e1) {
                    System.out.println("User not added.");
                } catch (ClassNotFoundException e1) {
                    System.out.println("User not added.");
                }
                System.out.println();
                try {
                    sidebarList.setListData(controller.getUsers().stream().map(EMSUser::getUsername).toArray());
                } catch (IOException | ClassNotFoundException e1) {
                    sidebarList.setListData(new String []{"Couldn't show users."});
                }
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    /**
     * Admin only: Show the screen to backup and restore data
     */
    private void manageData() {
        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();


    }

    /**
     * Admin only: Show the screen to modify emergency records
     */
    private void modifyRecords() {
        // Change the title
        frameTitle.setText("Modify Record");

        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

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
        JLabel zipLabel = new JLabel("Zip Code:");

        JTextField firstnameText = new JTextField();
        JTextField lastnameText = new JTextField();
        JTextField phoneText = new JTextField();
        JTextField addressText = new JTextField();
        JTextField stateText = new JTextField();
        JTextField zipText = new JTextField();

        JTextArea descriptionText = new JTextArea("",18,20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionText);

        JPanel left = new JPanel();
        JPanel right = new JPanel();

        JRadioButton fire = new JRadioButton("Fire");
        JRadioButton security = new JRadioButton("Security");
        JRadioButton health = new JRadioButton("Health");
        JRadioButton hoax = new JRadioButton("Hoax");
        JRadioButton crash = new JRadioButton("Car Crash");
        ButtonGroup categories = new ButtonGroup();

        JButton saveRecord = new JButton("Save Record");
        JButton deleteRecord = new JButton("Delete Record");

        // Add radiobuttons to the group
        categories.add(fire);
        categories.add(security);
        categories.add(health);
        categories.add(hoax);
        categories.add(crash);

        // Set properties of the fields
        callerTitle.setFont(new Font(callerTitle.getFont().getName(),Font.BOLD, 14));
        descriptionTitle.setFont(new Font(descriptionTitle.getFont().getName(),Font.BOLD, 14));
        categorizeTitle.setFont(new Font(categorizeTitle.getFont().getName(),Font.BOLD, 14));
        locationTitle.setFont(new Font(locationTitle.getFont().getName(),Font.BOLD, 14));

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        descriptionText.setLineWrap(true);

        firstnameText.setMaximumSize(new Dimension(200, firstnameText.getPreferredSize().height) );
        lastnameText.setMaximumSize(new Dimension(200, lastnameText.getPreferredSize().height) );
        phoneText.setMaximumSize(new Dimension(200, phoneText.getPreferredSize().height) );
        addressText.setMaximumSize(new Dimension(200, addressText.getPreferredSize().height) );
        stateText.setMaximumSize(new Dimension(200, stateText.getPreferredSize().height) );
        zipText.setMaximumSize(new Dimension(200, zipText.getPreferredSize().height) );

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        callerTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        categorizeTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        locationTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        zipLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        firstnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        lastnameText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        phoneText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        addressText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        stateText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        zipText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        fire.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        security.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        health.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        hoax.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        crash.setAlignmentX(JFrame.LEFT_ALIGNMENT);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        mainframe.setLayout(new GridLayout(1,2));

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
        left.add(stateLabel);
        left.add(new JLabel("  "));
        left.add(stateText);
        left.add(new JLabel("  "));
        left.add(zipLabel);
        left.add(new JLabel("  "));
        left.add(zipText);

        // Add components to right part of mainframe
        left.add(new JLabel("  "));
        right.add(categorizeTitle);
        right.add(new JLabel("  "));
        right.add(fire);
        right.add(new JLabel("  "));
        right.add(security);
        right.add(new JLabel("  "));
        right.add(health);
        right.add(new JLabel("  "));
        right.add(crash);
        right.add(new JLabel("  "));
        right.add(hoax);
        right.add(descriptionTitle);
        right.add(new JLabel("  "));
        right.add(descriptionScroll);

        sidebarList.setListData(new String[]{"a case record.........................","a case record.........................","a case record.........................","a case record.........................","a case record........................."});
        sidebar.add(sidebarList);

        // Add components to footer
        footer.add(saveRecord);
        footer.add(deleteRecord);

        // Populate the field with the record info

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        saveRecord.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Save the changes

            }
        });

        deleteRecord.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Save the changes

            }
        });
    }

}