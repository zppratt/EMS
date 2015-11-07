package com.baconfiesta.ems.view;

import com.baconfiesta.ems.controller.EMSController;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The main user interface window of the EMS system.
 * @author team_bacon_fiesta
 */
public class EMSInterface {

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
    private JButton viewActivity;

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
        viewActivity = new JButton("View User Activity");

        // Set panel properties
        header.setBackground(Color.WHITE);
        header.setLayout(new BorderLayout());
        mainframe.setBackground(Color.WHITE);
        footer.setBackground(Color.WHITE);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        sidebar.setBackground(Color.WHITE);
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
        frame.setSize(1280,1024);
        frame.setVisible(true);

        // Set logout actionListener
        logout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
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

        // Set manageData actionListener
        viewActivity.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Clear the window
                mainframe.removeAll();
                footer.removeAll();
                sidebar.removeAll();

                // Proceed to the next window
                viewUserActivity();
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
                    userActions();
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
        mainframe.add(usernameLabel);
        mainframe.add(usernameText);
        mainframe.add(passwordLabel);
        mainframe.add(passwordText);
        mainframe.add(loginButton);

        // Refresh the window
        frame.revalidate();
        frame.repaint();

        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // Check if the credentials are successful
//                EMSUser user = controller.authenticateUser(usernameText.getText(), passwordText.getPassword().toString());
//                if (user==null) return;
                // If successful then clear window
                mainframe.removeAll();
                // If a normal user then use useractions()
                // If an administrator then use adminActions()
                userActions();
            }
        });
    }

    /**
     * Show screen for user actions
     */
    private void userActions() {
        // Change the title
        frameTitle.setText("Select an Action");

        // Add the list back
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
     * Show screen to enter info for an emergency record
     */
    private void enterInfo() {
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
        ButtonGroup categories = new ButtonGroup();

        JButton selectRoute = new JButton("Select Route");

        // Add radiobuttons to the group
        categories.add(fire);
        categories.add(security);
        categories.add(health);
        categories.add(hoax);

        // Set properties of the fields
        callerTitle.setFont(new Font(callerTitle.getFont().getName(),Font.BOLD, 14));
        descriptionTitle.setFont(new Font(descriptionTitle.getFont().getName(),Font.BOLD, 14));
        categorizeTitle.setFont(new Font(categorizeTitle.getFont().getName(),Font.BOLD, 14));
        locationTitle.setFont(new Font(locationTitle.getFont().getName(),Font.BOLD, 14));

        descriptionTitle.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        descriptionText.setLineWrap(true);

        fire.setBackground(Color.WHITE);
        health.setBackground(Color.WHITE);
        security.setBackground(Color.WHITE);
        hoax.setBackground(Color.WHITE);

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
        fire.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        security.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        health.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        hoax.setAlignmentX(JFrame.CENTER_ALIGNMENT);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Color.WHITE);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.setBackground(Color.WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);

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

        // Add components to the sidebar
        sidebar.add(descriptionTitle);
        sidebar.add(new JLabel("  "));
        sidebar.add(descriptionScroll);

        // Add components to
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
        // Set previous frame
        previous = "info";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Show a summary of the emergency record to finalize it or cancel
     */
    private void summaryView() {
        // Set previous frame
        previous = "route";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Show the screen to generate some statistics about the emergency records
     */
    private void generateStats() {
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
        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Admin only: Show the screen to add and remove users
     */
    private void manageUsers() {
        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
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
     * Admin only: Show the screen to view the activity of a system user
     */
    private void viewUserActivity() {
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
        // Set previous frame
        previous = "user";

        // Enable back button
        back.setEnabled(true);

        // Refresh the window
        frame.revalidate();
        frame.repaint();
    }

}