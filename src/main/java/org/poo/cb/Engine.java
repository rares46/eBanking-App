package org.poo.cb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Engine {
    private static Engine instance;
    private Engine() {}
    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    final private static ArrayList<User> users = new ArrayList<>();
    final private static ArrayList<String> recommendedStocks = new ArrayList<>();

    public static void createUser(String email, String firstname, String lastname, String address) {
        if (!userExists(email)) {
            User user = new User.Builder().email(email).firstName(firstname).lastName(lastname).address(address).build();
            users.add(user);
        } else {
            System.out.println("User with " + email + " already exists");
        }
    }

    public static void addFriend(String email, String emailFriend) {
        if (userExists(email) && userExists(emailFriend)) {
            User user1 = null;
            User user2 = null;
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    user1 = user;
                }
                if (user.getEmail().equals(emailFriend)) {
                    user2 = user;
                }
            }
            if (!user1.getFriends().contains(emailFriend)) {
                user1.getFriends().add(emailFriend);
                user2.getFriends().add(email);
            } else {
                System.out.println("User with " + emailFriend + " is already a friend");
            }
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    public static void addAccount(String email, String currency) {
        if (userExists(email)) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    if (!accountExists(user, currency)) {
                        user.getAccounts().add(new Account(currency));
                    } else {
                        System.out.println("Account in currency " + currency + " already exists for user" + user);
                    }
                }
            }
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    public static void addMoney (String email, String currency, double amount) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                for (Account account : user.getAccounts()) {
                    if (account.getCurrency().equals(currency)) {
                        account.setBalance(account.getBalance() + amount);
                    }
                }
            }
        }
    }

    public static void exchangeMoney(String exchangeFile, String email, String source, String destination, Double amount) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                Account sourceAccount = null;
                Account destinationAccount = null;
                for (Account account : user.getAccounts()) {
                    if (account.getCurrency().equals(source)) {
                        sourceAccount = account;
                    }
                    if (account.getCurrency().equals(destination)) {
                        destinationAccount = account;
                    }
                }
                double exchangeRate = getExchangeRate(exchangeFile, destination, source);
                double converted = amount * exchangeRate;
                if (sourceAccount.getBalance() >= converted) {
                    if ((converted > 0.5 * sourceAccount.getBalance()) && (!user.getPremium())) {
                        double commision = 0.01 * converted;
                        sourceAccount.setBalance(sourceAccount.getBalance() - (converted + commision));
                    } else {
                        sourceAccount.setBalance(sourceAccount.getBalance() - converted);
                    }
                    destinationAccount.setBalance(destinationAccount.getBalance() + amount);
                } else {
                    System.out.println("Insufficient amount in account " + source + " for exchange");
                }
            }
        }
    }

    private static double getExchangeRate(String exchangeFile, String source, String destination) {
        Map<String, Map<String, Double>> exchangeRates = new HashMap<>();
        try (BufferedReader b = new BufferedReader(new FileReader(exchangeFile))) {
            String line = b.readLine();
            String[] currencyes = line.split(",");
            while((line = b.readLine()) != null) {
                String[] conv = line.split(",");
                String original = conv[0];
                Map<String, Double> rates = new HashMap<>();
                for (int i = 1; i < conv.length; i++) {
                    rates.put(currencyes[i], Double.parseDouble(conv[i]));
                }
                exchangeRates.put(original, rates);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return exchangeRates.get(source).get(destination);
    }

    public static void transferMoney(String email, String emailTransfer, String currency, Double amount) {
        Account sourceAccount = null;
        Account destinationAccount = null;
        User sourceUser = null;
        User destinationUser = null;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                sourceUser = user;
            }
            if (user.getEmail().equals(emailTransfer)) {
                destinationUser = user;
            }
        }
        if (sourceUser.getFriends().contains(emailTransfer)) {
            for (Account account : sourceUser.getAccounts()) {
                if (account.getCurrency().equals(currency)) {
                    sourceAccount = account;
                }
            }
            for (Account account : destinationUser.getAccounts()) {
                if (account.getCurrency().equals(currency)) {
                    destinationAccount = account;
                }
            }
            if (sourceAccount.getBalance() >= amount) {
                sourceAccount.setBalance(sourceAccount.getBalance() - amount);
                destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            } else {
                System.out.println("Insufficient amount in account " + currency + " for transfer");
            }
        } else {
            System.out.println("You are not allowed to transfer money to " + emailTransfer);
        }
    }

    public static void buyStocks(String file, String email, String company, int noOfStocks) {
        User user = null;
        for (User user1 : users) {
            if (user1.getEmail().equals(email)) {
                user = user1;
            }
        }
        Account account = null;
        for (Account account1 : user.getAccounts()) {
            if (account1.getCurrency().equals("USD")) {
                account = account1;
            }
        }
        double pricePerStock = getPricePerStock(file, company);
        double premiumPrice = pricePerStock - 0.05 * pricePerStock;
        double totalPremium = noOfStocks * premiumPrice;
        double total = noOfStocks * pricePerStock;
        if(user.getPremium() && recommendedStocks.contains(company)) {
            if (account.getBalance() >= totalPremium) {
                Integer currentNoOfStock = user.getStocks().get(company);
                if (currentNoOfStock != null) {
                    noOfStocks = noOfStocks + currentNoOfStock;
                }
                user.getStocks().put(company, noOfStocks);
                account.setBalance(account.getBalance() - totalPremium);
            } else {
                System.out.println("Insufficient amount in account for buying stock");
            }
        } else {
            if (account.getBalance() >= total) {
                Integer currentNoOfStock = user.getStocks().get(company);
                if (currentNoOfStock != null) {
                    noOfStocks = noOfStocks + currentNoOfStock;
                }
                user.getStocks().put(company, noOfStocks);
                account.setBalance(account.getBalance() - total);
            } else {
                System.out.println("Insufficient amount in account for buying stock");
            }
        }
    }

    private static double getPricePerStock(String stockValuesFile, String company) {
        Map<String, Double> stockPrices = new HashMap<>();
        try (BufferedReader b = new BufferedReader(new FileReader(stockValuesFile))) {
            String line = b.readLine();
            while((line = b.readLine()) != null) {
                String[] conv = line.split(",");
                String companyFile = conv[0];
                double lastPrice = Double.parseDouble(conv[10]);
                stockPrices.put(companyFile, lastPrice);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return stockPrices.get(company);
    }

    public static void recommendStocks(String file) {
        String print = "{\"stocksToBuy\":[";
        boolean firstComma = false;
        try (BufferedReader b = new BufferedReader(new FileReader(file))) {
            String line = b.readLine();
            while((line = b.readLine()) != null) {
                String[] prices = line.split(",");
                double shortSMA = (Double.parseDouble(prices[6]) + Double.parseDouble(prices[7]) + Double.parseDouble(prices[8])
                        + Double.parseDouble(prices[9]) + Double.parseDouble(prices[10])) / 5;
                double longSMA = (Double.parseDouble(prices[1]) + Double.parseDouble(prices[2]) + Double.parseDouble(prices[3])
                        + Double.parseDouble(prices[4]) + Double.parseDouble(prices[5]) + Double.parseDouble(prices[6])
                        + Double.parseDouble(prices[7]) + Double.parseDouble(prices[8]) + Double.parseDouble(prices[9])
                        + Double.parseDouble(prices[10])) / 10;
                if (shortSMA > longSMA) {
                    if (firstComma) {
                        print = print + ",";
                    }
                    firstComma = true;
                    print = print + "\"" + prices[0] + "\"";
                    recommendedStocks.add(prices[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        print = print + "]}";
        System.out.println(print);
    }

    public static void listUser(String email) {
        if (userExists(email)) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    String string = "{\"email\":\"" + user.getEmail() + "\",\"firstname\":\"" + user.getFirstname()
                                + "\",\"lastname\":\"" + user.getLastname() + "\",\"address\":\"" + user.getAddress()
                                + "\",\"friends\":[";
                        boolean comma = false;
                        for(String friend : user.getFriends()) {
                            if (comma) {
                                string = string + ",";
                            }
                            string = string + "\"" + friend + "\"";
                            comma = true;
                        }
                        string = string + "]}";
                        System.out.println(string);
                }
            }
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    public static void listPortofolio(String email) {
        if (userExists(email)) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    DecimalFormat decimal = new DecimalFormat("0.00");
                    String portofolio = "{\"stocks\":[";
                    boolean comma = false;
                    Map<String, Integer> sortedMap = new TreeMap<>(user.getStocks());
                    for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                        String company = entry.getKey();
                        int noOfStocks = entry.getValue();
                        if (comma) {
                            portofolio = portofolio + ",";
                        }
                        portofolio = portofolio + "{\"stockName\":\"" + company
                                + "\",\"amount\":" + noOfStocks + "}";
                        comma = true;
                    }
                    portofolio = portofolio + "],\"accounts\":[";
                    comma = false;
                    for (Account account : user.getAccounts()) {
                        String balance = decimal.format(account.getBalance());
                        if (comma) {
                            portofolio = portofolio + ",";
                        }
                        portofolio = portofolio + "{\"currencyName\":\"" + account.getCurrency() + "\",\"amount\":\"" + balance + "\"}";
                        comma = true;
                    }
                    portofolio = portofolio + "]}";
                    System.out.println(portofolio);
                }
            }
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    public static void buyPremium (String email) {
        User user = null;
        Account account = null;
        if (userExists(email)) {
            for (User user1 : users) {
                if (user1.getEmail().equals(email)) {
                    user = user1;
                }
            }
            for (Account account1 : user.getAccounts()) {
                if (account1.getCurrency().equals("USD")) {
                    account = account1;
                }
            }
            if (account.getBalance() >= 100) {
                account.setBalance(account.getBalance() - 100);
                //Premium premium = new PremiumSilver();  // nu sunt implementate mai multe optiuni premium
                //premium.apply(user);              // asa ca se merge automat pe silver
                user.setPremium(true);
            } else {
                System.out.println("Insufficient amount in account for buying premium option");
            }
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    private static boolean userExists(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private static boolean accountExists(User user, String currency) {
        for (Account account : user.getAccounts()) {
            if (account.getCurrency().equals(currency)) {
                return true;
            }
        }
        return false;
    }

    public static void clearUsers() {
        users.clear();
    }
}