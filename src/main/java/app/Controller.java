package app;

import ui.UserView;

public class Controller {
	
	public String getUserInput(String s) {
		// TODO: get user input from UserView
		return s;
	}

	public static void main(String[] args) {
		System.out.println("Hello World!~");
		System.out.println("and open window...");
		javafx.application.Application.launch(UserView.class);
	}
	
}
