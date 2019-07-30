package ub.edu.softwaredistribuit.utils;

import ub.edu.softwaredistribuit.commons.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Custom menu and action tracker
 */
public class CustomMenu {

    private String title;
    private HashMap<Command, Boolean> cmds;

    /**
     * Generate a menu with custom title and selected cmds
     * @param title Title of the menu to show
     * @param cmds Commands
     */
    public CustomMenu(String title, HashMap<Command, Boolean> cmds) {
        this.title = title;
        this.cmds = cmds;
    }

    /**
     * Generate a menu with custom title and predefined cmds
     * @param title Title of the menu to show
     */
    public CustomMenu(String title) {
        this.title = title;
        this.cmds = new HashMap<>();
        for(Command c : Command.values()){
            if(c == Command.STRT || c == Command.EXIT){
                cmds.put(c, true);
            }else{
                cmds.put(c, false);
            }
        }
        this.changeCommandState(Command.EXIT, false);
    }

    /**
     * Show the menu with all available commands
     */
    public void showMenu() {

        String lines = repeat("-", 18);
        System.out.println(lines);
        System.out.println(this.title.toUpperCase());
        System.out.println(lines);

        ArrayList<Command> availableCmds = this.availableCmds();
        for (int i = 0; i < availableCmds.size(); i++) {
            Command c = availableCmds.get(i);
            System.out.print("\t" + (i + 1) + ".- ");
            System.out.println(c);
        }
        System.out.println(lines);
    }

    /**
     * Get the option that player selected from the menu
     * @param sc Scanner
     * @return Command selected
     */
    public Command getOption(Scanner sc) {
        Command option = null;
        int optionInt = -1;
        do {
            System.out.print("Select a Command >> ");
            optionInt = sc.nextInt();
            sc.nextLine();
            if (optionInt > 0 && optionInt <= this.cmds.size()) {
                option = this.availableCmds().get(optionInt - 1);
            }
            else {
                System.err.println("The Command selected is not correct. Select a Command from 1 to " + this.cmds.size());
            }
        } while (option == null);
        return option;
    }

    private String repeat(String s, int times) {
        if (times <= 0) {
            return "";
        } else {
            return s + repeat(s, times - 1);
        }
    }

    /**
     * Get all the current enabled Commands
     * @return Arraylist with enabled commands
     */
    public ArrayList<Command> availableCmds() {
        ArrayList<Command> available = new ArrayList<>();
        cmds.forEach((key, value) -> {
            if (value) {
                available.add(key);
            }
        });
        return available;
    }

    /**
     * Change the Command state from enabled or disabled
     * @param cm Command to change its state
     */
    public void changeCommandState(Command cm){
        this.cmds.put(cm, !this.cmds.get(cm));

    }

    /**
     * Change the Command state to certain state enabled or disable
     * @param cm Command to change its state
     * @param active State to be changed to
     */
    public void changeCommandState(Command cm, boolean active){
        this.cmds.put(cm, active);

    }

    /**
     * Select random Command from all enabled commands
     * @return Command selected randomly
     */
    public Command selectRandomAction(){
        ArrayList<Command> pscmds = availableCmds();
        return pscmds.get(new Random().nextInt(pscmds.size()));
    }

}
