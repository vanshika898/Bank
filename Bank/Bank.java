import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class BankAccount {
    String name;
    String address;
    String accountType;
    int accountNumber;
    int balance;
    BankAccount next;

    public BankAccount(String name, String address, String accountType, int accountNumber, int balance) {
        this.name = name;
        this.address = address;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.next = null;
    }

    public String toString() {
        return "Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Type of account: " + accountType + "\n" +
                "Amount deposited: " + balance;
    }

    static class Transaction {
        static int transactionCounter = 1;
        int transactionId;
        Date date;
        int amount;

        public Transaction(int amount) {
            this.transactionId = transactionCounter++;
            this.date = new Date();
            this.amount = amount;
        }

        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return String.format("Transaction ID: %d\nDate: %s\nAmount: %d", transactionId, sdf.format(date), amount);
        }
    }
}

class CustomerGraph {
    private LinkedList<BankAccount>[] adjacencyList;

    public CustomerGraph(int numCustomers) {
        adjacencyList = new LinkedList[numCustomers];
        for (int i = 0; i < numCustomers; i++) {
            adjacencyList[i] = new LinkedList<>();
        }
    }

    public void displayCustomerInfo() {
        System.out.println("Customer Information:");
        for (int i = 0; i < adjacencyList.length; i++) {
            if (!adjacencyList[i].isEmpty()) {
                System.out.println("Customer Name: " + adjacencyList[i].get(0).name);
                System.out.println("Account Type: " + adjacencyList[i].get(0).accountType);
                System.out.println("---------------------");
            }
        }
    }

    public void addEdge(int from, BankAccount to) {
        adjacencyList[from].add(to);
    }
}

public class Bank {
    private BankAccount head;
    private Queue<Integer> transactionQueue;
    private int tbalance;
    private Stack<BankAccount> transactionStack;
    private CustomerGraph customerGraph;

    private List<BankAccount> accounts;

    public Bank() {
        head = null;
        transactionQueue = new LinkedList<>();
        tbalance = 0;
        transactionStack = new Stack<>();
        customerGraph = new CustomerGraph(10);
        accounts = new ArrayList<>();
    }

    public void saveAccountsToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            Set<Integer> accountNumbers = new HashSet<>();
            for (BankAccount account : accounts) {
                if (!accountNumbers.contains(account.accountNumber)) {
                    writer.println(account.name);
                    writer.println(account.address);
                    writer.println(account.accountType);
                    writer.println(account.accountNumber);
                    writer.println(account.balance);
                    writer.println("-----------------------#CUSTOMER#--------------------");
                    accountNumbers.add(account.accountNumber);
                }
            }
            System.out.println("Accounts saved to file: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAccountsFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNext()) {
                String name = scanner.nextLine();
                String address = scanner.nextLine();
                String accountType = scanner.nextLine();
                int accountNumber = Integer.parseInt(scanner.nextLine());
                int balance = Integer.parseInt(scanner.nextLine());

                while (scanner.hasNext()
                        && !scanner.nextLine().equals("-----------------------#CUSTOMER#--------------------"))
                    ;
                BankAccount account = new BankAccount(name, address, accountType, accountNumber, balance);
                accounts.add(account);
            }
            sortByAccountNumber();
            System.out.println("Accounts loaded from file: " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        }
    }

    private void merge(List<BankAccount> arr, int l, int m, int r, String sortBy) {
        int n1 = m - l + 1;
        int n2 = r - m;

        List<BankAccount> L = new ArrayList<>(arr.subList(l, m + 1));
        List<BankAccount> R = new ArrayList<>(arr.subList(m + 1, r + 1));

        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (sortBy.equals("accountNumber") && L.get(i).accountNumber <= R.get(j).accountNumber) {
                arr.set(k++, L.get(i++));
            } else if (sortBy.equals("name") && L.get(i).name.compareTo(R.get(j).name) <= 0) {
                arr.set(k++, L.get(i++));
            } else if (sortBy.equals("accountType")) {
                if (L.get(i).accountType.equals("savings")) {
                    arr.set(k++, L.get(i++));
                } else if (R.get(j).accountType.equals("savings")) {
                    arr.set(k++, R.get(j++));
                } else {
                    arr.set(k++, L.get(i++));
                }
            } else {
                arr.set(k++, R.get(j++));
            }
        }

        while (i < n1) {
            arr.set(k++, L.get(i++));
        }

        while (j < n2) {
            arr.set(k++, R.get(j++));
        }
    }

    private void mergeSort(List<BankAccount> arr, int l, int r, String sortBy) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(arr, l, m, sortBy);
            mergeSort(arr, m + 1, r, sortBy);
            merge(arr, l, m, r, sortBy);
        }
    }

    public void sortByAccountNumber() {
        mergeSort(accounts, 0, accounts.size() - 1, "accountNumber");
        // System.out.println("Accounts sorted by account number.");
    }

    public void sortByName() {
        mergeSort(accounts, 0, accounts.size() - 1, "name");
        System.out.println("Accounts sorted by name.");
    }

    public void sortByAccountType() {
        mergeSort(accounts, 0, accounts.size() - 1, "accountType");
        System.out.println("Accounts sorted by account type.");
    }

    public void openAccount() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter your full name: ");
            String name = null;
            while (true) {
                try {
                    name = scanner.nextLine();
                    if (!name.matches("[a-zA-Z ]+")) {
                        throw new IllegalArgumentException("Name must contain only letters.");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.print("Please enter a valid name: ");
                }
            }

            System.out.print("Enter your address: ");
            String address = scanner.nextLine();

            System.out.print("What type of account you want to open (savings/current): ");
            String accountType = null;
            while (true) {
                try {
                    accountType = scanner.nextLine().toLowerCase();
                    if (!accountType.equals("savings") && !accountType.equals("current")) {
                        throw new IllegalArgumentException(
                                "Invalid account type. Please enter 'savings' or 'current'.");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.print("Please enter a valid account type: ");
                }
            }

            int accountNumber = (int) ((Math.random() * 9000) + 1000);

            int balance;
            while (true) {
                try {
                    System.out.print("Enter amount for deposit: ");
                    balance = Integer.parseInt(scanner.nextLine());
                    if (balance <= 0) {
                        throw new IllegalArgumentException("Deposit amount must be greater than zero.");
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number for deposit amount.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            BankAccount newAccount = new BankAccount(name, address, accountType, accountNumber, balance);

            if (head == null) {
                head = newAccount;
            } else {
                BankAccount current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newAccount;
            }

            tbalance += newAccount.balance;
            System.out.println("Your account is created . Account number: " + newAccount.accountNumber);

            int customerIndex = newAccount.accountNumber % 10;
            customerGraph.addEdge(customerIndex, newAccount);
            accounts.add(newAccount); // Add the new account to the list
            saveAccountsToFile("accounts.txt");
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    public void depositMoney() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number to deposit money: ");
        int accountNumber = scanner.nextInt();

        BankAccount account = findAccount(accountNumber);

        if (account != null) {
            System.out.print("Enter amount to deposit: ");
            int amount = scanner.nextInt();

            if (amount > 0) {
                account.balance += amount;
                tbalance += amount;
                System.out.println("Deposit successful. New balance: " + account.balance);
                transactionStack.push(account);
                saveAccountsToFile("accounts.txt");

                int customerIndex = accountNumber % 10;
                customerGraph.addEdge(customerIndex, account);
            } else {
                System.out.println("Invalid deposit amount.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    public void withdrawMoney() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number to withdraw money: ");
        int accountNumber = scanner.nextInt();

        BankAccount account = findAccount(accountNumber);

        if (account != null) {
            System.out.print("Enter amount to withdraw: ");
            int amount = scanner.nextInt();

            if (amount > 0 && amount <= account.balance) {
                account.balance -= amount;
                tbalance -= amount;
                System.out.println("Withdrawal successful. New balance: " + account.balance);
                saveAccountsToFile("accounts.txt");
                transactionStack.push(account);

                int customerIndex = accountNumber % 10;
                customerGraph.addEdge(customerIndex, account);
            } else {
                System.out.println("Invalid withdrawal amount or insufficient funds.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    public void processTransactions() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number to process transactions: ");
        int accountNumber = scanner.nextInt();

        BankAccount account = findAccount(accountNumber);

        if (account != null) {
            int totalTransactionAmount = 0;
            Stack<BankAccount.Transaction> transactionStack = new Stack<>();

            while (true) {
                System.out.print("Enter amount for transaction (or -1 to exit): ");
                int amount = scanner.nextInt();

                if (amount == -1) {
                    break;
                }

                if (amount >= 0) {
                    if (account.balance - amount >= 0) {
                        account.balance -= amount;
                        tbalance -= amount;
                        totalTransactionAmount += amount;
                        System.out.println("Transaction successful. New balance: " + account.balance);

                        BankAccount.Transaction transaction = new BankAccount.Transaction(amount);
                        transactionStack.push(transaction);

                        int customerIndex = accountNumber % 10;
                        customerGraph.addEdge(customerIndex, account);
                    } else {
                        System.out.println("Invalid transaction. Insufficient funds.");
                    }
                } else {
                    System.out.println("Invalid transaction amount.");
                }
            }

            System.out.println("Total balance after transactions: " + account.balance);
            System.out.println("Transaction bill:");

            while (!transactionStack.isEmpty()) {
                System.out.println(transactionStack.pop());
                System.out.println("---------------------");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    public void displayAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number to display account details: ");
        int accountNumber = scanner.nextInt();

        BankAccount account = findAccount(accountNumber);

        if (account != null) {
            System.out.println("Name: " + account.name);
            System.out.println("Address: " + account.address);
            System.out.println("Type of account: " + account.accountType);
            System.out.println("Amount deposited: " + account.balance);
        } else {
            System.out.println("Account not found.");
        }
    }

    private BankAccount findAccount(int accountNumber) {
        sortByAccountNumber();
        int left = 0;
        int right = accounts.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            BankAccount midAccount = accounts.get(mid);

            if (midAccount.accountNumber == accountNumber) {
                return midAccount;
            } else if (midAccount.accountNumber < accountNumber) {
                left = mid + 1;
            } else {
                right = mid - 1; // Search in the left half
            }
        }
        return null; // Account not found
    }

    public void transfer_fund() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--------------------------------------------------------------------- ");
        System.out.println("                   Looking for fund transfer to another account-         ");
        System.out.print("Enter the amount you want to transfer: ");
        int fund = scanner.nextInt();

        if (fund <= tbalance) {
            System.out.print("Enter account_number you want to transfer money: ");
            int a_n = scanner.nextInt();
            int t = tbalance - fund;
            System.out.println("Your fund is successfully transferred to account number " + a_n + "!");
            System.out.println("After the transaction, your current balance becomes: " + t);
            System.out.println(
                    "------------------------------------------------------------------------------------------");
        } else {
            System.out.print(
                    "You don't have enough money to transfer, do you have another account to link with? (yes/no): ");
            String b = scanner.next();
            if (b.equals("yes")) {
                account_linking(200);
            } else {
                System.out.println("Invalid");
            }
        }
    }

    public void calinterest() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--------------------------------------------------------------------- ");
        System.out.println("                   Interest calculation of current balance-          ");
        System.out.print("Enter Account number: ");
        int accountNumber = scanner.nextInt();

        BankAccount account = findAccount(accountNumber);

        if (account != null) {
            System.out.print("Enter Account type (current / saving): ");
            String type = scanner.next();

            if (type.equals("saving") || type.equals("savings")) {
                int interest = (int) (0.05 * account.balance);
                int totalAmount = account.balance + interest;
                System.out.println("Interest calculated for the month: " + interest);
                System.out.println("Your current amount with 0.5% interest is: " + totalAmount);
            } else {
                System.out.println("No interest for the current account.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    public void account_linking(int a_mount) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the account number of the account for linking: ");
        int linkedAccountNumber = scanner.nextInt();
        BankAccount linkedAccount = findAccount(linkedAccountNumber);
        if (linkedAccount != null) {
            System.out.println("In the linked account, the balance is: " + linkedAccount.balance);
            int total_balance = a_mount + tbalance + linkedAccount.balance;
            System.out.println("By linking this account, your total balance is: " + total_balance);
        } else {
            System.out.println("Account not found. Cannot link to a non-existent account.");
        }
    }

    void exitProgram() {
        System.out.println("Exiting the program.");
        System.exit(0); // Exiting the program
    }

    private static void displaySortedAccounts(Bank bank, Scanner scanner) {
        System.out.println("How would you like to display the accounts?");
        System.out.println("1) Sort by name");
        System.out.println("2) Sort by account number");
        System.out.println("3) Sort by account type");
        System.out.print("Enter your choice: ");
        int sortChoice = scanner.nextInt();

        switch (sortChoice) {
            case 1:
                bank.sortByName();
                break;

            case 2:
                bank.sortByAccountNumber();
                break;

            case 3:
                bank.sortByAccountType();
                break;

            default:
                System.out.println("Invalid choice. Accounts will be displayed without sorting.");
        }

        bank.customerGraph.displayCustomerInfo();
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        int choice;
        do {
            System.out.println("1) Open account");
            System.out.println("2) Deposit money");
            System.out.println("3) Withdraw money");
            System.out.println("4) Display Customer Information");
            System.out.println("5) Process transactions and exit");
            System.out.println("6) Transfer fund");
            System.out.println("7) Calculate interest");
            System.out.println("8) Account Linking");
            System.out.println("9) Display Account");
            System.out.println("10) Exit");

            System.out.print("Please select an option: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    bank.openAccount();
                    break;
                case 2:
                    bank.depositMoney();
                    break;
                case 3:
                    bank.withdrawMoney();
                    break;
                case 4:
                    displaySortedAccounts(bank, scanner);
                    // jabbank.customerGraph.displayCustomerInfo();
                    bank.saveAccountsToFile("accounts.txt");
                    bank.sortByAccountNumber();
                    bank.loadAccountsFromFile("accounts.txt");
                    break;
                case 5:
                    bank.processTransactions();
                    bank.saveAccountsToFile("accounts.txt");
                    System.out.println("Exit");
                    break;
                case 6:
                    bank.transfer_fund();
                    bank.saveAccountsToFile("accounts.txt");
                    break;
                case 7:
                    bank.calinterest();
                    break;
                case 8:
                    bank.account_linking(200);
                    bank.saveAccountsToFile("accounts.txt");
                    break;
                case 9:
                    bank.displayAccount();
                    break;
                case 10:
                    bank.exitProgram();
                    bank.saveAccountsToFile("accounts.txt");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 11);
    }
}