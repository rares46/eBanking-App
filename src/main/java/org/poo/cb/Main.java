package org.poo.cb;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        if(args == null) {
            System.out.println("Running Main");
        } else {
            String email;
            String firstname;
            String lastname;
            String address;
            engine.clearUsers();

            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + args[2]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String command = parts[0] + " " + parts[1];
                    if (command.equals("CREATE USER")) {
                        email = parts[2];
                        firstname = parts[3];
                        lastname = parts[4];
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 5; i < parts.length; i++) {
                            stringBuilder.append(parts[i]);
                            if (i < parts.length - 1) {
                                stringBuilder.append(" ");
                            }
                        }
                        address = stringBuilder.toString();
                        engine.createUser(email, firstname, lastname, address);
                    } else if (command.equals("ADD FRIEND")) {
                        email = parts[2];
                        String emailFriend = parts[3];
                        engine.addFriend(email, emailFriend);
                    } else if (command.equals("ADD ACCOUNT")) {
                        email = parts[2];
                        String currency = parts[3];
                        engine.addAccount(email, currency);
                    } else if (command.equals("ADD MONEY")) {
                        email = parts[2];
                        String currency = parts[3];
                        double amount = Double.parseDouble(parts[4]);
                        engine.addMoney(email, currency, amount);
                    } else if (command.equals("EXCHANGE MONEY")) {
                        email = parts[2];
                        String source = parts[3];
                        String destination = parts[4];
                        double amount = Double.parseDouble(parts[5]);
                        engine.exchangeMoney("src/main/resources/" + args[0], email, source, destination, amount);
                    } else if (command.equals("TRANSFER MONEY")) {
                        email = parts[2];
                        String emailTransfer = parts[3];
                        String currency = parts[4];
                        double amount = Double.parseDouble(parts[5]);
                        engine.transferMoney(email, emailTransfer, currency, amount);
                    } else if (command.equals("BUY STOCKS")) {
                        email = parts[2];
                        String company = parts[3];
                        int noOfStocks = Integer.parseInt(parts[4]);
                        engine.buyStocks("src/main/resources/" + args[1], email, company, noOfStocks);
                    } else if (command.equals("RECOMMEND STOCKS")) {
                        engine.recommendStocks("src/main/resources/" + args[1]);
                    } else if (command.equals("LIST USER")) {
                        email = parts[2];
                        engine.listUser(email);
                    } else if (command.equals("LIST PORTFOLIO")) {
                        email = parts[2];
                        engine.listPortofolio(email);
                    } else if (command.equals("BUY PREMIUM")) {
                        email = parts[2];
                        engine.buyPremium(email);
                    } else {
                        System.out.println("e gresita comanta sef");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
