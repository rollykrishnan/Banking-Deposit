import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Bank Account Management System
enum AccountType {
    SAVINGS, CHECKING
}

class Transaction {
    private Date timestamp;
    private String description;
    private double amount;

    public Transaction(String description, double amount) {
        this.timestamp = new Date();
        this.description = description;
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return timestamp + " - " + description + ": $" + amount;
    }
}

class BankAccount {
    private String accountHolder;
    private AccountType accountType;
    private double balance;
    private double overdraftLimit;
    private List<Transaction> transactionHistory;

    public BankAccount(String accountHolder, AccountType accountType, double initialDeposit, double overdraftLimit) {
        this.accountHolder = accountHolder;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.overdraftLimit = overdraftLimit;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add(new Transaction("Initial Deposit", initialDeposit));
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        balance += amount;
        transactionHistory.add(new Transaction("Deposit", amount));
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount > balance + overdraftLimit) throw new IllegalArgumentException("Insufficient funds, including overdraft limit");
        balance -= amount;
        transactionHistory.add(new Transaction("Withdrawal", amount));
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}

// Unit Testing for BankAccount using JUnit
public class BankAccountTest {
    private BankAccount savingsAccount;
    private BankAccount checkingAccount;

    @Before
    public void setUp() {
        savingsAccount = new BankAccount("Alice", AccountType.SAVINGS, 1000.0, 0.0);
        checkingAccount = new BankAccount("Bob", AccountType.CHECKING, 500.0, 200.0);
    }

    @Test
    public void testDeposit() {
        savingsAccount.deposit(500.0);
        assertEquals(1500.0, savingsAccount.getBalance(), 0.001);
        assertTrue(savingsAccount.getTransactionHistory().get(1).getDescription().contains("Deposit"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDepositInvalidAmount() {
        savingsAccount.deposit(-100.0);
    }

    @Test
    public void testWithdrawWithinBalance() {
        checkingAccount.withdraw(300.0);
        assertEquals(200.0, checkingAccount.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawWithOverdraft() {
        checkingAccount.withdraw(600.0);  // Allowed due to $200 overdraft limit
        assertEquals(-100.0, checkingAccount.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithdrawExceedingOverdraft() {
        checkingAccount.withdraw(800.0);  // Exceeds overdraft limit
    }

    @Test
    public void testBalanceInquiry() {
        assertEquals(1000.0, savingsAccount.getBalance(), 0.001);
    }

    @Test
    public void testTransactionHistory() {
        savingsAccount.deposit(200.0);
        savingsAccount.withdraw(100.0);

        List<Transaction> history = savingsAccount.getTransactionHistory();
        assertEquals(3, history.size());  // Initial deposit + 2 transactions
        assertTrue(history.get(1).getDescription().contains("Deposit"));
        assertTrue(history.get(2).getDescription().contains("Withdrawal"));
    }

    @Test
    public void testAccountHolder() {
        assertEquals("Alice", savingsAccount.getAccountHolder());
        assertEquals("Bob", checkingAccount.getAccountHolder());
    }

    @Test
    public void testAccountType() {
        assertEquals(AccountType.SAVINGS, savingsAccount.getAccountType());
        assertEquals(AccountType.CHECKING, checkingAccount.getAccountType());
    }

    @Test
    public void testOverdraftLimit() {
        assertEquals(0.0, savingsAccount.getOverdraftLimit(), 0.001);
        assertEquals(200.0, checkingAccount.getOverdraftLimit(), 0.001);
    }
}