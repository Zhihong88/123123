package sg.edu.np.mad.assignment.Model;

public class Budget {
    String budget,expense;

    public Budget(String budget, String expense) {
        this.budget = budget;
        this.expense = expense;
    }

    public Budget() {
    }


    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }
}
