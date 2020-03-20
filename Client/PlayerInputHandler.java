package Version0a2.Client;

import java.util.Scanner;

public class PlayerInputHandler extends Thread {
    private Scanner in;
    private Handler handler;
    private int numberOfCards = 10;
    private String tag;

    private boolean lookingForResponse = false;

    public PlayerInputHandler(Scanner in) {
        this.in = in;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            handlePlayerInput(in.nextLine());
        }
    }

    public void setLookingForResponse(boolean val) {
        lookingForResponse = val;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void handlePlayerInput(String str) {
        if (lookingForResponse) {
            try {
                int response = Integer.valueOf(str);

                if (response >= 1 && response <= numberOfCards) {

                    lookingForResponse = false;
                    if (tag == null) {
                        handler.sendString("reply", String.valueOf(response));
                    } else if (tag.equals("czar")) {
                        handler.sendString(tag,String.valueOf(response));
                        tag = null;
                    }
                    System.out.println("Chosen card " + response);
                } else {
                    System.out.println("Invalid response!");
                }
            } catch(Exception e) {
                System.out.println("Invalid response!");
            }
        }
    }
}